package com.suqi8.oshin.ui.activity.com.android.launcher

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.components.FunPage
import com.suqi8.oshin.ui.activity.components.FunSwich
import com.suqi8.oshin.ui.activity.components.Card

@SuppressLint("SuspiciousIndentation")
@Composable
fun recent_task(navController: NavController) {
    FunPage(
        title = stringResource(R.string.recent_tasks),
        appList = listOf("com.android.launcher"),
        navController = navController
    ) {
        Card {
            FunSwich(
                title = stringResource(R.string.force_display_memory),
                category = "launcher\\recent_task",
                key = "force_display_memory",
                defValue = false
            )
        }
    }
}
