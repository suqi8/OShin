package com.suqi8.oshin.models

/**
 * 功能页面的完整定义，包含页面所需的所有数据。
 * Full definition of a feature page, containing all data required to render it.
 *
 * @param category SharedPreferences 文件名，该页面内所有功能项的值都存储在此文件中
 * @param appList 与此页面关联的应用包名列表（用于显示"重启应用"按钮）
 * @param title 页面标题
 * @param items 页面顶层元素列表（卡片、相关链接等）
 */
data class PageDefinition(
    val category: String,
    val appList: List<String>,
    val title: Title,
    val items: List<PageItem>
)
