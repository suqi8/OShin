package com.suqi8.oshin.hook.com.coloros.phonemanager

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.java.UnitType
import org.luckypray.dexkit.DexKitBridge
import java.lang.reflect.Modifier

class phonemanager: YukiBaseHooker() {
    override fun onHook() {
        var hasani = 0
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
            DexKitBridge.create(this.appInfo.sourceDir).use {
                if (prefs("phonemanager").getString("custom_prompt_content", "") != "") {
                    it.findMethod {
                        searchPackages("com.oplus.phonemanager.newrequest.delegate")
                        matcher {
                            modifiers = Modifier.PUBLIC
                            usingStrings("updateScanResult manualOptItems: ","main_entry_summary")
                        }
                    }.singleOrNull()?.also {
                        YLog.info("methodName:"+it.methodName + " className:" + it.className)
                        it.className.toClass().method { name = it.methodName }.hook {
                            before {
                                result = prefs("phonemanager").getString("custom_prompt_content", "")
                            }
                        }
                    }
                }
                it.findMethod {
                    searchPackages("com.oplus.phonemanager.common.view")
                    matcher {
                        modifiers = Modifier.PUBLIC
                        returnType = "void"
                        usingStrings(" not change, skip","[setScore] ")
                    }
                }.singleOrNull()?.also {
                    it.className.toClass().method { name = it.methodName }.hook {
                        before {
                            if (prefs("phonemanager").getInt("custom_score", -1) != -1) {
                                if (args[1] as Boolean) {
                                    hasani += 1
                                    if (hasani == 2) {
                                        args[0] = prefs("phonemanager").getInt("custom_score", -1)
                                    }
                                }
                            }
                            if (prefs("phonemanager").getInt("custom_animation_duration", -1) != -1) {
                                args[2] = prefs("phonemanager").getInt("custom_animation_duration", -1).toLong()
                            }
                        }
                    }
                }
            }
        }
    }
}
