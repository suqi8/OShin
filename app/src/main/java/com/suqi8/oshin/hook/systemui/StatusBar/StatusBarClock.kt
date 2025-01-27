package com.suqi8.oshin.hook.systemui.StatusBar

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.suqi8.oshin.hook.appilcations.StatusBarClock

class StatusBarClock: YukiBaseHooker() {
    override fun onHook() {
        loadApp("com.android.systemui"){
            //状态栏时钟
            if (prefs("systemui\\status_bar_clock").getBoolean("status_bar_clock", false)) {
                loadHooker(StatusBarClock())
            }
        }
    }
}
