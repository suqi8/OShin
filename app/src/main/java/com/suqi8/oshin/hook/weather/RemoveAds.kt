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
                            }
                            originalUrl = originalUrl.replace(
                                "&isNotificationGranted=",
                                "&infoEnable=false&isNotificationGranted="
                            )
                            result = originalUrl
                        }
                    }
                }

                "com.oplus.weather.utils.LocalUtils".toClass().resolve().apply {
                    firstMethod {
                        name = "getH5StringBuffer"
                    }.hook {
                        before {
                            var url = args[0].toString()
                            url = if (url.contains("&isNotificationGranted=")) {
                                url.replace("&isNotificationGranted=", "&infoEnable=false&isNotificationGranted=")
                            } else {
                                "$url&infoEnable=false"
                            }
                            result = StringBuffer("$url&frontCode=2.0")
                        }
                    }
                }
            }
        }
    }
}
