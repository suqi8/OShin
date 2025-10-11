package com.suqi8.oshin.hook.phonemanager

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.luckypray.dexkit.DexKitBridge
import java.lang.reflect.Modifier

class phonemanager: YukiBaseHooker() {
    override fun onHook() {
        var hasani = 0
        loadApp(name = "com.coloros.phonemanager") {
            if (prefs("phonemanager").getBoolean("remove_all_popup_delays", false)) {
                "com.oplus.phonemanager.common.DialogCrossActivity\$f".toClass().resolve().apply {
                    firstMethod {
                        name = "onTick"
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
                        //YLog.info("methodName:"+it.methodName + " className:" + it.className)
                        it.className.toClass().resolve().firstMethod { name = it.methodName }.hook {
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
                    it.className.toClass().resolve().firstMethod { name = it.methodName }.hook {
                        before {
                            if (prefs("phonemanager").getFloat("custom_score", -1f) != -1f) {
                                if (args[1] as Boolean) {
                                    hasani += 1
                                    if (hasani == 2) {
                                        args[0] = prefs("phonemanager").getFloat("custom_score", -1f)
                                    }
                                }
                            }
                            if (prefs("phonemanager").getFloat("custom_animation_duration", -1f) != -1f) {
                                args[2] = prefs("phonemanager").getFloat("custom_animation_duration", -1f).toLong()
                            }
                        }
                    }
                }
            }
        }
    }
}
