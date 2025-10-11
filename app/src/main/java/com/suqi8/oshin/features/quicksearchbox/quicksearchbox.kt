package com.suqi8.oshin.features.quicksearchbox

import com.suqi8.oshin.R
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object quicksearchbox {
    val definition = PageDefinition(
        category = "quicksearchbox",
        appList = listOf("com.heytap.quicksearchbox"),
        title = AppName("com.heytap.quicksearchbox"),
        items = listOf(
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.remove_app_recommendation_ads),
                        key = "remove_app_recommendation_ads"
                    )
                )
            )
        )
    )
}
