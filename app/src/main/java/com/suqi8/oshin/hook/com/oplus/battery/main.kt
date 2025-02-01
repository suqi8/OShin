package com.suqi8.oshin.hook.com.oplus.battery

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method

class battery: YukiBaseHooker() {
    override fun onHook() {
        if (prefs("battery").getBoolean("low_battery_fluid_cloud", false)) {
            loadApp(name = "com.oplus.battery") {
                "com.oplus.pantanal.seedling.intent.a".toClass().apply {
                    method {
                        name = "sendSeedling"
                    }.hook {
                        before {
                            result = 0
                        }
                    }
                }
            }
        }
    }
}
