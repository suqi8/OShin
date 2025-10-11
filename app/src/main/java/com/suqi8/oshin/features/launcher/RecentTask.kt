package com.suqi8.oshin.features.launcher

import com.suqi8.oshin.R
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object RecentTask {
    val definition = PageDefinition(
        category = "launcher\\recent_task",
        appList = listOf("com.android.launcher"),
        title = StringResource(R.string.recent_tasks),
        items = listOf(
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.force_display_memory),
                        key = "force_display_memory",
                        defaultValue = false
                    )
                )
            )
        )
    )
}
