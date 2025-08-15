package com.suqi8.oshin.ui.activity.com.oplus.battery

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.components.FunPage
import com.suqi8.oshin.ui.activity.components.FunSlider
import com.suqi8.oshin.ui.activity.components.FunSwich
import com.suqi8.oshin.utils.GetAppName
import com.suqi8.oshin.ui.activity.components.Card

@SuppressLint("SuspiciousIndentation")
@Composable
fun battery(navController: NavController) {
    FunPage(
        title = GetAppName(packageName = "com.oplus.battery"),
        appList = listOf("com.oplus.battery"),
        navController = navController
    ) {
        Card {
            FunSwich(
                title = stringResource(R.string.low_battery_fluid_cloud_off),
                category = "battery",
                key = "low_battery_fluid_cloud"
            )
        }
        Card {
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
