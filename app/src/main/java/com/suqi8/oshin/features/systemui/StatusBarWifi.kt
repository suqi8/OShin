package com.suqi8.oshin.features.systemui

import com.suqi8.oshin.R
import com.suqi8.oshin.models.AndCondition
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.Dropdown
import com.suqi8.oshin.models.NoEnable
import com.suqi8.oshin.models.Operator
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.SimpleCondition
import com.suqi8.oshin.models.Slider
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object StatusBarWifi {
    val definition = PageDefinition(
        title = StringResource(R.string.network_speed_indicator),
        category = "systemui\\status_bar_wifi",
        appList = listOf("com.android.systemui"),
        items = listOf(
            // --- 主开关 ---
            CardDefinition(items = listOf(
                Switch(key = "status_bar_wifi", title = StringResource(R.string.network_speed_indicator))
            )),
            NoEnable(condition = SimpleCondition("status_bar_wifi", requiredValue = false)),

            // --- 主要设置卡片 (仅在主开关开启时显示) ---
            CardDefinition(
                condition = SimpleCondition("status_bar_wifi", requiredValue = true),
                items = listOf(
                    Dropdown(
                        key = "StyleSelectedOption",
                        title = StringResource(R.string.network_speed_style),
                        optionsRes = R.array.network_speed_style_options
                    ),
                    // --- “默认”样式下的选项 ---
                    Slider(
                        key = "speed_font_size",
                        title = StringResource(R.string.speed_font_size),
                        summary = R.string.default_value_hint_negative_one,
                        defaultValue = -1f, valueRange = -1f..20f, unit = "sp", decimalPlaces = 0,
                        condition = SimpleCondition("StyleSelectedOption", requiredValue = 0)
                    ),
                    Slider(
                        key = "unit_font_size",
                        title = StringResource(R.string.unit_font_size),
                        summary = R.string.default_value_hint_negative_one,
                        defaultValue = -1f, valueRange = -1f..20f, unit = "sp", decimalPlaces = 0,
                        condition = SimpleCondition("StyleSelectedOption", requiredValue = 0)
                    ),

                    // --- “上下行分离”样式下的选项 ---
                    Slider(
                        key = "upload_font_size",
                        title = StringResource(R.string.upload_font_size),
                        summary = R.string.default_value_hint_negative_one,
                        defaultValue = -1f, valueRange = -1f..20f, unit = "sp", decimalPlaces = 0,
                        condition = SimpleCondition("StyleSelectedOption", requiredValue = 1)
                    ),
                    Slider(
                        key = "download_font_size",
                        title = StringResource(R.string.download_font_size),
                        summary = R.string.default_value_hint_negative_one,
                        defaultValue = -1f, valueRange = -1f..20f, unit = "sp", decimalPlaces = 0,
                        condition = SimpleCondition("StyleSelectedOption", requiredValue = 1)
                    ),

                    // --- 通用设置 ---
                    Slider(
                        key = "slow_speed_threshold",
                        title = StringResource(R.string.slow_speed_threshold),
                        defaultValue = 20f, valueRange = 0f..1024f, unit = "KB/S", decimalPlaces = 0
                    ),
                    Switch(
                        key = "hide_on_slow",
                        title = StringResource(R.string.hide_on_slow)
                    ),
                    // --- 复合条件：hide_on_slow 为 true 且 StyleSelectedOption 为 1 ---
                    Switch(
                        key = "hide_when_both_slow",
                        title = StringResource(R.string.hide_when_both_slow),
                        condition = AndCondition(listOf(
                            SimpleCondition("hide_on_slow", requiredValue = true),
                            SimpleCondition("StyleSelectedOption", requiredValue = 1)
                        ))
                    ),

                    // --- “上下行分离”样式下的额外选项 ---
                    Dropdown(
                        key = "icon_indicator",
                        title = StringResource(R.string.icon_indicator),
                        optionsRes = R.array.icon_indicator_options,
                        condition = SimpleCondition("StyleSelectedOption", requiredValue = 1)
                    ),
                    // --- 复合条件：icon_indicator 不为 0 且 StyleSelectedOption 为 1 ---
                    Switch(
                        key = "position_speed_indicator_front",
                        title = StringResource(R.string.position_speed_indicator_front),
                        condition = AndCondition(listOf(
                            SimpleCondition("StyleSelectedOption", requiredValue = 1),
                            SimpleCondition("icon_indicator", operator = Operator.NOT_EQUALS, requiredValue = 0)
                        ))
                    ),
                    Switch(
                        key = "hide_space",
                        title = StringResource(R.string.hide_space),
                        condition = SimpleCondition("StyleSelectedOption", requiredValue = 1)
                    ),
                    Switch(
                        key = "hide_bs",
                        title = StringResource(R.string.hide_bs),
                        condition = SimpleCondition("StyleSelectedOption", requiredValue = 1)
                    ),
                    Switch(
                        key = "swap_upload_download",
                        title = StringResource(R.string.swap_upload_download),
                        condition = SimpleCondition("StyleSelectedOption", requiredValue = 1)
                    )
                )
            )
        )
    )
}
