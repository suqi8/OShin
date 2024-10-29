package io.github.suqi8.opatch.hook.systemui.StatusBar

import androidx.compose.ui.platform.LocalContext
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import io.github.suqi8.opatch.hook.appilcations.StatusBarClock
import io.github.suqi8.opatch.getSetting
import io.github.suqi8.opatch.hook.appilcations.StatusLayout

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
