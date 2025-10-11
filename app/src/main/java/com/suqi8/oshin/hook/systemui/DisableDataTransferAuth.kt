package com.suqi8.oshin.hook.systemui

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

class DisableDataTransferAuth: YukiBaseHooker() {
    override fun onHook() {
        loadApp(name = "com.android.systemui") {
            if (prefs("systemui").getBoolean("disable_data_transfer_auth", false)) {
                "com.oplus.systemui.shutdown.ShutdownBiometricPrompt".toClass().resolve().firstMethod {
                    modifiers(Modifiers.PUBLIC, Modifiers.FINAL)
                    name = "authenticate"
                    parameters(Boolean::class, "android.os.CancellationSignal", "com.oplus.systemui.shutdown.ShutdownBiometricPrompt\$AuthenticationCallback")
                    returnType = Void.TYPE
                }.hook {
                    before {
                        val isCalledFromUsbService = Throwable().stackTrace.any { stackTraceElement ->
                            stackTraceElement.className == "com.oplus.systemui.usb.UsbService"
                        }
                        if (!isCalledFromUsbService) return@before
                        args[2]?.javaClass?.resolve()?.firstMethod {
                            name = "onAuthenticationSucceeded"
                        }?.invoke()
                    }
                }
            }
        }
    }
}
