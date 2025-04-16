package com.suqi8.oshin.ui.activity.com.oplus.ota

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.suqi8.oshin.GetAppName
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.funlistui.FunPage
import com.suqi8.oshin.ui.activity.funlistui.FunSwich
import com.suqi8.oshin.ui.activity.funlistui.addline
import top.yukonga.miuix.kmp.basic.Card

@SuppressLint("SuspiciousIndentation")
@Composable
fun ota(navController: NavController) {
    FunPage(
        title = GetAppName(packageName = "com.oplus.ota"),
        appList = listOf("com.oplus.ota"),
        navController = navController
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 6.dp, top = 6.dp)
        ) {
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
        }
    }
}
