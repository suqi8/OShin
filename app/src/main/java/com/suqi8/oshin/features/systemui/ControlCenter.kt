package com.suqi8.oshin.features.systemui

import com.suqi8.oshin.R
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.DisplayCondition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object ControlCenter {
    val definition = PageDefinition(
        category = "systemui\\controlCenter",
        appList = listOf("com.android.systemui"),
        title = StringResource(R.string.control_center),
        items = listOf(
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.enlarge_media_cover),
                        summary = R.string.media_cover_background_description,
                        key = "enlarge_media_cover",
                        defaultValue = false,
                    ),
                    Switch(
                        title = StringResource(R.string.qs_media_auto_color_label),
                        key = "qs_media_auto_color_label",
                        defaultValue = true,
                        condition = DisplayCondition(
                            dependencyKey = "enlarge_media_cover",
                            requiredValue = true
                        )
                    )
                )
            )
        )
    )
}
