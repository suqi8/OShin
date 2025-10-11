package com.suqi8.oshin.features.settings

import android.os.Environment
import com.suqi8.oshin.R
import com.suqi8.oshin.models.AndCondition
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.AppSelection
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.Picture
import com.suqi8.oshin.models.RelatedLinks
import com.suqi8.oshin.models.SimpleCondition
import com.suqi8.oshin.models.Slider
import com.suqi8.oshin.models.StringInput
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object settings {
    val definition = PageDefinition(
        title = AppName("com.android.settings"),
        category = "settings",
        appList = listOf("com.android.settings"),
        items = listOf(
            // --- 第一个设置卡片 ---
            CardDefinition(
                items = listOf(
                    StringInput(
                        key = "custom_display_model",
                        title = StringResource(R.string.custom_display_model),
                        summary = R.string.hint_empty_content_default,
                        nullable = true
                    ),
                    Switch(
                        key = "enable_ota_card_bg",
                        title = StringResource(R.string.enable_ota_card_bg)
                    ),
                    // --- OTA 卡片背景设置 (仅在 ota_card_bg 开启时显示) ---
                    Picture(
                        key = "ota_card_bg_image",
                        title = StringResource(R.string.select_background_btn),
                        targetPath = "${Environment.getExternalStorageDirectory()}/.OShin/settings/ota_card.png",
                        condition = SimpleCondition("enable_ota_card_bg", requiredValue = true)
                    ),
                    Slider(
                        key = "ota_corner_radius",
                        title = StringResource(R.string.corner_radius_title),
                        valueRange = 0f..300f, unit = "px", decimalPlaces = 1,
                        condition = SimpleCondition("enable_ota_card_bg", requiredValue = true)
                    ),
                    Switch(
                        key = "force_show_nfc_security_chip",
                        title = StringResource(R.string.force_show_nfc_security_chip),
                        summary = R.string.confirm_privacy_password_is_not_set
                    )
                )
            ),

            // --- 第二个设置卡片 (无障碍相关) ---
            CardDefinition(
                items = listOf(
                    // “授予”开关：!jump && !autoauth
                    Switch(
                        key = "auth",
                        title = StringResource(R.string.accessibility_service_authorize),
                        condition = AndCondition(
                            listOf(
                                SimpleCondition("jump", requiredValue = false),
                                SimpleCondition("autoauth", requiredValue = false)
                            )
                        )
                    ),
                    // “直接跳转”开关：!auth && !autoauth
                    Switch(
                        key = "jump",
                        title = StringResource(R.string.accessibility_service_direct),
                        condition = AndCondition(
                            listOf(
                                SimpleCondition("auth", requiredValue = false),
                                SimpleCondition("autoauth", requiredValue = false)
                            )
                        )
                    ),
                    // “智能授权”开关：!auth && !jump
                    Switch(
                        key = "autoauth",
                        title = StringResource(R.string.smart_accessibility_service),
                        summary = R.string.whitelist_app_auto_authorization,
                        condition = AndCondition(
                            listOf(
                                SimpleCondition("auth", requiredValue = false),
                                SimpleCondition("jump", requiredValue = false)
                            )
                        )
                    ),
                    // “白名单”应用选择器：autoauth && !auth && !jump
                    AppSelection(
                        key = "autoauthwhite",
                        title = StringResource(R.string.accessibility_whitelist),
                        condition = AndCondition(
                            listOf(
                                SimpleCondition("autoauth", requiredValue = true),
                                SimpleCondition("auth", requiredValue = false),
                                SimpleCondition("jump", requiredValue = false)
                            )
                        )
                    )
                )
            ),

            // --- 独立的 WantFind/RelatedLinks 卡片 ---
            RelatedLinks(
                links = listOf(
                    RelatedLinks.Link(
                        titleRes = R.string.auto_start_max_limit,
                        route = "battery"
                    )
                )
            )
        )
    )
}
