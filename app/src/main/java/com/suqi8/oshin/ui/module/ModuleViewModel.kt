package com.suqi8.oshin.ui.module

import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.features.FeatureRegistry
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PlainText
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Title
import com.suqi8.oshin.models.TitledScreenItem
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ModuleViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ModuleUiState())
    val uiState = _uiState.asStateFlow()

    // 缓存所有可搜索项的列表，避免重复构建
    private var allSearchableItems: List<SearchableItem> = emptyList()

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
        viewModelScope.launch(Dispatchers.Default) { // 使用 Default 调度器进行 CPU 密集型操作
            // 从注册表加载主模块的静态入口列表
            val entries = FeatureRegistry.moduleEntries

            // 核心：构建全量搜索索引
            val searchableItemsJobs = FeatureRegistry.screenMap.flatMap { (routeId, pageDef) ->
                // 1. 先从页面中筛选出所有 CardDefinition
                pageDef.items.filterIsInstance<CardDefinition>()
                    // 2. 将所有 Card 里的 items 拍平为一个列表
                    .flatMap { it.items }
                    // 3. 从 items 列表中筛选出所有可搜索的项
                    .filterIsInstance<TitledScreenItem>()
                    // 4. 使用 async 并发解析标题，为每一项创建一个 SearchableItem
                    .map { item ->
                        async {
                            SearchableItem(
                                title = resolveTitle(item.title),
                                summary = item.summary?.let { context.getString(it) } ?: "",
                                route = "feature/$routeId",
                                key = item.key
                            )
                        }
                    }
            }

            // 等待所有 async 任务完成
            allSearchableItems = searchableItemsJobs.awaitAll()

            // 更新 UI 状态，显示入口列表并结束加载状态
            _uiState.update {
                it.copy(
                    moduleEntries = entries,
                    isLoading = false
                )
            }
        }
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
            // 在预构建的索引中执行快速过滤
            val results = allSearchableItems.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.summary.contains(query, ignoreCase = true)
            }
            _uiState.update { it.copy(searchResults = results) }
        }
    }

    /**
     * 辅助函数，用于将 Title 模型解析为最终的 String。
     */
    private suspend fun resolveTitle(title: Title): String {
        return when (title) {
            is StringResource -> context.getString(title.id)
            is PlainText -> title.text
            is AppName -> getAppName(title.packageName)
        }
    }

    /**
     * 安全地获取应用名称。
     */
    private suspend fun getAppName(packageName: String): String = withContext(Dispatchers.IO) {
        try {
            val pm = context.packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(appInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            packageName // 如果找不到，返回包名作为兜底
        }
    }
}
