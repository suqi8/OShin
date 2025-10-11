package com.suqi8.oshin.features

import com.suqi8.oshin.features.android.OplusServices
import com.suqi8.oshin.features.android.PMS
import com.suqi8.oshin.features.android.SplitScreenMultiWindow
import com.suqi8.oshin.features.android.android
import com.suqi8.oshin.features.incallui.incallui
import com.suqi8.oshin.features.launcher.RecentTask
import com.suqi8.oshin.features.launcher.launcher
import com.suqi8.oshin.features.mms.mms
import com.suqi8.oshin.features.ocrscanner.ocrscanner
import com.suqi8.oshin.features.oshare.oshare
import com.suqi8.oshin.features.phone.phone
import com.suqi8.oshin.features.phonemanager.phonemanager
import com.suqi8.oshin.features.securepay.securepay
import com.suqi8.oshin.features.settings.settings
import com.suqi8.oshin.features.systemui.ControlCenter
import com.suqi8.oshin.features.systemui.HardwareIndicator
import com.suqi8.oshin.features.systemui.StatusBarClock
import com.suqi8.oshin.features.systemui.StatusBarWifi
import com.suqi8.oshin.features.systemui.notification
import com.suqi8.oshin.features.systemui.systemui
import com.suqi8.oshin.features.wallet.wallet
import com.suqi8.oshin.models.ModuleEntry
import com.suqi8.oshin.models.PageDefinition

object FeatureRegistry {
    /**
     * 主模块页面的应用入口列表。
     */
    val moduleEntries = listOf(
        ModuleEntry("android", "android"),
        ModuleEntry("com.android.systemui", "systemui"),
        ModuleEntry("com.android.incallui", "incallui"),
        ModuleEntry("com.android.settings", "settings"),
        ModuleEntry("com.android.phone", "phone"),
        ModuleEntry("com.android.mms", "mms"),
        ModuleEntry("com.android.launcher", "launcher"),
        ModuleEntry("com.coloros.ocrscanner", "ocrscanner"),
        ModuleEntry("com.coloros.oshare", "oshare"),
        ModuleEntry("com.coloros.phonemanager", "phonemanager"),
        ModuleEntry("com.coloros.securepay", "securepay"),
        ModuleEntry("com.finshell.wallet", "wallet")
    )

    /**
     * 所有功能页面的详细定义。
     * Key: 页面路由ID (与 ModuleEntry.routeId 对应)
     * Value: 页面的完整定义
     */
    val screenMap: Map<String, PageDefinition> = mapOf(
        "android" to android.definition,
        "android\\package_manager_services" to PMS.definition,
        "android\\oplus_system_services" to OplusServices.definition,
        "android\\split_screen_multi_window" to SplitScreenMultiWindow.definition,

        "systemui" to systemui.definition,
        "systemui\\controlCenter" to ControlCenter.definition,
        "systemui\\hardware_indicator" to HardwareIndicator.definition,
        "systemui\\notification" to notification.definition,
        "systemui\\status_bar_clock" to StatusBarClock.definition,
        "systemui\\status_bar_wifi" to StatusBarWifi.definition,

        "incallui" to incallui.definition,

        "settings" to settings.definition,

        "phone" to phone.definition,

        "mms" to mms.definition,

        "launcher" to launcher.definition,
        "launcher\\recent_task" to RecentTask.definition,

        "ocrscanner" to ocrscanner.definition,

        "oshare" to oshare.definition,

        "phonemanager" to phonemanager.definition,

        "securepay" to securepay.definition,

        "wallet" to wallet.definition
    )
}
