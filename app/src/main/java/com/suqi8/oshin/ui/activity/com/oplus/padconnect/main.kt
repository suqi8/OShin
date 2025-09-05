package com.suqi8.oshin.ui.activity.com.oplus.padconnect

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.components.Card
import com.suqi8.oshin.ui.activity.components.FunPage
import com.suqi8.oshin.ui.activity.components.FunSwich
import com.suqi8.oshin.utils.GetAppName

@SuppressLint("SuspiciousIndentation")
@Composable
fun padconnect(navController: NavController) {
    FunPage(
        title = GetAppName(packageName = "com.oplus.padconnect"),
        appList = listOf("com.oplus.padconnect"),
        navController = navController
    ) {
        Card {
            FunSwich(
                title = stringResource(R.string.bypass_same_account_unlock_safety_check),
                summary = stringResource(R.string.bypass_same_account_unlock_safety_check_summary),
                category = "padconnect",
                key = "bypass_same_account_unlock_safety_check"
            )
        }
    }
}
