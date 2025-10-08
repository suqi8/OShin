package com.suqi8.oshin.models

/**
 * 定义 Main_Module 页面上的一个应用入口。
 */
data class ModuleEntry(
    val packageName: String,
    val routeId: String // 点击后导航的目标页面ID
)
