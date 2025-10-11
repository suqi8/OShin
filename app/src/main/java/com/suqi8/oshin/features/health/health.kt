package com.suqi8.oshin.features.health

import com.suqi8.oshin.R
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object health {
    val definition = PageDefinition(
        category = "health",
        appList = listOf("com.heytap.health"),
        title = AppName("com.heytap.health"),
        items = listOf(
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.disable_root_dialog),
                        key = "disable_root_dialog"
                    )
                )
            )
        )
    )
}
