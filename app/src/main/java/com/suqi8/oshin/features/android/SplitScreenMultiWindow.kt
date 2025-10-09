package com.suqi8.oshin.features.android

import com.suqi8.oshin.R
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.Slider
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object SplitScreenMultiWindow {
    val definition = PageDefinition(
        category = "android\\split_screen_multi_window",
        appList = listOf("android"),
        title = StringResource(R.string.split_screen_multi_window),
        items = listOf(
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.remove_all_small_window_restrictions),
                        key = "remove_all_small_window_restrictions"
                    ),
                    Switch(
                        title = StringResource(R.string.force_multi_window_mode),
                        key = "force_multi_window_mode"
                    ),
                    Slider(
                        title = StringResource(R.string.max_simultaneous_small_windows),
                        summary = R.string.default_value_hint_negative_one,
                        key = "max_simultaneous_small_windows",
                        defaultValue = -1f,
                        valueRange = -1f..30f,
                        decimalPlaces = 0
                    ),
                    Slider(
                        title = StringResource(R.string.small_window_corner_radius),
                        summary = R.string.default_value_hint_negative_one,
                        key = "small_window_corner_radius",
                        defaultValue = -1f,
                        unit = "px",
                        valueRange = -1f..300f,
                        decimalPlaces = 0
                    ),
                    Slider(
                        title = StringResource(R.string.small_window_focused_shadow),
                        summary = R.string.default_value_hint_negative_one,
                        key = "small_window_focused_shadow",
                        defaultValue = -1f,
                        unit = "px",
                        valueRange = -1f..300f,
                        decimalPlaces = 0
                    ),
                    Slider(
                        key = "small_window_unfocused_shadow",
                        title = StringResource(R.string.small_window_unfocused_shadow),
                        summary = R.string.default_value_hint_negative_one,
                        defaultValue = -1f,
                        valueRange = -1f..300f,
                        unit = "px",
                        decimalPlaces = 0
                    )
                )
            )
        )
    )
}
