package com.suqi8.oshin.utils

import android.content.Context
import com.suqi8.oshin.GetAppName1
import com.suqi8.oshin.R

fun GetFuncRoute(route: String, context: Context): String {
    var FuncRoute = route.replace("\\", " › ")
    FuncRoute = FuncRoute.replace("package_manager_services", context.getString(R.string.package_manager_services))
    FuncRoute = FuncRoute.replace("oplus_system_services", context.getString(R.string.oplus_system_services))
    FuncRoute = FuncRoute.replace("split_screen_multi_window", context.getString(R.string.split_screen_multi_window))
    FuncRoute = FuncRoute.replace("status_bar_clock", context.getString(R.string.status_bar_clock))
    FuncRoute = FuncRoute.replace("hardware_indicator", context.getString(R.string.hardware_indicator))
    FuncRoute = FuncRoute.replace("statusbar_icon", context.getString(R.string.status_bar_icon))
    FuncRoute = FuncRoute.replace("notification", context.getString(R.string.status_bar_notification))
    FuncRoute = FuncRoute.replace("status_bar_wifi", context.getString(R.string.network_speed_indicator))
    FuncRoute = FuncRoute.replace("recent_task", context.getString(R.string.recent_tasks))
    FuncRoute = FuncRoute.replace("feature", context.getString(R.string.feature))
    FuncRoute = FuncRoute.replace("android", GetAppName1("android",context))
    FuncRoute = FuncRoute.replace("systemui", GetAppName1("com.android.systemui",context))
    FuncRoute = FuncRoute.replace("settings", GetAppName1("com.android.settings",context))
    FuncRoute = FuncRoute.replace("launcher", GetAppName1("com.android.launcher",context))
    FuncRoute = FuncRoute.replace("battery", GetAppName1("com.oplus.battery",context))
    FuncRoute = FuncRoute.replace("speechassist", GetAppName1("com.heytap.speechassist",context))
    FuncRoute = FuncRoute.replace("ocrscanner", GetAppName1("com.coloros.ocrscanner",context))
    FuncRoute = FuncRoute.replace("games", GetAppName1("com.oplus.games",context))
    FuncRoute = FuncRoute.replace("wallet", GetAppName1("com.finshell.wallet",context))
    FuncRoute = FuncRoute.replace("oplusphonemanager", GetAppName1("com.oplus.phonemanager",context))
    FuncRoute = FuncRoute.replace("phonemanager", GetAppName1("com.coloros.phonemanager",context))
    FuncRoute = FuncRoute.replace("mms", GetAppName1("com.android.mms",context))
    FuncRoute = FuncRoute.replace("securepay", GetAppName1("com.coloros.securepay",context))
    FuncRoute = FuncRoute.replace("mihealth", GetAppName1("com.mi.health",context))
    FuncRoute = FuncRoute.replace("health", GetAppName1("com.heytap.health",context))
    FuncRoute = FuncRoute.replace("appdetail", GetAppName1("com.oplus.appdetail",context))
    FuncRoute = FuncRoute.replace("quicksearchbox", GetAppName1("com.heytap.quicksearchbox",context))

    return FuncRoute
}
