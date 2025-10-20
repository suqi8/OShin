package com.suqi8.oshin.hook.weather

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker


class RemoveAds: YukiBaseHooker() {
    override fun onHook() {
        val prefs = prefs("weather")
        loadApp(name = "com.coloros.weather2") {
            if (prefs.getBoolean("remove_second_page_ads")) {
                "com.oplus.weather.utils.SecondaryPageUtil".toClass().resolve().apply {
                    firstMethod {
                        name = "newLink"
                    }.hook {
                        after {
                            var originalUrl = result.toString()
                            if (originalUrl.contains("infoEnable=true")) {
                                originalUrl = originalUrl.replace("infoEnable=true", "infoEnable=false")
                            } else if (!originalUrl.contains("infoEnable=")) {
                                originalUrl = "$originalUrl&infoEnable=false"
                            }
                            result = originalUrl
                        }
                    }
                }

                "com.oplus.weather.utils.LocalUtils".toClass().resolve().apply {
                    firstMethod {
                        name = "getH5StringBuffer"
                    }.hook {
                        before {
                            val url = StringBuffer("${args[0].toString()}&infoEnable=false&frontCode=2.0")
                            result = url
                        }
                    }
                }
            }
        }
    }
}
