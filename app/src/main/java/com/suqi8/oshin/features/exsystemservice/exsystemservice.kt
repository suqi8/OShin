package com.suqi8.oshin.features.exsystemservice

import com.suqi8.oshin.R
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object exsystemservice {
    val definition = PageDefinition(
        category = "exsystemservice",
        appList = listOf("com.oplus.exsystemservice"),
        title = AppName("com.oplus.exsystemservice"),
        items = listOf(
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.remove_system_tamper_warning),
                        key = "remove_system_tamper_warning"
                    )
                )
            )
        )
    )
}
