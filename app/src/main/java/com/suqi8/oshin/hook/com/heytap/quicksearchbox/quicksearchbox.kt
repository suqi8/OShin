package com.suqi8.oshin.hook.com.heytap.quicksearchbox

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.UnitType

class quicksearchbox: YukiBaseHooker() {
    override fun onHook() {
        if (prefs("quicksearchbox").getBoolean("remove_app_recommendation_ads", false)) {
            loadApp(name = "com.heytap.quicksearchbox") {
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
