package com.suqi8.oshin.ui.activity.com.coloros.oshare

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
fun oshare(navController: NavController) {
    FunPage(
        title = GetAppName(packageName = "com.coloros.oshare"),
        appList = listOf("com.coloros.oshare"),
        navController = navController
    ) {
        Card {
            FunSwich(
                title = stringResource(R.string.remove_oshare_auto_off),
                category = "oshare",
                key = "remove_oshare_auto_off"
            )
        }
    }
}
