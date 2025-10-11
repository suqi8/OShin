package com.suqi8.oshin.hook.incallui

import android.annotation.SuppressLint
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType

class call_ringtone : YukiBaseHooker() {
    @SuppressLint("PrivateApi")
    override fun onHook() {
        if (prefs("incallui").getBoolean("hide_call_ringtone", false)) {
            loadApp(name = "com.android.incallui") {
                "com.android.incallui.Call".toClass().apply {
                    method {
                        name = "getVideoCall"
                        emptyParam()
                        returnType = "android.telecom.InCallService\$VideoCall"
                    }.hook {
                        before { result = null }
                    }
                    method {
                        name = "getIsVideoRingTone"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        replaceToFalse()
                    }
                }
            }
        }
    }
}
