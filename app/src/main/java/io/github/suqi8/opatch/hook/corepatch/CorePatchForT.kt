package io.github.suqi8.opatch.hook.corepatch

import android.content.pm.Signature
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import java.io.PrintWriter
import java.lang.reflect.InvocationTargetException

open class CorePatchForT : CorePatchForS() {
    @Throws(
        IllegalAccessException::class,
        InvocationTargetException::class,
        InstantiationException::class
    )
    override fun handleLoadPackage(loadPackageParam: LoadPackageParam) {
        super.handleLoadPackage(loadPackageParam)
        val checkDowngrade = XposedHelpers.findMethodExactIfExists(
            "com.android.server.pm.PackageManagerServiceUtils", loadPackageParam.classLoader,
            "checkDowngrade",
            "com.android.server.pm.parsing.pkg.AndroidPackage",
            "android.content.pm.PackageInfoLite"
        )
        if (checkDowngrade != null) {
            XposedBridge.hookMethod(checkDowngrade, ReturnConstant(prefs, "downgrade", null))
        }

        val signingDetails = getSigningDetails(loadPackageParam.classLoader)
        //New package has a different signature
        //处理覆盖安装但签名不一致
        hookAllMethods(signingDetails, "checkCapability", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                // Don't handle PERMISSION (grant SIGNATURE permissions to pkgs with this cert)
                // Or applications will have all privileged permissions
                // https://cs.android.com/android/platform/superproject/+/master:frameworks/base/core/java/android/content/pm/PackageParser.java;l=5947?q=CertCapabilities
                if (prefs.getBoolean("digestCreak", true)) {
                    if (param.args[1] as Int != 4) {
                        param.result = true
                    }
                }
            }
        })

        findAndHookMethod("com.android.server.pm.InstallPackageHelper",
            loadPackageParam.classLoader,
            "doesSignatureMatchForPermissions",
            String::class.java,
            "com.android.server.pm.parsing.pkg.ParsedPackage",
            Int::class.javaPrimitiveType,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    if (prefs.getBoolean("digestCreak", true) && prefs.getBoolean(
                            "UsePreSig",
                            false
                        )
                    ) {
                        //If we decide to crack this then at least make sure they are same apks, avoid another one that tries to impersonate.
                        if (param.result == false) {
                            val pPname =
                                XposedHelpers.callMethod(param.args[1], "getPackageName") as String
                            if (pPname.contentEquals(param.args[0] as String)) {
                                param.result = true
                            }
                        }
                    }
                }
            })

        val assertMinSignatureSchemeIsValid = XposedHelpers.findMethodExactIfExists(
            "com.android.server.pm.ScanPackageUtils", loadPackageParam.classLoader,
            "assertMinSignatureSchemeIsValid",
            "com.android.server.pm.parsing.pkg.AndroidPackage", Int::class.javaPrimitiveType
        )
        if (assertMinSignatureSchemeIsValid != null) {
            XposedBridge.hookMethod(assertMinSignatureSchemeIsValid, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    if (prefs.getBoolean("authcreak", false)) {
                        param.result = null
                    }
                }
            })
        }

        val strictJarVerifier =
            findClass("android.util.jar.StrictJarVerifier", loadPackageParam.classLoader)
        if (strictJarVerifier != null) {
            XposedBridge.hookAllConstructors(strictJarVerifier, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    if (prefs.getBoolean("authcreak", false)) {
                        XposedHelpers.setBooleanField(
                            param.thisObject,
                            "signatureSchemeRollbackProtectionsEnforced",
                            false
                        )
                    }
                }
            })
        }

        // ensure verifySignatures success
        // https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/services/core/java/com/android/server/pm/PackageManagerServiceUtils.java;l=621;drc=2e50991320cbef77d3e8504a4b284adae8c2f4d2
        val utils = XposedHelpers.findClassIfExists(
            "com.android.server.pm.PackageManagerServiceUtils",
            loadPackageParam.classLoader
        )
        if (utils != null) {
            deoptimizeMethod(utils, "canJoinSharedUserId")
        }
    }

    override fun getSigningDetails(classLoader: ClassLoader?): Class<*> {
        return XposedHelpers.findClassIfExists("android.content.pm.SigningDetails", classLoader)
    }

    override fun dumpSigningDetails(signingDetails: Any?, pw: PrintWriter) {
        var i = 0
        for (sign in XposedHelpers.callMethod(
            signingDetails,
            "getSignatures"
        ) as Array<Signature>) {
            i++
            pw.println(i.toString() + ": " + sign.toCharsString())
        }
    }

    override fun SharedUserSetting_packages(sharedUser: Any?): Any {
        return XposedHelpers.getObjectField(sharedUser, "mPackages")
    }

    override fun SigningDetails_mergeLineageWith(self: Any?, other: Any?): Any {
        return XposedHelpers.callMethod(
            self,
            "mergeLineageWith",
            other,
            2 /*MERGE_RESTRICTED_CAPABILITY*/
        )
    }

    override fun getIsVerificationEnabledClass(classLoader: ClassLoader?): Class<*> {
        return XposedHelpers.findClass("com.android.server.pm.VerificationParams", classLoader)
    }
}
