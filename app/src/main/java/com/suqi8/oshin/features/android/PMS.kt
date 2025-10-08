package com.suqi8.oshin.features.android

import com.suqi8.oshin.R
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object PMS {
    val definition = PageDefinition(
        category = "android\\package_manager_services",
        appList = listOf("android"),
        title = StringResource(R.string.package_manager_services),
        // 2. 定义页面的卡片列表
        items = listOf(
            // --- 第一个 Card ---
            CardDefinition(
                titleRes = R.string.common_settings,
                // 这个卡片前没有 SmallTitle，所以 titleRes 为 null
                items = listOf(
                    Switch(
                        title = StringResource(R.string.downgr),
                        summary = R.string.downgr_summary,
                        key = "downgrade"
                    ),
                    Switch(
                        title = StringResource(R.string.authcreak),
                        summary = R.string.authcreak_summary,
                        key = "authcreak"
                    ),
                    Switch(
                        title = StringResource(R.string.digestCreak),
                        summary = R.string.digestCreak_summary,
                        key = "digestCreak"
                    ),
                    Switch(
                        title = StringResource(R.string.UsePreSig),
                        summary = R.string.UsePreSig_summary,
                        key = "UsePreSig"
                    ),
                    Switch(
                        title = StringResource(R.string.enhancedMode),
                        summary = R.string.enhancedMode_summary,
                        key = "enhancedMode"
                    )
                )
            ),
            CardDefinition(
                titleRes = R.string.other_settings,
                items = listOf(
                    Switch(
                        title = StringResource(R.string.bypassBlock),
                        summary = R.string.bypassBlock_summary,
                        key = "bypassBlock"
                    ),
                    Switch(
                        title = StringResource(R.string.shared_user_title),
                        summary = R.string.shared_user_summary,
                        key = "sharedUser"
                    ),
                    Switch(
                        title = StringResource(R.string.disable_verification_agent_title),
                        summary = R.string.disable_verification_agent_summary,
                        key = "disableVerificationAgent"
                    )
                )
            )
        )
    )
}
