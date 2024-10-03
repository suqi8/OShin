package io.github.suqi8.opatch.hook

import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import io.github.suqi8.opatch.hook.StatusBar.StatusBarClock

@InjectYukiHookWithXposed(entryClassName = "opatch", isUsingResourcesHook = true)
class HookEntry : IYukiHookXposedInit {

    override fun onInit() = configs {
        // Your code here.
    }

    override fun onHook() = encase {
        // Your code here.
        loadApp(name = "com.android.settings") {
            "com.oplus.settings.feature.deviceinfo.controller.OplusDeviceModelPreferenceController".toClass().apply {
                method{
                    name = "getStatusText"
                    emptyParam()
                    returnType = StringClass
                }.hook {
                    replaceTo("原神手机酸奶独家定制版"+prefs("settings").getBoolean("com_android_systemui_status_bar_clock").toString())
                }
            }
        }
        loadApp(hooker = StatusBarClock())
    }
}
