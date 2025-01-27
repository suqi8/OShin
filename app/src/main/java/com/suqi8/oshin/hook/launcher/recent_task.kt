package com.suqi8.oshin.hook.launcher

import android.annotation.SuppressLint
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method

class recent_task: YukiBaseHooker() {
    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onHook() {
        loadApp("com.android.launcher"){
            if (prefs("launcher\\recent_task").getBoolean("com_android_launcher_icon_text", false)) {
                "com.oplus.quickstep.memory.MemoryInfoManager".toClass().apply {
                    method {
                        name = "isAllowMemoryInfoDisplay"
                    }.hook {
                        after {
                            args[0] = true
                        }
                    }
                }
                "com.oplus.quickstep.memory.MemoryInfoManager".toClass().apply {
                    method {
                        name = "needMemoryDetail"
                    }.hook{
                        after {
                            args[0] = true
                        }
                    }
                }
            }
        }
    }
}
