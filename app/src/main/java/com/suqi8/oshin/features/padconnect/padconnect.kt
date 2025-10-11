package com.suqi8.oshin.features.padconnect

import com.suqi8.oshin.R
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object padconnect {
    val definition = PageDefinition(
        category = "padconnect",
        appList = listOf("com.oplus.padconnect"),
        title = AppName("com.oplus.padconnect"),
        items = listOf(
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.bypass_same_account_unlock_safety_check),
                        summary = R.string.bypass_same_account_unlock_safety_check_summary,
                        key = "bypass_same_account_unlock_safety_check"
                    )
                )
            )
        )
    )
}
