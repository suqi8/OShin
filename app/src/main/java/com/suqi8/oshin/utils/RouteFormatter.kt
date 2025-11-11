package com.suqi8.oshin.utils

import android.content.Context
import com.suqi8.oshin.data.repository.AppInfoProvider
import com.suqi8.oshin.features.FeatureRegistry
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.PlainText
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Title
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RouteFormatter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appInfoProvider: AppInfoProvider
) {

    /**
     * 将内部路由字符串 (e.g., "systemui\\controlCenter")
     * 智能解析为人类可读的面包屑导航字符串 (e.g., "System UI › 控制中心").
     *
     * @param route 内部路由 ID
     * @return 格式化后的面包屑字符串
     */
    suspend fun formatRouteAsBreadcrumb(route: String): String {
        val routeParts = route.split('\\')
        val resolvedParts = mutableListOf<String>()
        var currentRouteKey = ""

        for (part in routeParts) {
            if (resolvedParts.isEmpty()) {
                // --- 1. 解析第一部分 (根模块/应用) ---
                currentRouteKey = part

                // 从注册表找到对应的包名
                val pkgName = FeatureRegistry.moduleEntries
                    .find { it.routeId == part }
                    ?.packageName

                if (pkgName != null) {
                    // 使用 AppInfoProvider 异步解析应用名
                    resolvedParts.add(appInfoProvider.getAppName(pkgName))
                } else {
                    // 兜底，直接使用 part
                    resolvedParts.add(part)
                }

            } else {
                // --- 2. 解析后续部分 (子功能) ---
                currentRouteKey = "$currentRouteKey\\$part"

                // 从 screenMap 查找定义
                val definition = FeatureRegistry.screenMap[currentRouteKey]

                if (definition != null) {
                    // 找到了定义, 解析其 Title
                    resolvedParts.add(resolveTitle(definition.title))
                } else {
                    // 兜底，直接使用 part
                    resolvedParts.add(part)
                }
            }
        }

        // --- 3. 组合所有部分 ---
        return resolvedParts.joinToString(" › ")
    }

    /**
     * 辅助函数，用于将 Title 模型解析为最终的 String。
     * (这个函数之前在 ModuleViewModel 中，现在由本类统一处理)
     */
    private suspend fun resolveTitle(title: Title): String {
        return when (title) {
            is StringResource -> context.getString(title.id)
            is PlainText -> title.text
            is AppName -> appInfoProvider.getAppName(title.packageName)
        }
    }
}
