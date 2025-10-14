package com.suqi8.oshin.hook.systemui

import android.annotation.SuppressLint
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.suqi8.oshin.hook.systemui.StatusBar.BatteryBar
import com.suqi8.oshin.hook.systemui.StatusBar.Clock
import com.suqi8.oshin.hook.systemui.StatusBar.Fragment
import com.suqi8.oshin.hook.systemui.StatusBar.HardwareIndicator
import com.suqi8.oshin.hook.systemui.StatusBar.Notification
import com.suqi8.oshin.hook.systemui.StatusBar.ShowRealBattery
import com.suqi8.oshin.hook.systemui.StatusBar.StatusBarLayout
import com.suqi8.oshin.hook.systemui.StatusBar.Wifi
import com.suqi8.oshin.hook.systemui.controlCenter.BigMediaArt


class systemui: YukiBaseHooker() {

    @SuppressLint("UseCompatLoadingForDrawables", "UseKtx")
    override fun onHook() {
        loadApp(hooker = Clock())
        loadApp(hooker = HardwareIndicator())
        loadApp(hooker = Notification())
        loadApp(hooker = Fragment())
        loadApp(hooker = allday_screenoff())
        loadApp(hooker = BatteryBar())
        loadApp(hooker = Wifi())
        loadApp(hooker = BigMediaArt())
        loadApp(hooker = DisableDataTransferAuth())
        loadApp(hooker = UsbDefaultFileTransfer())
        loadApp(hooker = RemoveUsbSelectionDialog())
        loadApp(hooker = ToastForceShowAppIcon())
        loadHooker(ShowRealBattery())
        loadHooker(StatusBarLayout())

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
