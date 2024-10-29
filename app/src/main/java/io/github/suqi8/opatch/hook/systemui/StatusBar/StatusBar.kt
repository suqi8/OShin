package io.github.suqi8.opatch.hook.systemui.StatusBar

import android.view.View
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.android.ViewClass

class StatusBar: YukiBaseHooker() {
    override fun onHook() {
        if (prefs("settings").getBoolean("hide_status_bar", false)) {
            loadApp(name = "com.android.systemui") {
                "com.android.systemui.statusbar.phone.fragment.CollapsedStatusBarFragment".toClass().apply {
                    method {
                        name = "onViewCreated"
                        param(ViewClass, BundleClass)
                    }.hook {
                        after {
                            val view = args[0] as View
                            view.visibility = View.INVISIBLE
                        }
                    }
                }
            }
        }
    }
}
