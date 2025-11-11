package com.suqi8.oshin.ui.activity.components.appselection

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.highcapable.yukihookapi.YukiHookAPI
import com.suqi8.oshin.data.repository.AppInfoProvider
import com.suqi8.oshin.ui.module.AppUiInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * 封装应用选择对话框的所有状态和逻辑
 */
@HiltViewModel
class AppSelectionViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appInfoProvider: AppInfoProvider // 注入我们的单一数据源
) : ViewModel() {

    // 状态 1: 完整的、未过滤的应用列表 (名称 + 包名)
    private val _allApps = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val allApps = _allApps.asStateFlow()

    // 状态 2: 搜索框的文本
    val searchText = mutableStateOf("")

    // 状态 3: UI 缓存 (图标 + 颜色)
    // 这是 ViewModel 私有的缓存，随 ViewModel 销毁
    private val _appUiCache = mutableStateMapOf<String, AppUiInfo?>()

    /**
     * 触发加载所有非系统应用
     */
    fun loadAllApps() {
        // 只有在列表为空时才加载
        if (_allApps.value.isNotEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            val pm = context.packageManager
            val allAppsInfo = pm.getInstalledApplications(0)

            // 关键：一次性构建整个列表
            val appList = allAppsInfo
                .filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 } // 仅非系统应用
                .map { app ->
                    val appName = pm.getApplicationLabel(app).toString()
                    val pkgName = app.packageName
                    Pair(appName, pkgName)
                }
                .sortedBy { it.first.lowercase() } // 按名称排序

            // 一次性更新状态
            _allApps.value = appList
        }
    }

    /**
     * 获取单个应用的详细 UI 信息 (图标 + 颜色)
     * 这是一个 Flow，它会首先发射缓存/默认值，然后在 I/O 完成后发射最终值。
     */
    fun getAppUiInfo(packageName: String, defaultColor: Color): Flow<AppUiInfo?> = flow {
        // 1. 检查缓存
        if (_appUiCache.containsKey(packageName)) {
            emit(_appUiCache[packageName])
            return@flow
        }

        // 2. 从 AppInfoProvider 获取基础信息
        val appInfo = appInfoProvider.getInfo(packageName)
        if (appInfo == null) {
            _appUiCache[packageName] = null
            emit(null) // 未安装
            return@flow
        }

        // 3. 发射 "加载中" 状态 (有图标，但用默认色)
        val partialInfo = AppUiInfo(appInfo.name, appInfo.icon, defaultColor)
        _appUiCache[packageName] = partialInfo
        emit(partialInfo)

        // 4. 异步提取主色
        val newColor = withContext(Dispatchers.Default) {
            try {
                // 仅在模块激活时提取颜色
                if (YukiHookAPI.Status.isModuleActive) {
                    val bitmap = appInfo.icon.asAndroidBitmap()
                    Palette.from(bitmap).generate().dominantSwatch?.rgb?.let { Color(it) } ?: defaultColor
                } else {
                    defaultColor // 否则使用默认色
                }
            } catch (e: Exception) {
                defaultColor // 提取失败也用默认色
            }
        }

        // 5. 发射 "已完成" 状态 (包含最终颜色)
        if (newColor != defaultColor) {
            val finalInfo = partialInfo.copy(dominantColor = newColor)
            _appUiCache[packageName] = finalInfo
            emit(finalInfo)
        }
    }
}
