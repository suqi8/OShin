package com.suqi8.oshin.ui.tools

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.GetAppIconAndName
import com.suqi8.oshin.R
import com.suqi8.oshin.drawColoredShadow
import com.suqi8.oshin.getautocolor
import com.suqi8.oshin.ui.activity.funlistui.addline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog

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
                val context = LocalContext.current
                val auto_color = context.prefs("settings").getBoolean("auto_color", true)
                val defaultColor = MiuixTheme.colorScheme.primary
                val dominantColor: MutableState<Color> = remember { mutableStateOf(defaultColor) }
                val isLoading = remember { mutableStateOf(true) }

                LaunchedEffect(icon) {

                    withContext(Dispatchers.IO) {
                        if (auto_color) dominantColor.value = getautocolor(icon)
                        isLoading.value = false
                    }

                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isLoading.value) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.padding(start = 16.dp)) {
                            Text(text = appName)
                            SmallTitle(text = packageName, insideMargin = PaddingValues(0.dp, 0.dp))
                        }
                    } else {
                        Card(
                            color = if (YukiHookAPI.Status.isModuleActive) dominantColor.value else MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier
                                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                                .drawColoredShadow(
                                    if (YukiHookAPI.Status.isModuleActive) dominantColor.value else MaterialTheme.colorScheme.errorContainer,
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
                        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.padding(start = 16.dp)) {
                            Text(text = appName)
                            SmallTitle(text = packageName, insideMargin = PaddingValues(0.dp, 0.dp))
                        }
                    }
                }
            } else {
                Text(text = "$packageName 没有安装", modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 4.dp))
            }
        }
    }
}
