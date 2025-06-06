package com.suqi8.oshin.ui.activity.funlistui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.highcapable.yukihookapi.YukiHookAPI
import com.suqi8.oshin.R
import com.suqi8.oshin.utils.GetAppIconAndName
import com.suqi8.oshin.utils.drawColoredShadow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme

class resetApp {
    @Composable
    fun AppRestartScreen(appList: List<String>, showresetAppDialog: MutableState<Boolean>) {
        if (showresetAppDialog.value) {
            ConfirmationDialog(
                appPackage = appList,
                onConfirm = {
                    appList.forEach {
                        restartApp(it)
                    }
                    showresetAppDialog.value = false
                },
                show = showresetAppDialog,
                onDismiss = {
                    showresetAppDialog.value = false
                }
            )
        }
    }

    @SuppressLint("UnrememberedMutableState")
    @Composable
    fun ConfirmationDialog(
        appPackage: List<String>,
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
            }, summary = stringResource(R.string.confirm_restart_applications)
        ) {
            /*Text(text = """${stringResource(R.string.confirm_restart_applications)}
                |$appPackage""".trimMargin())
            Spacer(Modifier.height(12.dp))*/
            LazyColumn {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        color = MiuixTheme.colorScheme.background
                    ) {
                        appPackage.forEachIndexed { index, it ->
                            ResetAppList(it)
                            if (index < appPackage.size - 1) {
                                addline()
                            }
                        }
                    }
                }
            }
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

    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    fun ResetAppList(packageName: String) {
        GetAppIconAndName(packageName = packageName) { appName, icon ->
            if (appName != "noapp") {
                val defaultColor = MiuixTheme.colorScheme.primary

                // 使用 remember 缓存 dominantColor 的状态
                val dominantColor = remember { mutableStateOf(colorCache[packageName] ?: defaultColor) }
                val isLoading = remember { mutableStateOf(dominantColor.value == defaultColor) }

                // 使用 LaunchedEffect 在 icon 或 dominantColor 变化时启动协程
                LaunchedEffect(icon, dominantColor.value) {
                    if (isLoading.value) {
                        val newColor = withContext(Dispatchers.Default) {
                            if (!YukiHookAPI.Status.isModuleActive) defaultColor else getAutoColor(icon)
                        }
                        dominantColor.value = newColor
                        colorCache[packageName] = newColor
                        isLoading.value = false
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!isLoading.value) {
                        Card(
                            color = dominantColor.value,
                            modifier = Modifier
                                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                                .drawColoredShadow(
                                    dominantColor.value,
                                    1f,
                                    borderRadius = 13.dp,
                                    shadowRadius = 7.dp,
                                    offsetX = 0.dp,
                                    offsetY = 0.dp,
                                    roundedRect = false
                                )
                        ) {
                            Image(bitmap = icon, contentDescription = "App Icon", modifier = Modifier.size(45.dp))
                        }
                        Column(modifier = Modifier.padding(start = 16.dp)) {
                            Text(text = appName)
                            Text(
                                text = packageName,
                                fontSize = MiuixTheme.textStyles.subtitle.fontSize,
                                fontWeight = FontWeight.Medium,
                                color = MiuixTheme.colorScheme.onBackgroundVariant
                            )
                        }
                    }
                }
            } else {
                Text(text = "$packageName 没有安装", modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 4.dp))
            }
        }
    }
}
