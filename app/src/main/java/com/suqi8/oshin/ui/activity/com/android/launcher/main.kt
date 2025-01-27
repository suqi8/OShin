package com.suqi8.oshin.ui.activity.com.android.launcher

import android.annotation.SuppressLint
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
import com.suqi8.oshin.ui.activity.funlistui.FunSlider
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.extra.SuperArrow

@SuppressLint("SuspiciousIndentation")
@Composable
fun launcher(navController: NavController) {
    FunPage(
        title = GetAppName(packageName = "com.android.launcher"),
        appList = listOf("com.android.launcher"),
        navController = navController
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 6.dp, top = 15.dp)
        ) {
            SuperArrow(title = stringResource(id = R.string.recent_tasks),
                onClick = {
                    navController.navigate("launcher\\recent_task")
                })
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 6.dp, top = 6.dp)
        ) {
            FunSlider(
                title = stringResource(R.string.desktop_icon_and_text_size_multiplier),
                summary = stringResource(R.string.icon_size_limit_note),
                category = "launcher",
                key = "icon_text",
                defValue = 1.0f,
                endtype = "x",
                max = 2f,
                min = 0f,
                decimalPlaces = 1
            )
        }
    }
}
