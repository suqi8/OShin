package com.suqi8.oshin.hook.com.heytap.health

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method

class health: YukiBaseHooker() {
    override fun onHook() {
        if (prefs("health").getBoolean("disable_root_dialog", false)) {
            loadApp(name = "com.heytap.health") {
                "com.heytap.health.safety.safetycheck.SafetyCheckManager\$check\$1".toClass().apply {
                    method {
                        name = "invokeSuspend"
                    }.hook {
                        before {
                            args[0] = null
                        }
                    }
                }
            }
        }
    }
}
