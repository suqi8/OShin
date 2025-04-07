package com.suqi8.oshin.hook.com.oplus.appdetail

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
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
        }
        /*"com.oplus.appdetail.model.entrance.ChannelBarrageActivity".toClass().apply {
            method {
                name = "x0"
                emptyParam()
                returnType = UnitType
            }.hook {
                replaceUnit {  }
            }
        }*/
        /*"com.oplus.appdetail.model.entrance.ChannelBarrageActivity".toClass().apply {
            method {
                name = "onCreate"
                param("android.os.Bundle")
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
        }*/
    }
}
