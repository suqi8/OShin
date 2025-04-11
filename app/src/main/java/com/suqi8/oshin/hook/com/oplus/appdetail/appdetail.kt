package com.suqi8.oshin.hook.com.oplus.appdetail

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.UnitType

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
            //安装频繁
            if (prefs("appdetail").getBoolean("remove_installation_frequency_popup", false)) {
                "com.oplus.appdetail.model.entrance.ChannelBarrageActivity".toClass().apply {
                    method {
                        name = "x0"
                        emptyParam()
                        returnType = UnitType
                    }.hook {
                        replaceUnit {
                            method {
                                name = "A0"
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
                        name = "A0"
                        emptyParam()
                        returnType = UnitType
                    }.hook {
                        replaceUnit {
                            method {
                                name = "w0"
                                emptyParam()
                                returnType = UnitType
                            }.get(instance).call()
                        }
                    }
                }
            }
            //移除版本号检测
            if (prefs("appdetail").getBoolean("remove_attempt_installation_popup", false)) {
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
            if (prefs("appdetail").getBoolean("remove_attempt_installation_popup", false)) {
                "com.oplus.appdetail.model.guide.viewModel.GuideShareViewModel".toClass().apply {
                    method {
                        name = "i"
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
