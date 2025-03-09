package com.suqi8.oshin.ui.activity.com.android.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.suqi8.oshin.GetAppName
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.funlistui.FunPage
import com.suqi8.oshin.ui.activity.funlistui.FunString
import com.suqi8.oshin.ui.activity.funlistui.WantFind
import top.yukonga.miuix.kmp.basic.Card

@SuppressLint("SuspiciousIndentation")
@Composable
fun settings(navController: NavController) {
    FunPage(
        title = GetAppName(packageName = "com.android.settings"),
        appList = listOf("com.android.settings"),
        navController = navController
    ) {
        Column {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 6.dp, top = 6.dp)
            ) {
                FunString(
                    title = stringResource(R.string.custom_display_model),
                    summary = stringResource(R.string.hint_empty_content_default),
                    category = "settings",
                    key = "custom_display_model",
                    defValue = "",
                    nullable = true
                )
            }
            WantFind(
                listOf(
                    WantFind(stringResource(R.string.auto_start_max_limit),"battery")
                ),
                navController
            )
        }
    }
}
