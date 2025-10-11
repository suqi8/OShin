package com.suqi8.oshin.features.notificationmanager

import com.suqi8.oshin.R
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object notificationmanager {
    val definition = PageDefinition(
        category = "notificationmanager",
        appList = listOf("com.oplus.notificationmanager"),
        title = AppName("com.oplus.notificationmanager"),
        items = listOf(
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.allow_turn_off_all_categories),
                        summary = R.string.enable_all_category_control_summary,
                        key = "allow_turn_off_all_categories"
                    )
                )
            )
        )
    )
}
