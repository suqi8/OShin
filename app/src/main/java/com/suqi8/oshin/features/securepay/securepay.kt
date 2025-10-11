package com.suqi8.oshin.features.securepay

import com.suqi8.oshin.R
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object securepay {
    val definition = PageDefinition(
        category = "securepay",
        appList = listOf("com.coloros.securepay"),
        title = AppName("com.coloros.securepay"),
        items = listOf(
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.security_payment_remove_risky_fluid_cloud),
                        key = "security_payment_remove_risky_fluid_cloud"
                    )
                )
            )
        )
    )
}
