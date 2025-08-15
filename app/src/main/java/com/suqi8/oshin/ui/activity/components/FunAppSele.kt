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
import com.highcapable.yukihookapi.hook.factory.prefs
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
fun FunAppSele(title: String, summary: String? = null, category: String, key: String, onCheckedChange: ((Int) -> Unit)? = null) {
    val context = LocalContext.current
    val showAppListSele = remember { mutableStateOf(false) }
    val searchText = remember { mutableStateOf("") }

    // 读取已选应用列表，转换为 Set 存储，防止重复
    val seleappList = remember {
        mutableStateOf(
            context.prefs(category)
                .getString(key, "")
                .split(",")
                .filter { it.isNotEmpty() }
                .toSet()
        )
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            requestPermissions(context, PermissionLists.getGetInstalledAppsPermission()) {}
        }
    }

    FunArrow(
        title = title,
        summary = if (summary == null) stringResource(R.string.selected_app) + seleappList.value.joinToString(
            ","
        ) else summary + "\n" + stringResource(R.string.selected_app) + seleappList.value.joinToString(
            ","
        ),
        onClick = { showAppListSele.value = true }
    )

    val appList = remember { mutableStateOf(listOf<Pair<String, String>>()) }

    // 获取所有应用信息
    if (appList.value.isEmpty()) {
        getApps(context, isSystemApp = false) { appInfo ->
            appList.value += appInfo
        }
    }
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    val maxHeightDp = with(density) { (screenHeightPx * 0.85f).toDp() }

    SuperDialog(
        show = showAppListSele,
        onDismissRequest = { showAppListSele.value = false }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = maxHeightDp)
                .animateContentSize() // 自动调整高度，并带有动画
        ) {
            Column {
                // 搜索框
                TextField(
                    value = searchText.value,
                    onValueChange = { searchText.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .animateContentSize() // 列表高度变化时，带动画
                        .overScrollVertical()
                ) {
                    // 过滤并排序应用列表
                    val filteredList = appList.value
                        .filter {
                            it.first.contains(searchText.value, ignoreCase = true) ||
                                    it.second.contains(searchText.value, ignoreCase = true)
                        }
                        .sortedByDescending { seleappList.value.contains(it.second) } // 选中的置顶

                    items(filteredList) { app ->
                        val isChecked = seleappList.value.contains(app.second)
                        ResetAppList(app.second, isChecked) { isSelected ->
                            seleappList.value = if (isSelected) {
                                seleappList.value + app.second  // 使用 Set 追加数据
                            } else {
                                seleappList.value - app.second  // 从 Set 移除数据
                            }

                            // 更新 `SharedPreferences`
                            context.prefs(category).edit {
                                putString(key, seleappList.value.joinToString(","))
                            }
                        }
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
                    colors = CardDefaults.defaultColors(dominantColor.value),
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
                SuperCheckbox(
                    title = appName,
                    checked = isChecked,
                    onCheckedChange = {
                        onCheckedChange(it) // 更新选中状态
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
