package com.suqi8.oshin.hook.systemui

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

class UsbDefaultFileTransfer: YukiBaseHooker() {
    override fun onHook() {
        loadApp(name = "com.android.systemui") {
            if (prefs("systemui").getBoolean("usb_default_file_transfer", false)) {
                "com.oplus.systemui.usb.UsbService".toClass().resolve().firstMethod {
                    modifiers(Modifiers.PRIVATE)
                    name = "getDefaultSelectType"
                    emptyParameters()
                    returnType = Int::class
                }.hook {
                    before { result = 1 }
                }
            }
        }
    }
}
