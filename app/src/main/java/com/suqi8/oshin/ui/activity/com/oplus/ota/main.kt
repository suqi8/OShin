package com.suqi8.oshin.ui.activity.com.oplus.ota

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.components.FunPage
import com.suqi8.oshin.ui.activity.components.FunSwich
import com.suqi8.oshin.ui.activity.components.addline
import com.suqi8.oshin.utils.GetAppName
import com.suqi8.oshin.ui.activity.components.Card

@SuppressLint("SuspiciousIndentation")
@Composable
fun ota(navController: NavController) {
    FunPage(
        title = GetAppName(packageName = "com.oplus.ota"),
        appList = listOf("com.oplus.ota"),
        navController = navController
    ) {
        Card {
            FunSwich(
                title = stringResource(R.string.remove_system_update_dialog),
                category = "ota",
                key = "remove_system_update_dialog"
            )
            addline()
            FunSwich(
                title = stringResource(R.string.remove_system_update_notification),
                category = "ota",
                key = "remove_system_update_notification"
            )
            addline()
            FunSwich(
                title = stringResource(R.string.remove_wlan_auto_download_dialog),
                category = "ota",
                key = "remove_wlan_auto_download_dialog"
            )
            addline()
            FunSwich(
                title = stringResource(R.string.remove_unlock_and_dmverity_check),
                category = "ota",
                key = "remove_unlock_and_dmverity_check"
            )
        }
    }
}
