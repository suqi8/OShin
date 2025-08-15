package com.suqi8.oshin.ui.activity.com.heytap.quicksearchbox

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
fun quicksearchbox(navController: NavController) {
    FunPage(
        title = GetAppName(packageName = "com.heytap.quicksearchbox"),
        appList = listOf("com.heytap.quicksearchbox"),
        navController = navController
    ) {
        Card {
            FunSwich(
                title = stringResource(R.string.remove_app_recommendation_ads),
                category = "quicksearchbox",
                key = "remove_app_recommendation_ads"
            )
        }
    }
}
