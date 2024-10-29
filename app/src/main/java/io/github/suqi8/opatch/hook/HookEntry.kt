package io.github.suqi8.opatch.hook

import android.annotation.SuppressLint
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.core.annotation.LegacyResourcesHook
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import io.github.suqi8.opatch.hook.systemui.StatusBar.StatusBar
import io.github.suqi8.opatch.hook.systemui.StatusBar.StatusBarClock
import io.github.suqi8.opatch.hook.systemui.StatusBar.StatusBarIcon
import io.github.suqi8.opatch.hook.systemui.StatusBar.StatusBarhardware_indicator
import io.github.suqi8.opatch.hook.launcher.LauncherIcon
import io.github.suqi8.opatch.hook.systemui.aod.allday_screenoff

@InjectYukiHookWithXposed(entryClassName = "opatch", isUsingResourcesHook = true)
class HookEntry : IYukiHookXposedInit {

    override fun onInit() = configs {
        // Your code here.
    }

    @OptIn(LegacyResourcesHook::class)
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
}
