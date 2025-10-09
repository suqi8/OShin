package com.suqi8.oshin.features.android

import com.suqi8.oshin.R
import com.suqi8.oshin.models.Action
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.RelatedLinks
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object android {

    val definition = PageDefinition(
        // 1. 定义整个页面的共享信息
        category = "android", // 所有 Switch 项将使用这个 category
        appList = listOf("android"),
        title = AppName("android"),
        // 2. 定义页面的卡片列表
        items = listOf(
            // --- 第一个 Card ---
            CardDefinition(
                // 这个卡片前没有 SmallTitle，所以 titleRes 为 null
                items = listOf(
                    // 对应第一个 FunArrow
                    Action(
                        title = StringResource(R.string.package_manager_services),
                        route = "android\\package_manager_services" // 点击后跳转的页面路由ID
                    ),
                    // 对应第二个 FunArrow
                    Action(
                        title = StringResource(R.string.oplus_system_services),
                        route = "android\\oplus_system_services"
                    ),
                    // 对应第三个 FunArrow
                    Action(
                        title = StringResource(R.string.split_screen_multi_window),
                        route = "android\\split_screen_multi_window"
                    )
                )
            ),

            // --- 第二个 Card ---
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.disable_72h_verify),
                        key = "DisablePinVerifyPer72h"
                    ),
                    Switch(
                        title = StringResource(R.string.allow_untrusted_touch),
                        key = "AllowUntrustedTouch"
                    )
                )
            ),
            RelatedLinks(
                links = listOf(
                    RelatedLinks.Link(
                        titleRes = R.string.allow_turn_off_all_categories,
                        route = "notificationmanager"
                    )
                )
            )
        )
    )
}
