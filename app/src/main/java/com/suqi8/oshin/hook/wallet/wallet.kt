package com.suqi8.oshin.hook.wallet

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method

class wallet: YukiBaseHooker() {
    override fun onHook() {
        loadApp(name = "com.finshell.wallet") {
            if (prefs("wallet").getBoolean("remove_swipe_page_ads", false)) {
                "com.finshell.quickcardpkg.travel.repository.QcpTaxiServiceAppRequest".toClass().apply {
                    method {
                        name = "getUrl"
                        emptyParam()
                        returnType = "java.lang.String"
                    }.hook {
                        before {
                            result = null
                        }
                    }
                }
                "com.finshell.setting.net.SimpleSwipeBizIdRequest".toClass().apply {
                    method {
                        name = "getUrl"
                        emptyParam()
                        returnType = "java.lang.String"
                    }.hook {
                        before {
                            result = null
                        }
                    }
                }
                "com.nearme.domain.OperatePageResourceRequest".toClass().apply {
                    method {
                        name = "getUrl"
                        emptyParam()
                        returnType = "java.lang.String"
                    }.hook {
                        before {
                            result = null
                        }
                    }
                }
            }
        }
    }
}
