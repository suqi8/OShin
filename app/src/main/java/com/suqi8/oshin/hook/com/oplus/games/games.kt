package com.suqi8.oshin.hook.com.oplus.games

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import org.luckypray.dexkit.DexKitBridge
import java.lang.reflect.Modifier

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
            DexKitBridge.create(this.appInfo.sourceDir).use {
                it.findMethod {
                    matcher {
                        modifiers = Modifier.PRIVATE
                        returnType = "boolean"
                        usingStrings("feature.support.game.AI_PLAY")
                    }
                }.forEach {
                    YLog.info(it.name + it.className)
                    it.className.toClass().apply {
                        if (prefs("games").getBoolean("hok_ai_v1", false) && it.className in "sgame") {
                            method {
                                name = it.methodName
                                returnType = BooleanType
                            }.hook { before { result = true } }
                            method {
                                name = it.methodName
                                returnType = BooleanType
                            }.hook { before { result = true } }
                        }
                        if (prefs("games").getBoolean("pubg_ai", false) && it.className in "pubg") {
                            method {
                                name = it.methodName
                                returnType = BooleanType
                            }.hook { before { result = true } }
                            method {
                                name = it.methodName
                                returnType = BooleanType
                            }.hook { before { result = true } }
                        }
                        if (prefs("games").getBoolean("enable_mlbb_ai_god_assist", false) && it.className in "mlbb") {
                            method {
                                name = it.methodName
                                returnType = BooleanType
                            }.hook { before { result = true } }
                            method {
                                name = it.methodName
                                returnType = BooleanType
                            }.hook { before { result = true } }
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
            if (prefs("games").getBoolean("remove_game_filter_root_detection", false)) {
                "business.module.gamefilter.GameFilterFeature".toClass().apply {
                    method {
                        name = "R"
                        emptyParam()
                        returnType = "java.lang.Integer"
                    }.hook {
                        after {
                            val Rresult = result
                            //YLog.info("Root返回模式：$Rresult")
                            if (Rresult == 1) result = 0
                        }
                    }
                }
            }
        }
    }
}
