package com.suqi8.oshin.ui.activity.com.coloros.ocrscanner

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.funlistui.Card
import com.suqi8.oshin.ui.activity.funlistui.FunPage
import com.suqi8.oshin.ui.activity.funlistui.FunSwich
import com.suqi8.oshin.utils.GetAppName

@SuppressLint("SuspiciousIndentation")
@Composable
fun ocrscanner(navController: NavController) {
    FunPage(
        title = GetAppName(packageName = "com.coloros.ocrscanner"),
        appList = listOf("com.coloros.ocrscanner"),
        navController = navController
    ) {
         Card {
            FunSwich(
                title = stringResource(R.string.remove_full_screen_translation_restriction),
                category = "ocrscanner",
                key = "full_screen_translation"
            )
        }
    }
}
