package com.suqi8.oshin.ui.activity.com.oplus.exsystemservice

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
fun exsystemservice(navController: NavController) {
    FunPage(
        title = GetAppName(packageName = "com.oplus.exsystemservice"),
        appList = listOf("com.oplus.exsystemservice"),
        navController = navController
    ) {
        Card {
            FunSwich(
                title = stringResource(R.string.remove_system_tamper_warning),
                category = "exsystemservice",
                key = "remove_system_tamper_warning"
            )
        }
    }
}
