package com.suqi8.oshin.hook.systemui.StatusBar

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.suqi8.oshin.hook.appilcations.StatusBarConsumption_indicator
import com.suqi8.oshin.hook.appilcations.StatusBartemperature_indicator

class StatusBarhardware_indicator: YukiBaseHooker() {
    override fun onHook() {
        loadApp("com.android.systemui"){
            //功耗指示器
            if (prefs("systemui\\hardware_indicator").getBoolean("power_consumption_indicator", false)) {
                loadHooker(StatusBarConsumption_indicator())
            }
            if (prefs("systemui\\hardware_indicator").getBoolean("temperature_indicator", false)) {
                loadHooker(StatusBartemperature_indicator())
            }
        }
    }
}
