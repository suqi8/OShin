package com.suqi8.oshin.hook.com.coloros.phonemanager

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.UnitType

class phonemanager: YukiBaseHooker() {
    override fun onHook() {
        loadApp(name = "com.coloros.phonemanager") {
            if (prefs("phonemanager").getBoolean("remove_all_popup_delays", false)) {
                "com.oplus.phonemanager.common.DialogCrossActivity\$f".toClass().apply {
                    method {
                        name = "onTick"
                        returnType = UnitType
                    }.hook {
                        before {
                            args[0] = 0
                        }
                    }
                }
            }
        }
    }
}
