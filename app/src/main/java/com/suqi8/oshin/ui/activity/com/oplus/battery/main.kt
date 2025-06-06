package com.suqi8.oshin.ui.activity.com.oplus.battery

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
import com.suqi8.oshin.ui.activity.funlistui.FunSlider
import com.suqi8.oshin.ui.activity.funlistui.FunSwich
import com.suqi8.oshin.utils.GetAppName
import top.yukonga.miuix.kmp.basic.Card

@SuppressLint("SuspiciousIndentation")
@Composable
fun battery(navController: NavController) {
    FunPage(
        title = GetAppName(packageName = "com.oplus.battery"),
        appList = listOf("com.oplus.battery"),
        navController = navController
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 6.dp, top = 6.dp)
        ) {
            FunSwich(
                title = stringResource(R.string.low_battery_fluid_cloud_off),
                category = "battery",
                key = "low_battery_fluid_cloud"
            )
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 6.dp, top = 6.dp)
        ) {
            FunSlider(
                title = stringResource(R.string.auto_start_max_limit),
                summary = stringResource(R.string.auto_start_default_hint),
                category = "battery",
                key = "auto_start_max_limit",
                defValue = 5,
                max = 100f,
                min = 0f,
                decimalPlaces = 0
            )
        }
    }
}
