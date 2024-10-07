package io.github.suqi8.opatch.hook.corepatch

import android.content.pm.ApplicationInfo
import android.content.pm.Signature
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import io.github.suqi8.opatch.BuildConfig
import java.lang.reflect.InvocationTargetException
import java.util.Arrays

class CorePatchForQ : XposedHelper(), IXposedHookLoadPackage, IXposedHookZygoteInit {
    val prefs: XSharedPreferences = XSharedPreferences(BuildConfig.APPLICATION_ID, "conf")

    @Throws(
        IllegalAccessException::class,
        InvocationTargetException::class,
        InstantiationException::class
    )
    override fun handleLoadPackage(loadPackageParam: LoadPackageParam) {
        // 允许降级
        val packageClazz = XposedHelpers.findClass(
            "android.content.pm.PackageParser.Package",
            loadPackageParam.classLoader
        )
        hookAllMethods(
            "com.android.server.pm.PackageManagerService",
            loadPackageParam.classLoader,
            "checkDowngrade",
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                public override fun beforeHookedMethod(methodHookParam: MethodHookParam) {
                    if (prefs.getBoolean("downgrade", true)) {
                        val packageInfoLite = methodHookParam.args[0]

                        if (prefs.getBoolean("downgrade", true)) {
                            var field = packageClazz.getField("mVersionCode")
                            field.isAccessible = true
                            field[packageInfoLite] = 0
                            field = packageClazz.getField("mVersionCodeMajor")
                            field.isAccessible = true
                            field[packageInfoLite] = 0
                        }
                    }
                }
            })

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
        hookAllMethods(
            "com.android.server.pm.PackageManagerServiceUtils",
            loadPackageParam.classLoader,
            "verifySignatures",
            ReturnConstant(prefs, "authcreak", false)
        )

        val signingDetails = XposedHelpers.findClass(
            "android.content.pm.PackageParser.SigningDetails",
            loadPackageParam.classLoader
        )
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
        signingDetailsArgs[0] = arrayOf(Signature(SIGNATURE))
        signingDetailsArgs[1] = 1
        val newInstance = findConstructorExact.newInstance(*signingDetailsArgs)
        hookAllMethods(
            "android.util.apk.ApkSignatureVerifier",
            loadPackageParam.classLoader,
            "verifyV1Signature",
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                public override fun afterHookedMethod(methodHookParam: MethodHookParam) {
                    if (prefs.getBoolean("authcreak", false)) {
                        val throwable = methodHookParam.throwable
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
                    }
                }
            })

        //New package has a different signature
        //处理覆盖安装但签名不一致
        hookAllMethods(signingDetails, "checkCapability", object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun beforeHookedMethod(param: MethodHookParam) {
                if (prefs.getBoolean("digestCreak", true)) {
                    if (param.args[1] as Int != 4 && prefs.getBoolean("authcreak", false)) {
                        param.result = java.lang.Boolean.TRUE
                    }
                }
            }
        })
        hookAllMethods(signingDetails, "checkCapabilityRecover",
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    if (prefs.getBoolean("digestCreak", true)) {
                        if (param.args[1] as Int != 4 && prefs.getBoolean("authcreak", false)) {
                            param.result = java.lang.Boolean.TRUE
                        }
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
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam) {
                        if (prefs.getBoolean("digestCreak", true) &&
                            Arrays.stream<StackTraceElement>(Thread.currentThread().stackTrace)
                                .anyMatch { o: StackTraceElement -> ( /* API 29 */"preparePackageLI" == o.methodName || "installPackageLI" == o.methodName) }
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
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam) {
                        if (prefs.getBoolean("digestCreak", true) && shouldBypass.get()) {
                            param.result = true
                        }
                    }
                })
        }
        hookAllMethods(
            XposedHelpers.findClass(
                "com.android.server.pm.PackageManagerService",
                loadPackageParam.classLoader
            ),
            "isVerificationEnabled",
            ReturnConstant(prefs, "disableVerificationAgent", false)
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
}
