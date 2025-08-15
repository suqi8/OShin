package com.suqi8.oshin.ui.activity.com.heytap.health

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
fun health(navController: NavController) {
    FunPage(
        title = GetAppName(packageName = "com.heytap.health"),
        appList = listOf("com.heytap.health"),
        navController = navController
    ) {
        Card {
            FunSwich(
                title = stringResource(R.string.disable_root_dialog),
                category = "health",
                key = "disable_root_dialog"
            )
        }
    }
}
