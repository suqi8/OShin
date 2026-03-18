package com.suqi8.oshin.utils

import android.util.Log
import com.suqi8.oshin.TAG
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

fun executeCommand(command: String): String {
    var process: Process? = null
    val outputStringBuilder = StringBuilder() // 用于收集标准输出 (stdout)
    val errorStringBuilder = StringBuilder()  // 用于收集标准错误 (stderr)
    var exitCode: Int

    try {
        Log.d(TAG, "Executing command with su: $command")
        process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))

        // 读取标准输出 (stdout)
        BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
            reader.forEachLine { outputStringBuilder.append(it).append("\n") }
        }

        // 读取标准错误 (stderr)
        BufferedReader(InputStreamReader(process.errorStream)).use { reader ->
            reader.forEachLine { errorStringBuilder.append(it).append("\n") }
        }

        // --- 等待并获取结果 ---
        val finished = process.waitFor(1, TimeUnit.MINUTES) // 等待最多1分钟

        if (finished) {
            exitCode = process.exitValue()
            Log.d(TAG, "Command finished with exit code: $exitCode")

            // --- 打印错误流（如果非空）---
            val errorOutput = errorStringBuilder.toString().trim()
            if (errorOutput.isNotEmpty()) {
                Log.e(TAG, "Command stderr:\n$errorOutput")
            }
            // --- 打印标准输出（如果非空，主要用于调试确认）---
            val standardOutput = outputStringBuilder.toString().trim()
            if (standardOutput.isNotEmpty()) {
                Log.d(TAG, "Command stdout:\n$standardOutput")
            }

        } else {
            Log.e(TAG, "Command timed out: $command")
            process.destroy() // 销毁超时的进程
            // 对于超时的旧行为是返回 "", 这里保持一致或记录错误后返回 ""
            return "" // 或者可以抛出异常，但为了兼容性返回 ""
        }

    } catch (e: Exception) {
        Log.e(TAG, "executeCommand failed for: $command", e)
        process?.destroy()
        return "" // 保持原有错误行为：返回空字符串
    } finally {
        // 不需要手动关闭流，BufferedReader.use 会处理
        process?.destroy() // 确保进程被销毁
    }

    // --- 保持原有返回值：只返回标准输出 ---
    return outputStringBuilder.toString().trim()
}
