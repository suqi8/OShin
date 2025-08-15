package com.suqi8.oshin.ui.activity.com.android.mms

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.components.FunPage
import com.suqi8.oshin.ui.activity.components.FunSwich
import com.suqi8.oshin.utils.GetAppName
import com.suqi8.oshin.ui.activity.components.Card

@SuppressLint("SuspiciousIndentation")
@Composable
fun mms(navController: NavController) {
    FunPage(
        title = GetAppName(packageName = "com.android.mms"),
        appList = listOf("com.android.mms"),
        navController = navController
    ) {
        Card {
            FunSwich(
                title = stringResource(R.string.remove_message_ads),
                category = "mms",
                key = "remove_message_ads",
            )
        }
    }
}
