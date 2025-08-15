package com.suqi8.oshin.ui.activity.com.coloros.securepay

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
fun securepay(navController: NavController) {
    FunPage(
        title = GetAppName(packageName = "com.coloros.securepay"),
        appList = listOf("com.coloros.securepay"),
        navController = navController
    ) {
        Card {
            FunSwich(
                title = stringResource(R.string.security_payment_remove_risky_fluid_cloud),
                category = "securepay",
                key = "security_payment_remove_risky_fluid_cloud"
            )
        }
    }
}
