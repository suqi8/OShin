package com.suqi8.oshin.features.wallet

import com.suqi8.oshin.R
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object wallet {
    val definition = PageDefinition(
        category = "wallet",
        appList = listOf("com.finshell.wallet"),
        title = AppName("com.finshell.wallet"),
        items = listOf(
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.remove_swipe_page_ads),
                        summary = R.string.clear_wallet_data_notice,
                        key = "remove_swipe_page_ads"
                    )
                )
            )
        )
    )
}
