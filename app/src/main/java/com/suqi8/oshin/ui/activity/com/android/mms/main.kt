package com.suqi8.oshin.ui.activity.com.android.mms

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.funlistui.FunPage
import com.suqi8.oshin.ui.activity.funlistui.FunSwich
import com.suqi8.oshin.utils.GetAppName
import com.suqi8.oshin.ui.activity.funlistui.Card

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
