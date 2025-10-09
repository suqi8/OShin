package com.suqi8.oshin.ui.module

import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.data.repository.FeatureRepository
import com.suqi8.oshin.features.FeatureRegistry
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.PlainText
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Title
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
            // 2. 直接从 Repository 获取数据，不再需要复杂的计算逻辑！
            allSearchableItems = featureRepository.getAllSearchableItems()

            _uiState.update {
                it.copy(
                    moduleEntries = FeatureRegistry.moduleEntries, // moduleEntries 依然从 Registry 获取
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
