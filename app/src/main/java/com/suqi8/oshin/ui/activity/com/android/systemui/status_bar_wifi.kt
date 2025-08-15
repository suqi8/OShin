package com.suqi8.oshin.ui.activity.com.android.systemui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.R
import com.suqi8.oshin.tools.AnimTools
import com.suqi8.oshin.ui.activity.components.FunDropdown
import com.suqi8.oshin.ui.activity.components.FunNoEnable
import com.suqi8.oshin.ui.activity.components.FunPage
import com.suqi8.oshin.ui.activity.components.FunSlider
import com.suqi8.oshin.ui.activity.components.FunSwich
import com.suqi8.oshin.ui.activity.components.addline
import com.suqi8.oshin.ui.activity.components.Card

@SuppressLint("RtlHardcoded")
@Composable
fun status_bar_wifi(navController: NavController) {
    val context = LocalContext.current
    FunPage(
        title = stringResource(id = R.string.network_speed_indicator),
        appList = listOf("com.android.systemui"),
        navController = navController
    ) {
        Column {
            var status_bar_wifi by remember {
                mutableStateOf(
                    context.prefs("systemui\\status_bar_wifi").getBoolean("status_bar_wifi", false)
                )
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 6.dp, top = 6.dp)
            ) {
                FunSwich(
                    title = stringResource(R.string.network_speed_indicator),
                    category = "systemui\\status_bar_wifi",
                    key = "status_bar_wifi",
                    defValue = false,
                    onCheckedChange = {
                        status_bar_wifi = it
                    }
                )
            }
            AnimatedVisibility(
                visible = !status_bar_wifi
            ) {
                FunNoEnable()
            }
            val Style = listOf(
                stringResource(R.string.default_mode),
                stringResource(R.string.upload_download)
            )
            AnimatedVisibility(
                visible = status_bar_wifi,
                enter = AnimTools().enterTransition(0),
                exit = AnimTools().exitTransition(100)
            ) {
                Column {
                    val selected = remember {
                        mutableStateOf(
                            context.prefs("systemui\\status_bar_wifi")
                                .getInt("StyleSelectedOption", 0)
                        )
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        FunDropdown(
                            title = stringResource(R.string.network_speed_style),
                            category = "systemui\\status_bar_wifi",
                            key = "StyleSelectedOption",
                            selectedList = Style,
                            onCheckedChange = {
                                selected.value = it
                            }
                        )
                        AnimatedVisibility(selected.value == 0) {
                            Column {
                                addline()
                                FunSlider(
                                    title = stringResource(R.string.speed_font_size),
                                    summary = stringResource(R.string.default_value_hint_negative_one),
                                    category = "systemui\\status_bar_wifi",
                                    key = "speed_font_size",
                                    defValue = -1,
                                    endtype = "sp",
                                    max = 20f,
                                    min = -1f,
                                    decimalPlaces = 0
                                )
                                addline()
                                FunSlider(
                                    title = stringResource(R.string.unit_font_size),
                                    summary = stringResource(R.string.default_value_hint_negative_one),
                                    category = "systemui\\status_bar_wifi",
                                    key = "unit_font_size",
                                    defValue = -1,
                                    endtype = "sp",
                                    max = 20f,
                                    min = -1f,
                                    decimalPlaces = 0
                                )
                            }
                        }
                        AnimatedVisibility(selected.value == 1) {
                            Column {
                                addline()
                                FunSlider(
                                    title = stringResource(R.string.upload_font_size),
                                    summary = stringResource(R.string.default_value_hint_negative_one),
                                    category = "systemui\\status_bar_wifi",
                                    key = "upload_font_size",
                                    defValue = -1,
                                    endtype = "sp",
                                    max = 20f,
                                    min = -1f,
                                    decimalPlaces = 0
                                )
                                addline()
                                FunSlider(
                                    title = stringResource(R.string.download_font_size),
                                    summary = stringResource(R.string.default_value_hint_negative_one),
                                    category = "systemui\\status_bar_wifi",
                                    key = "download_font_size",
                                    defValue = -1,
                                    endtype = "sp",
                                    max = 20f,
                                    min = -1f,
                                    decimalPlaces = 0
                                )
                            }
                        }
                        addline()
                        FunSlider(
                            title = stringResource(R.string.slow_speed_threshold),
                            category = "systemui\\status_bar_wifi",
                            key = "slow_speed_threshold",
                            defValue = 20,
                            endtype = "KB/S",
                            max = 1024f,
                            min = 0f,
                            decimalPlaces = 0
                        )
                        val hide_on_slow = remember {
                            mutableStateOf(
                                context.prefs("systemui\\status_bar_wifi")
                                    .getBoolean("hide_on_slow", false)
                            )
                        }
                        addline()
                        FunSwich(
                            title = stringResource(R.string.hide_on_slow),
                            category = "systemui\\status_bar_wifi",
                            key = "hide_on_slow",
                            onCheckedChange = {
                                hide_on_slow.value = it
                            }
                        )
                        AnimatedVisibility(hide_on_slow.value && selected.value == 1) {
                            addline()
                            FunSwich(
                                title = stringResource(R.string.hide_when_both_slow),
                                category = "systemui\\status_bar_wifi",
                                key = "hide_when_both_slow"
                            )
                        }
                        AnimatedVisibility(selected.value == 1) {
                            Column {
                                val icon_indicator = remember { mutableStateOf(context.prefs("systemui\\status_bar_wifi").getInt("icon_indicator", 0)) }
                                addline()
                                FunDropdown(
                                    title = stringResource(R.string.icon_indicator),
                                    category = "systemui\\status_bar_wifi",
                                    key = "icon_indicator",
                                    selectedList = listOf(stringResource(R.string.no_icon),"△▽▲▼","▵▿▴▾","☖⛉☗⛊","↑↓","⇧⇩"),
                                    onCheckedChange = {
                                        icon_indicator.value = it
                                    }
                                )
                                AnimatedVisibility(icon_indicator.value != 0) {
                                    addline()
                                    FunSwich(
                                        title = stringResource(R.string.position_speed_indicator_front),
                                        category = "systemui\\status_bar_wifi",
                                        key = "position_speed_indicator_front"
                                    )
                                }
                                addline()
                                FunSwich(
                                    title = stringResource(R.string.hide_space),
                                    category = "systemui\\status_bar_wifi",
                                    key = "hide_space"
                                )
                                addline()
                                FunSwich(
                                    title = stringResource(R.string.hide_bs),
                                    category = "systemui\\status_bar_wifi",
                                    key = "hide_bs"
                                )
                                addline()
                                FunSwich(
                                    title = stringResource(R.string.swap_upload_download),
                                    category = "systemui\\status_bar_wifi",
                                    key = "swap_upload_download"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
