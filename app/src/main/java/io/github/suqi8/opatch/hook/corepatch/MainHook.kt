package io.github.suqi8.opatch.hook.corepatch

import android.os.Build
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import io.github.suqi8.opatch.BuildConfig

class MainHook : IXposedHookLoadPackage, IXposedHookZygoteInit {
    @Throws(Throwable::class)
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (("android" == lpparam.packageName) && (lpparam.processName == "android")) {
            if (BuildConfig.DEBUG) XposedBridge.log("D/" + TAG + " handleLoadPackage")
            when (Build.VERSION.SDK_INT) {
                Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> CorePatchForU().handleLoadPackage(lpparam)
                Build.VERSION_CODES.TIRAMISU -> CorePatchForT().handleLoadPackage(lpparam)
                Build.VERSION_CODES.S_V2, Build.VERSION_CODES.S -> CorePatchForS().handleLoadPackage(
                    lpparam
                )

                Build.VERSION_CODES.R -> CorePatchForR().handleLoadPackage(lpparam)
                Build.VERSION_CODES.Q, Build.VERSION_CODES.P -> CorePatchForQ().handleLoadPackage(
                    lpparam
                )

                else -> XposedBridge.log("W/" + TAG + " Unsupported Version of Android " + Build.VERSION.SDK_INT)
            }
        }
    }

    override fun initZygote(startupParam: StartupParam) {
        if (startupParam.startsSystemServer) {
            if (BuildConfig.DEBUG) XposedBridge.log("D/" + TAG + " initZygote: Current sdk version " + Build.VERSION.SDK_INT)
            when (Build.VERSION.SDK_INT) {
                Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> CorePatchForU().initZygote(startupParam)
                Build.VERSION_CODES.TIRAMISU -> CorePatchForT().initZygote(startupParam)
                Build.VERSION_CODES.S_V2, Build.VERSION_CODES.S -> CorePatchForS().initZygote(
                    startupParam
                )

                Build.VERSION_CODES.R -> CorePatchForR().initZygote(startupParam)
                Build.VERSION_CODES.Q, Build.VERSION_CODES.P -> CorePatchForQ().initZygote(
                    startupParam
                )

                else -> XposedBridge.log("W/" + TAG + " Unsupported Version of Android " + Build.VERSION.SDK_INT)
            }
        }
    }

    companion object {
        const val TAG: String = "CorePatch"
    }
}
