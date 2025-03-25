package com.suqi8.oshin.hook.com.android.systemui

import android.provider.Settings
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType

class allday_screenoff: YukiBaseHooker() {
    override fun onHook() {
        loadApp(name = "com.android.systemui") {
            if (prefs("systemui").getBoolean("enable_all_day_screen_off", false)) {
                "com.oplus.systemui.aod.display.SmoothTransitionController".toClass().apply {
                    method {
                        name = "shouldWindowBeTransparent"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("systemui").getBoolean("force_trigger_ltpo", false)) {
                "com.oplus.systemui.aod.display.BaseDisplayUtil".toClass().apply {
                    constructor().hook {
                        before {
                            Settings.Secure.putInt(
                                appContext!!.contentResolver,
                                "Setting_AodClockModeOriginalType_ONEHZ",
                                1
                            )
                        }
                    }
                }
            }
        }
    }
}
