package com.suqi8.oshin.hook.com.android.systemui.StatusBar

import android.view.View
import androidx.core.view.isVisible
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method

class Icon: YukiBaseHooker() {
    override fun onHook() {
        if (prefs("systemui\\statusbar_icon").getBoolean("statusbar_icon", false)) {
            loadApp(name = "com.android.systemui") {
                "com.android.systemui.statusbar.StatusBarWifiView".toClass().apply {
                    if (prefs("systemui\\statusbar_icon").getInt("show_Wifi_icon",0) == 1) {
                        method {
                            name = "initViewState"
                        }.hook {
                            before {
                                val wifiView = instance<View>()
                                field {
                                    name = "mWifiIcon"
                                }.get(wifiView).cast<View>()?.isVisible = false
                            }
                        }
                    }
                    if (prefs("systemui\\statusbar_icon").getInt("icon_show_Wifi_arrow",0) == 1) {
                        method{
                            name = "updateState"
                        }.hook{
                            after {
                                field {
                                    name = "mIn"
                                }.get(instance).cast<View>()?.visibility = View.GONE
                                field {
                                    name = "mOut"
                                }.get(instance).cast<View>()?.visibility = View.GONE
                            }
                        }
                    }
                }
                if (prefs("systemui\\statusbar_icon").getInt("show_Wifi_arrow",0) == 1) {
                    "com.oplus.systemui.statusbar.phone.signal.OplusStatusBarWifiViewExImpl".toClass().apply {
                        method {
                            name = "updateState"
                        }.hook {
                            before {
                                val mWifiActivity = instance<View>()
                                field{
                                    name = "mWifiActivity"
                                }.get(mWifiActivity).cast<View>()?.isVisible = false
                            }
                        }
                        method {
                            name = "initViewState"
                        }.hook {
                            before {
                                val mWifiActivity = instance<View>()
                                field{
                                    name = "mWifiActivity"
                                }.get(mWifiActivity).cast<View>()?.isVisible = false
                            }
                        }
                    }
                }
            }
        }
    }
}
