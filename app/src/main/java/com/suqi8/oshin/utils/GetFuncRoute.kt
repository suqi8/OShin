package com.suqi8.oshin.utils

import android.content.Context
import com.suqi8.oshin.R

fun GetFuncRoute(route: String, context: Context): String {
    val appNameCache = AppNameCache(context)
    val replacements = mapOf(
        "\\" to " › ",
        "package_manager_services" to context.getString(R.string.package_manager_services),
        "oplus_system_services" to context.getString(R.string.oplus_system_services),
        "split_screen_multi_window" to context.getString(R.string.split_screen_multi_window),
        "status_bar_clock" to context.getString(R.string.status_bar_clock),
        "hardware_indicator" to context.getString(R.string.hardware_indicator),
        "statusbar_icon" to context.getString(R.string.status_bar_icon),
        "notification" to context.getString(R.string.status_bar_notification),
        "status_bar_wifi" to context.getString(R.string.network_speed_indicator),
        "recent_task" to context.getString(R.string.recent_tasks),
        "feature" to context.getString(R.string.feature),
        "android" to appNameCache.getAppName("android"),
        "systemui" to appNameCache.getAppName("com.android.systemui"),
        "settings" to appNameCache.getAppName("com.android.settings"),
        "launcher" to appNameCache.getAppName("com.android.launcher"),
        "battery" to appNameCache.getAppName("com.oplus.battery"),
        "speechassist" to appNameCache.getAppName("com.heytap.speechassist"),
        "ocrscanner" to appNameCache.getAppName("com.coloros.ocrscanner"),
        "games" to appNameCache.getAppName("com.oplus.games"),
        "wallet" to appNameCache.getAppName("com.finshell.wallet"),
        "oplusphonemanager" to appNameCache.getAppName("com.oplus.phonemanager"),
        "phonemanager" to appNameCache.getAppName("com.coloros.phonemanager"),
        "mms" to appNameCache.getAppName("com.android.mms"),
        "securepay" to appNameCache.getAppName("com.coloros.securepay"),
        "mihealth" to appNameCache.getAppName("com.mi.health"),
        "health" to appNameCache.getAppName("com.heytap.health"),
        "appdetail" to appNameCache.getAppName("com.oplus.appdetail"),
        "quicksearchbox" to appNameCache.getAppName("com.heytap.quicksearchbox"),
        "ota" to appNameCache.getAppName("com.oplus.ota")
    )

    // 使用正则表达式和替换映射表进行替换
    var FuncRoute = route
    replacements.forEach { (key, value) ->
        FuncRoute = FuncRoute.replace(key, value)
    }

    return FuncRoute
}
