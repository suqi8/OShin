package com.suqi8.oshin.ui.activity.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.highcapable.yukihookapi.YukiHookAPI
import com.hjq.permissions.permission.PermissionLists
import com.suqi8.oshin.R
import com.suqi8.oshin.utils.GetAppIconAndName
import com.suqi8.oshin.utils.drawColoredShadow
import com.suqi8.oshin.utils.requestPermissions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.extra.CheckboxLocation
import top.yukonga.miuix.kmp.extra.SuperCheckbox
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun FunAppSele(
    title: String,
    summary: String?,
    selectedApps: Set<String>,
    onSelectionChanged: (Set<String>) -> Unit
) {
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }

    // 获取所有应用信息 (这部分可以在 ViewModel 中完成以优化性能，但放在这里也可用)
    val appList = remember { mutableStateOf(listOf<Pair<String, String>>()) }

    val loadApps = {
        // 只有在列表为空时才加载，避免重复加载
        if (appList.value.isEmpty()) {
            // 在协程中异步加载
            CoroutineScope(Dispatchers.Default).launch {
                getApps(
                    context
                ) { appInfo ->
                    appList.value += appInfo
                }
            }
        }
        showDialog.value = true
    }

    val summaryText = summary ?: "${stringResource(R.string.selected_app)}: ${selectedApps.size}"

    FunArrow(
        title = title,
        summary = summaryText,
        onClick = {
            requestPermissions(
                context,
                PermissionLists.getGetInstalledAppsPermission(), // Android 11+ 需要此权限
                onGranted = {
                    // 权限获取成功后，执行加载应用和显示对话框的逻辑
                    loadApps()
                }
            )
        }
    )

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val maxHeightDp = with(density) { (configuration.screenHeightDp.dp.toPx() * 0.85f).toDp() }

    SuperDialog(
        show = showDialog,
        onDismissRequest = { showDialog.value = false }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = maxHeightDp)
                .animateContentSize() // 自动调整高度，并带有动画
        ) {
            Column {
                val searchText = remember { mutableStateOf("") }

                // 1. 添加 TextField 用于搜索
                TextField(
                    value = searchText.value,
                    onValueChange = { searchText.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )

                // 2. 派生状态，用于高效地处理搜索和排序
                val filteredAndSortedList by remember(searchText.value, appList.value, selectedApps) {
                    derivedStateOf {
                        appList.value
                            .filter { (appName, pkgName) ->
                                // 搜索逻辑：匹配应用名或包名
                                appName.contains(searchText.value, ignoreCase = true) ||
                                        pkgName.contains(searchText.value, ignoreCase = true)
                            }
                            // 3. 核心逻辑：使用 sortedByDescending 将选中的项置顶
                            .sortedByDescending { (_, pkgName) ->
                                selectedApps.contains(pkgName)
                            }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .animateContentSize() // 列表高度变化时，带动画
                        .overScrollVertical()
                ) {
                    items(filteredAndSortedList, key = { it.second }) { app ->
                        val isChecked = selectedApps.contains(app.second)
                        resetAppList(
                            packageName = app.second,
                            appName = app.first,
                            isChecked = isChecked,
                            onCheckedChange = { isSelected ->
                                val newSet = if (isSelected) {
                                    selectedApps + app.second
                                } else {
                                    selectedApps - app.second
                                }
                                onSelectionChanged(newSet)
                            }
                        )
                    }
                }
            }
        }
    }
}

// 全局颜色缓存
internal val colorCache = mutableMapOf<String, Color>()
// 获取主色调的函数
suspend fun getAutoColor(icon: ImageBitmap): Color {
    return withContext(Dispatchers.IO) {
        val bitmap = icon.asAndroidBitmap()
        Palette.from(bitmap).generate().dominantSwatch?.rgb?.let { Color(it) } ?: Color.White
    }
}
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun resetAppList(
    packageName: String,
    appName: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    GetAppIconAndName(packageName = packageName) { _, icon ->
        val defaultColor = MiuixTheme.colorScheme.primary
        val dominantColor = remember { mutableStateOf(colorCache[packageName] ?: defaultColor) }
        val isLoading = remember { mutableStateOf(dominantColor.value == defaultColor) }

        LaunchedEffect(icon) {
            if (isLoading.value) {
                val newColor = withContext(Dispatchers.Default) {
                    if (YukiHookAPI.Status.isModuleActive) getAutoColor(icon) else Color.Red
                }
                dominantColor.value = newColor
                colorCache[packageName] = newColor
                isLoading.value = false
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isLoading.value) {
                Card(
                    colors = CardDefaults.defaultColors(color = dominantColor.value),
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                        .drawColoredShadow(
                            color = dominantColor.value,
                            alpha = 1f,
                            borderRadius = 13.dp,
                            shadowRadius = 7.dp,
                        )
                ) {
                    Image(bitmap = icon, contentDescription = appName, modifier = Modifier.size(45.dp))
                }
                SuperCheckbox(
                    title = appName,
                    checked = isChecked,
                    onCheckedChange = onCheckedChange,
                    summary = packageName,
                    checkboxLocation = CheckboxLocation.Right
                )
            }
        }
    }
}

fun getApps(
    context: Context,
    isSystemApp: Boolean = false,
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
