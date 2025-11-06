package com.suqi8.oshin.models

/**
 * 应用模块入口定义。
 *
 * 定义 Main_Module 页面上的一个应用入口。每个入口代表一个可点击的应用模块卡片，
 * 点击后导航到该模块对应的详情页面。
 *
 * @property packageName 入口对应的应用包名
 * @property routeId 点击该入口后导航的目标页面 ID
 *
 * @see PageDefinition 页面定义
 */
data class ModuleEntry(
    val packageName: String,
    val routeId: String
)
