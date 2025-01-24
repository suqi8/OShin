package com.suqi8.oshin.ui.activity.com.android.systemui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.TypedValue
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.R
import com.suqi8.oshin.tools.AnimTools
import com.suqi8.oshin.ui.activity.funlistui.FunNoEnable
import com.suqi8.oshin.ui.activity.funlistui.FunPage
import com.suqi8.oshin.ui.activity.funlistui.FunSlider
import com.suqi8.oshin.ui.activity.funlistui.FunSwich
import com.suqi8.oshin.ui.activity.funlistui.addline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.extra.SuperDropdown
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog

@SuppressLint("RtlHardcoded")
@Composable
fun status_bar_clock(navController: NavController) {
    val context = LocalContext.current
    val Status_Bar_Time_gravitySelectedOption = remember { mutableIntStateOf(context.prefs("systemui\\Status_Bar_Time").getInt("alignment", 0)) }
    val showCustomClockDialog = remember { mutableStateOf(false) }
    val customClockCache = remember { mutableStateOf(context.prefs("systemui\\Status_Bar_Time").getString("CustomClockStyle", "HH:mm")) }
    val customClock = remember { mutableStateOf(context.prefs("systemui\\Status_Bar_Time").getString("CustomClockStyle", "HH:mm")) }
    val focusManager = LocalFocusManager.current
    val Status_Bar_Time_gravityOptions = listOf(
        stringResource(R.string.status_bar_time_gravity_center),
        stringResource(R.string.status_bar_time_gravity_top),
        stringResource(R.string.status_bar_time_gravity_bottom),
        stringResource(R.string.status_bar_time_gravity_end),
        stringResource(R.string.status_bar_time_gravity_center_horizontal),
        stringResource(R.string.status_bar_time_gravity_center_vertical),
        stringResource(R.string.status_bar_time_gravity_fill),
        stringResource(R.string.status_bar_time_gravity_fill_horizontal),
        stringResource(R.string.status_bar_time_gravity_fill_vertical)
    )
    FunPage(
        title = stringResource(id = R.string.status_bar_clock),
        appList = listOf("com.android.systemui"),
        navController = navController
    ) {
        Column {
            var status_bar_clock by remember { mutableStateOf(context.prefs("systemui\\status_bar_clock").getBoolean("status_bar_clock", false)) }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 6.dp, top = 15.dp)
            ) {
                FunSwich(
                    title = stringResource(R.string.status_bar_clock),
                    category = "systemui\\status_bar_clock",
                    key = "status_bar_clock",
                    defValue = false,
                    onCheckedChange = {
                        status_bar_clock = it
                    }
                )
            }
            AnimatedVisibility(
                visible = !status_bar_clock
            ) {
                FunNoEnable()
            }
            val ClockStyleSelectedOption = remember { mutableIntStateOf(context.prefs("systemui\\Status_Bar_Time").getInt("ClockStyleSelectedOption", 0)) }
            val ClockStyle = listOf(stringResource(R.string.preset), stringResource(R.string.geek))
            AnimatedVisibility(
                visible = status_bar_clock,
                enter = AnimTools().enterTransition(0),
                exit = AnimTools().exitTransition(100)
            ) {
                Column {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        SuperDropdown(
                            title = stringResource(R.string.clock_style),
                            items = ClockStyle,
                            selectedIndex = ClockStyleSelectedOption.intValue,
                            onSelectedIndexChange = { newOption ->
                                ClockStyleSelectedOption.intValue = newOption
                                CoroutineScope(Dispatchers.IO).launch {
                                    context.prefs("systemui\\Status_Bar_Time").edit { putInt("ClockStyleSelectedOption", newOption) }
                                }
                            }
                        )
                        addline()
                        FunSlider(
                            title = stringResource(R.string.clock_size),
                            summary = stringResource(R.string.clock_size_summary),
                            category = "systemui\\Status_Bar_Time",
                            key = "ClockSize",
                            defValue = 0f,
                            endtype = "dp",
                            max = 30f,
                            min = 0f,
                            decimalPlaces = 1
                        )
                        addline()
                        FunSlider(
                            title = stringResource(R.string.clock_update_time_title),
                            summary = stringResource(R.string.clock_update_time_summary),
                            category = "systemui\\Status_Bar_Time",
                            key = "ClockUpdateSpeed",
                            defValue = 0,
                            endtype = "ms",
                            max = 2000f,
                            min = 0f,
                            decimalPlaces = 0
                        )
                    }
                    SmallTitle("dp To px")
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 6.dp)
                    ) {
                        val px = remember { mutableStateOf("0") }
                        TextField(value = px.value, onValueChange = { px.value = it }, modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp))
                        if (px.value.isNotEmpty()) {
                            AnimatedVisibility(visible = px.value.isNotEmpty()) {
                                SmallTitle(text = "${px.value}dp = ${dpToPx(px.value.toFloat(),context)}px")
                            }
                        }
                    }
                    SmallTitle(stringResource(R.string.clock_margin))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 6.dp)
                    ) {
                        FunSlider(
                            title = stringResource(R.string.clock_top_margin),
                            category = "systemui\\Status_Bar_Time",
                            key = "TopPadding",
                            defValue = 0,
                            endtype = "px",
                            max = 300f,
                            min = 0f,
                            decimalPlaces = 0
                        )
                        addline()
                        FunSlider(
                            title = stringResource(R.string.clock_bottom_margin),
                            category = "systemui\\Status_Bar_Time",
                            key = "BottomPadding",
                            defValue = 0,
                            endtype = "px",
                            max = 300f,
                            min = 0f,
                            decimalPlaces = 0
                        )
                        addline()
                        FunSlider(
                            title = stringResource(R.string.clock_left_margin),
                            category = "systemui\\Status_Bar_Time",
                            key = "LeftPadding",
                            defValue = 0,
                            endtype = "px",
                            max = 300f,
                            min = 0f,
                            decimalPlaces = 0
                        )
                        addline()
                        FunSlider(
                            title = stringResource(R.string.clock_right_margin),
                            category = "systemui\\Status_Bar_Time",
                            key = "RightPadding",
                            defValue = 0,
                            endtype = "px",
                            max = 300f,
                            min = 0f,
                            decimalPlaces = 0
                        )
                    }
                }
            }
            AnimatedVisibility(visible = ClockStyleSelectedOption.intValue == 0 && status_bar_clock) {
                Column {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        FunSwich(
                            title = stringResource(R.string.show_years_title),
                            summary = stringResource(R.string.show_years_summary),
                            category = "systemui\\status_bar_clock",
                            key = "ShowYears",
                            defValue = false
                        )
                        addline()
                        FunSwich(
                            title = stringResource(R.string.show_month_title),
                            summary = stringResource(R.string.show_month_summary),
                            category = "systemui\\status_bar_clock",
                            key = "ShowMonth",
                            defValue = false
                        )
                        addline()
                        FunSwich(
                            title = stringResource(R.string.show_day_title),
                            summary = stringResource(R.string.show_day_summary),
                            category = "systemui\\status_bar_clock",
                            key = "ShowDay",
                            defValue = false
                        )
                        addline()
                        FunSwich(
                            title = stringResource(R.string.show_week_title),
                            summary = stringResource(R.string.show_week_summary),
                            category = "systemui\\status_bar_clock",
                            key = "ShowWeek",
                            defValue = false
                        )
                        addline()
                        FunSwich(
                            title = stringResource(R.string.show_cn_hour_title),
                            summary = stringResource(R.string.show_cn_hour_summary),
                            category = "systemui\\status_bar_clock",
                            key = "ShowCNHour",
                            defValue = false
                        )
                        addline()
                        FunSwich(
                            title = stringResource(R.string.showtime_period_title),
                            summary = stringResource(R.string.showtime_period_summary),
                            category = "systemui\\status_bar_clock",
                            key = "Showtime_period",
                            defValue = false
                        )
                        addline()
                        FunSwich(
                            title = stringResource(R.string.show_seconds_title),
                            summary = stringResource(R.string.show_seconds_summary),
                            category = "systemui\\status_bar_clock",
                            key = "ShowSeconds",
                            defValue = true
                        )
                        addline()
                        FunSwich(
                            title = stringResource(R.string.show_millisecond_title),
                            summary = stringResource(R.string.show_millisecond_summary),
                            category = "systemui\\status_bar_clock",
                            key = "ShowMillisecond",
                            defValue = false
                        )
                        addline()
                        FunSwich(
                            title = stringResource(R.string.hide_space_title),
                            summary = stringResource(R.string.hide_space_summary),
                            category = "systemui\\status_bar_clock",
                            key = "HideSpace",
                            defValue = false
                        )
                        addline()
                        FunSwich(
                            title = stringResource(R.string.dual_row_title),
                            summary = stringResource(R.string.dual_row_summary),
                            category = "systemui\\status_bar_clock",
                            key = "DualRow",
                            defValue = false
                        )
                    }
                }
            }
            AnimatedVisibility(visible = ClockStyleSelectedOption.intValue == 1 && status_bar_clock,
                enter = AnimTools().enterTransition(0),
                exit = AnimTools().exitTransition(100)) {

                Column {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        SuperDropdown(
                            title = stringResource(R.string.alignment),
                            items = Status_Bar_Time_gravityOptions,
                            selectedIndex = Status_Bar_Time_gravitySelectedOption.intValue,
                            onSelectedIndexChange = { newOption ->
                                Status_Bar_Time_gravitySelectedOption.intValue = newOption
                                CoroutineScope(Dispatchers.IO).launch {
                                    context.prefs("systemui\\Status_Bar_Time").edit { putInt("alignment", newOption) }
                                }
                            }
                        )
                        addline()
                        SuperArrow(title = stringResource(R.string.clock_format)
                            , rightText = customClock.value, onClick = {
                                showCustomClockDialog.value = true
                            })
                        addline()
                        SuperArrow(title = stringResource(R.string.clock_format_example), onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://oshin.mikusignal.top/docs/timeformat.html")
                            )
                            context.startActivity(intent)
                        })
                        //SmallTitle(text = stringResource(R.string.status_bar_clock_custom_tips), insideMargin = PaddingValues(18.dp, 8.dp))
                    }
                }
            }
        }
    }
    CustomClockDialog(showCustomClockDialog,customClockCache,customClock,focusManager)
}

fun dpToPx(dp: Float, context: Context): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
    )
}

@Composable
fun CustomClockDialog(showCustomClockDialog: MutableState<Boolean>, customClockCache: MutableState<String>, CustomClock: MutableState<String>, focusManager: FocusManager) {
    val context = LocalContext.current
    if (!showCustomClockDialog.value) return
    SuperDialog(title = stringResource(R.string.clock_format),
        show = showCustomClockDialog,
        onDismissRequest = {
            showCustomClockDialog.value = false
        }) {
        TextField(
            value = customClockCache.value,
            onValueChange = { customClockCache.value = it },
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
            singleLine = false
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.cancel),
                onClick = {
                    dismissDialog(showCustomClockDialog)
                }
            )
            Spacer(Modifier.width(12.dp))
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.ok),
                colors = ButtonDefaults.textButtonColorsPrimary(),
                onClick = {
                    dismissDialog(showCustomClockDialog)
                    CustomClock.value = customClockCache.value
                    context.prefs("systemui\\Status_Bar_Time").edit {
                        putString(
                            "CustomClockStyle",
                            customClockCache.value
                        )
                    }
                }
            )
        }
    }
}
