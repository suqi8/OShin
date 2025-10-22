package com.suqi8.oshin.features.securitypermission

import com.suqi8.oshin.R
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object securitypermission {
    val definition = PageDefinition(
        category = "securitypermission",
        appList = listOf("com.oplus.securitypermission"),
        title = AppName("com.oplus.securitypermission"),
        items = listOf(
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.app_start_dialog_legacy_mode_title),
                        summary = R.string.app_start_dialog_legacy_mode_summary,
                        key = "app_start_dialog_legacy_mode"
                    ),
                    Switch(
                        title = StringResource(R.string.app_start_dialog_always_allow),
                        key = "app_start_dialog_always_allow"
                    )
                )
            )
        )
    )
}
