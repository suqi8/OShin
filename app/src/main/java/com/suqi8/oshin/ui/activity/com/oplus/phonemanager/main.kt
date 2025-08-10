package com.suqi8.oshin.ui.activity.com.oplus.phonemanager

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
import com.suqi8.oshin.ui.activity.funlistui.FunString
import com.suqi8.oshin.ui.activity.funlistui.FunSwich
import com.suqi8.oshin.ui.activity.funlistui.addline
import com.suqi8.oshin.utils.GetAppName
import com.suqi8.oshin.ui.activity.funlistui.Card

@SuppressLint("SuspiciousIndentation")
@Composable
fun oplusphonemanager(navController: NavController) {
    FunPage(
        title = GetAppName(packageName = "com.oplus.phonemanager"),
        appList = listOf("com.oplus.phonemanager"),
        navController = navController
    ) {
        Card {
            FunSwich(
                title = stringResource(R.string.remove_all_popup_delays),
                summary = stringResource(R.string.remove_all_popup_delays_eg),
                category = "oplusphonemanager",
                key = "remove_all_popup_delays"
            )
            addline()
            FunSlider(
                title = stringResource(R.string.custom_score),
                summary = stringResource(R.string.default_value_hint_negative_one),
                category = "oplusphonemanager",
                key = "custom_score",
                defValue = -1,
                min = -1f,
                max = 100f,
                decimalPlaces = 0
            )
            addline()
            FunString(
                title = stringResource(R.string.custom_prompt_content),
                category = "oplusphonemanager",
                key = "custom_prompt_content",
                defValue = "",
                nullable = true
            )
            addline()
            FunSlider(
                title = stringResource(R.string.custom_animation_duration),
                summary = stringResource(R.string.default_value_hint_negative_one),
                category = "oplusphonemanager",
                key = "custom_animation_duration",
                defValue = -1,
                endtype = "ms",
                min = -1f,
                max = 10000f,
                decimalPlaces = 0
            )
        }
    }
}
