package com.suqi8.oshin.features.android

import com.suqi8.oshin.R
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object OplusServices {
    val definition = PageDefinition(
        category = "android\\oplus_system_services",
        appList = listOf("android"),
        title = StringResource(R.string.oplus_system_services),
        items = listOf(
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.oplus_root_check),
                        summary = R.string.oplus_root_check_summary,
                        key = "disable_root_check"
                    )
                )
            )
        )
    )
}
