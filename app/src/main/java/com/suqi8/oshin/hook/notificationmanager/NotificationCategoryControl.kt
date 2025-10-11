package com.suqi8.oshin.hook.notificationmanager

import android.app.NotificationChannel
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

class NotificationCategoryControl : YukiBaseHooker() {
    override fun onHook() {
        if (prefs("notificationmanager").getBoolean("allow_turn_off_all_categories", false)) {
            NotificationChannel::class.java.resolve().apply {
                constructor {
                    optional()
                }.hookAll {
                    after {
                        instance.asResolver().firstField { name = "mBlockableSystem" }.set(true)
                        instance.asResolver().firstField { name = "mImportanceLockedDefaultApp" }.set(false)
                    }
                }
                firstMethod { name = "isBlockable" }.hook { replaceToTrue() }
                firstMethod { name = "setBlockable" }.hook { replaceUnit {  } }
                firstMethod { name = "isImportanceLockedByCriticalDeviceFunction" }.hook { replaceAny { false } }
                firstMethod { name = "setImportanceLockedByCriticalDeviceFunction" }.hook { replaceUnit { } }
            }
            loadApp(name = "com.oplus.notificationmanager") {
                "com.oplus.notificationmanager.property.uicontroller.BooleanController".toClass().resolve().apply {
                    firstMethod {
                        modifiers(Modifiers.PUBLIC)
                        name = "isEnabled"
                        emptyParameters()
                        returnType = Boolean::class
                    }.hook {
                        replaceToTrue()
                    }
                }
            }
        }
    }
}
