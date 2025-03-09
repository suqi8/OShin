package com.suqi8.oshin.ui.activity.android

import androidx.compose.foundation.layout.Column
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
import com.suqi8.oshin.ui.activity.funlistui.addline
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.SmallTitle

@Composable
fun split_screen_multi_window(navController: NavController) {
    FunPage(
        title = stringResource(id = R.string.split_screen_multi_window),
        appList = listOf("android"),
        navController = navController
    ) {
        Column {
            SmallTitle(stringResource(R.string.floating_window))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 6.dp)
            ) {
                FunSwich(
                    title = stringResource(R.string.remove_all_small_window_restrictions),
                    category = "android\\split_screen_multi_window",
                    key = "remove_all_small_window_restrictions"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_multi_window_mode),
                    category = "android\\split_screen_multi_window",
                    key = "force_multi_window_mode"
                )
                addline()
                FunSlider(
                    title = stringResource(R.string.max_simultaneous_small_windows),
                    category = "android\\split_screen_multi_window",
                    summary = stringResource(R.string.default_value_hint_negative_one),
                    key = "max_simultaneous_small_windows",
                    defValue = -1,
                    max = 30f,
                    min = -1f,
                    decimalPlaces = 0
                )
                addline()
                FunSlider(
                    title = stringResource(R.string.small_window_corner_radius),
                    category = "android\\split_screen_multi_window",
                    summary = stringResource(R.string.default_value_hint_negative_one),
                    key = "small_window_corner_radius",
                    defValue = -1,
                    max = 300f,
                    endtype = "px",
                    min = -1f,
                    decimalPlaces = 0
                )
                addline()
                FunSlider(
                    title = stringResource(R.string.small_window_focused_shadow),
                    category = "android\\split_screen_multi_window",
                    summary = stringResource(R.string.default_value_hint_negative_one),
                    key = "small_window_focused_shadow",
                    defValue = -1,
                    max = 300f,
                    endtype = "px",
                    min = -1f,
                    decimalPlaces = 0
                )
                addline()
                FunSlider(
                    title = stringResource(R.string.small_window_unfocused_shadow),
                    category = "android\\split_screen_multi_window",
                    summary = stringResource(R.string.default_value_hint_negative_one),
                    key = "small_window_unfocused_shadow",
                    defValue = -1,
                    max = 300f,
                    endtype = "px",
                    min = -1f,
                    decimalPlaces = 0
                )
            }
        }
    }
}
