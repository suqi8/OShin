package com.suqi8.oshin.utils

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

fun executeCommand(command: String): String {
    return try {
        // 使用 "sh -c" 或 "su -c" 来执行命令
        // "su -c" 需要Root权限
        val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))

        // 读取命令的输出
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val output = reader.readText()

        // 等待命令执行完毕
        process.waitFor()
        reader.close()

        // 返回去除首尾空白的输出结果
        output.trim()
    } catch (e: Exception) {
        // 如果出错，记录日志并返回空字符串
        Log.e(com.suqi8.oshin.TAG, "executeCommand failed for: $command", e)
        ""
    }
}
