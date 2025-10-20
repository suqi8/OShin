package com.suqi8.oshin.hook.weather

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.kavaref.extension.JInteger
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

class weather: YukiBaseHooker() {
    override fun onHook() {
        val prefs = prefs("weather")
        loadApp(name = "com.coloros.weather2") {
            "com.oplus.weather.utils.SecondaryPageUtil".toClass().resolve().apply {
                firstMethod {
                    modifiers(Modifiers.PUBLIC, Modifiers.STATIC, Modifiers.FINAL)
                    name = "startJumpToBrowser"
                    parameters("android.content.Context", String::class, Boolean::class, Boolean::class, Boolean::class, Boolean::class, Boolean::class, JInteger::class, "kotlin.jvm.functions.Function0")
                    returnType = Void.TYPE
                }.hook {
                    before {
                        val context = args[0] as Context
                        var originalUrl = args[1] as String

                        if (prefs.getBoolean("remove_second_page_ads")) {
                            originalUrl = if (originalUrl.contains("infoEnable=true")) {
                                originalUrl.replace("infoEnable=true", "infoEnable=false")
                            } else {
                                "$originalUrl&infoEnable=false"
                            }
                            args[1] = originalUrl
                        }

                        if (prefs.getBoolean("prevent_system_browser_redirect")) {
                            val redirectIntent = Intent(Intent.ACTION_VIEW, originalUrl.toUri()).apply {
                                // 添加此标志位，以便在非Activity的上下文中启动Activity
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(redirectIntent)
                            resultNull()
                        }
                    }
                }
            }

            "com.oplus.weather.utils.LocalUtils".toClass().resolve().apply {
                firstMethod {
                    modifiers(Modifiers.PUBLIC, Modifiers.STATIC)
                    name = "launchHeyTapBrowserFirst"
                    parameters("android.content.Context", Int::class, String::class, String::class, Boolean::class)
                    returnType = Void.TYPE
                }.hook {
                    before {
                        val context = args[0] as Context
                        var originalUrl = args[2] as String

                        if (prefs.getBoolean("remove_second_page_ads")) {
                            originalUrl = if (originalUrl.contains("&isNotificationGranted=")) {
                                originalUrl.replace("&isNotificationGranted=", "&infoEnable=false&isNotificationGranted=")
                            } else {
                                "$originalUrl&infoEnable=false"
                            }
                        }

                        if (prefs.getBoolean("prevent_system_browser_redirect")) {
                            val redirectIntent = Intent(Intent.ACTION_VIEW, originalUrl.toUri()).apply {
                                // 添加此标志位，以便在非Activity的上下文中启动Activity
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(redirectIntent)
                            resultNull()
                        }
                    }
                }
            }
        }
    }
}
