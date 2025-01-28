package com.suqi8.oshin.hook.systemui.StatusBar

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method

class notification: YukiBaseHooker() {
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
    }
}
