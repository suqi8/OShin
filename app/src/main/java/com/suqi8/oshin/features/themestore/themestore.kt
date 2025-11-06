package com.suqi8.oshin.features.themestore

import com.suqi8.oshin.R
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object themestore {
    val definition = PageDefinition(
        category = "themestore",
        appList = listOf("com.heytap.themestore"),
        title = AppName("com.heytap.themestore"),
        items = listOf(
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.unlock_themestore_vip_features),
                        key = "unlock_themestore_vip_features"
                    )
                )
            ),
        )
    )
}
