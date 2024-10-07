package io.github.suqi8.opatch.hook.corepatch

import android.util.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import java.lang.reflect.InvocationTargetException

class CorePatchForU : CorePatchForT() {
    @Throws(
        IllegalAccessException::class,
        InvocationTargetException::class,
        InstantiationException::class
    )
    override fun handleLoadPackage(loadPackageParam: LoadPackageParam) {
        super.handleLoadPackage(loadPackageParam)
        val utilClass =
            findClass("com.android.server.pm.ReconcilePackageUtils", loadPackageParam.classLoader)
        if (utilClass != null) {
            try {
                deoptimizeMethod(utilClass, "reconcilePackages")
            } catch (e: Throwable) {
                XposedBridge.log(
                    "E/" + MainHook.TAG + " deoptimizing failed" + Log.getStackTraceString(
                        e
                    )
                )
            }
        }
        // ee11a9c (Rename AndroidPackageApi to AndroidPackage)
        findAndHookMethod(
            "com.android.server.pm.PackageManagerServiceUtils", loadPackageParam.classLoader,
            "checkDowngrade",
            "com.android.server.pm.pkg.AndroidPackage",
            "android.content.pm.PackageInfoLite",
            ReturnConstant(prefs, "downgrade", null)
        )
        findAndHookMethod("com.android.server.pm.ScanPackageUtils", loadPackageParam.classLoader,
            "assertMinSignatureSchemeIsValid",
            "com.android.server.pm.pkg.AndroidPackage", Int::class.javaPrimitiveType,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    if (prefs.getBoolean("authcreak", false)) {
                        param.result = null
                    }
                }
            })

        val ntService = XposedHelpers.findClassIfExists(
            "com.nothing.server.ex.NtConfigListServiceImpl",
            loadPackageParam.classLoader
        )
        if (ntService != null) {
            findAndHookMethod(
                ntService, "isInstallingAppForbidden", String::class.java,
                ReturnConstant(prefs, "bypassBlock", false)
            )

            findAndHookMethod(
                ntService, "isStartingAppForbidden", String::class.java,
                ReturnConstant(prefs, "bypassBlock", false)
            )
        }
    }

    override fun getIsVerificationEnabledClass(classLoader: ClassLoader?): Class<*> {
        return XposedHelpers.findClass("com.android.server.pm.VerifyingSession", classLoader)
    }
}
