package com.suqi8.oshin.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.ui.graphics.ImageBitmap
//dataclass
data class AppInfo(
    val name: String,
    val icon: ImageBitmap
)
//functions
fun checkIfRooted(): Boolean {
    return try {
        val process = Runtime.getRuntime().exec(arrayOf("which", "su"))
        process.waitFor() == 0
    } catch (e: Exception) {
        false
    }
}


fun toast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
fun launchApp(context: Context, pkg: String) {
    val pm = context.packageManager
    val intent = pm.getLaunchIntentForPackage(pkg)
    if (intent != null) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
fun hasShortcut(context: Context, pkg: String): Boolean {
    val pm = context.packageManager
    return try {
        pm.getLaunchIntentForPackage(pkg) != null
    } catch (_: Exception) {
        false
    }
}
@SuppressLint("PrivateApi")
fun getPhoneName(): String {
    return try {
        val clazz = Class.forName("android.os.SystemProperties")
        val method = clazz.getMethod("get", String::class.java, String::class.java)
        method.invoke(null, "ro.vendor.oplus.market.name", "OPlus") as String
    } catch (_: Exception) {
        "OPlus"
    }
}
