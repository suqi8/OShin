package com.suqi8.oshin.hook.android

import android.app.NotificationChannel
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

class NotificationCategoryControl : YukiBaseHooker() {
    override fun onHook() {
        if (prefs("android").getBoolean("allow_turn_off_all_categories", false)) {
            NotificationChannel::class.java.resolve().apply {
                constructor { }.hookAll {
                    after {
                        instance.resolve().firstField { name = "mBlockableSystem" }.set(true)
                        instance.resolve().firstField { name = "mImportanceLockedDefaultApp" }.set(false)
                    }
                }
                firstMethod { name = "isBlockable" }.hook { replaceToTrue() }
                firstMethod { name = "setBlockable" }.hook { replaceUnit {  } }
                firstMethod { name = "isImportanceLockedByCriticalDeviceFunction" }.hook { replaceAny { false } }
                firstMethod { name = "setImportanceLockedByCriticalDeviceFunction" }.hook { replaceUnit { } }
            }
        }
    }
}
