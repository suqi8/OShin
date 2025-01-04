package io.github.suqi8.opatch.hook

import android.annotation.SuppressLint
import android.os.Build
import android.os.Build.VERSION_CODES.R
import android.os.Build.VERSION_CODES.S
import android.os.Build.VERSION_CODES.S_V2
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE
import android.os.Build.VERSION_CODES.VANILLA_ICE_CREAM
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.core.annotation.LegacyResourcesHook
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.xposed.bridge.event.YukiXposedEvent
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.github.suqi8.opatch.hook.corepatch.CorePatchForU
import io.github.suqi8.opatch.hook.corepatch.CorePatchForV
import io.github.suqi8.opatch.hook.systemui.StatusBar.StatusBar
import io.github.suqi8.opatch.hook.systemui.StatusBar.StatusBarClock
import io.github.suqi8.opatch.hook.systemui.StatusBar.StatusBarIcon
import io.github.suqi8.opatch.hook.systemui.StatusBar.StatusBarhardware_indicator
import io.github.suqi8.opatch.hook.launcher.LauncherIcon
import io.github.suqi8.opatch.hook.services.OplusRootCheck
import io.github.suqi8.opatch.hook.systemui.aod.allday_screenoff

@InjectYukiHookWithXposed(entryClassName = "opatch", isUsingResourcesHook = true)
class HookEntry : IYukiHookXposedInit {

    override fun onInit() = configs {
        // Your code here.
    }

    @SuppressLint("RestrictedApi")
    override fun onHook() = encase {
        // Your code here.
        /*loadApp(name = "com.android.settings") {
            "com.oplus.settings.feature.deviceinfo.controller.OplusDeviceModelPreferenceController".toClass().apply {
                method{
                    name = "getStatusText"
                    emptyParam()
                    returnType = StringClass
                }.hook {
                    replaceTo("原神手机酸奶独家定制版")
                }
            }
        }*/
        loadApp(hooker = OplusRootCheck())
        loadApp(hooker = StatusBarClock())
        loadApp(hooker = StatusBarhardware_indicator())
        loadApp(hooker = LauncherIcon())
        loadApp(hooker = StatusBarIcon())
        loadApp(name = "com.android.systemui") {
            /*"com.android.systemui.statusbar.phone.StatusBarIconController".toClass().apply {
                method {
                    name = "setIconVisibility"
                    param(StringClass, BooleanClass)
                }.hook {
                    before {
                        args[1] = true
                    }
                }
            }*/
        }
        /*loadApp(name = "com.android.settings") {
            resources().hook {
                injectResource {
                    conditions {
                        name = "device_ota_card_bg"
                        drawable()
                    }
                    replaceToModuleResource(R.drawable.aboutbackground)
                }
            }
        }*/

        loadApp(hooker = StatusBar())
        loadApp(hooker = allday_screenoff())
    }

    override fun onXposedEvent() {
        YukiXposedEvent.onHandleLoadPackage { lpparam: XC_LoadPackage.LoadPackageParam ->
            run {
                if (lpparam.packageName == "android" && lpparam.processName == "android") {
                    if (Build.VERSION.SDK_INT == VANILLA_ICE_CREAM) {
                        CorePatchForV().handleLoadPackage(lpparam)
                    }
                }
            }
        }
        YukiXposedEvent.onInitZygote { startupParam: IXposedHookZygoteInit.StartupParam ->
            run {
            }
        }
    }
}
