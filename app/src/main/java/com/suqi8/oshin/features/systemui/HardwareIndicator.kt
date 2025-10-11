package com.suqi8.oshin.features.systemui

import com.suqi8.oshin.R
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.Dropdown
import com.suqi8.oshin.models.NoEnable
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.SimpleCondition
import com.suqi8.oshin.models.Slider
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object HardwareIndicator {
    val definition = PageDefinition(
        title = StringResource(R.string.hardware_indicator),
        category = "systemui\\hardware_indicator",
        appList = listOf("com.android.systemui"),
        items = listOf(
            // --- 电量消耗指示器 ---
            CardDefinition(items = listOf(
                Switch(key = "power_indicator_enabled", title = StringResource(R.string.power_consumption_indicator))
            )),
            NoEnable(condition = SimpleCondition("power_indicator_enabled", requiredValue = false)),

            // --- 电量指示器设置 (显示内容) ---
            CardDefinition(
                titleRes = R.string.display_content,
                condition = SimpleCondition("power_indicator_enabled", requiredValue = true),
                items = listOf(
                    Switch(key = "power_indicator_dual_row", title = StringResource(R.string.dual_row_title)),
                    Dropdown(
                        key = "power_indicator_line1_content",
                        title = StringResource(R.string.first_line_content),
                        optionsRes = R.array.hardware_indicator_display_options
                    ),
                    Dropdown(
                        key = "power_indicator_line2_content",
                        title = StringResource(R.string.second_line_content),
                        optionsRes = R.array.hardware_indicator_display_options,
                        condition = SimpleCondition("power_indicator_dual_row", requiredValue = true)
                    )
                )
            ),
            // --- 电量指示器设置 (外观和更新) ---
            CardDefinition(
                titleRes = R.string.appearance_and_update,
                condition = SimpleCondition("power_indicator_enabled", requiredValue = true),
                items = listOf(
                    Switch(key = "power_indicator_bold", title = StringResource(R.string.bold_text)),
                    Dropdown(
                        key = "power_indicator_alignment",
                        title = StringResource(R.string.alignment),
                        optionsRes = R.array.hardware_indicator_gravity_options
                    ),
                    Slider(
                        key = "power_indicator_update_interval",
                        title = StringResource(R.string.update_time),
                        defaultValue = 1000f, valueRange = 0f..2000f, unit = "ms", decimalPlaces = 0
                    ),
                    Slider(
                        key = "power_indicator_font_size",
                        title = StringResource(R.string.font_size),
                        defaultValue = 8f, valueRange = 0f..20f, unit = "sp", decimalPlaces = 1
                    )
                )
            ),

            // --- 温度指示器 ---
            CardDefinition(items = listOf(
                Switch(key = "temp_indicator_enabled", title = StringResource(R.string.temperature_indicator))
            )),
            NoEnable(condition = SimpleCondition("temp_indicator_enabled", requiredValue = false)),

            // --- 温度指示器设置 (显示内容) ---
            CardDefinition(
                titleRes = R.string.display_content,
                condition = SimpleCondition("temp_indicator_enabled", requiredValue = true),
                items = listOf(
                    Switch(key = "temp_indicator_dual_row", title = StringResource(R.string.dual_row_title)),
                    Dropdown(
                        key = "temp_indicator_line1_content",
                        title = StringResource(R.string.first_line_content),
                        optionsRes = R.array.hardware_indicator_display_options
                    ),
                    Dropdown(
                        key = "temp_indicator_line2_content",
                        title = StringResource(R.string.second_line_content),
                        optionsRes = R.array.hardware_indicator_display_options,
                        condition = SimpleCondition("temp_indicator_dual_row", requiredValue = true)
                    )
                )
            ),
            // --- 温度指示器设置 (外观和更新) ---
            CardDefinition(
                titleRes = R.string.appearance_and_update,
                condition = SimpleCondition("temp_indicator_enabled", requiredValue = true),
                items = listOf(
                    Switch(key = "temp_indicator_bold", title = StringResource(R.string.bold_text)),
                    Dropdown(
                        key = "temp_indicator_alignment",
                        title = StringResource(R.string.alignment),
                        optionsRes = R.array.hardware_indicator_gravity_options
                    ),
                    Slider(
                        key = "temp_indicator_update_interval",
                        title = StringResource(R.string.update_time),
                        defaultValue = 1000f, valueRange = 0f..2000f, unit = "ms", decimalPlaces = 0
                    ),
                    Slider(
                        key = "temp_indicator_font_size",
                        title = StringResource(R.string.font_size),
                        defaultValue = 8f, valueRange = 0f..20f, unit = "sp", decimalPlaces = 1
                    )
                )
            ),

            // --- 全局数据源设置 ---
            CardDefinition(
                titleRes = R.string.data_source_settings,
                items = listOf(
                    Switch(key = "data_power_dual_cell", title = StringResource(R.string.dual_cell)),
                    Switch(key = "data_power_absolute_current", title = StringResource(R.string.absolute_value)),
                    Slider(
                        key = "data_temp_cpu_source",
                        title = StringResource(R.string.change_cpu_temp_source),
                        summary = R.string.enter_thermal_zone_number,
                        valueRange = 0f..100f,
                        decimalPlaces = 0
                    ),
                    Slider(
                        key = "data_freq_cpu_source",
                        title = StringResource(R.string.change_cpu_freq_source),
                        summary = R.string.enter_cpu_core_number,
                        valueRange = 0f..15f,
                        decimalPlaces = 0
                    )
                )
            ),

            // --- 全局单位设置 ---
            CardDefinition(
                titleRes = R.string.unit_display_settings,
                items = listOf(
                    Switch(key = "unit_hide_power", title = StringResource(R.string.power)),
                    Switch(key = "unit_hide_current", title = StringResource(R.string.current)),
                    Switch(key = "unit_hide_voltage", title = StringResource(R.string.voltage)),
                    Switch(key = "unit_hide_temp_battery", title = StringResource(R.string.battery_temperature)),
                    Switch(key = "unit_hide_temp_cpu", title = StringResource(R.string.cpu_temperature)),
                    Switch(key = "unit_hide_cpu_frequency", title = StringResource(R.string.cpu_frequency)),
                    Switch(key = "unit_hide_cpu_usage", title = StringResource(R.string.cpu_usage)),
                    Switch(key = "unit_hide_ram_usage", title = StringResource(R.string.ram_usage))
                )
            )
        )
    )
}
