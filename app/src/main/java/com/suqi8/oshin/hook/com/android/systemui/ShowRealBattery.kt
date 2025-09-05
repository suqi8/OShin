package com.suqi8.oshin.hook.com.android.systemui

import android.content.Intent
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import java.io.File

class ShowRealBattery: YukiBaseHooker() {
    override fun onHook() {
        loadApp(name = "com.android.systemui") {
            if (prefs("systemui").getBoolean("show_real_battery", false)) {
                "com.android.systemui.statusbar.policy.BatteryControllerImpl".toClass().resolve().apply {
                    firstMethod {
                        modifiers(Modifiers.PUBLIC, Modifiers.FINAL)
                        name = "onReceive"
                        parameters("android.content.Context", "android.content.Intent")
                        returnType = Void.TYPE
                    }.hook {
                        before {
                            val intent = args[1] as? Intent ?: return@before
                            if (intent.action == "android.intent.action.BATTERY_CHANGED") {
                                val oplusSocPath = "/sys/class/oplus_chg/battery/chip_soc"
                                val socFile = File(oplusSocPath)
                                if (socFile.exists() && socFile.canRead()) {
                                    val socValue = socFile.readText().trim().toIntOrNull()
                                    if (socValue != null) {
                                        intent.putExtra("level", socValue)
                                        intent.putExtra("scale", 100)
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
