package com.suqi8.oshin.ui.activity.com.heytap.health

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
