package com.suqi8.oshin.hook.settings

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

class Settings: YukiBaseHooker() {
    override fun onHook() {
        loadApp(name = "com.android.settings") {
            if (prefs("settings").getBoolean("hide_status_bar", false)) {

            }
        }
    }
}
