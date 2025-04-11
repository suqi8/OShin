package com.suqi8.oshin.hook.com.android.systemui

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.suqi8.oshin.hook.com.android.systemui.StatusBar.BatteryBar
import com.suqi8.oshin.hook.com.android.systemui.StatusBar.Clock
import com.suqi8.oshin.hook.com.android.systemui.StatusBar.Fragment
import com.suqi8.oshin.hook.com.android.systemui.StatusBar.Hardware_indicator
import com.suqi8.oshin.hook.com.android.systemui.StatusBar.Icon
import com.suqi8.oshin.hook.com.android.systemui.StatusBar.Notification
import com.suqi8.oshin.hook.com.android.systemui.StatusBar.Wifi

class systemui: YukiBaseHooker() {
    override fun onHook() {
        loadApp(hooker = Clock())
        loadApp(hooker = Hardware_indicator())
        loadApp(hooker = Icon())
        loadApp(hooker = Notification())
        loadApp(hooker = Fragment())
        loadApp(hooker = allday_screenoff())
        loadApp(hooker = BatteryBar())
        loadApp(hooker = Wifi())
        loadApp(name = "com.android.systemui") {
            "com.oplus.systemui.plugins.qs.OplusQSSpecialModeProvider".toClass().apply {
                method {
                    name = "getActiveColor"
                    emptyParam()
                    returnType = IntType
                }.hook {
                    before {
                        result = -3342490
                    }
                }
            }
        }
    }
}
