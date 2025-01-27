package com.suqi8.oshin.ui.activity.com.android.settings

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.suqi8.oshin.GetAppName
import com.suqi8.oshin.ui.activity.funlistui.FunPage

@SuppressLint("SuspiciousIndentation")
@Composable
fun settings(navController: NavController) {
    FunPage(
        title = GetAppName(packageName = "com.android.settings"),
        appList = listOf("com.android.settings"),
        navController = navController
    ) {
    }
}
