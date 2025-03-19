package com.suqi8.oshin.hook.com.coloros.securepay

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.UnitType

class securepay: YukiBaseHooker() {
    override fun onHook() {
        loadApp(name = "com.coloros.securepay") {
            if (prefs("securepay").getBoolean("security_payment_remove_risky_fluid_cloud", false)) {
                "o8.m".toClass().apply {
                    method {
                        name = "M"
                        param("java.util.List")
                        returnType = UnitType
                    }.hook {
                        replaceUnit {  }
                    }
                }
            }
        }
    }
}
