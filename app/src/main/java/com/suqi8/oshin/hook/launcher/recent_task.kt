package com.suqi8.oshin.hook.launcher

import android.annotation.SuppressLint
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType

class recent_task: YukiBaseHooker() {
    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onHook() {
        loadApp("com.android.launcher"){
            if (prefs("launcher\\recent_task").getBoolean("force_display_memory", false)) {
                "com.oplus.quickstep.memory.MemoryInfoManager".toClass().apply {
                    method {
                        name = "isAllowMemoryInfoDisplay"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
                "com.oplus.quickstep.memory.MemoryInfoManager".toClass().apply {
                    method {
                        name = "needMemoryDetail"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
        }
    }
}
