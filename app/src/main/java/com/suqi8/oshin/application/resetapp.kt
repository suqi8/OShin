package com.suqi8.oshin.application

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context

@SuppressLint("ServiceCast")
fun RestartApp(context: Context, packageName: String) {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    activityManager.killBackgroundProcesses(packageName)

    // 重启应用
    val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
    context.startActivity(launchIntent)
}
