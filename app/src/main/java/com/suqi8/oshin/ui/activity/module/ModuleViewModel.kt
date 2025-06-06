package com.suqi8.oshin.ui.activity.module

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.suqi8.oshin.utils.AppInfoProvider
import com.suqi8.oshin.utils.FeatureRepository
import com.suqi8.oshin.utils.GetFuncRoute
import com.suqi8.oshin.utils.item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.Collator
import java.util.Locale

data class SearchResultItem(
    val title: String,
    val summary: String?,
    val category: String,
    val displayPath: String
)
// UI状态的数据类
data class ModuleUiState(
    val searchValue: String = "",
    val isSearchExpanded: Boolean = false,
    val filteredFeatures: List<SearchResultItem> = emptyList(),
    val appList: List<AppInfoItem> = emptyList(),
    val uninstalledApps: List<String> = emptyList(),
    val appStyle: Int = 0,
    val isLoading: Boolean = true
)

// 应用列表项的数据类，包含从图标提取的颜色
data class AppInfoItem(
    val packageName: String,
    val activityName: String,
    val appName: String = "",
    val icon: ImageBitmap? = null,
    val dominantColor: Color = Color.Transparent,
    val isInstalled: Boolean = true
)

class ModuleViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ModuleUiState())
    val uiState: StateFlow<ModuleUiState> = _uiState.asStateFlow()

    private val _fullFeaturesList = MutableStateFlow<List<item>>(emptyList())
    private val appInfoProvider = AppInfoProvider(application)
    private val colorCache = mutableMapOf<String, Color>()
    private val collator: Collator = Collator.getInstance(Locale.CHINA)

    init {
        loadInitialData()
        observeSearchChanges()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // 从Repository加载一次性数据
            _fullFeaturesList.value = FeatureRepository.getFeatures(getApplication())
            // 加载App列表
            loadAppList()
            // 读取用户偏好
            val savedAppStyle = 0 // 您可以使用 context.prefs(...) 来读取
            _uiState.update { it.copy(appStyle = savedAppStyle, isLoading = false) }
        }
    }

    private fun loadAppList() {
        val appPackages = listOf(
            "android" to "android",
            "com.android.systemui" to "systemui",
            "com.android.settings" to "settings",
            "com.android.launcher" to "launcher",
            "com.oplus.battery" to "battery",
            "com.heytap.speechassist" to "speechassist",
            "com.coloros.ocrscanner" to "ocrscanner",
            "com.oplus.games" to "games",
            "com.finshell.wallet" to "wallet",
            "com.coloros.phonemanager" to "phonemanager",
            "com.oplus.phonemanager" to "oplusphonemanager",
            "com.android.mms" to "mms",
            "com.coloros.securepay" to "securepay",
            "com.heytap.health" to "health",
            "com.oplus.appdetail" to "appdetail",
            "com.heytap.quicksearchbox" to "quicksearchbox",
            "com.mi.health" to "mihealth",
            "com.oplus.ota" to "ota",
            "com.coloros.oshare" to "oshare"
        )
        viewModelScope.launch {
            val installedApps = mutableListOf<AppInfoItem>()
            val uninstalledPackageNames = mutableListOf<String>() // ⭐ 新建一个列表来收集

            appPackages.forEach { (pkg, act) ->
                val info = appInfoProvider.getInfo(pkg)
                if (info != null) {
                    installedApps.add(AppInfoItem(
                        packageName = pkg,
                        activityName = act,
                        appName = info.name,
                        icon = info.icon,
                        dominantColor = getOrExtractColor(pkg, info.icon),
                        isInstalled = true
                    ))
                } else {
                    // ⭐ 如果应用未安装，将包名添加到 uninstalledPackageNames 列表中
                    uninstalledPackageNames.add(pkg)
                }
            }
            // ⭐ 更新UI状态时，同时更新 appList 和 uninstalledApps
            _uiState.update {
                it.copy(
                    appList = installedApps,
                    uninstalledApps = uninstalledPackageNames
                )
            }
        }
    }

    private suspend fun getOrExtractColor(packageName: String, icon: ImageBitmap): Color {
        // 检查缓存，如果存在则直接返回
        return colorCache[packageName] ?:
        // 如果缓存不存在，则在IO线程中提取颜色
        withContext(Dispatchers.IO) {
            try {
                Palette.from(icon.asAndroidBitmap())
                    .generate()
                    .dominantSwatch?.rgb?.let { Color(it) }
                    ?: Color.White // 提取失败或没有主色调时的默认颜色
            } catch (e: Exception) {
                Color.White // 发生异常时的默认颜色
            }
        }.also { newColor ->
            // 将新提取的颜色存入缓存
            colorCache[packageName] = newColor
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchChanges() {
        viewModelScope.launch {
            // 获取context和appNameCache以供后续使用
            val context = getApplication<Application>().applicationContext
            _uiState.map { it.searchValue }
                .combine(_fullFeaturesList) { query, features ->
                    if (query.isBlank()) {
                        emptyList()
                    } else {
                        features.filter {
                            it.title.contains(query, ignoreCase = true) ||
                                    it.summary?.contains(query, ignoreCase = true) ?: false
                        }
                    }
                }
                .map { filteredList -> // ⭐ 新增一个 map 操作
                    // 将 List<FeatureItem> 转换为 List<SearchResultItem>
                    filteredList.map { feature ->
                        SearchResultItem(
                            title = feature.title,
                            summary = feature.summary,
                            category = feature.category,
                            // ⭐ 在这里调用 GetFuncRoute 生成路径
                            displayPath = GetFuncRoute(feature.category, context)
                        )
                    }
                }
                .collect { finalResults ->
                    // 更新UI状态
                    _uiState.update { it.copy(filteredFeatures = finalResults) }
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchValue = query) }
    }

    fun onSearchExpandedChanged(isExpanded: Boolean) {
        _uiState.update { it.copy(isSearchExpanded = isExpanded) }
        if (!isExpanded) {
            onSearchQueryChanged("") // 关闭搜索时清空文本
        }
    }

    fun onAppStyleChanged() {
        val newStyle = if (_uiState.value.appStyle == 0) 1 else 0
        _uiState.update { it.copy(appStyle = newStyle) }
    }
}
