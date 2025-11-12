package com.suqi8.oshin.ui.mainscreen.module

import com.suqi8.oshin.models.ModuleEntry

// 定义UI状态
data class ModuleUiState(
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val isLoading: Boolean = true, // 用于初次加载索引时的加载状态
    val appStyle: Int = 0,
    val moduleEntries: List<ModuleEntry> = emptyList(),
    val searchResults: List<SearchResultUiItem> = emptyList(),
    val notInstalledApps: Set<String> = emptySet()
)
