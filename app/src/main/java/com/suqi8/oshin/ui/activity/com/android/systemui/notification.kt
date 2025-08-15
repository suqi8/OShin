package com.suqi8.oshin.ui.activity.com.android.systemui

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.components.FunPage
import com.suqi8.oshin.ui.activity.components.FunSwich
import com.suqi8.oshin.ui.activity.components.addline
import com.suqi8.oshin.ui.activity.components.Card
import top.yukonga.miuix.kmp.basic.SmallTitle

@SuppressLint("SuspiciousIndentation")
@Composable
fun notification(navController: NavController) {
    FunPage(
        title = stringResource(R.string.status_bar_notification),
        appList = listOf("com.android.systemui"),
        navController = navController
    ) {
        Card {
            FunSwich(
                title = stringResource(R.string.remove_developer_options_notification),
                category = "systemui\\notification",
                key = "remove_developer_options_notification",
                defValue = false
            )
            addline()
            FunSwich(
                title = stringResource(R.string.remove_and_do_not_disturb_notification),
                category = "systemui\\notification",
                key = "remove_and_do_not_disturb_notification",
                defValue = false
            )
            addline()
            FunSwich(
                title = stringResource(R.string.remove_active_vpn_notification),
                summary = stringResource(R.string.reboot_required_to_take_effect),
                category = "systemui\\notification",
                key = "remove_active_vpn_notification",
                defValue = false
            )
            addline()
            FunSwich(
                title = stringResource(R.string.remove_charging_complete_notification),
                category = "systemui\\notification",
                key = "remove_charging_complete_notification",
                defValue = false
            )
        }
        SmallTitle(stringResource(R.string.notification_restriction_message))
    }
}
