package com.suqi8.oshin.hook.com.heytap.speechassist

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method

class speechassist: YukiBaseHooker() {
    override fun onHook() {
        if (prefs("speechassist").getBoolean("ai_call", false)) {
            loadApp(name = "com.heytap.speechassist") {
                "com.heytap.speechassist.aicall.setting.config.AiCallCommonBean".toClass().apply {
                    method {
                        name = "getSupportAiCall"
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
                "com.heytap.speechassist.aicall.setting.config.AiCallCommonBean".toClass().apply {
                    method {
                        name = "getSupportAiCallV2"
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
