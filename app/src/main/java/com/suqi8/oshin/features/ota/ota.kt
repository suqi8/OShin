package com.suqi8.oshin.features.ota

import com.suqi8.oshin.R
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object ota {
    val definition = PageDefinition(
        category = "ota",
        appList = listOf("com.oplus.ota"),
        title = AppName("com.oplus.ota"),
        items = listOf(
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.remove_system_update_dialog),
                        key = "remove_system_update_dialog"
                    ),
                    Switch(
                        title = StringResource(R.string.remove_system_update_notification),
                        key = "remove_system_update_notification"
                    ),
                    Switch(
                        title = StringResource(R.string.remove_wlan_auto_download_dialog),
                        key = "remove_wlan_auto_download_dialog"
                    ),
                    Switch(
                        title = StringResource(R.string.remove_unlock_and_dmverity_check),
                        key = "remove_unlock_and_dmverity_check"
                    ),
                    Switch(
                        title = StringResource(R.string.bypass_preinstall_checks),
                        summary = R.string.bypass_preinstall_checks_summary,
                        key = "bypass_preinstall_checks"
                    ),
                    Switch(
                        title = StringResource(R.string.force_show_local_install),
                        key = "force_show_local_install"
                    ),
                    Switch(
                        title = StringResource(R.string.force_download_last_update_package),
                        summary = R.string.force_download_last_update_package_summary,
                        key = "force_download_last_update_package"
                    )
                )
            )
        )
    )
}
