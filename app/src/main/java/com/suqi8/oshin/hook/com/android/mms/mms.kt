package com.suqi8.oshin.hook.com.android.mms

import android.annotation.SuppressLint
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method

class mms: YukiBaseHooker() {
    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onHook() {
        loadApp("com.android.mms"){
            if (prefs("mms").getBoolean("remove_message_ads", false)) {
                "oh.c".toClass().apply {
                    method {
                        name = "d"
                        param("nh.j", "java.util.HashMap", "java.util.HashMap")
                        returnType = "java.util.List"
                    }.hook {
                        before {
                            result = emptyList<message_ads>()
                        }
                    }
                }
            }
        }
    }
    data class message_ads(
        val any: Any
    )
}
