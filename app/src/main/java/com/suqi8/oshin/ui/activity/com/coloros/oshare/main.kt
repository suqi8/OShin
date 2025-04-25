package com.suqi8.oshin.ui.activity.com.coloros.oshare

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

@SuppressLint("SuspiciousIndentation")
@Composable
fun oshare(navController: NavController) {
    FunPage(
        title = GetAppName(packageName = "com.coloros.oshare"),
        appList = listOf("com.coloros.oshare"),
        navController = navController
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 6.dp, top = 6.dp)
        ) {
            FunSlider(
                title = stringResource(R.string.transfer_time_modify),
                category = "oshare",
                key = "transfer_time_modify",
                summary = stringResource(R.string.transfer_time_effect),
                defValue = 10,
                endtype = "m",
                max = 60f,
                min = -10f,
                decimalPlaces = 0
            )
        }
    }
}
