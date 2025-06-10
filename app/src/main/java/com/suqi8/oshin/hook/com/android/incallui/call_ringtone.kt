package com.suqi8.oshin.hook.com.android.incallui

import android.annotation.SuppressLint
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method

class call_ringtone : YukiBaseHooker() {
    @SuppressLint("PrivateApi")
    override fun onHook() {
        if (prefs("incallui").getBoolean("hide_call_ringtone", false)) {
            loadApp(name = "com.android.incallui") {
                "com.android.incallui.Call".toClass().method {
                    name = "getVideoCall"
                    emptyParam()
                    returnType = "android.telecom.InCallService\$VideoCall"
                }.hook {
                    before { result = null }
                }
            }
        }
    }
}
