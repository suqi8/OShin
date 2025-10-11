package com.suqi8.oshin.hook.exsystemservice

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.luckypray.dexkit.DexKitBridge

class RemoveSystemTamperWarning: YukiBaseHooker() {
    override fun onHook() {
        loadApp(name = "com.oplus.exsystemservice") {
            DexKitBridge.create(this.appInfo.sourceDir).use {
                if (prefs("exsystemservice").getBoolean("remove_system_tamper_warning", false)) {
                    it.findMethod {
                        matcher {
                            usingStrings("OplusAntiRootDialogService","displayDialog uid = ","phone","power")
                        }
                    }.singleOrNull()?.also {
                        it.className.toClass().resolve().firstMethod { name = it.methodName }.hook { replaceUnit {  } }
                    }
                }
            }
        }
    }
}
