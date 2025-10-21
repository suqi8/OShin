package com.suqi8.oshin.hook.browser

import android.view.View
import android.widget.RelativeLayout
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.luckypray.dexkit.DexKitBridge
import java.lang.reflect.Modifier

class browser: YukiBaseHooker() {
    override fun onHook() {
        val prefs = prefs("browser")
        loadApp("com.heytap.browser") {
            val bridge = DexKitBridge.create(this.appInfo.sourceDir)
            if (prefs.getBoolean("remove_weather_injected_ads", false)) {
                bridge.findClass {
                    matcher {
                        className = "com.heytap.browser.business.weather.js.ThirdCommonJsHook"
                    }
                }.findMethod {
                    matcher {
                        modifiers = Modifier.PUBLIC
                        paramCount = 0
                        returnType = "void"
                        usingNumbers(4)
                    }
                }.singleOrNull()?.also {
                    it.className.toClass().asResolver().firstMethod {
                        name = it.methodName
                    }.hook {
                        replaceUnit {  }
                    }
                }
            }
            if (prefs.getBoolean("remove_weather_search_box", false)) {
                val TitleBarCommonClass = bridge.findClass {
                    matcher {
                        className = "com.heytap.browser.business.weather.titlebar.TitleBarCommon"
                    }
                }
                val searchBarMethodName = TitleBarCommonClass.findMethod {
                    matcher {
                        usingStrings("findViewById(this, R.id.search_layout_common)")
                    }
                }.singleOrNull()?.methodName
                val searchBarField = TitleBarCommonClass.findField {
                    matcher {
                        type = "com.heytap.browser.business.weather.titlebar.ThirdSearchLayout"
                    }
                }.singleOrNull()?.fieldName
                "com.heytap.browser.business.weather.titlebar.TitleBarCommon".toClass().resolve().apply {
                    firstMethod {
                        name = searchBarMethodName
                    }.hook {
                        after {
                            val titleBarInstance = instance

                            val searchLayoutField = titleBarInstance.asResolver().firstField { name = searchBarField }
                            val searchLayout = searchLayoutField.get() as RelativeLayout

                            searchLayout.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }
}
