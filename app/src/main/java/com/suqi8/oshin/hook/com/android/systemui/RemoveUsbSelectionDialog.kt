package com.suqi8.oshin.hook.com.android.systemui

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

class RemoveUsbSelectionDialog: YukiBaseHooker() {
    override fun onHook() {
        loadApp(name = "com.android.systemui") {
            if (prefs("systemui").getBoolean("remove_usb_selection_dialog", false)) {
                "com.oplus.systemui.usb.UsbService".toClass().resolve().firstMethod {
                    modifiers(Modifiers.PUBLIC, Modifiers.FINAL)
                    name = "performUsbDialogAction"
                    parameters(Int::class)
                    returnType = Void.TYPE
                }.hook {
                    before {
                        if (args[0] == 1002 || args[0] == 1003) resultNull()
                    }
                }
            }
        }
    }
}
