package com.suqi8.oshin.hook.com.oplus.battery

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method

class battery: YukiBaseHooker() {
    override fun onHook() {
        loadApp(name = "com.oplus.battery") {
            if (prefs("battery").getBoolean("low_battery_fluid_cloud", false)) {
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
            if (prefs("battery").getInt("auto_start_max_limit", 5) != 5) {
                /*DexKitBridge.create(this.appInfo.sourceDir).use {
                    it.findMethod {
                        matcher {
                            modifiers = Modifier.PUBLIC
                            returnType = "int"
                            paramTypes()
                            usingNumbers(5,20)
                        }
                    }.forEach {
                        it.className.toClass().method { name = it.methodName }.hook { before { replaceTo(prefs("battery").getInt("auto_start_max_limit", 5)) } }
                    }
                }*/
                "qa.c".toClass().apply {
                    method {
                        name = "k"
                        emptyParam()
                    }.hook {
                        replaceTo(prefs("battery").getInt("auto_start_max_limit", 5))
                    }
                }
            }
        }
    }
}
