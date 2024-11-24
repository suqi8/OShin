package io.github.suqi8.opatch.hook.systemui.StatusBar

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import io.github.suqi8.opatch.hook.appilcations.StatusBarClock

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
