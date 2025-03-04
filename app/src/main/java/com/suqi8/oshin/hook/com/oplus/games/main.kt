package com.suqi8.oshin.hook.com.oplus.games

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType

class games: YukiBaseHooker() {
    override fun onHook() {
        loadApp(name = "com.oplus.games") {
            if (prefs("games").getBoolean("ultra_combo", false)) {
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
            /*"v20.b".toClass().apply {
                method {
                    name = "b"
                    param("android.content.ContentResolver", "java.lang.String")
                    returnType = BooleanType
                }.hook {
                    before {
                        result = true
                    }
                }
            }*/
            if (prefs("games").getBoolean("hok_ai_v1", false)) {
                "business.module.aiplay.sgame.AIPlayFeature".toClass().apply {
                    method {
                        name = "f0"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("games").getBoolean("hok_ai_v2", false)) {
                "business.module.aiplay.sgame.AIPlayFeature".toClass().apply {
                    method {
                        name = "e0"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("games").getBoolean("hok_ai_v3", false)) {
                "business.module.aiplay.sgame.AIPlayFeature".toClass().apply {
                    method {
                        name = "g0"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("games").getBoolean("hok_ai_remove_pkg_restriction", false)) {
                "business.module.aiplay.sgame.AIPlayFeature".toClass().apply {
                    method {
                        name = "isFeatureEnabled"
                        param("java.lang.String")
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
