package com.suqi8.oshin.features.mihealth

import com.suqi8.oshin.R
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object mihealth {
    val definition = PageDefinition(
        category = "mihealth",
        appList = listOf("com.mi.health"),
        title = AppName("com.mi.health"),
        items = listOf(
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.enable_alarm_reminder),
                        summary = R.string.alarm_reminder_description,
                        key = "enable_alarm_reminder"
                    )
                )
            )
        )
    )
}
