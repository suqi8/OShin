package com.suqi8.oshin.features.phonemanager

import com.suqi8.oshin.R
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.Slider
import com.suqi8.oshin.models.StringInput
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object phonemanager {
    val definition = PageDefinition(
        category = "phonemanager",
        appList = listOf("com.coloros.phonemanager"),
        title = AppName("com.coloros.phonemanager"),
        items = listOf(
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.remove_all_popup_delays),
                        summary = R.string.remove_all_popup_delays_eg,
                        key = "remove_all_popup_delays"
                    ),
                    Slider(
                        title = StringResource(R.string.custom_score),
                        summary = R.string.default_value_hint_negative_one,
                        key = "custom_score",
                        defaultValue = -1f,
                        valueRange = -1f..100f,
                        decimalPlaces = 0
                    ),
                    StringInput(
                        title = StringResource(R.string.custom_prompt_content),
                        key = "custom_prompt_content",
                        defaultValue = "",
                        nullable = true
                    ),
                    Slider(
                        title = StringResource(R.string.custom_animation_duration),
                        summary = R.string.default_value_hint_negative_one,
                        key = "custom_animation_duration",
                        defaultValue = -1f,
                        unit = "ms",
                        valueRange = -1f..10000f,
                        decimalPlaces = 0
                    )
                )
            )
        )
    )
}
