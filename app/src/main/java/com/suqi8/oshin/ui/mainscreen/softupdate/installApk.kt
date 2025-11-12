package com.suqi8.oshin.ui.mainscreen.softupdate

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import com.suqi8.oshin.R
import com.suqi8.oshin.utils.checkIfRooted
import com.suqi8.oshin.utils.executeCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@SuppressLint("QueryPermissionsNeeded")
fun installApk(
    context: Context,
    apkFile: File,
    launcher: ActivityResultLauncher<Intent>,
    scope: CoroutineScope
) {
    val TAG = "InstallApk"
    Log.d(TAG, "开始安装流程: ${apkFile.absolutePath}")

    if (checkIfRooted()) {
        scope.launch(Dispatchers.IO) {
            handleRootInstall(context, apkFile, launcher, TAG)
        }
    } else {
        scope.launch(Dispatchers.Main) {
            fallbackInstall(context, apkFile, launcher)
        }
    }
}

private suspend fun handleRootInstall(
    context: Context,
    apkFile: File,
    launcher: ActivityResultLauncher<Intent>,
    TAG: String
) {
    val originalPath = apkFile.absolutePath
    val tempDir = "/data/local/tmp"
    val tempPath = "$tempDir/${apkFile.name}"
    val mainActivityName = "com.suqi8.oshin.MainActivity"
    val packageName = context.packageName

    var operationFailed = false
    var errorMessage = context.getString(R.string.install_error_root_unknown) // 修改

    try {
        Log.d(TAG, "尝试复制 APK 到临时目录")
        val copyCommand = "cp \"$originalPath\" \"$tempPath\""
        val copyOutput = executeCommand(copyCommand)
        Log.d(TAG, "复制命令输出: $copyOutput")

        Log.d(TAG, "尝试安装并启动应用")
        val installAndStartCommand = "pm install -r \"$tempPath\" && am start -n $packageName/$mainActivityName"
        val installOutput = executeCommand(installAndStartCommand)
        Log.d(TAG, "安装并启动命令输出: $installOutput")

        if (installOutput.contains("Failure", ignoreCase = true)) {
            Log.e(TAG, "安装命令报告失败")
            errorMessage = if (installOutput.isNotBlank()) {
                context.getString(R.string.install_error_root_output, installOutput) // 修改
            } else {
                context.getString(R.string.install_error_root_no_output) // 修改
            }
            operationFailed = true
        } else {
            Log.d(TAG, "安装并启动命令已发送 (假设成功)")
        }

    } catch (e: Exception) {
        Log.e(TAG, "Root 操作序列异常", e)
        errorMessage = context.getString(R.string.install_error_root_exception, e.message ?: "") // 修改
        operationFailed = true
    } finally {
        // 清理临时文件
        Log.d(TAG, "尝试清理临时文件")
        try {
            if (checkIfRooted()) {
                val removeCommand = "rm -f \"$tempPath\""
                executeCommand(removeCommand)
                Log.d(TAG, "清理命令已发送")
            }
        } catch (e: Exception) {
            Log.e(TAG, "清理临时文件失败", e)
        }

        // 如果 Root 安装失败，回退到系统安装器
        if (operationFailed) {
            Log.w(TAG, "Root 操作失败，回退到系统安装器")
            withContext(Dispatchers.Main) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                fallbackInstall(context, apkFile, launcher)
            }
        }
    }
}


fun fallbackInstall(
    context: Context,
    apkFile: File,
    launcher: ActivityResultLauncher<Intent>
) {
    val TAG = "FallbackInstall"
    Log.d(TAG, "开始 Fallback 安装: ${apkFile.absolutePath}")
    try {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            apkFile
        )
        Log.d(TAG, "生成 Content URI: $uri")

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        Log.d(TAG, "启动安装 Intent...")
        launcher.launch(intent)
        Log.d(TAG, "安装 Intent 已启动")
    } catch (e: Exception) {
        Log.e(TAG, "启动安装 Intent 失败", e)
        Toast.makeText(context, context.getString(R.string.install_error_fallback, e.message ?: ""), Toast.LENGTH_SHORT).show() // 修改
    }
}
