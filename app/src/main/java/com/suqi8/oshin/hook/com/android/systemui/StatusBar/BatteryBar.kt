package com.suqi8.oshin.hook.com.android.systemui.StatusBar

import android.annotation.SuppressLint
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

class BatteryBar: YukiBaseHooker() {
    @SuppressLint("SetTextI18n")
    override fun onHook() {
        loadApp("com.android.systemui"){
            "com.android.systemui.statusbar.policy.Clock".toClass().apply {

            }
        }
    }
}
