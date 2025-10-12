package com.suqi8.oshin.utils

import android.content.Context

// 判断是否存在桌面快捷方式
fun hasShortcut(context: Context, pkg: String): Boolean {
    val pm = context.packageManager
    return try {
        val launchIntent = pm.getLaunchIntentForPackage(pkg)
        launchIntent != null
    } catch (_: Exception) {
        false
    }
}
