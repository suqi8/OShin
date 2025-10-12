package com.suqi8.oshin.features.systemui

import com.suqi8.oshin.R
import com.suqi8.oshin.models.AndCondition
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.Dropdown
import com.suqi8.oshin.models.NoEnable
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.SimpleCondition
import com.suqi8.oshin.models.Slider
import com.suqi8.oshin.models.StringInput
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch
import com.suqi8.oshin.models.UrlAction

object StatusBarClock {
    val definition = PageDefinition(
        title = StringResource(R.string.status_bar_clock),
        category = "systemui\\status_bar_clock",
        appList = listOf("com.android.systemui"),
        items = listOf(
            // --- 主开关 ---
            CardDefinition(
                items = listOf(
                    Switch(
                        key = "status_bar_clock",
                        title = StringResource(R.string.status_bar_clock)
                    )
                )
            ),
            NoEnable(condition = SimpleCondition("status_bar_clock", requiredValue = false)),

            // --- 主要设置 (仅在主开关开启时显示) ---
            CardDefinition(
                condition = SimpleCondition("status_bar_clock", requiredValue = true),
                items = listOf(
                    Dropdown(
                        key = "ClockStyleSelectedOption",
                        title = StringResource(R.string.clock_style),
                        optionsRes = R.array.clock_style_options // "预设", "极客"
                    ),
                    Slider(
                        key = "ClockSize",
                        title = StringResource(R.string.clock_size),
                        summary = R.string.clock_size_summary,
                        valueRange = 0f..30f, unit = "dp", decimalPlaces = 1
                    ),
                    Slider(
                        key = "ClockUpdateSpeed",
                        title = StringResource(R.string.clock_update_time_title),
                        summary = R.string.clock_update_time_summary,
                        defaultValue = 1000f, valueRange = 0f..2000f, unit = "ms", decimalPlaces = 0
                    )
                )
            ),
            // --- 时钟边距 (仅在主开关开启时显示) ---
            CardDefinition(
                titleRes = R.string.clock_margin,
                condition = SimpleCondition("status_bar_clock", requiredValue = true),
                items = listOf(
                    Slider(
                        key = "TopPadding",
                        title = StringResource(R.string.clock_top_margin),
                        valueRange = 0f..30f,
                        unit = "dp",
                        decimalPlaces = 0
                    ),
                    Slider(
                        key = "BottomPadding",
                        title = StringResource(R.string.clock_bottom_margin),
                        valueRange = 0f..30f,
                        unit = "dp",
                        decimalPlaces = 0
                    ),
                    Slider(
                        key = "LeftPadding",
                        title = StringResource(R.string.clock_left_margin),
                        valueRange = 0f..30f,
                        unit = "dp",
                        decimalPlaces = 0
                    ),
                    Slider(
                        key = "RightPadding",
                        title = StringResource(R.string.clock_right_margin),
                        valueRange = 0f..30f,
                        unit = "dp",
                        decimalPlaces = 0
                    )
                )
            ),

            // --- “预设”风格选项 ---
            CardDefinition(
                condition = AndCondition(
                    listOf( // <-- 使用 AndCondition
                        SimpleCondition("status_bar_clock", requiredValue = true),
                        SimpleCondition("ClockStyleSelectedOption", requiredValue = 0)
                    )
                ),
                items = listOf(
                    Switch(
                        title = StringResource(R.string.show_years_title),
                        summary = R.string.show_years_summary,
                        key = "ShowYears",
                        defaultValue = false
                    ),
                    Switch(
                        title = StringResource(R.string.show_month_title),
                        summary = R.string.show_month_summary,
                        key = "ShowMonth",
                        defaultValue = false
                    ),
                    Switch(
                        title = StringResource(R.string.show_day_title),
                        summary = R.string.show_day_summary,
                        key = "ShowDay",
                        defaultValue = false
                    ),
                    Switch(
                        title = StringResource(R.string.show_week_title),
                        summary = R.string.show_week_summary,
                        key = "ShowWeek",
                        defaultValue = false
                    ),
                    Switch(
                        title = StringResource(R.string.show_cn_hour_title),
                        summary = R.string.show_cn_hour_summary,
                        key = "ShowCNHour",
                        defaultValue = false
                    ),
                    Switch(
                        title = StringResource(R.string.showtime_period_title),
                        summary = R.string.showtime_period_summary,
                        key = "Showtime_period",
                        defaultValue = false
                    ),
                    Switch(
                        title = StringResource(R.string.show_seconds_title),
                        summary = R.string.show_seconds_summary,
                        key = "ShowSeconds",
                        defaultValue = true
                    ),
                    Switch(
                        title = StringResource(R.string.show_millisecond_title),
                        summary = R.string.show_millisecond_summary,
                        key = "ShowMillisecond",
                        defaultValue = false
                    ),
                    Switch(
                        title = StringResource(R.string.hide_space_title),
                        summary = R.string.hide_space_summary,
                        key = "HideSpace",
                        defaultValue = false
                    ),
                    Switch(
                        title = StringResource(R.string.dual_row_title),
                        summary = R.string.dual_row_summary,
                        key = "DualRow",
                        defaultValue = false
                    )
                )
            ),

            // --- “极客”风格选项 ---
            CardDefinition(
                condition = AndCondition(
                    listOf( // <-- 使用 AndCondition
                        SimpleCondition("status_bar_clock", requiredValue = true),
                        SimpleCondition("ClockStyleSelectedOption", requiredValue = 1)
                    )
                ),
                items = listOf(
                    Dropdown(
                        key = "alignment",
                        title = StringResource(R.string.alignment),
                        optionsRes = R.array.hardware_indicator_gravity_options
                    ),
                    StringInput(
                        key = "CustomClockStyle",
                        title = StringResource(R.string.clock_format),
                        defaultValue = "HH:mm"
                    ),
                    UrlAction(
                        title = StringResource(R.string.clock_format_example),
                        url = "https://oshin.mikusignal.top/docs/timeformat.html"
                    )
                )
            )
        )
    )
}
