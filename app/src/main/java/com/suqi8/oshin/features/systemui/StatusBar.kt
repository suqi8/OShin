package com.suqi8.oshin.features.systemui

import com.suqi8.oshin.R
import com.suqi8.oshin.models.Action
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object StatusBar {
    val definition = PageDefinition(
        category = "systemui\\status_bar",
        appList = listOf("com.android.systemui"),
        title = StringResource(R.string.status_bar),
        items = listOf(
            CardDefinition(
                items = listOf(
                    Action(
                        title = StringResource(id = R.string.status_bar_clock),
                        route = "systemui\\status_bar\\status_bar_clock"
                    ),
                    Action(
                        title = StringResource(id = R.string.network_speed_indicator),
                        route = "systemui\\status_bar\\status_bar_wifi"
                    ),
                    Action(
                        title = StringResource(id = R.string.hardware_indicator),
                        route = "systemui\\status_bar\\hardware_indicator"
                    ),
                    Action(
                        title = StringResource(id = R.string.status_bar_layout),
                        route = "systemui\\status_bar\\StatusBarLayout"
                    )
                )
            ),
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.hide_status_bar),
                        key = "hide_status_bar",
                        defaultValue = false
                    ),
                    Switch(
                        title = StringResource(R.string.show_real_battery),
                        summary = R.string.show_real_battery_summary,
                        key = "show_real_battery"
                    )
                )
            ),
        )
    )
}
