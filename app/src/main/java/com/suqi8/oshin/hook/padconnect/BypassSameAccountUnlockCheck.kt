package com.suqi8.oshin.hook.padconnect

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import org.luckypray.dexkit.DexKitBridge

class BypassSameAccountUnlockCheck: YukiBaseHooker() {
    override fun onHook() {
        loadApp(name = "com.oplus.padconnect") {
            DexKitBridge.create(this.appInfo.sourceDir).use {
                if (prefs("padconnect").getBoolean("bypass_same_account_unlock_safety_check", false)) {
                    it.findClass {
                        searchPackages("com.oplus.sdp")
                        matcher {
                            usingStrings("deviceNotSupportDialog != null && deviceNotSupportDialog.isShowing()")
                        }
                    }.singleOrNull()?.apply {
                        name.toClass().resolve().apply {
                            firstConstructor {
                                modifiers(Modifiers.PUBLIC)
                                parameters("com.oplus.padconnect.nfc.unlock.ui.NFCUnlockDevicePreference", Int::class)
                            }.hook {
                                before {
                                    //YLog.debug("原始参数 args[1] = ${args[1]}")
                                    if (args[1] == 0) {
                                        args[1] = 1
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
