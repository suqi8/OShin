package com.suqi8.oshin.hook.quicksearchbox

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.UnitType

class quicksearchbox: YukiBaseHooker() {
    override fun onHook() {
        if (prefs("quicksearchbox").getBoolean("remove_app_recommendation_ads", false)) {
            loadApp(name = "com.heytap.quicksearchbox") {
                /*DexKitBridge.create(this.appInfo.sourceDir).use {
                    it.findMethod {
                        searchPackages("com.heytap.quicksearchbox.ui.widget.advicesub")
                        matcher {
                            modifiers = Modifier.PUBLIC
                            paramTypes(null, "java.lang.Boolean")
                        }
                    }.forEach {
                        YLog.info("methodName:"+it.methodName + " className:" + it.className)
                    }
                }*/
                "com.heytap.quicksearchbox.ui.widget.advicesub.AliveAppRecommendView".toClass().apply {
                    method {
                        name = "o"
                        param("java.util.List", BooleanType)
                        returnType = UnitType
                    }.hook {
                        replaceUnit {  }
                    }
                }
            }
        }
    }
}
