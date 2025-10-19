package com.suqi8.oshin.hook.android

import android.content.pm.ApplicationInfo
import android.content.pm.Signature
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.xposed.prefs.YukiHookPrefsBridge
import java.io.PrintWriter

class PackageManagerServices : YukiBaseHooker() {

    private var mPMS: Any? = null

    override fun onHook() {
        loadSystem {
            val prefs = prefs("android\\package_manager_services")
            hookDowngradeChecks(prefs)
            hookCoreSignatureChecks(prefs)
            hookInstallationChecks(prefs)
            hookSystemApiChecks(prefs)
            hookDebugCommands()
        }
    }

    /**
     * Hook 允许应用降级安装的功能
     */
    private fun PackageParam.hookDowngradeChecks(prefs: YukiHookPrefsBridge) {
        if (!prefs.getBoolean("allow_downgrade", true)) return
        "com.android.server.pm.PackageManagerServiceUtils".toClass().resolve()
            .firstMethod { name = "checkDowngrade" }.hook {
                before { result = null }
            }
    }

    /**
     * Hook 核心签名验证
     */
    private fun PackageParam.hookCoreSignatureChecks(prefs: YukiHookPrefsBridge) {
        if (prefs.getBoolean("disable_jar_verifier", true)) {
            "android.util.jar.StrictJarVerifier".toClass().resolve().apply {
                method { name = "verifyMessageDigest" }.forEach { it.hook { before { result = true } } }
                method { name = "verify" }.forEach { it.hook { before { result = true } } }
            }
        }
        if (prefs.getBoolean("disable_message_digest", true)) {
            "java.security.MessageDigest".toClass().resolve().method { name = "isEqual" }.forEach {
                it.hook { before { result = true } }
            }
        }
    }

    /**
     * Hook 安装过程中的各种检查
     */
    private fun PackageParam.hookInstallationChecks(prefs: YukiHookPrefsBridge) {
        // 绕过 resources.arsc 校验
        if (prefs.getBoolean("bypass_arsc_uncompressed_check", true)) {
            "android.content.res.AssetManager".toClass().resolve().method { name = "containsAllocatedTable" }.forEach {
                it.hook { before { result = false } }
            }
        }

        // 绕过最小签名版本检查
        if (prefs.getBoolean("bypass_min_signature_version_check", true)) {
            "android.util.apk.ApkSignatureVerifier".toClass().resolve()
                .firstMethod {
                    name = "getMinimumSignatureSchemeVersionForTargetSdk"
                    parameters(Int::class)
                }.hook {
                    before { result = 0 }
                }
        }

        // 覆盖安装签名不一致处理
        if (prefs.getBoolean("allow_signature_mismatch_on_update", true)) {
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
        }

        // 禁用安装验证
        if (prefs.getBoolean("disable_install_verification", true)) {
            "com.android.server.pm.VerifyingSession".toClass().resolve().method { name = "isVerificationEnabled" }.forEach {
                it.hook { before { result = false } }
            }
        }
    }

    /**
     * Hook 系统 API 权限相关的检查
     */
    private fun PackageParam.hookSystemApiChecks(prefs: YukiHookPrefsBridge) {
        // 允许系统应用使用隐藏 API
        if (prefs.getBoolean("allow_system_app_hidden_api", true)) {
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
        if (prefs.getBoolean("allow_nonsystem_shared_uid", true)) {
            "com.android.server.pm.ReconcilePackageUtils".toClass().resolve().firstField {
                name = "ALLOW_NON_PRELOADS_SYSTEM_SHAREDUIDS"
            }.set(true)
        }
    }

    /**
     * Hook adb shell 调试命令
     */
    private fun PackageParam.hookDebugCommands() {
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
                    if (cmd != "corepatch" || mPMS == null) return@before

                    result = 0 // 阻止原始方法执行
                    val shellCommandInstance = instance
                    try {
                        val localPms = mPMS ?: return@before

                        val pw = shellCommandInstance.asResolver().firstMethod { name = "getOutPrintWriter" }.invoke() as PrintWriter
                        val type = shellCommandInstance.asResolver().firstMethod { name = "getNextArgRequired" }.invoke() as String
                        val settings = localPms.asResolver().firstField { name = "mSettings" }.get()

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
