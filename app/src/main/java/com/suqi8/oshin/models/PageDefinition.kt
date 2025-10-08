package com.suqi8.oshin.models

/**
 * 定义一个完整的页面，包含其所有元数据和UI结构。
 */
data class PageDefinition(
    val title: Title,
    val category: String,
    val appList: List<String> = emptyList(),
    val items: List<PageItem>
)
