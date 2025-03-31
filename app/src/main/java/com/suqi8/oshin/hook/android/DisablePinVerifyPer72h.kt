package com.suqi8.oshin.hook.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.LongType
import com.highcapable.yukihookapi.hook.type.java.UnitType

class DisablePinVerifyPer72h : YukiBaseHooker() {
    override fun onHook() {
        loadSystem {
            if (prefs("android").getBoolean("DisablePinVerifyPer72h", false)) {
                "com.android.server.locksettings.LockSettingsStrongAuth".toClass().apply {
                    method {
                        name = "rescheduleStrongAuthTimeoutAlarm"
                        param(LongType, IntType)
                        returnType = UnitType
                    }.hook {
                        replaceUnit { }
                    }
                }
            }
        }
    }
}
