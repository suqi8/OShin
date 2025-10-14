package com.suqi8.oshin.features.systemui

import com.suqi8.oshin.R
import com.suqi8.oshin.models.Action
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.Operator
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.RelatedLinks
import com.suqi8.oshin.models.SimpleCondition
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object systemui {
    val definition = PageDefinition(
        category = "systemui",
        appList = listOf("com.android.systemui"),
        title = AppName("com.android.systemui"),
        items = listOf(
            CardDefinition(
                items = listOf(
                    Action(
                        title = StringResource(id = R.string.status_bar_clock),
                        route = "systemui\\status_bar_clock"
                    ),
                    Action(
                        title = StringResource(id = R.string.network_speed_indicator),
                        route = "systemui\\status_bar_wifi"
                    ),
                    Action(
                        title = StringResource(id = R.string.hardware_indicator),
                        route = "systemui\\hardware_indicator"
                    ),
                    /*Action(
                        title = StringResource(id = R.string.status_bar_icon),
                        route = "systemui\\statusbar_icon"
                    ),*/
                    Action(
                        title = StringResource(id = R.string.status_bar_notification),
                        route = "systemui\\notification"
                    ),
                    Action(
                        title = StringResource(id = R.string.control_center),
                        route = "systemui\\controlCenter"
                    ),
                    Action(
                        title = StringResource(id = R.string.status_bar_layout),
                        route = "systemui\\StatusBarLayout"
                    )
                )
            ),
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.hide_status_bar),
                        key = "hide_status_bar",
                        defaultValue = false
                    ),
                    Switch(
                        title = StringResource(R.string.show_real_battery),
                        summary = R.string.show_real_battery_summary,
                        key = "show_real_battery"
                    )
                )
            ),
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.enable_all_day_screen_off),
                        key = "enable_all_day_screen_off"
                    ),
                    Switch(
                        title = StringResource(R.string.force_trigger_ltpo),
                        key = "force_trigger_ltpo",
                        defaultValue = true,
                        condition = SimpleCondition(
                            dependencyKey = "enable_all_day_screen_off", // 它依赖的项的 key
                            operator = Operator.EQUALS,                  // 依赖项的值必须等于
                            requiredValue = true                         // true
                        )
                    )
                )
            ),
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.disable_data_transfer_auth),
                        key = "disable_data_transfer_auth",
                        defaultValue = false
                    ),
                    Switch(
                        title = StringResource(R.string.usb_default_file_transfer),
                        key = "usb_default_file_transfer",
                        defaultValue = false
                    ),
                    Switch(
                        title = StringResource(R.string.remove_usb_selection_dialog),
                        key = "remove_usb_selection_dialog",
                        defaultValue = false
                    ),
                    Switch(
                        title = StringResource(R.string.toast_force_show_app_icon),
                        summary = R.string.toast_icon_source_module,
                        key = "toast_force_show_app_icon",
                        defaultValue = false
                    )
                )
            ),
            RelatedLinks(
                links = listOf(
                    RelatedLinks.Link(
                        R.string.security_payment_remove_risky_fluid_cloud,
                        "securepay"
                    ),
                    RelatedLinks.Link(R.string.low_battery_fluid_cloud_off, "battery")
                )
            )
        )
    )
}
