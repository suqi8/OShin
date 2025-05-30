package com.suqi8.oshin.ui.activity.com.android.systemui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.funlistui.FunPage
import com.suqi8.oshin.ui.activity.funlistui.FunSwich
import com.suqi8.oshin.ui.activity.funlistui.addline
import top.yukonga.miuix.kmp.basic.Card

@SuppressLint("SuspiciousIndentation")
@Composable
fun notification(navController: NavController) {
    FunPage(
        title = stringResource(R.string.status_bar_notification),
        appList = listOf("com.android.systemui"),
        navController = navController
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 6.dp, top = 6.dp)
        ) {
            FunSwich(
                title = stringResource(R.string.remove_developer_options_notification),
                summary = stringResource(R.string.notification_restriction_message),
                category = "systemui\\notification",
                key = "remove_developer_options_notification",
                defValue = false
            )
            addline()
            FunSwich(
                title = stringResource(R.string.remove_and_do_not_disturb_notification),
                summary = stringResource(R.string.notification_restriction_message),
                category = "systemui\\notification",
                key = "remove_and_do_not_disturb_notification",
                defValue = false
            )
        }
    }
}
