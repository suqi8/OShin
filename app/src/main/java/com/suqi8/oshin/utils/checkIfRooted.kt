package com.suqi8.oshin.utils

fun checkIfRooted(): Boolean {
    return try {
        val process = Runtime.getRuntime().exec(arrayOf("which", "su"))
        val result = process.waitFor()
        result == 0
    } catch (e: Exception) {
        false
    }
}
