package com.suqi8.oshin.models

/**
 * 模块页面中的一个应用入口项。
 * An application entry in the module list page.
 *
 * @param packageName 应用的包名，用于获取应用名称和图标
 * @param routeId 对应功能页面的路由 ID
 */
data class ModuleEntry(
    val packageName: String,
    val routeId: String
)
