package com.suqi8.oshin.hook.com.android.systemui

import android.annotation.SuppressLint
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.suqi8.oshin.hook.com.android.systemui.StatusBar.BatteryBar
import com.suqi8.oshin.hook.com.android.systemui.StatusBar.Clock
import com.suqi8.oshin.hook.com.android.systemui.StatusBar.Fragment
import com.suqi8.oshin.hook.com.android.systemui.StatusBar.Hardware_indicator
import com.suqi8.oshin.hook.com.android.systemui.StatusBar.Icon
import com.suqi8.oshin.hook.com.android.systemui.StatusBar.Notification
import com.suqi8.oshin.hook.com.android.systemui.StatusBar.Wifi
import com.suqi8.oshin.hook.com.android.systemui.controlCenter.BigMediaArt


class systemui: YukiBaseHooker() {

    @SuppressLint("UseCompatLoadingForDrawables", "UseKtx")
    override fun onHook() {
        loadApp(hooker = Clock())
        loadApp(hooker = Hardware_indicator())
        loadApp(hooker = Icon())
        loadApp(hooker = Notification())
        loadApp(hooker = Fragment())
        loadApp(hooker = allday_screenoff())
        loadApp(hooker = BatteryBar())
        loadApp(hooker = Wifi())
        loadApp(hooker = BigMediaArt())
        loadApp(hooker = DisableDataTransferAuth())
        loadApp(hooker = UsbDefaultFileTransfer())
        loadApp(hooker = RemoveUsbSelectionDialog())

        /*loadApp(name = "com.android.systemui") {
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
        }*/
    }
}
