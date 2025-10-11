package com.suqi8.oshin.features.ocrscanner

import com.suqi8.oshin.R
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object ocrscanner {
    val definition = PageDefinition(
        category = "ocrscanner",
        appList = listOf("com.coloros.ocrscanner"),
        title = AppName("com.coloros.ocrscanner"),
        items = listOf(
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.remove_full_screen_translation_restriction),
                        key = "full_screen_translation"
                    )
                )
            )
        )
    )
}
