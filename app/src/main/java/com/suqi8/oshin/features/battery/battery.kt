package com.suqi8.oshin.features.battery

import com.suqi8.oshin.R
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.Slider
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object battery {
    val definition = PageDefinition(
        category = "battery",
        appList = listOf("com.oplus.battery"),
        title = AppName("com.oplus.battery"),
        items = listOf(
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.low_battery_fluid_cloud_off),
                        key = "low_battery_fluid_cloud"
                    )
                )
            ),
            CardDefinition(
                items = listOf(
                    Slider(
                        title = StringResource(R.string.auto_start_max_limit),
                        summary = R.string.auto_start_default_hint,
                        key = "auto_start_max_limit",
                        defaultValue = 5f,
                        valueRange = 0f..100f,
                        decimalPlaces = 0
                    )
                )
            )
        )
    )
}
