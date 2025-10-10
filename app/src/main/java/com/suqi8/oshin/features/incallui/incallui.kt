package com.suqi8.oshin.features.incallui

import com.suqi8.oshin.R
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object incallui {
    val definition = PageDefinition(
        category = "incallui",
        appList = listOf("com.android.incallui"),
        title = AppName("com.android.incallui"),
        items = listOf(
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.hide_call_ringtone),
                        key = "hide_call_ringtone",
                    )
                )
            )
        )
    )
}
