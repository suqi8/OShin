package io.github.suqi8.opatch.ui.tools

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import io.github.suqi8.opatch.R
import io.github.suqi8.opatch.saveDeviceName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.showDialog

class resetApp {
    @Composable
    fun AppRestartScreen(appList: List<String>,showresetAppDialog: MutableState<Boolean>) {
        if (showresetAppDialog.value) {
            ConfirmationDialog(
                appPackage = appList.joinToString(separator = "\n"),
                onConfirm = {
                    appList.forEach {
                        restartApp(it)
                    }
                    dismissDialog()
                    showresetAppDialog.value = false
                },
                show = showresetAppDialog,
                onDismiss = { showresetAppDialog.value = false
                dismissDialog()
                }
            )
        }
    }

    @SuppressLint("UnrememberedMutableState")
    @Composable
    fun ConfirmationDialog(appPackage: String,show: MutableState<Boolean>, onConfirm: () -> Unit, onDismiss: () -> Unit) {
        if (!show.value) return
        showDialog(content = {
            SuperDialog(title = stringResource(R.string.Researt_app),
                show = show,
                onDismissRequest = {
                    onDismiss()
                }, summary = """${stringResource(R.string.confirm_restart_applications)}
                    |$appPackage""".trimMargin()) {
                /*Text(text = """${stringResource(R.string.confirm_restart_applications)}
                    |$appPackage""".trimMargin())
                Spacer(Modifier.height(12.dp))*/
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.cancel),
                        onClick = {
                            onDismiss()
                        }
                    )
                    Spacer(Modifier.width(12.dp))
                    Button(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.ok),
                        submit = true,
                        onClick = {
                            onConfirm()
                        }
                    )
                }
            }
        })
    }

    private fun restartApp(packageName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val command = "pkill -f " + packageName
                // 使用 su 执行命令
                val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))

                // 等待命令执行完成
                process.waitFor()
            } catch (e: Exception) {
                e.printStackTrace()
                // 处理错误
            }
        }
    }
}
