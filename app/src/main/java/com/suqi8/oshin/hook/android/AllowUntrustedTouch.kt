package com.suqi8.oshin.hook.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.IntType

class AllowUntrustedTouch : YukiBaseHooker() {
    override fun onHook() {
        loadSystem {
            if (prefs("android").getBoolean("AllowUntrustedTouch", false)) {
                "com.android.server.wm.WindowState".toClass().apply {
                    method {
                        name = "getTouchOcclusionMode"
                        emptyParam()
                        returnType = IntType
                    }.hook {
                        before {
                            result = 2
                        }
                    }
                }
            }
        }
    }
}
