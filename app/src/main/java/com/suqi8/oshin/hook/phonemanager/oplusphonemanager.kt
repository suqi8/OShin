package com.suqi8.oshin.hook.phonemanager

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

class oplusphonemanager: YukiBaseHooker() {
    override fun onHook() {
        var hasani = 0
        loadApp(name = "com.oplus.phonemanager") {
            if (prefs("oplusphonemanager").getBoolean("remove_all_popup_delays", false)) {
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
            "com.oplus.phonemanager.common.view.ScanCircleView".toClass().resolve().apply {
                firstMethod {
                    name = "B"
                }.hook {
                    before {
                        if (prefs("oplusphonemanager").getFloat("custom_score", -1f) != -1f) {
                            if (args[1] as Boolean) {
                                hasani += 1
                                if (hasani == 2) {
                                    args[0] = prefs("oplusphonemanager").getFloat("custom_score", -1f)
                                }
                            }
                        }
                        if (prefs("oplusphonemanager").getFloat("custom_animation_duration", -1f) != -1f) {
                            args[2] = prefs("oplusphonemanager").getFloat("custom_animation_duration", -1f).toLong()
                        }
                    }
                }
            }
            if (prefs("oplusphonemanager").getString("custom_prompt_content", "") != "") {
                "com.oplus.phonemanager.newrequest.delegate.m0".toClass().resolve().apply {
                    firstMethod {
                        name = "a"
                    }.hook {
                        before {
                            result = prefs("oplusphonemanager").getString("custom_prompt_content", "")
                        }
                    }
                }
            }
        }
    }
}
