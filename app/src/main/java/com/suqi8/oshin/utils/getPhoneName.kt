package com.suqi8.oshin.utils

import android.annotation.SuppressLint

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
