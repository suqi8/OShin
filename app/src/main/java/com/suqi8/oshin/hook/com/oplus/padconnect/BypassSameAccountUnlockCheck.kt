package com.suqi8.oshin.hook.com.oplus.padconnect

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.luckypray.dexkit.DexKitBridge

class BypassSameAccountUnlockCheck: YukiBaseHooker() {
    override fun onHook() {
        loadApp(name = "com.oplus.padconnect") {
            DexKitBridge.create(this.appInfo.sourceDir).use {
                if (prefs("padconnect").getBoolean("bypass_same_account_unlock_safety_check", false)) {
                    it.findClass {
                        matcher {
                            usingStrings("deviceNotSupportDialog != null && deviceNotSupportDialog.isShowing()")
                        }
                    }.findMethod {
                        matcher {
                            paramTypes = listOf(null, null)
                        }
                    }.singleOrNull()?.also {
                        it.className.toClass().resolve().firstMethod { name = it.methodName }.hook { before {
                            if (args[1] == 0) args[1] = 1
                        } }
                    }
                }
            }
        }
    }
}
