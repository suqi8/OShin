package com.suqi8.oshin.features.oshare

import com.suqi8.oshin.R
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object oshare {
    val definition = PageDefinition(
        category = "oshare",
        appList = listOf("com.coloros.oshare"),
        title = AppName("com.coloros.oshare"),
        items = listOf(
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.remove_oshare_auto_off),
                        key = "remove_oshare_auto_off"
                    )
                )
            )
        )
    )
}
