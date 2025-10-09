package com.suqi8.oshin.features

import com.suqi8.oshin.features.android.OplusServices
import com.suqi8.oshin.features.android.PMS
import com.suqi8.oshin.features.android.SplitScreenMultiWindow
import com.suqi8.oshin.features.android.android
import com.suqi8.oshin.features.systemui.systemui
import com.suqi8.oshin.models.ModuleEntry
import com.suqi8.oshin.models.PageDefinition

object FeatureRegistry {
    /**
     * 主模块页面的应用入口列表。
     */
    val moduleEntries = listOf(
        ModuleEntry("android", "android"),
        ModuleEntry("com.android.systemui", "systemui"),
    )

    /**
     * 所有功能页面的详细定义。
     * Key: 页面路由ID (与 ModuleEntry.routeId 对应)
     * Value: 页面的完整定义
     */
    val screenMap: Map<String, PageDefinition> = mapOf(
        "android" to android.definition,
        "android\\package_manager_services" to PMS.definition,
        "android\\oplus_system_services" to OplusServices.definition,
        "android\\split_screen_multi_window" to SplitScreenMultiWindow.definition,

        "systemui" to systemui.definition,
    )
}
