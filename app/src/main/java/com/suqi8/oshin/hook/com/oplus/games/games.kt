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
            if (prefs("games").getBoolean("enable_all_features", false)) {
                "v20.b".toClass().apply {
                    method {
                        name = "b"
                        param("android.content.ContentResolver", "java.lang.String")
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
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
            if (prefs("games").getBoolean("feature_disable_cloud_control", false)) {
                "com.coloros.gamespaceui.config.cloud.CloudConditionUtil".toClass().apply {
                    method {
                        name = "j"
                        param("java.lang.String", "java.util.Map")
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("games").getBoolean("remove_package_restriction", false)) {
                "com.coloros.gamespaceui.config.cloud.CloudConditionUtil".toClass().apply {
                    method {
                        name = "a"
                        param("java.util.Set", "java.util.Map")
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("games").getBoolean("pubg_ai", false)) {
                "business.module.aiplay.pubg.AIPlayPubgFeature".toClass().apply {
                    method {
                        name = "Z"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
                "business.module.aiplay.pubg.AIPlayPubgFeature".toClass().apply {
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
