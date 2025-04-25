package com.suqi8.oshin.hook.com.coloros.oshare

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import org.luckypray.dexkit.DexKitBridge

class oshare: YukiBaseHooker() {
    override fun onHook() {
        loadApp(name = "com.coloros.oshare") {
            if (prefs("oshare").getInt("transfer_time_modify", 10) != 10) {
                DexKitBridge.create(this.appInfo.sourceDir).use {
                    it.findClass {
                        matcher {
                            addMethod {
                                usingStrings("last_radar_signal_state")
                            }
                            addMethod {
                                usingStrings("updateLastTurnOnTime time  = ")
                            }
                            addMethod {
                                usingStrings("cta_dialog_should_show")
                            }
                        }
                    }.findMethod {
                        matcher {
                            usingStrings("updateLastTurnOnTime time  = ")
                        }
                    }.singleOrNull()?.also {
                        it.className.toClass().method { name = it.methodName }.hook { before {
                            val addtime = (prefs("oshare").getInt("transfer_time_modify", 10) * 1000 * 60).toLong()
                            args[1] = args[1] as Long + addtime
                        } }
                    }
                }
            }
        }
    }
}
