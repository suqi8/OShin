package com.suqi8.oshin.hook.phonemanager

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.luckypray.dexkit.DexKitBridge
import java.lang.reflect.Modifier

class oplusphonemanager: YukiBaseHooker() {
    override fun onHook() {
        val prefs = prefs("oplusphonemanager")
        loadApp(name = "com.oplus.phonemanager") {
            if (prefs.getBoolean("remove_all_popup_delays", false)) {
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
                if (prefs.getString("custom_prompt_content", "") != "") {
                    it.findMethod {
                        searchPackages("com.oplus.phonemanager.newrequest.delegate")
                        matcher {
                            modifiers = Modifier.PUBLIC
                            usingStrings("updateScanResult manualOptItems: ", "main_entry_summary")
                        }
                    }.singleOrNull()?.also {
                        //YLog.info("methodName:"+it.methodName + " className:" + it.className)
                        it.className.toClass().resolve().firstMethod { name = it.methodName }.hook {
                            before {
                                result =
                                    prefs.getString("custom_prompt_content", "")
                            }
                        }
                    }
                }
                it.findMethod {
                    searchPackages("com.oplus.phonemanager.common.view")
                    matcher {
                        modifiers = Modifier.PUBLIC
                        returnType = "void"
                        usingStrings(" not change, skip", "[setScore] ")
                    }
                }.singleOrNull()?.also {
                    var triggerCount = 0
                    it.className.toClass().resolve().firstMethod { name = it.methodName }.hook {
                        before {
                            val customScore = prefs.getFloat("custom_score", -1f)
                            val customAnimationDuration =
                                prefs.getFloat("custom_animation_duration", -1f)

                            val isSecondPassAnimation = args[1] as Boolean

                            if (customScore != -1f && isSecondPassAnimation) {
                                triggerCount += 1 // 条件触发次数加 1

                                // 仅在第二次触发时才应用自定义分数
                                if (triggerCount == 2) {
                                    args[0] = customScore.toInt()
                                }
                            }

                            if (customAnimationDuration != -1f) {
                                // args[2] 对应的是动画时长参数
                                args[2] = customAnimationDuration.toLong()
                            }
                        }
                    }
                }
            }
        }
    }
}
