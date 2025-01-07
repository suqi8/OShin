package com.suqi8.oshin.hook.systemui.StatusBar

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

class StatusBarClock: YukiBaseHooker() {
    override fun onHook() {
        loadApp("com.android.systemui"){
            //状态栏时钟
            if (prefs("settings").getBoolean("com_android_systemui_status_bar_clock", false)) {
                loadHooker(StatusBarClock())
            }
        }
    }
}
