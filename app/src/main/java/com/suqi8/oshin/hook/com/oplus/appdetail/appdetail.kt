package com.suqi8.oshin.hook.com.oplus.appdetail

import androidx.lifecycle.MutableLiveData
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.UnitType
import org.luckypray.dexkit.DexKitBridge
import java.lang.reflect.Modifier

class appdetail: YukiBaseHooker() {
    override fun onHook() {
        loadApp(name = "com.oplus.appdetail") {
            if (prefs("appdetail").getBoolean("remove_recommendations", false)) {
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
            }
            //移除安装前安全检测
            if (prefs("appdetail").getBoolean("remove_security_check", false)) {
                "com.oplus.appdetail.model.guide.viewModel.GuideShareViewModel".toClass().apply {
                    method {
                        name = security_check_method
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
                "com.oplus.appdetail.modelv2.guide.viewmodel.RiskScanViewModel".toClass().resolve().apply {
                    firstMethod {
                        modifiers(Modifiers.PUBLIC, Modifiers.FINAL)
                        name = "r"
                        parameters("com.oplus.appdetail.model.guide.repository.ExtJumpParam", "com.heytap.cdo.security.domain.safeguide.GuideResult", Long::class)
                        returnType = Void.TYPE
                    }.hook {
                        before {
                            "com.oplus.appdetail.modelv2.guide.viewmodel.RiskScanViewModel".toClass().resolve().apply {
                                firstField {
                                    modifiers(Modifiers.PRIVATE, Modifiers.FINAL)
                                    name = "c"
                                    type = "androidx.lifecycle.MutableLiveData"
                                }.of(instance).get() as MutableLiveData<Any>
                            }
                        }
                    }
                }
            }
        }
    }
}
