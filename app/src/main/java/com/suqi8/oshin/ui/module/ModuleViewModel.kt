package com.suqi8.oshin.ui.module

import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.data.repository.FeatureRepository
import com.suqi8.oshin.features.FeatureRegistry
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.ModuleEntry
import com.suqi8.oshin.models.PlainText
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Title
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

@HiltViewModel
class ModuleViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val featureRepository: FeatureRepository
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
                    getAppName(entry.packageName) to entry
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
