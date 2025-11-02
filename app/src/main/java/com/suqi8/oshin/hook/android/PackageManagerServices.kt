package com.suqi8.oshin.hook.android

import android.content.pm.ApplicationInfo
import android.content.pm.Signature
import com.github.kyuubiran.ezxhelper.paramTypes
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.xposed.prefs.YukiHookPrefsBridge
import java.io.PrintWriter

class PackageManagerServices : YukiBaseHooker() {

    override fun onHook() {
        loadSystem {
            val prefs = prefs("android\\package_manager_services")
            hookDowngradeChecks(prefs)
            hookCoreSignatureChecks(prefs)
            hookInstallationChecks(prefs)
            hookSystemApiChecks(prefs)
            hookDebugCommands(prefs)
        }
    }

    /**
     * Hook 允许应用降级安装的功能
     */
    private fun PackageParam.hookDowngradeChecks(prefs: YukiHookPrefsBridge) {
        if (!prefs.getBoolean("allow_downgrade", false)) return
        "com.android.server.pm.PackageManagerServiceUtils".toClass().resolve()
            .firstMethod { name = "checkDowngrade" }.hook {
                before { result = null }
            }
    }

    /**
     * Hook 核心签名验证
     */
    private fun PackageParam.hookCoreSignatureChecks(prefs: YukiHookPrefsBridge) {
        if (prefs.getBoolean("disable_jar_verifier", false)) {
            "android.util.jar.StrictJarVerifier".toClass().resolve().apply {
                method { name = "verifyMessageDigest" }.forEach { it.hook { before { result = true } } }
                method { name = "verify" }.forEach { it.hook { before { result = true } } }
            }
        }
        if (prefs.getBoolean("disable_message_digest", false)) {
            "java.security.MessageDigest".toClass().resolve().method { name = "isEqual" }.forEach {
                it.hook { before { result = true } }
            }
        }
        if (prefs.getBoolean("disable_jar_verifier", false)) {
            "android.util.jar.StrictJarVerifier".toClass().resolve().constructor { }.hookAll {
                after {
                    instance.asResolver().firstField { name = "signatureSchemeRollbackProtectionsEnforced" }.set(false)
                }
            }
        }
    }

    /**
     * Hook 安装过程中的各种检查
     */
    private fun PackageParam.hookInstallationChecks(prefs: YukiHookPrefsBridge) {
        // 绕过 resources.arsc 校验
        if (prefs.getBoolean("bypass_arsc_uncompressed_check", false)) {
            "android.content.res.AssetManager".toClass().resolve().method { name = "containsAllocatedTable" }.forEach {
                it.hook { before { result = false } }
            }
        }

        // 绕过最小签名版本检查
        if (prefs.getBoolean("bypass_min_signature_version_check", false)) {
            "android.util.apk.ApkSignatureVerifier".toClass().resolve()
                .firstMethod {
                    name = "getMinimumSignatureSchemeVersionForTargetSdk"
                    parameters(Int::class)
                }.hook {
                    before { result = 0 }
                }
            "com.android.server.pm.ScanPackageUtils".toClass().resolve().method {
                name = "assertMinSignatureSchemeIsValid"
            }.hookAll {
                after { result = null } // 设为 null 以跳过异常
            }
        }

        // 覆盖安装签名不一致处理
        if (prefs.getBoolean("allow_signature_mismatch_on_update", false)) {
            "android.content.pm.SigningDetails".toClass().resolve().apply {
                method { name = "checkCapability" }.forEach {
                    it.hook {
                        before {
                            val capability = args(1).int()
                            if (capability != 4 && capability != 16) result = true
                        }
                    }
                }
            }
            val shouldBypassKeySet = ThreadLocal<Boolean>()
            "com.android.server.pm.KeySetManagerService".toClass().resolve().apply {
                method { name = "shouldCheckUpgradeKeySetLocked" }.hookAll {
                    after {
                        val stackTrace = Thread.currentThread().stackTrace
                        // 检查是否在安装包的堆栈调用中
                        val isFromPreparePackage = stackTrace.any { it.methodName.startsWith("preparePackage") }
                        if (isFromPreparePackage) {
                            shouldBypassKeySet.set(true)
                            result = true // 告诉系统“不需要检查”
                        } else {
                            shouldBypassKeySet.set(false)
                        }
                    }
                }
                method { name = "checkUpgradeKeySetLocked" }.hookAll {
                    after {
                        if (shouldBypassKeySet.get() == true) {
                            result = true // 告诉系统“检查通过”
                        }
                    }
                }
            }
            "com.android.server.pm.InstallPackageHelper".toClass().resolve().method {
                name = "doesSignatureMatchForPermissions"
                parameters(String::class.java, "com.android.internal.pm.parsing.pkg.ParsedPackage", Int::class.java)
            }.hookAll {
                after {
                    // 如果系统判定签名不匹配 (false)
                    if (result == false) {
                        val parsedPackage = args(1)
                        val targetPackageName = args(0).string()
                        // 检查包名是否一致，如果一致，则强行改为 true
                        val pPname = parsedPackage.asResolver().firstMethod { name = "getPackageName" }.invoke<String>()
                        if (pPname == targetPackageName) {
                            result = true
                        }
                    }
                }
            }
        }

        // 禁用安装验证
        if (prefs.getBoolean("disable_install_verification", false)) {
            "com.android.server.pm.VerifyingSession".toClass().resolve().method { name = "isVerificationEnabled" }.forEach {
                it.hook { before { result = false } }
            }
        }

        if (prefs.getBoolean("bypass_v1_signature_errors", false)) {
            hookV1SignatureErrors()
        }

        // 允许 Split APK 签名不一致
        if (prefs.getBoolean("allow_mismatched_split_apk_signatures", false)) {
            "android.content.pm.SigningDetails".toClass().resolve().method {
                name = "signaturesMatchExactly"
            }.hookAll {
                before { result = true }
            }
        }
    }

    private fun PackageParam.hookV1SignatureErrors() {
        val COREPATCH_SIGNATURE = "308203c6308202aea003020102021426d148b7c65944abcf3a683b4c3dd3b139c4ec85300d06092a864886f70d01010b05003074310b3009060355040613025553311330110603550408130a43616c69666f726e6961311630140603550407130d4d6f756e7461696e205669657731143012060355040a130b476f6f676c6520496e632e3110300e060355040b1307416e64726f69643110300e06035504031307416e64726f6964301e170d3139303130323138353233385a170d3439303130323138353233385a3074310b3009060355040613025553311330110603550408130a43616c69666f726e6961311630140603550407130d4d6f756e7461696e205669657731143012060355040a130b476f6f676c6520496e632e3110300e060355040b1307416e64726f69643110300e06035504031307416e64726f696430820122300d06092a864886f70d01010105000382010f003082010a028201010087fcde48d9beaeba37b733a397ae586fb42b6c3f4ce758dc3ef1327754a049b58f738664ece587994f1c6362f98c9be5fe82c72177260c390781f74a10a8a6f05a6b5ca0c7c5826e15526d8d7f0e74f2170064896b0cf32634a388e1a975ed6bab10744d9b371cba85069834bf098f1de0205cdee8e715759d302a64d248067a15b9beea11b61305e367ac71b1a898bf2eec7342109c9c5813a579d8a1b3e6a3fe290ea82e27fdba748a663f73cca5807cff1e4ad6f3ccca7c02945926a47279d1159599d4ecf01c9d0b62e385c6320a7a1e4ddc9833f237e814b34024b9ad108a5b00786ea15593a50ca7987cbbdc203c096eed5ff4bf8a63d27d33ecc963990203010001a350304e300c0603551d13040530030101ff301d0603551d0e04160414a361efb002034d596c3a60ad7b0332012a16aee3301f0603551d23041830168014a361efb002034d596c3a60ad7b0332012a16aee3300d06092a864886f70d01010b0500038201010022ccb684a7a8706f3ee7c81d6750fd662bf39f84805862040b625ddf378eeefae5a4f1f283deea61a3c7f8e7963fd745415153a531912b82b596e7409287ba26fb80cedba18f22ae3d987466e1fdd88e440402b2ea2819db5392cadee501350e81b8791675ea1a2ed7ef7696dff273f13fb742bb9625fa12ce9c2cb0b7b3d94b21792f1252b1d9e4f7012cb341b62ff556e6864b40927e942065d8f0f51273fcda979b8832dd5562c79acf719de6be5aee2a85f89265b071bf38339e2d31041bc501d5e0c034ab1cd9c64353b10ee70b49274093d13f733eb9d3543140814c72f8e003f301c7a00b1872cc008ad55e26df2e8f07441002c4bcb7dc746745f0db"

        val packageParserExceptionClass = "android.content.pm.PackageParser.PackageParserException".toClass()
        val errorField = packageParserExceptionClass.resolve().firstField { name = "error" }

        "android.util.apk.ApkSignatureVerifier".toClass().resolve().method {
            name = "verifyV1Signature"
        }.hookAll {
            after {
                val throwable = this.throwable ?: return@after

                var isV1Error = false

                // 检查 throwable 本身
                if (throwable::class.java == packageParserExceptionClass) {
                    if (errorField.get() as Int == -103) isV1Error = true
                }

                // 检查 throwable 的 cause
                if (!isV1Error) {
                    val cause = throwable.cause
                    if (cause != null && cause::class.java == packageParserExceptionClass) {
                        if (errorField.get() as Int == -103) isV1Error = true
                    }
                }

                if (!isV1Error) return@after

                // 创建签名
                val fakeSigs = arrayOf(Signature(COREPATCH_SIGNATURE))
                val newInstance = "android.content.pm.SigningDetails".toClass().resolve()
                    .firstConstructor {
                        paramTypes(Array<Signature>::class.java, Int::class.java)
                    }.create(fakeSigs, 1)

                result = newInstance
            }
        }
    }

    /**
     * Hook 系统 API 权限相关的检查
     */
    private fun PackageParam.hookSystemApiChecks(prefs: YukiHookPrefsBridge) {
        // 允许系统应用使用隐藏 API
        if (prefs.getBoolean("allow_system_app_hidden_api", false)) {
            ApplicationInfo::class.java.resolve().firstMethod {
                name = "isPackageWhitelistedForHiddenApis"
            }.hook {
                before {
                    val info = instance<ApplicationInfo>()
                    val FLAG_SYSTEM = 1
                    val FLAG_UPDATED_SYSTEM_APP = 128
                    if ((info.flags and FLAG_SYSTEM) != 0 || (info.flags and FLAG_UPDATED_SYSTEM_APP) != 0) {
                        result = true
                    }
                }
            }
        }

        // 共享用户ID签名逻辑
        if (prefs.getBoolean("allow_nonsystem_shared_uid", false)) {
            "com.android.server.pm.ReconcilePackageUtils".toClass().resolve().firstField {
                name = "ALLOW_NON_PRELOADS_SYSTEM_SHAREDUIDS"
            }.set(true)
        }
    }

    /**
     * Hook adb shell 调试命令
     */
    private fun PackageParam.hookDebugCommands(prefs: YukiHookPrefsBridge) {
        if (prefs.getBoolean("pms_command", false)) {
            var mPMS: Any? = null

            // Hook PMS 的构造函数，以获取其实例
            "com.android.server.pm.PackageManagerService".toClass().resolve().constructor { }.hookAll {
                after {
                    mPMS = instance
                }
            }

            // Hook adb shell pm 命令的入口
            "com.android.server.pm.PackageManagerShellCommand".toClass().resolve()
                .firstMethod {
                    name = "onCommand"
                    parameters(String::class)
                }.hook {
                    before {
                        val cmd = args(0).string()
                        if (cmd != "pms" || mPMS == null) return@before

                        result = 0 // 阻止原始方法执行
                        val shellCommandInstance = instance
                        try {
                            val localPms = mPMS

                            val pw = shellCommandInstance.asResolver().firstMethod { name = "getOutPrintWriter" }.invoke() as PrintWriter
                            val type = shellCommandInstance.asResolver().firstMethod { name = "getNextArgRequired" }.invoke() as String
                            val settings = localPms?.asResolver()?.firstField { name = "mSettings" }
                                ?.get()

                            if (settings == null) {
                                pw.println("Error: Could not get mSettings from PMS.")
                                return@before
                            }

                            when (type) {
                                "p", "package" -> {
                                    val packageName = shellCommandInstance.asResolver().firstMethod { name = "getNextArgRequired" }.invoke() as String
                                    val packageSetting = settings.asResolver().firstMethod { name = "getPackageLPr"; parameters(String::class) }.invoke(packageName)
                                    if (packageSetting != null) {
                                        dumpPackageSetting(packageSetting, pw, settings)
                                    } else {
                                        pw.println("no package $packageName found")
                                    }
                                }
                                "su", "shareduser" -> {
                                    val name = shellCommandInstance.asResolver().firstMethod { name = "getNextArgRequired" }.invoke() as String
                                    val su = getSharedUser(name, settings)
                                    if (su != null) {
                                        dumpSharedUserSetting(su, pw)
                                    } else {
                                        pw.println("no shared user $name found")
                                    }
                                }
                                else -> pw.println("usage: <p|package|su|shareduser> <name>")
                            }
                        } catch (t: Throwable) {
                            YLog.error("CorePatch command failed", t)
                            instance.asResolver().firstMethod { name = "getErrPrintWriter" }.invoke<PrintWriter>()?.println(t)
                        }
                    }
                }
        }
    }

    /**
     * 调试功能所需的辅助方法
     */
    private fun dumpPackageSetting(packageSetting: Any, pw: PrintWriter, settings: Any) {
        val signingDetails = getSigningDetailsFromSetting(packageSetting)
        pw.println("signing for package $packageSetting")
        if (signingDetails != null) dumpSigningDetails(signingDetails, pw)

        val pkg = packageSetting.asResolver().firstField { name = "pkg" }.get()
        if (pkg == null) {
            pw.println("android package is null!")
            return
        }
        val sharedUserId = pkg.asResolver().firstMethod { name = "getSharedUserId" }.invoke<String>()
        pw.println("shared user id: $sharedUserId")
        if (sharedUserId != null) {
            getSharedUser(sharedUserId, settings)?.let { dumpSharedUserSetting(it, pw) }
        }
    }

    private fun getSharedUser(id: String, settings: Any): Any? {
        val sharedUserSettings = settings.asResolver().firstField { name = "mSharedUsers" }.get()
        return sharedUserSettings?.asResolver()?.firstMethod { name = "get"; parameters(Any::class) }
            ?.invoke(id)
    }

    private fun dumpSharedUserSetting(sharedUser: Any, pw: PrintWriter) {
        val signingDetails = getSigningDetailsFromSetting(sharedUser)
        pw.println("signing for shared user $sharedUser")
        if (signingDetails != null) dumpSigningDetails(signingDetails, pw)
    }

    private fun getSigningDetailsFromSetting(pkgOrSharedUser: Any): Any? {
        // 路径: <PackageSetting or SharedUserSetting>.signatures.mSigningDetails
        return pkgOrSharedUser.asResolver().firstField { name = "signatures" }.get()
            ?.asResolver()?.firstField { name = "mSigningDetails" }?.get()
    }

    private fun dumpSigningDetails(signingDetails: Any, pw: PrintWriter) {
        // 在 Android T (API 33) 及以上, signatures 是方法调用
        val signatures = signingDetails.asResolver().firstMethod { name = "getSignatures" }.invoke<Array<Signature>>()

        if (signatures == null) {
            pw.println("Could not get signatures.")
            return
        }
        signatures.forEachIndexed { i, sign ->
            pw.println("${i + 1}: ${sign.toCharsString()}")
        }
    }
}
