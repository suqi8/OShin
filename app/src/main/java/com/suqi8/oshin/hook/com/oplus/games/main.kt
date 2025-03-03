package com.suqi8.oshin.hook.com.oplus.games

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType

class games: YukiBaseHooker() {
    override fun onHook() {
        if (prefs("games").getBoolean("ultra_combo", false)) {
            loadApp(name = "com.oplus.games") {
                "business.module.assistkey.GameAssistKeyFeature".toClass().apply {
                    method {
                        name = "L"
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
