package com.suqi8.oshin.utils

import android.content.Context
import android.content.Intent

fun launchApp(context: Context, pkg: String) {
    val pm = context.packageManager
    val intent = pm.getLaunchIntentForPackage(pkg)
    if (intent != null) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
