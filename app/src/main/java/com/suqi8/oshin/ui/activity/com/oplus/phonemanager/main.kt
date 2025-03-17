package com.suqi8.oshin.ui.activity.com.oplus.phonemanager

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
import top.yukonga.miuix.kmp.basic.Card

@SuppressLint("SuspiciousIndentation")
@Composable
fun oplusphonemanager(navController: NavController) {
    FunPage(
        title = GetAppName(packageName = "com.oplus.phonemanager"),
        appList = listOf("com.oplus.phonemanager"),
        navController = navController
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 6.dp, top = 6.dp)
        ) {
            FunSwich(
                title = stringResource(R.string.remove_all_popup_delays),
                summary = stringResource(R.string.remove_all_popup_delays_eg),
                category = "oplusphonemanager",
                key = "remove_all_popup_delays"
            )
        }
    }
}
