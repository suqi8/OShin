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
            if (prefs.getBoolean("app_start_dialog_always_allow",false)) {
                bridge.findClass {
                    matcher {
                        usingStrings("remove ignored activity: callerPackage=", ", targetActivity=", "ignored_activity")
                        usingStrings("valid_time", "user set, s=")
                    }
                }.findMethod {
                    matcher {
                        paramTypes("long")
                    }
                }.singleOrNull()?.also {
                    it.className.toClass().resolve().firstMethod { name = it.methodName }.hook {
                        before {
                            result = null
                        }
                    }
                }
            }
        }
    }
}
