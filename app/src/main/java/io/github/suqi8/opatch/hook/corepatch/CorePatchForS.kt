package io.github.suqi8.opatch.hook.corepatch

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import java.lang.reflect.InvocationTargetException

open class CorePatchForS : CorePatchForR() {
    @Throws(
        IllegalAccessException::class,
        InvocationTargetException::class,
        InstantiationException::class
    )
    override fun handleLoadPackage(loadPackageParam: LoadPackageParam) {
        super.handleLoadPackage(loadPackageParam)
        val pmService = XposedHelpers.findClassIfExists(
            "com.android.server.pm.PackageManagerService",
            loadPackageParam.classLoader
        )
        if (pmService != null) {
            val doesSignatureMatchForPermissions = XposedHelpers.findMethodExactIfExists(
                pmService,
                "doesSignatureMatchForPermissions",
                String::class.java,
                "com.android.server.pm.parsing.pkg.ParsedPackage",
                Int::class.javaPrimitiveType
            )
            if (doesSignatureMatchForPermissions != null) {
                XposedBridge.hookMethod(doesSignatureMatchForPermissions, object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        if (prefs.getBoolean("digestCreak", true) && prefs.getBoolean(
                                "UsePreSig",
                                false
                            )
                        ) {
                            //If we decide to crack this then at least make sure they are same apks, avoid another one that tries to impersonate.
                            if (param.result == false) {
                                val pPname = XposedHelpers.callMethod(
                                    param.args[1],
                                    "getPackageName"
                                ) as String
                                if (pPname.contentEquals(param.args[0] as String)) {
                                    param.result = true
                                }
                            }
                        }
                    }
                })
            }
        }
    }
}
