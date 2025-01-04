package io.github.suqi8.opatch.hook.services

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method

class OplusRootCheck : YukiBaseHooker() {
    override fun onHook() {
        loadSystem {
            if (prefs("oplus_system_services").getBoolean("disable_root_check", false)) {
                "com.android.server.oplus.heimdall.HeimdallService".toClass().apply {
                    method {
                        name = "isRootEnable"
                        emptyParam()
                    }.hook {
                        after {
                            result = false
                        }
                    }
                }
            }
        }
    }
}