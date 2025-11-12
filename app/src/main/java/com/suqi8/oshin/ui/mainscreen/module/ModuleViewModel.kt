package com.suqi8.oshin.ui.mainscreen.module

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.data.repository.AppInfoProvider
import com.suqi8.oshin.data.repository.FeatureRepository
import com.suqi8.oshin.features.FeatureRegistry
import com.suqi8.oshin.models.ModuleEntry
import com.suqi8.oshin.utils.RouteFormatter
import com.suqi8.oshin.utils.getAutoColor
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.Collator
import java.util.Locale
import javax.inject.Inject

/**
 * 应用信息缓存数据类
 */
data class AppUiInfo(
    val name: String,
    val icon: ImageBitmap,
    val dominantColor: Color
)

data class SearchResultUiItem(
    val item: SearchableItem,
    val formattedRoute: String
)

@HiltViewModel
class ModuleViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val featureRepository: FeatureRepository,
    private val appInfoProvider: AppInfoProvider,
    private val routeFormatter: RouteFormatter
) : ViewModel() {

    private val _uiState = MutableStateFlow(ModuleUiState())
    val uiState = _uiState.asStateFlow()

    // 缓存所有可搜索项的列表，避免重复构建
    private var allSearchableItems: List<SearchableItem> = emptyList()

    // 滚动位置状态
    var scrollIndex by mutableStateOf(0)
        private set
    var scrollOffset by mutableStateOf(0)
        private set

    private val _appInfoCache = mutableStateMapOf<String, AppUiInfo>()
    val appInfoCache: Map<String, AppUiInfo> = _appInfoCache

    /**
     * 保存滚动位置
     */
    fun saveScrollPosition(index: Int, offset: Int) {
        scrollIndex = index
        scrollOffset = offset
    }

    /**
     * 缓存应用信息
     */
    fun cacheAppInfo(packageName: String, appInfo: AppUiInfo) {
        _appInfoCache[packageName] = appInfo
    }

    /**
     * 获取缓存的应用信息
     */
    fun getAppInfo(packageName: String): AppUiInfo? {
        return _appInfoCache[packageName]
    }

    init {
        viewModelScope.launch {
            val savedStyle = context.prefs("settings").getInt("appstyle", 0)
            _uiState.update { it.copy(appStyle = savedStyle) }
        }
        buildSearchIndex()
    }

    fun onAppStyleChanged() {
        val currentStyle = _uiState.value.appStyle
        val newStyle = if (currentStyle == 0) 1 else 0
        _uiState.update { it.copy(appStyle = newStyle) }

        // 将新样式保存到 SharedPreferences
        viewModelScope.launch(Dispatchers.IO) {
            context.prefs("settings").edit { putInt("appstyle", newStyle) }
        }
    }

    private fun buildSearchIndex() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val sortedEntriesJob = async(Dispatchers.IO) { getSortedModuleEntries() }
            val searchableItemsJob = async { featureRepository.getAllSearchableItems() }

            _uiState.update {
                it.copy(
                    moduleEntries = sortedEntriesJob.await(),
                    isLoading = false
                )
            }

            // 等待搜索索引任务完成并存入缓存
            allSearchableItems = searchableItemsJob.await()
        }
    }

    private suspend fun getSortedModuleEntries(): List<ModuleEntry> {
        val originalEntries = FeatureRegistry.moduleEntries

        // 1. 为每个 Entry 异步获取其应用名
        val entriesWithAppName = coroutineScope {
            originalEntries.map { entry ->
                async(Dispatchers.IO) {
                    appInfoProvider.getAppName(entry.packageName) to entry
                }
            }.awaitAll()
        }

        // 2. 根据应用名进行排序 (使用 Collator 以支持中文等复杂排序)
        val collator = Collator.getInstance(Locale.getDefault())
        val sortedEntries = entriesWithAppName.sortedWith(
            compareBy(collator) { it.first }
        ).map {
            // 3. 排序后，只返回原始的 ModuleEntry 列表
            it.second
        }

        return sortedEntries
    }

    fun onAppNotFound(packageName: String) {
        _uiState.update {
            it.copy(notInstalledApps = it.notInstalledApps + packageName)
        }
    }

    /**
     * 当搜索框内容变化时由 UI 调用。
     */
    fun onSearchQueryChanged(query: String) {
        val isSearching = query.isNotBlank()
        _uiState.update { it.copy(searchQuery = query, isSearching = isSearching) }

        if (isSearching) {
            viewModelScope.launch {
                val results = allSearchableItems.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.summary.contains(query, ignoreCase = true)
                }
                val uiResults = results.map { item ->
                    val routeId = item.route.substringAfter("feature/")
                    val formattedRoute = routeFormatter.formatRouteAsBreadcrumb(routeId)
                    SearchResultUiItem(item, formattedRoute)
                }

                _uiState.update { it.copy(searchResults = uiResults) }
            }
        }
    }

    /**
     * 统一加载应用信息和主色
     * 1. 调用 AppInfoProvider 获取名称和图标
     * 2. 异步提取主色
     * 3. 将最终的 AppUiInfo 存入 _appInfoCache
     *
     * @param packageName 要加载的应用
     * @param defaultColor UI层传入的默认颜色，用于加载时显示
     */
    fun loadAppInfoWithColor(
        packageName: String,
        defaultColor: Color
    ) {
        if (getAppInfo(packageName) != null || uiState.value.notInstalledApps.contains(packageName)) {
            return
        }

        viewModelScope.launch {
            val appInfo = appInfoProvider.getInfo(packageName)

            if (appInfo == null) {
                // 2a. 应用未找到，更新 UiState
                onAppNotFound(packageName)
            } else {
                // 2b. 应用已找到，立即缓存部分信息（带默认颜色）
                // 这会让UI立即从 "null" 变为 "加载中"
                val partialInfo = AppUiInfo(appInfo.name, appInfo.icon, defaultColor)
                cacheAppInfo(packageName, partialInfo) // 使用 ViewModel 已有的 cacheAppInfo

                // 3. 异步提取主色
                val newColor = withContext(Dispatchers.Default) {
                    try {
                        if (!YukiHookAPI.Status.isModuleActive) {
                            defaultColor
                        } else {
                            // 调用我们放在 ColorUtils.kt 中的全局工具函数
                            getAutoColor(appInfo.icon, defaultColor)
                        }
                    } catch (e: Exception) {
                        // 颜色提取失败，使用默认值
                        defaultColor
                    }
                }

                // 4. 更新缓存，提供包含最终颜色的完整信息
                if (newColor != defaultColor) {
                    cacheAppInfo(packageName, AppUiInfo(appInfo.name, appInfo.icon, newColor))
                }
            }
        }
    }
}
