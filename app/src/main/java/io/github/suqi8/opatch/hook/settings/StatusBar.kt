package io.github.suqi8.opatch.hook.settings

import android.view.View
import androidx.core.view.isVisible
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.android.ViewClass

class Settings: YukiBaseHooker() {
    override fun onHook() {
        loadApp(name = "com.android.settings") {
            if (prefs("settings").getBoolean("hide_status_bar", false)) {

            }
        }
    }
}
