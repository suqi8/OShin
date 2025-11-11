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
            if (prefs.getBoolean("app_start_dialog_legacy_mode", false)) {
                bridge.findClass {
                    matcher {
                        usingStrings(
                            "callerPackage",
                            "callerName",
                            "calleePackage",
                            "calleeName",
                            "sourceIntent"
                        )
                    }
                }.singleOrNull()?.also {
                    it.name.toClass().resolve().apply {
                        firstConstructor {
                            modifiers(Modifiers.PUBLIC)
                            parameters(
                                String::class,
                                String::class,
                                String::class,
                                String::class,
                                "android.content.Intent",
                                Int::class,
                                Int::class,
                                Int::class,
                                "d9.a\$b",
                                Int::class
                            )
                        }.hook {
                            before {
                                args[9] = 3
                            }
                        }
                    }
                }
            }
            if (prefs.getBoolean("app_start_dialog_always_allow", false)) {
                bridge.findClass {
                    matcher {
                        usingStrings(
                            "remove ignored activity: callerPackage=",
                            ", targetActivity=",
                            "ignored_activity"
                        )
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

                bridge.findClass {
                    matcher {
                        usingStrings(
                            "COUIAlertDialogBuilder",
                            "customImageview is error; Need to check whether the application has a layout"
                        )
                    }
                }.singleOrNull()?.let { cls ->
                    cls.name.toClass().resolve().apply {
                        firstMethod {
                            modifiers(Modifiers.PUBLIC)
                            name = "t0"
                            parameters(
                                Int::class,
                                "android.content.DialogInterface\$OnClickListener",
                                Boolean::class
                            )
                            returnType = cls.name
                        }.hook {
                            before {
                                "com.oplus.securitypermission.R\$string".toClass().resolve().apply {
                                    val allow30Res = firstField {
                                        modifiers(
                                            Modifiers.PUBLIC,
                                            Modifiers.STATIC,
                                            Modifiers.FINAL
                                        )
                                        name = "app_start_dialog_allow_30"
                                        type = Int::class
                                    }.get()
                                    val alwaysAllowRes = firstField {
                                        modifiers(
                                            Modifiers.PUBLIC,
                                            Modifiers.STATIC,
                                            Modifiers.FINAL
                                        )
                                        name = "app_start_dialog_always_allow"
                                        type = Int::class
                                    }.get()
                                    if (args[0] == allow30Res) {
                                        args[0] = alwaysAllowRes
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
