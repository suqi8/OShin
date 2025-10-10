package com.suqi8.oshin.features.systemui

import com.suqi8.oshin.R
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object notification {
    val definition = PageDefinition(
        category = "systemui\\notification",
        appList = listOf("com.android.systemui"),
        title = StringResource(R.string.status_bar_notification),
        items = listOf(
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.remove_developer_options_notification),
                        key = "remove_developer_options_notification",
                        defaultValue = false
                    ),
                    Switch(
                        title = StringResource(R.string.remove_and_do_not_disturb_notification),
                        key = "remove_and_do_not_disturb_notification",
                        defaultValue = false
                    ),
                    Switch(
                        title = StringResource(R.string.remove_active_vpn_notification),
                        summary = R.string.reboot_required_to_take_effect,
                        key = "remove_active_vpn_notification",
                        defaultValue = false
                    ),
                    Switch(
                        title = StringResource(R.string.remove_charging_complete_notification),
                        key = "remove_charging_complete_notification",
                        defaultValue = false
                    )
                )
            ),
            CardDefinition(
                titleRes = R.string.notification_restriction_message,
                items = listOf()
            )
        )
    )
}
