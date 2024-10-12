package io.github.suqi8.opatch.hook.corepatch

import android.annotation.TargetApi
import android.app.AndroidAppHelper
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import android.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import io.github.suqi8.opatch.BuildConfig
import java.io.PrintWriter
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.security.cert.Certificate
import java.security.cert.X509Certificate
import java.util.Arrays
import java.util.Objects
import java.util.zip.ZipEntry

@TargetApi(Build.VERSION_CODES.R)
open class CorePatchForR : XposedHelper(), IXposedHookLoadPackage, IXposedHookZygoteInit {
    @JvmField
    val prefs: XSharedPreferences = XSharedPreferences(BuildConfig.APPLICATION_ID, "conf")

    @Throws(
        IllegalAccessException::class,
        InvocationTargetException::class,
        InstantiationException::class
    )
    override fun handleLoadPackage(loadPackageParam: LoadPackageParam) {
        if (BuildConfig.DEBUG) {
            XposedBridge.log(
                "D/" + MainHook.TAG + " downgrade=" + prefs.getBoolean(
                    "downgrade",
                    true
                )
            )
            XposedBridge.log(
                "D/" + MainHook.TAG + " authcreak=" + prefs.getBoolean(
                    "authcreak",
                    false
                )
            )
            XposedBridge.log(
                "D/" + MainHook.TAG + " digestCreak=" + prefs.getBoolean(
                    "digestCreak",
                    true
                )
            )
            XposedBridge.log(
                "D/" + MainHook.TAG + " UsePreSig=" + prefs.getBoolean(
                    "UsePreSig",
                    false
                )
            )
            XposedBridge.log(
                "D/" + MainHook.TAG + " enhancedMode=" + prefs.getBoolean(
                    "enhancedMode",
                    false
                )
            )
            XposedBridge.log(
                "D/" + MainHook.TAG + " bypassBlock=" + prefs.getBoolean(
                    "bypassBlock",
                    true
                )
            )
            XposedBridge.log(
                "D/" + MainHook.TAG + " sharedUser=" + prefs.getBoolean(
                    "sharedUser",
                    false
                )
            )
            XposedBridge.log(
                "D/" + MainHook.TAG + " disableVerificationAgent=" + prefs.getBoolean(
                    "disableVerificationAgent",
                    true
                )
            )
        }

        val pmService = XposedHelpers.findClassIfExists(
            "com.android.server.pm.PackageManagerService",
            loadPackageParam.classLoader
        )
        if (pmService != null) {
            val checkDowngrade = XposedHelpers.findMethodExactIfExists(
                pmService, "checkDowngrade",
                "com.android.server.pm.parsing.pkg.AndroidPackage",
                "android.content.pm.PackageInfoLite"
            )
            if (checkDowngrade != null) {
                // 允许降级
                XposedBridge.hookMethod(checkDowngrade, ReturnConstant(prefs, "downgrade", null))
            }
            // exists on flyme 9(Android 11) only
            val flymeCheckDowngrade = XposedHelpers.findMethodExactIfExists(
                pmService, "checkDowngrade",
                "android.content.pm.PackageInfoLite",
                "android.content.pm.PackageInfoLite"
            )
            if (flymeCheckDowngrade != null) XposedBridge.hookMethod(
                flymeCheckDowngrade,
                ReturnConstant(prefs, "downgrade", true)
            )
        }

        // apk内文件修改后 digest校验会失败
        hookAllMethods(
            "android.util.jar.StrictJarVerifier",
            loadPackageParam.classLoader,
            "verifyMessageDigest",
            ReturnConstant(prefs, "authcreak", true)
        )
        hookAllMethods(
            "android.util.jar.StrictJarVerifier", loadPackageParam.classLoader, "verify",
            ReturnConstant(prefs, "authcreak", true)
        )
        hookAllMethods(
            "java.security.MessageDigest", loadPackageParam.classLoader, "isEqual",
            ReturnConstant(prefs, "authcreak", true)
        )

        // Targeting R+ (version " + Build.VERSION_CODES.R + " and above) requires"
        // + " the resources.arsc of installed APKs to be stored uncompressed"
        // + " and aligned on a 4-byte boundary
        // target >=30 的情况下 resources.arsc 必须是未压缩的且4K对齐
        hookAllMethods(
            "android.content.res.AssetManager",
            loadPackageParam.classLoader,
            "containsAllocatedTable",
            ReturnConstant(prefs, "authcreak", false)
        )

        // No signature found in package of version " + minSignatureSchemeVersion
        // + " or newer for package " + apkPath
        findAndHookMethod(
            "android.util.apk.ApkSignatureVerifier",
            loadPackageParam.classLoader,
            "getMinimumSignatureSchemeVersionForTargetSdk",
            Int::class.javaPrimitiveType,
            ReturnConstant(prefs, "authcreak", 0)
        )
        val apkVerifierClass = XposedHelpers.findClassIfExists(
            "com.android.apksig.ApkVerifier",
            loadPackageParam.classLoader
        )
        if (apkVerifierClass != null) {
            findAndHookMethod(
                apkVerifierClass,
                "getMinimumSignatureSchemeVersionForTargetSdk",
                Int::class.javaPrimitiveType,
                ReturnConstant(prefs, "authcreak", 0)
            )
        }

        // 当verifyV1Signature抛出转换异常时，替换一个签名作为返回值
        // 如果用户已安装apk，并且其定义了私有权限，则安装时会因签名与模块内硬编码的不一致而被拒绝。尝试从待安装apk中获取签名。如果其中apk的签名和已安装的一致（只动了内容）就没有问题。此策略可能有潜在的安全隐患。
        val pkc = XposedHelpers.findClass("sun.security.pkcs.PKCS7", loadPackageParam.classLoader)
        val constructor = XposedHelpers.findConstructorExact(pkc, ByteArray::class.java)
        constructor.isAccessible = true
        val ASV = XposedHelpers.findClass(
            "android.util.apk.ApkSignatureVerifier",
            loadPackageParam.classLoader
        )
        val sJarClass =
            XposedHelpers.findClass("android.util.jar.StrictJarFile", loadPackageParam.classLoader)
        val constructorExact = XposedHelpers.findConstructorExact(
            sJarClass,
            String::class.java,
            Boolean::class.javaPrimitiveType,
            Boolean::class.javaPrimitiveType
        )
        constructorExact.isAccessible = true
        val signingDetails = getSigningDetails(loadPackageParam.classLoader)
        val findConstructorExact = XposedHelpers.findConstructorExact(
            signingDetails,
            Array<Signature>::class.java,
            Integer.TYPE
        )
        findConstructorExact.isAccessible = true
        val packageParserException = XposedHelpers.findClass(
            "android.content.pm.PackageParser.PackageParserException",
            loadPackageParam.classLoader
        )
        val error = XposedHelpers.findField(packageParserException, "error")
        error.isAccessible = true
        val signingDetailsArgs = arrayOfNulls<Any>(2)
        signingDetailsArgs[1] = 1
        val parseResult = XposedHelpers.findClassIfExists(
            "android.content.pm.parsing.result.ParseResult",
            loadPackageParam.classLoader
        )
        hookAllMethods(
            "android.util.jar.StrictJarVerifier",
            loadPackageParam.classLoader,
            "verifyBytes",
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                public override fun afterHookedMethod(param: MethodHookParam) {
                    if (prefs.getBoolean("digestCreak", true)) {
                        if (!prefs.getBoolean("UsePreSig", false)) {
                            val block = constructor.newInstance(param.args[0])
                            val infos =
                                XposedHelpers.callMethod(block, "getSignerInfos") as Array<Any>
                            val info = infos[0]
                            val verifiedSignerCertChain = XposedHelpers.callMethod(
                                info,
                                "getCertificateChain",
                                block
                            ) as List<X509Certificate>
                            param.result = verifiedSignerCertChain.toTypedArray<X509Certificate>()
                        }
                    }
                }
            })
        hookAllMethods(
            "android.util.apk.ApkSignatureVerifier",
            loadPackageParam.classLoader,
            "verifyV1Signature",
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                public override fun afterHookedMethod(methodHookParam: MethodHookParam) {
                    if (prefs.getBoolean("authcreak", false)) {
                        val throwable = methodHookParam.throwable
                        var parseErr: Int? = null
                        if (parseResult != null && (methodHookParam.method as Method).returnType == parseResult) {
                            val result = methodHookParam.result
                            if (XposedHelpers.callMethod(result, "isError") as Boolean) {
                                parseErr = XposedHelpers.callMethod(result, "getErrorCode") as Int
                            }
                        }
                        if (throwable != null || parseErr != null) {
                            var lastSigs: Array<Signature?>? = null
                            try {
                                if (prefs.getBoolean("UsePreSig", false)) {
                                    val PM = AndroidAppHelper.currentApplication().packageManager
                                    if (PM == null) {
                                        XposedBridge.log("E/" + MainHook.TAG + " " + BuildConfig.APPLICATION_ID + " Cannot get the Package Manager... Are you using MiUI?")
                                    } else {
                                        val pI = if (parseErr != null) {
                                            PM.getPackageArchiveInfo(
                                                (methodHookParam.args[1] as String),
                                                0
                                            )
                                        } else {
                                            PM.getPackageArchiveInfo(
                                                (methodHookParam.args[0] as String),
                                                0
                                            )
                                        }
                                        val InstpI = PM.getPackageInfo(
                                            pI!!.packageName,
                                            PackageManager.GET_SIGNING_CERTIFICATES
                                        )
                                        lastSigs = InstpI.signingInfo!!.signingCertificateHistory
                                    }
                                }
                            } catch (ignored: Throwable) {
                            }
                            try {
                                if (lastSigs == null && prefs.getBoolean("digestCreak", true)) {
                                    val origJarFile = constructorExact.newInstance(
                                        methodHookParam.args[if (parseErr == null) 0 else 1],
                                        true,
                                        false
                                    )
                                    val manifestEntry = XposedHelpers.callMethod(
                                        origJarFile,
                                        "findEntry",
                                        "AndroidManifest.xml"
                                    ) as ZipEntry
                                    val lastCerts = if (parseErr != null) {
                                        XposedHelpers.callMethod(
                                            XposedHelpers.callStaticMethod(
                                                ASV,
                                                "loadCertificates",
                                                methodHookParam.args[0],
                                                origJarFile,
                                                manifestEntry
                                            ), "getResult"
                                        ) as Array<Array<Certificate>>
                                    } else {
                                        XposedHelpers.callStaticMethod(
                                            ASV,
                                            "loadCertificates",
                                            origJarFile,
                                            manifestEntry
                                        ) as Array<Array<Certificate>>
                                    }
                                    lastSigs = XposedHelpers.callStaticMethod(
                                        ASV,
                                        "convertToSignatures",
                                        lastCerts as Any
                                    ) as Array<Signature?>
                                }
                            } catch (ignored: Throwable) {
                            }
                            signingDetailsArgs[0] = Objects.requireNonNullElseGet(lastSigs) {
                                arrayOf<Signature?>(
                                    Signature(SIGNATURE)
                                )
                            }
                            var newInstance = findConstructorExact.newInstance(*signingDetailsArgs)

                            //修复 java.lang.ClassCastException: Cannot cast android.content.pm.PackageParser$SigningDetails to android.util.apk.ApkSignatureVerifier$SigningDetailsWithDigests
                            val signingDetailsWithDigests = XposedHelpers.findClassIfExists(
                                "android.util.apk.ApkSignatureVerifier.SigningDetailsWithDigests",
                                loadPackageParam.classLoader
                            )
                            if (signingDetailsWithDigests != null) {
                                val signingDetailsWithDigestsConstructorExact =
                                    XposedHelpers.findConstructorExact(
                                        signingDetailsWithDigests,
                                        signingDetails,
                                        MutableMap::class.java
                                    )
                                signingDetailsWithDigestsConstructorExact.isAccessible = true
                                newInstance = signingDetailsWithDigestsConstructorExact.newInstance(
                                    newInstance,
                                    null
                                )
                            }
                            if (throwable != null) {
                                val cause = throwable.cause
                                if (throwable.javaClass == packageParserException) {
                                    if (error.getInt(throwable) == -103) {
                                        methodHookParam.result = newInstance
                                    }
                                }
                                if (cause != null && cause.javaClass == packageParserException) {
                                    if (error.getInt(cause) == -103) {
                                        methodHookParam.result = newInstance
                                    }
                                }
                            }
                            if (parseErr != null && parseErr == -103) {
                                val input = methodHookParam.args[0]
                                XposedHelpers.callMethod(input, "reset")
                                methodHookParam.result =
                                    XposedHelpers.callMethod(input, "success", newInstance)
                            }
                        }
                    }
                }
            })

        //New package has a different signature
        //处理覆盖安装但签名不一致
        hookAllMethods(signingDetails, "checkCapability", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                // Don't handle PERMISSION (grant SIGNATURE permissions to pkgs with this cert)
                // Or applications will have all privileged permissions
                // https://cs.android.com/android/platform/superproject/+/master:frameworks/base/core/java/android/content/pm/PackageParser.java;l=5947?q=CertCapabilities
                if ((param.args[1] as Int != 4) && prefs.getBoolean("digestCreak", true)) {
                    param.result = true
                }
            }
        })
        // if app is system app, allow to use hidden api, even if app not using a system signature
        findAndHookMethod(
            "android.content.pm.ApplicationInfo",
            loadPackageParam.classLoader,
            "isPackageWhitelistedForHiddenApis",
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    if (prefs.getBoolean("digestCreak", true)) {
                        val info = param.thisObject as ApplicationInfo
                        if ((info.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                            || (info.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
                        ) {
                            param.result = true
                        }
                    }
                }
            })

        val keySetManagerClass =
            findClass("com.android.server.pm.KeySetManagerService", loadPackageParam.classLoader)
        if (keySetManagerClass != null) {
            val shouldBypass = ThreadLocal<Boolean>()
            hookAllMethods(
                keySetManagerClass,
                "shouldCheckUpgradeKeySetLocked",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        if (prefs.getBoolean(
                                "digestCreak",
                                true
                            ) && Arrays.stream<StackTraceElement>(
                                Thread.currentThread().stackTrace
                            )
                                .anyMatch { o: StackTraceElement -> "preparePackageLI" == o.methodName }
                        ) {
                            shouldBypass.set(true)
                            param.result = true
                        } else {
                            shouldBypass.set(false)
                        }
                    }
                })
            hookAllMethods(
                keySetManagerClass,
                "checkUpgradeKeySetLocked",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        if (prefs.getBoolean("digestCreak", true) && shouldBypass.get()) {
                            param.result = true
                        }
                    }
                })
        }

        // for SharedUser
        // "Package " + packageName + " has a signing lineage " + "that diverges from the lineage of the sharedUserId"
        // https://cs.android.com/android/platform/superproject/+/android-11.0.0_r21:frameworks/base/services/core/java/com/android/server/pm/PackageManagerServiceUtils.java;l=728;drc=02a58171a9d41ad0048d6a1a48d79dee585c22a5
        hookAllMethods(signingDetails, "hasCommonAncestor", object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun beforeHookedMethod(param: MethodHookParam) {
                if (prefs.getBoolean("digestCreak", true)
                    && prefs.getBoolean(
                        "sharedUser",
                        false
                    ) // because of LSPosed's bug, we can't hook verifySignatures while deoptimize it
                    && Arrays.stream(Thread.currentThread().stackTrace)
                        .anyMatch { o: StackTraceElement -> "verifySignatures" == o.methodName }
                ) param.result = true
            }
        })

        val utilClass = findClass(
            "com.android.server.pm.PackageManagerServiceUtils",
            loadPackageParam.classLoader
        )
        if (utilClass != null) {
            try {
                deoptimizeMethod(utilClass, "verifySignatures")
            } catch (e: Throwable) {
                XposedBridge.log(
                    "E/" + MainHook.TAG + " deoptimizing failed" + Log.getStackTraceString(
                        e
                    )
                )
            }
        }

        // choose a signature after all old signed packages are removed
        val sharedUserSettingClass = XposedHelpers.findClass(
            "com.android.server.pm.SharedUserSetting",
            loadPackageParam.classLoader
        )
        XposedBridge.hookAllMethods(
            sharedUserSettingClass,
            "removePackage",
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    if (!prefs.getBoolean("digestCreak", true) || !prefs.getBoolean(
                            "sharedUser",
                            false
                        )
                    ) return
                    val flags = XposedHelpers.getObjectField(param.thisObject, "uidFlags") as Int
                    if ((flags and ApplicationInfo.FLAG_SYSTEM) != 0) return  // do not modify system's signature

                    val toRemove = param.args[0] ?: return // PackageSetting
                    var removed = false // Is toRemove really needed to be removed
                    val sharedUserSig = Setting_getSigningDetails(param.thisObject)
                    var newSig: Any? = null
                    val packages =  /*Watchable?ArraySet<PackageSetting>*/
                        SharedUserSetting_packages(param.thisObject)
                    val size = XposedHelpers.callMethod(packages, "size") as Int
                    for (i in 0 until size) {
                        val p = XposedHelpers.callMethod(packages, "valueAt", i)
                        // skip the removed package
                        if (toRemove == p) {
                            removed = true
                            continue
                        }
                        val packageSig = Setting_getSigningDetails(p)
                        // if old signing exists, return
                        if (callOriginMethod(
                                packageSig,
                                "checkCapability",
                                sharedUserSig,
                                0
                            ) as Boolean || callOriginMethod(
                                sharedUserSig,
                                "checkCapability",
                                packageSig,
                                0
                            ) as Boolean
                        ) {
                            return
                        }
                        // otherwise, choose the first signature we meet, and merge with others if possible
                        // https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/services/core/java/com/android/server/pm/ReconcilePackageUtils.java;l=193;drc=c9a8baf585e8eb0f3272443930301a61331b65c1
                        // respect to system
                        newSig = if (newSig == null) packageSig
                        else SigningDetails_mergeLineageWith(newSig, packageSig)
                    }
                    if (!removed || newSig == null) return
                    XposedBridge.log("updating signature in sharedUser during remove: " + param.thisObject)
                    Setting_setSigningDetails(param.thisObject, newSig)
                }
            }
        )

        XposedBridge.hookAllMethods(
            sharedUserSettingClass,
            "addPackage",
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    if (!prefs.getBoolean("digestCreak", true) || !prefs.getBoolean(
                            "sharedUser",
                            false
                        )
                    ) return
                    val flags = XposedHelpers.getObjectField(param.thisObject, "uidFlags") as Int
                    if ((flags and ApplicationInfo.FLAG_SYSTEM) != 0) return  // do not modify system's signature

                    val toAdd = param.args[0] ?: return // PackageSetting
                    var added = false
                    val sharedUserSig = Setting_getSigningDetails(param.thisObject)
                    var newSig: Any? = null
                    val packages =  /*Watchable?ArraySet<PackageSetting>*/
                        SharedUserSetting_packages(param.thisObject)
                    val size = XposedHelpers.callMethod(packages, "size") as Int
                    for (i in 0 until size) {
                        var p = XposedHelpers.callMethod(packages, "valueAt", i)
                        if (toAdd == p) {
                            // must be an existing package
                            added = true
                            p = toAdd
                        }
                        val packageSig = Setting_getSigningDetails(p)
                        // if old signing exists, return
                        if (callOriginMethod(
                                packageSig,
                                "checkCapability",
                                sharedUserSig,
                                0
                            ) as Boolean || callOriginMethod(
                                sharedUserSig,
                                "checkCapability",
                                packageSig,
                                0
                            ) as Boolean
                        ) {
                            return
                        }
                        // otherwise, choose the first signature we meet, and merge with others if possible
                        // https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/services/core/java/com/android/server/pm/ReconcilePackageUtils.java;l=193;drc=c9a8baf585e8eb0f3272443930301a61331b65c1
                        // respect to system
                        newSig = if (newSig == null) packageSig
                        else SigningDetails_mergeLineageWith(newSig, packageSig)
                    }
                    if (!added || newSig == null) return
                    XposedBridge.log("CorePatch: updating signature in sharedUser during add " + toAdd + ": " + param.thisObject)
                    Setting_setSigningDetails(param.thisObject, newSig)
                }
            }
        )

        hookAllMethods(
            getIsVerificationEnabledClass(loadPackageParam.classLoader),
            "isVerificationEnabled",
            ReturnConstant(prefs, "disableVerificationAgent", false)
        )

        if (BuildConfig.DEBUG) initializeDebugHook(loadPackageParam)
    }

    open fun getIsVerificationEnabledClass(classLoader: ClassLoader?): Class<*>? {
        return XposedHelpers.findClass("com.android.server.pm.PackageManagerService", classLoader)
    }

    open fun getSigningDetails(classLoader: ClassLoader?): Class<*> {
        return XposedHelpers.findClass(
            "android.content.pm.PackageParser.SigningDetails",
            classLoader
        )
    }

    override fun initZygote(startupParam: StartupParam) {
        hookAllMethods(
            "android.content.pm.PackageParser",
            null,
            "getApkSigningVersion",
            XC_MethodReplacement.returnConstant(1)
        )
        hookAllConstructors("android.util.jar.StrictJarVerifier", object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun beforeHookedMethod(param: MethodHookParam) {
                if (prefs.getBoolean("enhancedMode", false)) {
                    param.args[3] = java.lang.Boolean.FALSE
                }
            }
        })
    }

    var mPMS: Any? = null

    @Throws(IllegalAccessException::class, InvocationTargetException::class)
    fun initializeDebugHook(lpparam: LoadPackageParam) {
        XposedBridge.hookAllMethods(
            XposedHelpers.findClass(
                "com.android.server.pm.PackageManagerShellCommand",
                lpparam.classLoader
            ),
            "onCommand",
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    try {
                        val pms = mPMS ?: return
                        val cmd = param.args[0] as String
                        if ("corepatch" != cmd) return
                        val self = param.thisObject
                        val pw = XposedHelpers.callMethod(self, "getOutPrintWriter") as PrintWriter
                        val type = XposedHelpers.callMethod(self, "getNextArgRequired") as String
                        val settings = XposedHelpers.getObjectField(pms, "mSettings")
                        if ("p" == type || "package" == type) {
                            val packageName =
                                XposedHelpers.callMethod(self, "getNextArgRequired") as String
                            val packageSetting =
                                XposedHelpers.callMethod(settings, "getPackageLPr", packageName)
                            if (packageSetting != null) {
                                dumpPackageSetting(packageSetting, pw, settings)
                            } else {
                                pw.println("no package $packageName found")
                            }
                        } else if ("su" == type || "shareduser" == type) {
                            val name =
                                XposedHelpers.callMethod(self, "getNextArgRequired") as String
                            val su = getSharedUser(name, settings)
                            if (su != null) {
                                dumpSharedUserSetting(su, pw)
                            } else {
                                pw.println("no shared user $name found")
                            }
                        } else {
                            pw.println("usage: <p|package|su|shareduser> <name>")
                        }
                        param.result = 0
                    } catch (t: Throwable) {
                        XposedBridge.log(t)
                        param.throwable = t
                    }
                }
            }
        )

        val pmsClass = XposedHelpers.findClassIfExists(
            "com.android.server.pm.PackageManagerService",
            lpparam.classLoader
        )

        XposedBridge.hookAllConstructors(pmsClass, object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun afterHookedMethod(param: MethodHookParam) {
                mPMS = param.thisObject
            }
        }
        )

        deoptimizeMethod(pmsClass, "onShellCommand")
    }

    fun dumpPackageSetting(packageSetting: Any, pw: PrintWriter,  /*Settings*/settings: Any?) {
        val signingDetails = Setting_getSigningDetails(packageSetting)
        pw.println("signing for package $packageSetting")
        dumpSigningDetails(signingDetails, pw)
        val pkg = XposedHelpers.getObjectField(packageSetting, "pkg") // AndroidPackage
        if (pkg == null) {
            pw.println("android package is null!")
            return
        }
        val id = XposedHelpers.callMethod(pkg, "getSharedUserId") as String
        pw.println("shared user id:$id")
        if (settings != null) {
            val su = getSharedUser(id, settings)
            if (su != null) {
                dumpSharedUserSetting(su, pw)
            }
        }
    }

    fun getSharedUser(id: String?,  /*Settings*/settings: Any?): Any? {
        // TODO: use Setting.getSharedUserSettingLPr(appId)?
        val sharedUserSettings =
            XposedHelpers.getObjectField(settings, "mSharedUsers") ?: return null
        return XposedHelpers.callMethod(sharedUserSettings, "get", id)
    }

    fun dumpSharedUserSetting(sharedUser: Any, pw: PrintWriter) {
        val signingDetails = Setting_getSigningDetails(sharedUser)
        pw.println("signing for shared user $sharedUser")
        dumpSigningDetails(signingDetails, pw)
    }

    protected open fun dumpSigningDetails(signingDetails: Any?, pw: PrintWriter) {
        var i = 0
        for (sign in XposedHelpers.getObjectField(
            signingDetails,
            "signatures"
        ) as Array<Signature>) {
            i++
            pw.println(i.toString() + ": " + sign.toCharsString())
        }
    }

    /**
     * Get signing details for PackageSetting or SharedUserSetting
     */
    fun Setting_getSigningDetails(pkgOrSharedUser: Any?): Any {
        // PackageSettingBase(A11)|PackageSetting(A13)|SharedUserSetting.<PackageSignatures>signatures.<PackageParser.SigningDetails>mSigningDetails
        return XposedHelpers.getObjectField(
            XposedHelpers.getObjectField(
                pkgOrSharedUser,
                "signatures"
            ), "mSigningDetails"
        )
    }

    /**
     * Set signing details for PackageSetting or SharedUserSetting
     */
    fun Setting_setSigningDetails(pkgOrSharedUser: Any?, signingDetails: Any?) {
        XposedHelpers.setObjectField(
            XposedHelpers.getObjectField(pkgOrSharedUser, "signatures"),
            "mSigningDetails",
            signingDetails
        )
    }

    protected open fun SharedUserSetting_packages( /*SharedUserSetting*/sharedUser: Any?): Any {
        return XposedHelpers.getObjectField(sharedUser, "packages")
    }

    protected open fun SigningDetails_mergeLineageWith(self: Any?, other: Any?): Any {
        return XposedHelpers.callMethod(self, "mergeLineageWith", other)
    }

    companion object {
        private val deoptimizeMethod: Method?

        init {
            var m: Method? = null
            try {
                m = XposedBridge::class.java.getDeclaredMethod(
                    "deoptimizeMethod",
                    Member::class.java
                )
            } catch (t: Throwable) {
                XposedBridge.log("E/" + MainHook.TAG + " " + Log.getStackTraceString(t))
            }
            deoptimizeMethod = m
        }

        @JvmStatic
        @Throws(InvocationTargetException::class, IllegalAccessException::class)
        fun deoptimizeMethod(c: Class<*>, n: String) {
            for (m in c.declaredMethods) {
                if (deoptimizeMethod != null && m.name == n) {
                    deoptimizeMethod.invoke(null, m)
                    if (BuildConfig.DEBUG) XposedBridge.log("D/" + MainHook.TAG + " Deoptimized " + m.name)
                }
            }
        }

        fun callOriginMethod(obj: Any, methodName: String?, vararg args: Any?): Any {
            try {
                val method = XposedHelpers.findMethodBestMatch(obj.javaClass, methodName, *args)
                return XposedBridge.invokeOriginalMethod(method, obj, args)
            } catch (e: IllegalAccessException) {
                // should not happen
                XposedBridge.log(e)
                throw IllegalAccessError(e.message)
            } catch (e: IllegalArgumentException) {
                throw e
            } catch (e: InvocationTargetException) {
                throw RuntimeException(e.cause)
            }
        }
    }
}
