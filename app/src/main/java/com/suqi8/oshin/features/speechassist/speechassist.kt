package com.suqi8.oshin.features.speechassist

import com.suqi8.oshin.R
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object speechassist {
    val definition = PageDefinition(
        category = "speechassist",
        appList = listOf("com.heytap.speechassist"),
        title = AppName("com.heytap.speechassist"),
        items = listOf(
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.force_enable_xiaobu_call),
                        key = "ai_call"
                    )
                )
            )
        )
    )
}
