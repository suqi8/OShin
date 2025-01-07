package com.suqi8.oshin.ui.tools

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.suqi8.oshin.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog

class resetApp {
    @Composable
    fun AppRestartScreen(appList: List<String>, showresetAppDialog: MutableState<Boolean>) {
        if (showresetAppDialog.value) {
            ConfirmationDialog(
                appPackage = appList.joinToString(separator = "\n"),
                onConfirm = {
                    appList.forEach {
                        restartApp(it)
                    }
                    dismissDialog(showresetAppDialog)
                },
                show = showresetAppDialog,
                onDismiss = {
                    dismissDialog(showresetAppDialog)
                }
            )
        }
    }

    @SuppressLint("UnrememberedMutableState")
    @Composable
    fun ConfirmationDialog(
        appPackage: String,
        show: MutableState<Boolean>,
        onConfirm: () -> Unit,
        onDismiss: () -> Unit
    ) {
        if (!show.value) return
        SuperDialog(
            title = stringResource(R.string.Researt_app),
            show = show,
            onDismissRequest = {
                onDismiss()
            }, summary = """${stringResource(R.string.confirm_restart_applications)}
                    |$appPackage""".trimMargin()
        ) {
            /*Text(text = """${stringResource(R.string.confirm_restart_applications)}
                |$appPackage""".trimMargin())
            Spacer(Modifier.height(12.dp))*/
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.cancel),
                    onClick = {
                        onDismiss()
                    }
                )
                Spacer(Modifier.width(12.dp))
                TextButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.ok),
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                    onClick = {
                        onConfirm()
                    }
                )
            }
        }
    }

    private fun restartApp(packageName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (packageName == "android") {
                    val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "reboot"))
                    process.waitFor()
                } else {
                    val command = "pkill -f " + packageName
                    // 使用 su 执行命令
                    val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))

                    // 等待命令执行完成
                    process.waitFor()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // 处理错误
            }
        }
    }
}
