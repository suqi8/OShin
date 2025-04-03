package com.suqi8.oshin.ui.activity.funlistui

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.factory.prefs
import com.hjq.permissions.Permission
import com.suqi8.oshin.GetAppIconAndName
import com.suqi8.oshin.R
import com.suqi8.oshin.colorCache
import com.suqi8.oshin.drawColoredShadow
import com.suqi8.oshin.getAutoColor
import com.suqi8.oshin.utils.requestPermissions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.extra.CheckboxLocation
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperCheckbox
import top.yukonga.miuix.kmp.theme.MiuixTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FunAppSele(title: String, summary: String? = null, category: String, key: String, onCheckedChange: ((Int) -> Unit)? = null) {
    val context = LocalContext.current
    val seleappList = remember { mutableStateOf(context.prefs(category).getString(key, "")) }
    val showAppListSele = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            requestPermissions(context, arrayOf(Permission.GET_INSTALLED_APPS)) {}
        }
    }
    SuperArrow(
        title = title,
        summary = if (summary == null) stringResource(R.string.selected_app) + seleappList.value else summary + "\n" + stringResource(R.string.selected_app) + seleappList.value,
        onClick = {
            showAppListSele.value = true
        }
    )
    if (showAppListSele.value) {
        val appList = remember { mutableStateOf(listOf<Pair<String, String>>()) }
        getApps(context, isSystemApp = false) { appInfo ->
            appList.value += appInfo
        }
        ModalBottomSheet(
            onDismissRequest = { showAppListSele.value = false },
            sheetState = sheetState,
            containerColor = MiuixTheme.colorScheme.surface,
        ) {
            LazyColumn {
                items(appList.value) { app ->
                    ResetAppList(app.second, isChecked = seleappList.value.contains(app.second)) { isChecked ->
                        seleappList.value = if (isChecked) {
                            (seleappList.value
                                .split(",") // 先转换为 List
                                .filter { it.isNotEmpty() } // 过滤掉空值
                                .toMutableList()
                                .apply { add(app.second) } // 添加新的 packageName
                                    ).joinToString(",") // 重新转换为 String
                        } else {
                            (seleappList.value
                                .split(",") // 转换为 List
                                .filter { it.isNotEmpty() } // 过滤掉空值
                                .toMutableList()
                                .apply { remove(app.second) } // 移除目标 packageName
                                    ).joinToString(",") // 重新转换为 String
                        }
                        context.prefs(category).edit {
                            putString(key, seleappList.value)
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ResetAppList(packageName: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    GetAppIconAndName(packageName = packageName) { appName, icon ->
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
                SuperCheckbox(
                    title = appName,
                    checked = isChecked,
                    onCheckedChange = {
                        onCheckedChange(it)
                    },
                    summary = packageName,
                    checkboxLocation = CheckboxLocation.Right
                )
            }
        }
    }
}

fun getApps(
    context: Context,
    isSystemApp: Boolean,
    onAppAdded: (Pair<String, String>) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        val packageManager = context.packageManager
        val allApps = packageManager.getInstalledApplications(0)

        for (app in allApps) {
            val appName = packageManager.getApplicationLabel(app).toString()
            val packageName = app.packageName
            val isSystem = (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0

            if (isSystem == isSystemApp) {
                withContext(Dispatchers.Main) {
                    onAppAdded(Pair(appName, packageName))
                }
            }
        }
    }
}
