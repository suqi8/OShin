package com.suqi8.oshin.features.weather

import com.suqi8.oshin.R
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object weather {
    val definition = PageDefinition(
        category = "weather",
        appList = listOf("com.coloros.weather2"),
        title = AppName("com.coloros.weather2"),
        items = listOf(
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.remove_second_page_ads),
                        key = "remove_second_page_ads"
                    )
                )
            )
        )
    )
}
