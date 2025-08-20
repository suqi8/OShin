package com.suqi8.oshin.hook.com.oplus.games

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.luckypray.dexkit.DexKitBridge
import java.lang.reflect.Modifier

class games: YukiBaseHooker() {
    override fun onHook() {
        loadApp(name = "com.oplus.games") {
            DexKitBridge.create(this.appInfo.sourceDir).use {
                //游戏AI
                it.findMethod {
                    searchPackages("business.module.aiplay")
                    matcher {
                        modifiers = Modifier.PRIVATE
                        returnType = "boolean"
                        usingStrings("feature.support.game.AI_PLAY")
                    }
                }.forEach {
                    it.className.toClass().resolve().apply {
                        if (prefs("games").getBoolean("hok_ai_v1", false) && it.className in "sgame") {
                            firstMethod {
                                name = it.methodName
                                returnType = Boolean::class
                            }.hook { before { result = true } }
                        }
                        if (prefs("games").getBoolean("pubg_ai", false) && it.className in "pubg") {
                            firstMethod {
                                name = it.methodName
                                returnType = Boolean::class
                            }.hook { before { result = true } }
                        }
                        if (prefs("games").getBoolean("enable_mlbb_ai_god_assist", false) && it.className in "mlbb") {
                            firstMethod {
                                name = it.methodName
                                returnType = Boolean::class
                            }.hook { before { result = true } }
                        }
                    }
                }
                //全部功能
                if (prefs("games").getBoolean("enable_all_features", false)) {
                    it.findMethod {
                        matcher {
                            modifiers = Modifier.PUBLIC
                            returnType = "boolean"
                            paramTypes("android.content.ContentResolver", "java.lang.String")
                        }
                    }.singleOrNull()?.also {
                        //YLog.info("methodName:"+it.methodName + " className:" + it.className)
                        it.className.toClass().resolve().firstMethod { name = it.methodName }.hook { before { result = true } }
                    }
                }
                //超神连招
                if (prefs("games").getBoolean("ultra_combo", false)) {
                    it.findMethod {
                        searchPackages("business.module.assistkey")
                        matcher {
                            modifiers = Modifier.PRIVATE
                            returnType = "boolean"
                            usingStrings("feature.support.game.ASSIST_KEY")
                        }
                    }.singleOrNull()?.also {
                        it.className.toClass().resolve().firstMethod { name = it.methodName }.hook { before { result = true } }
                    }
                }
                //云控
                if (prefs("games").getBoolean("feature_disable_cloud_control", false)) {
                    it.findMethod {
                        searchPackages("com.coloros.gamespaceui.config.cloud")
                        matcher {
                            modifiers = Modifier.PUBLIC
                            returnType = "boolean"
                            usingStrings("cloudKey")
                            paramTypes("java.lang.String", "java.util.Map")
                        }
                    }.singleOrNull()?.also {
                        it.className.toClass().resolve().firstMethod { name = it.methodName }.hook { before { result = true } }
                    }
                }
                //去除报名限制
                if (prefs("games").getBoolean("remove_package_restriction", false)) {
                    it.findMethod {
                        searchPackages("com.coloros.gamespaceui.config.cloud")
                        matcher {
                            modifiers = Modifier.PRIVATE
                            returnType = "boolean"
                            paramTypes("java.util.Set", "java.util.Map")
                        }
                    }.singleOrNull()?.also {
                        it.className.toClass().resolve().firstMethod { name = it.methodName }.hook { before { result = true } }
                    }
                }
                //root检测
                if (prefs("games").getBoolean("remove_game_filter_root_detection", false)) {
                    it.findMethod {
                        searchPackages("business.module.gamefilter")
                        matcher {
                            modifiers = Modifier.PUBLIC
                            returnType = "java.lang.Integer"
                        }
                    }.forEach {
                        it.className.toClass().resolve().firstMethod { name = it.methodName }.hook { after {
                            val Rresult = result
                            //YLog.info(it.methodName+"Root返回模式：$Rresult")
                            if (Rresult == 1) result = 0
                        } }
                    }
                }
                if (prefs("games").getBoolean("hok_ai_v2", false)) {
                    it.findMethod {
                        searchPackages("business.module.aiplay.sgame")
                        matcher {
                            modifiers = Modifier.PUBLIC
                            returnType = "boolean"
                            usingStrings("feature.support.game.AI_PLAY_version2")
                        }
                    }.singleOrNull()?.also {
                        it.className.toClass().resolve().firstMethod { name = it.methodName }.hook { before { result = true } }
                    }
                }
                if (prefs("games").getBoolean("hok_ai_v3", false)) {
                    it.findMethod {
                        searchPackages("business.module.aiplay.sgame")
                        matcher {
                            modifiers = Modifier.PUBLIC
                            returnType = "boolean"
                            usingStrings("feature.support.game.AI_PLAY_version3")
                        }
                    }.singleOrNull()?.also {
                        it.className.toClass().resolve().firstMethod { name = it.methodName }.hook { before { result = true } }
                    }
                }
                /*it.findClass {
                    searchPackages("com.coloros.gamespaceui.config")
                    matcher {
                        usingStrings("ServerConfigManager","parseJsonToUnit() error, json: ")
                        usingStrings("setCloudControlRecord key: ")
                        usingStrings("getApplicationContext(...)")
                    }
                }.findMethod {
                    matcher {
                        paramTypes = emptyList()
                        returnType = "java.util.Map"
                    }
                }.singleOrNull()?.also {
                    it.className.toClass().resolve().firstMethod {
                        name = it.methodName
                    }.hook {
                        after {
                            val res = result
                            if (res !is Map<*, *>) {
                                return@after
                            }
                            val newMap = res.toMutableMap()
                            if (newMap.containsKey("game_assist_key_back_list_key")) {
                                newMap["game_assist_key_back_list_key"] = "[\\n  {\\n    \\\"conditionSet\\\": [\\n      {\\n        \\\"name\\\": \\\"supportedGames\\\",\\n        \\\"logic\\\": 4,\\n        \\\"value\\\": [\\n          \\\"com.tencent.tmgp.sg\\\"\\n        ]\\n      }\\n    ],\\n    \\\"result\\\": {\\n      \\\"functionEnabled\\\": 1\\n    }\\n  },\\n  {\\n    \\\"conditionSet\\\": [\\n    ],\\n    \\\"result\\\": {\\n      \\\"functionEnabled\\\": 0\\n    }\\n  }\\n]"
                            }
                            YLog.info(newMap.toMap())
                            result = newMap.toMap()
                        }
                    }
                }*/
            }
        }
    }
}
