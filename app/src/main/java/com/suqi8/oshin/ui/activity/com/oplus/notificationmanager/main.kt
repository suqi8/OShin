package com.suqi8.oshin.ui.activity.com.oplus.notificationmanager

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
import com.suqi8.oshin.utils.GetAppName
import com.suqi8.oshin.ui.activity.funlistui.Card

@SuppressLint("SuspiciousIndentation")
@Composable
fun notificationmanager(navController: NavController) {
    FunPage(
        title = GetAppName(packageName = "com.oplus.notificationmanager"),
        appList = listOf("com.oplus.notificationmanager"),
        navController = navController
    ) {
        Card {
            FunSwich(
                title = stringResource(R.string.allow_turn_off_all_categories),
                summary = stringResource(R.string.enable_all_category_control_summary),
                category = "notificationmanager",
                key = "allow_turn_off_all_categories"
            )
        }
    }
}
