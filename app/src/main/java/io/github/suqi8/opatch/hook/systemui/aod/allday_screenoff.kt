package io.github.suqi8.opatch.hook.systemui.aod

import android.provider.Settings
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.method

class allday_screenoff: YukiBaseHooker() {
    override fun onHook() {
        if (prefs("settings").getBoolean("enable_all_day_screen_off", false)) {
            "com.oplus.systemui.aod.display.SmoothTransitionController".toClass().apply {
                method {
                    name = "shouldWindowBeTransparent"
                }.hook {
                    after {
                        resultTrue()
                    }
                }
            }
        }
        if (prefs("settings").getBoolean("force_trigger_ltpo", false)) {
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
