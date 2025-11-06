package com.suqi8.oshin.models

/**
 * 完整页面定义模型。
 *
 * 定义一个完整的页面，包含页面的所有元数据（标题、分类等）和 UI 结构（页面元素列表）。
 * 这是页面配置和渲染的顶层数据模型。
 *
 * @property title 页面的标题，使用灵活的 [Title] 模型
 * @property category 页面的分类标签，用于组织和分类多个页面
 * @property appList 该页面关联的应用包名列表，用于标记该页面适用的应用范围。默认为空列表
 * @property items 页面的所有 UI 元素列表，包括卡片、链接组等
 *
 * @see Title 标题模型
 * @see PageItem 页面级别 UI 元素基类
 */
data class PageDefinition(
    val title: Title,
    val category: String,
    val appList: List<String> = emptyList(),
    val items: List<PageItem>
)
