package com.suqi8.oshin.features.games

import com.suqi8.oshin.R
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object games {
    val definition = PageDefinition(
        category = "games",
        appList = listOf("com.oplus.games"),
        title = AppName("com.oplus.games"),
        items = listOf(
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.enable_ultra_combo),
                        key = "ultra_combo"
                    ),
                    Switch(
                        title = StringResource(R.string.feature_disable_cloud_control),
                        key = "feature_disable_cloud_control"
                    ),
                    Switch(
                        title = StringResource(R.string.remove_package_restriction),
                        key = "remove_package_restriction"
                    ),
                    Switch(
                        title = StringResource(R.string.enable_all_features),
                        summary = R.string.enable_all_features_warning,
                        key = "enable_all_features"
                    ),
                    Switch(
                        title = StringResource(R.string.remove_game_filter_root_detection),
                        key = "remove_game_filter_root_detection"
                    ),
                    Switch(
                        title = StringResource(R.string.enable_mlbb_ai_god_assist),
                        key = "enable_mlbb_ai_god_assist"
                    ),
                    Switch(
                        title = StringResource(R.string.enable_pubg_ai),
                        key = "pubg_ai"
                    )
                )
            ),
            CardDefinition(
                titleRes = R.string.hok,
                items = listOf(
                    Switch(
                        title = StringResource(R.string.enable_hok_ai_v1),
                        key = "hok_ai_v1"
                    ),
                    Switch(
                        title = StringResource(R.string.enable_hok_ai_v2),
                        summary = R.string.realme_gt7pro_feature_unlock_device_restriction,
                        key = "hok_ai_v2"
                    ),
                    Switch(
                        title = StringResource(R.string.enable_hok_ai_v3),
                        key = "hok_ai_v3"
                    )
                )
            )
        )
    )
}
