package com.suqi8.oshin.ui.activity.com.heytap.speechassist

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
fun speechassist(navController: NavController) {
    FunPage(
        title = GetAppName(packageName = "com.heytap.speechassist"),
        appList = listOf("com.heytap.speechassist"),
        navController = navController
    ) {
        Card {
            FunSwich(
                title = stringResource(R.string.force_enable_xiaobu_call),
                category = "speechassist",
                key = "ai_call"
            )
        }
    }
}
