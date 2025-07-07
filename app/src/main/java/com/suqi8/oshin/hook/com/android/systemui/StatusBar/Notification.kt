package com.suqi8.oshin.hook.com.android.systemui.StatusBar

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method

class Notification: YukiBaseHooker() {
    override fun onHook() {
        if (prefs("systemui\\notification").getBoolean("remove_developer_options_notification", false)) {
            loadApp(name = "com.android.systemui") {
                "com.oplus.systemui.statusbar.controller.SystemPromptController".toClass().apply {
                    method {
                        name = "updateDeveloperMode"
                    }.hook {
                        replaceUnit {

                        }
                    }
                }
            }
        }
        if (prefs("systemui\\notification").getBoolean("remove_and_do_not_disturb_notification", false)) {
            loadApp(name = "com.android.systemui") {
                "com.oplus.systemui.statusbar.controller.NoDisturbController".toClass().resolve().apply {
                    firstMethod {
                        modifiers(Modifiers.PUBLIC, Modifiers.FINAL)
                        name = "updateNoDisturbStatus"
                        emptyParameters()
                        returnType = Void.TYPE
                    }.hook {
                        replaceUnit {  }
                    }
                }
            }
        }
    }
}
