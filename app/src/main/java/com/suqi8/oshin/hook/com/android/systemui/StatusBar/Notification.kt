package com.suqi8.oshin.hook.com.android.systemui.StatusBar

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

class Notification: YukiBaseHooker() {
    override fun onHook() {
        loadApp(name = "com.android.systemui") {
            if (prefs("systemui\\notification").getBoolean("remove_developer_options_notification", false)) {
                "com.oplus.systemui.statusbar.controller.SystemPromptController".toClass().resolve().apply {
                    firstMethod {
                        name = "updateDeveloperMode"
                    }.hook {
                        replaceUnit { }
                    }
                }

            }
            if (prefs("systemui\\notification").getBoolean("remove_and_do_not_disturb_notification", false)) {
                "com.oplus.systemui.statusbar.controller.NoDisturbController".toClass().resolve().apply {
                    firstMethod {
                        modifiers(Modifiers.PUBLIC, Modifiers.FINAL)
                        name = "updateNoDisturbStatus"
                        emptyParameters()
                        returnType = Void.TYPE
                    }.hook {
                        replaceUnit {  }
                    }
                }
            }
            if (prefs("systemui\\notification").getBoolean("remove_charging_complete_notification", false)) {
                "com.oplus.systemui.statusbar.notification.power.OplusPowerNotificationWarnings".toClass().resolve().apply {
                    firstMethod {
                        modifiers(Modifiers.PUBLIC, Modifiers.FINAL)
                        name = "showChargeErrorDialog"
                        parameters(Int::class)
                        returnType = Void.TYPE
                    }.hook {
                        before {
                            if (args[0] != 7) return@before
                            resultNull()
                        }
                    }
                }
            }
        }
        loadSystem {
            if (prefs("systemui\\notification").getBoolean("remove_active_vpn_notification", false)) {
                "com.android.server.connectivity.VpnExtImpl".toClass().resolve().apply {
                    firstMethod {
                        modifiers(Modifiers.PUBLIC)
                        name = "showNotification"
                        parameters(String::class, Int::class, Int::class, String::class, "android.app.PendingIntent", "com.android.internal.net.VpnConfig")
                        returnType = Void.TYPE
                    }.hook {
                        replaceUnit {  }
                    }
                }
            }
        }
    }
}
