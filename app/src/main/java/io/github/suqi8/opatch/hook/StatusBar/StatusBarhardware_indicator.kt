package io.github.suqi8.opatch.hook.StatusBar

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import io.github.suqi8.opatch.hook.appilcations.StatusBarConsumption_indicator
import io.github.suqi8.opatch.hook.appilcations.StatusBartemperature_indicator

class StatusBarhardware_indicator: YukiBaseHooker() {
    override fun onHook() {
        loadApp("com.android.systemui"){
            //功耗指示器
            if (prefs("settings").getBoolean("com_android_systemui_power_consumption_indicator", false)) {
                loadHooker(StatusBarConsumption_indicator())
            }
            if (prefs("settings").getBoolean("com_android_systemui_temperature_indicator", false)) {
                loadHooker(StatusBartemperature_indicator())
            }
        }
    }
}
