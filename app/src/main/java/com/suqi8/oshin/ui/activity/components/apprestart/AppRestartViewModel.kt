package com.suqi8.oshin.ui.activity.components.apprestart

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.highcapable.yukihookapi.YukiHookAPI
import com.suqi8.oshin.data.repository.AppInfoProvider
import com.suqi8.oshin.ui.module.AppUiInfo
import com.suqi8.oshin.utils.getAutoColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@HiltViewModel
class AppRestartViewModel @Inject constructor(
    private val appInfoProvider: AppInfoProvider
) : ViewModel() {

    // 状态缓存：PkgName -> AppUiInfo
    // 这个缓存是此 ViewModel 实例私有的
    private val _appInfoCache = mutableStateMapOf<String, AppUiInfo?>()
    val appInfoCache: Map<String, AppUiInfo?> = _appInfoCache

    // 用于跟踪哪些应用未安装
    private val _notInstalledApps = ConcurrentHashMap.newKeySet<String>()

    /**
     * 加载此对话框所需的所有应用信息
     */
    fun loadAppsInfo(
        packageNames: List<String>,
        defaultColor: Color
    ) {
        viewModelScope.launch {
            packageNames.forEach { pkg ->
                // 只有在没有被加载过，且未被标记为 "未安装" 时才加载
                if (!_appInfoCache.containsKey(pkg) && !_notInstalledApps.contains(pkg)) {
                    loadAppInfoWithColor(pkg, defaultColor)
                }
            }
        }
    }

    /**
     * 重启应用的核心逻辑
     */
    fun restartApps(packageNames: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            packageNames.forEach { packageName ->
                try {
                    if (packageName == "android") {
                        val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "reboot"))
                        process.waitFor()
                    } else {
                        val command = "pkill -f $packageName"
                        val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
                        process.waitFor()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // 可以在这里向 UI 暴露一个错误状态
                }
            }
        }
    }

    /**
     * 内部函数：加载单个应用的信息（图标、名称、颜色）
     */
    private suspend fun loadAppInfoWithColor(packageName: String, defaultColor: Color) {
        // 1. 从 Repository 获取基础信息
        val appInfo = appInfoProvider.getInfo(packageName)

        if (appInfo == null) {
            // 2a. 应用未找到
            _notInstalledApps.add(packageName)
            // 缓存一个 null 来表示 "未安装"
            _appInfoCache[packageName] = null
        } else {
            // 2b. 应用已找到，立即缓存（带默认颜色）
            val partialInfo = AppUiInfo(appInfo.name, appInfo.icon, defaultColor)
            _appInfoCache[packageName] = partialInfo

            // 3. 异步提取主色
            val newColor = withContext(Dispatchers.Default) {
                try {
                    if (!YukiHookAPI.Status.isModuleActive) defaultColor else getAutoColor(appInfo.icon)
                } catch (e: Exception) {
                    defaultColor
                }
            }

            // 4. 更新缓存，提供最终颜色
            if (newColor != defaultColor) {
                _appInfoCache[packageName] = AppUiInfo(appInfo.name, appInfo.icon, newColor)
            }
        }
    }
}
