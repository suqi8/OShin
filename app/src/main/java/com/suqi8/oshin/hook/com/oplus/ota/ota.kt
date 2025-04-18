package com.suqi8.oshin.hook.com.oplus.ota

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.UnitType

class ota: YukiBaseHooker() {
    override fun onHook() {
        loadApp(name = "com.oplus.ota") {
            if (prefs("ota").getBoolean("remove_system_update_dialog", false)) {
                "b5.u".toClass().apply {
                    method {
                        name = "m"
                        param("android.content.Context")
                        returnType = UnitType
                    }.hook {
                        replaceUnit {  }
                    }
                }
            }
            if (prefs("ota").getBoolean("remove_system_update_notification", false)) {
                "b5.g".toClass().apply {
                    method {
                        name = "l"
                        emptyParam()
                        returnType = UnitType
                    }.hook {
                        replaceUnit {  }
                    }
                }
            }
            if (prefs("ota").getBoolean("remove_wlan_auto_download_dialog", false)) {
                "b5.u".toClass().apply {
                    method {
                        name = "a"
                        param("b5.u", "android.content.Context", "android.content.DialogInterface", IntType)
                        returnType = UnitType
                    }.hook {
                        replaceUnit {  }
                    }
                }
            }
            if (prefs("ota").getBoolean("remove_wlan_auto_download_dialog", false)) {
                "com.oplus.common.a".toClass().apply {
                    method {
                        name = "J0"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = false
                        }
                    }
                }
            }
        }
    }
}
