package com.suqi8.oshin.hook.securitypermission

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.luckypray.dexkit.DexKitBridge

class securitypermission : YukiBaseHooker() {
    override fun onHook() {
        val prefs = prefs("securitypermission")
        loadApp("com.oplus.securitypermission") {
            val bridge = DexKitBridge.create(this.appInfo.sourceDir)
            if (prefs.getBoolean("app_start_dialog_legacy_mode",false)) {
                bridge.findClass {
                    matcher {
                        usingStrings("callerPackage", "callerName", "calleePackage", "calleeName", "sourceIntent")
                    }
                }.singleOrNull()?.also {
                    it.name.toClass().resolve().apply {
                        firstConstructor {
                            modifiers(Modifiers.PUBLIC)
                            parameters(String::class, String::class, String::class, String::class, "android.content.Intent", Int::class, Int::class, Int::class, "d9.a\$b", Int::class)
                        }.hook {
                            before {
                                args[9] = 3
                            }
                        }
                    }
                }
            }
        }
    }
}
