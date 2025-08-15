package com.suqi8.oshin.ui.activity.com.finshell.wallet

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
fun wallet(navController: NavController) {
    FunPage(
        title = GetAppName(packageName = "com.finshell.wallet"),
        appList = listOf("com.finshell.wallet"),
        navController = navController
    ) {
        Card {
            FunSwich(
                title = stringResource(R.string.remove_swipe_page_ads),
                summary = stringResource(R.string.clear_wallet_data_notice),
                category = "wallet",
                key = "remove_swipe_page_ads"
            )
        }
    }
}
