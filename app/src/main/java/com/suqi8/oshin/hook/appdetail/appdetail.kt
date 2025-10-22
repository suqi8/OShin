package com.suqi8.oshin.hook.appdetail

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.luckypray.dexkit.DexKitBridge
import java.lang.reflect.Modifier

class appdetail: YukiBaseHooker() {
    override fun onHook() {
        loadApp(name = "com.oplus.appdetail") {
            val prefs = prefs("appdetail")
            /*if (prefs("appdetail").getBoolean("remove_recommendations", false)) {
                "com.oplus.appdetail.model.install.view.InstallPageContent\$initLiveDataObserver\$1".toClass().apply {
                    method {
                        name = "invoke"
                        param("com.heytap.cdo.card.domain.dto.CardDto")
                        returnType = UnitType
                    }.hook {
                        replaceUnit {  }
                    }
                }
                "com.oplus.appdetail.model.uninstall.UiDataObserverHelper\$initLiveDataObserver\$1".toClass().apply {
                    method {
                        name = "invoke"
                        param("com.heytap.cdo.card.domain.dto.CardDto")
                        returnType = UnitType
                    }.hook {
                        replaceUnit {  }
                    }
                }
            }
            var installation_frequency_methodName = ""
            var attempt_installation_method = ""
            var attempt_installation_callMethod = ""
            var security_check_method = ""
            DexKitBridge.create(this.appInfo.sourceDir).use {
                installation_frequency_methodName = it.findMethod {
                    searchPackages("com.oplus.appdetail.model.entrance")
                    matcher {
                        modifiers = Modifier.PRIVATE
                        paramTypes = listOf<String>()
                        returnType("void")
                        usingStrings("1")
                    }
                }.singleOrNull()?.methodName.toString()
                attempt_installation_method = it.findMethod {
                    searchPackages("com.oplus.appdetail.model.entrance")
                    matcher {
                        modifiers = Modifier.PRIVATE
                        paramTypes = listOf<String>()
                        returnType("void")
                        usingStrings("channel_risk_dialog")
                    }
                }.singleOrNull()?.methodName.toString()
                attempt_installation_callMethod = it.findMethod {
                    searchPackages("com.oplus.appdetail.model.entrance")
                    matcher {
                        modifiers = Modifier.PRIVATE
                        paramTypes = listOf<String>()
                        returnType("void")
                        usingStrings("oplus_extra_app_op_mode")
                    }
                }.singleOrNull()?.methodName.toString()
                it.findClass {
                    searchPackages("com.oplus.appdetail.model.guide.viewModel")
                    matcher {
                        source("GuideShareViewModel.kt")
                    }
                }.also {
                    security_check_method = it.findMethod {
                        matcher {
                            modifiers = Modifier.PUBLIC
                            paramTypes = listOf<String>()
                            returnType("boolean")
                            usingNumbers(0)
                            invokeMethods {
                                add {
                                    name = "getPackageUri"
                                }
                            }
                        }
                    }.singleOrNull()?.methodName.toString()
                }
            }
            //安装频繁
            if (prefs("appdetail").getBoolean("remove_installation_frequency_popup", false)) {
                "com.oplus.appdetail.model.entrance.ChannelBarrageActivity".toClass().apply {
                    method {
                        name = installation_frequency_methodName
                        emptyParam()
                        returnType = UnitType
                    }.hook {
                        replaceUnit {
                            method {
                                name = attempt_installation_method
                                emptyParam()
                                returnType = UnitType
                            }.get(instance).call()
                        }
                    }
                }
            }
            //尝试安装应用
            if (prefs("appdetail").getBoolean("remove_attempt_installation_popup", false)) {
                "com.oplus.appdetail.model.entrance.ChannelBarrageActivity".toClass().apply {
                    method {
                        name = attempt_installation_method
                        emptyParam()
                        returnType = UnitType
                    }.hook {
                        replaceUnit {
                            method {
                                name = attempt_installation_callMethod
                                emptyParam()
                                returnType = UnitType
                            }.get(instance).call()
                        }
                    }
                }
            }
            //移除版本号检测
            if (prefs("appdetail").getBoolean("remove_version_check", false)) {
                "com.nearme.common.util.AppUtil".toClass().apply {
                    method {
                        name = "getAppVersionCode"
                        param("android.content.Context", "java.lang.String")
                        returnType = IntType
                    }.hook {
                        before {
                            result = -1
                        }
                    }
                }
            }*/
            val bridge = DexKitBridge.create(this.appInfo.sourceDir)
            var remove_security_checkClassName = ""
            var remove_security_checkMethodName = ""
            bridge.apply {
                findClass {
                    searchPackages("com.oplus.appdetail.common.utils")
                    matcher {
                        usingStrings("com.heytap.market")
                        usingStrings("oplus.intent.action.settings.SCREEN_LOCK","oplus.intent.action.settings.BIOMETRIC_ENROLL_GUIDE")
                    }
                }.findMethod {
                    matcher {
                        modifiers = Modifier.STATIC or Modifier.PUBLIC
                        returnType = "boolean"
                        paramTypes("android.content.Context")
                    }
                }.singleOrNull()?.apply {
                    remove_security_checkClassName = className
                    remove_security_checkMethodName = methodName
                }
            }
            //移除安装前安全检测
            if (prefs.getBoolean("remove_security_check", false)) {
                remove_security_checkClassName.toClass().resolve().firstMethod {
                    modifiers(Modifiers.PUBLIC, Modifiers.STATIC)
                    name = remove_security_checkMethodName
                    parameters("android.content.Context")
                    returnType = Boolean::class
                }.hook {
                    replaceToFalse()
                }
            }
            //移除版本检测
            if (prefs.getBoolean("remove_version_check",false)) {
                "com.nearme.common.util.AppUtil".toClass().resolve().apply {
                    firstMethod {
                        modifiers(Modifiers.PUBLIC, Modifiers.STATIC, Modifiers.FINAL)
                        name = "getAppVersionCode"
                        parameters("android.content.Context", String::class)
                        returnType = Int::class
                    }.hook {
                        before {
                            result = -1
                        }
                    }
                }
            }
        }
    }
}
