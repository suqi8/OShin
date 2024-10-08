package io.github.suqi8.opatch

import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.highcapable.yukihookapi.hook.factory.prefs
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import io.github.suqi8.opatch.application.RestartApp
import io.github.suqi8.opatch.tools.AnimTools
import io.github.suqi8.opatch.ui.tools.resetApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.extra.SuperDropdown
import top.yukonga.miuix.kmp.extra.SuperSwitch
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.showDialog
import top.yukonga.miuix.kmp.utils.getWindowSize

@SuppressLint("RtlHardcoded")
@Composable
fun Fun_com_android_systemui_status_bar_clock(navController: NavController) {
    val context = LocalContext.current
    val topappbarzt = MiuixScrollBehavior(top.yukonga.miuix.kmp.basic.rememberTopAppBarState())
    val ClockStyle = listOf(stringResource(R.string.preset),
        stringResource(R.string.geek))
    val ClockStyleSelectedOption = remember { mutableStateOf(0) }
    val ShowYears = remember { mutableStateOf(false) }
    val ShowMonth = remember { mutableStateOf(false) }
    val ShowDay = remember { mutableStateOf(false) }
    val ShowWeek = remember { mutableStateOf(false) }
    val ShowCNHour = remember { mutableStateOf(false) }
    val Showtime_period = remember { mutableStateOf(false) }
    val ShowSeconds = remember { mutableStateOf(false) }
    val HideSpace = remember { mutableStateOf(false) }
    val DualRow = remember { mutableStateOf(false) }
    val ShowMillisecond = remember { mutableStateOf(false) }
    var com_android_systemui_status_bar_clock by remember {
        mutableStateOf(false)
    }
    var ClockSize = remember { mutableStateOf(0) }
    var ClockUpdateSpeed = remember { mutableStateOf(0) }
    val appList = listOf("com.android.systemui")
    val RestartAPP = remember { mutableStateOf(false) }
    val resetApp = resetApp()
    val Status_Bar_Time_gravitySelectedOption = remember { mutableStateOf(0) }
    val showCustomClockDialog = remember { mutableStateOf(false) }
    val customClockCache = remember { mutableStateOf("HH:mm") }
    val customClock = remember { mutableStateOf("HH:mm") }
    val ClockLeftPadding = remember { mutableStateOf(0) }
    val ClockRightPadding = remember { mutableStateOf(0) }
    val ClockTopPadding = remember { mutableStateOf(0) }
    val ClockBottomPadding = remember { mutableStateOf(0) }
    val showclock_update_timeDialog = remember { mutableStateOf(false) }
    val showClockSizeDialog = remember { mutableStateOf(false) }
    val showClockLeftPaddingDialog = remember { mutableStateOf(false) }
    val showClockRightPaddingDialog = remember { mutableStateOf(false) }
    val showClockTopPaddingDialog = remember { mutableStateOf(false) }
    val showClockBottomPaddingDialog = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val ClockSizeTitle = stringResource(R.string.clock_size)
    val ClockUpdateSpeedTitle = stringResource(R.string.clock_update_time_title)
    val ClockLeftPaddingTitle = stringResource(R.string.clock_left_margin)
    val ClockRightPaddingTitle = stringResource(R.string.clock_right_margin)
    val ClockTopPaddingTitle = stringResource(R.string.clock_top_margin)
    val ClockBottomPaddingTitle = stringResource(R.string.clock_bottom_margin)

    var isDebug = context.prefs("settings").getBoolean("Debug", false)

    val alpha = context.prefs("settings").getFloat("AppAlpha", 0.75f)
    val blurRadius: Dp = context.prefs("settings").getInt("AppblurRadius", 25).dp
    val noiseFactor = context.prefs("settings").getFloat("AppnoiseFactor", 0f)
    val containerColor: Color = MiuixTheme.colorScheme.background
    val hazeState = remember { HazeState() }
    val hazeStyle = remember(containerColor, alpha, blurRadius, noiseFactor) {
        HazeStyle(
            backgroundColor = containerColor,
            tint = HazeTint.Color(containerColor.copy(alpha)),
            blurRadius = blurRadius,
            noiseFactor = noiseFactor
        )
    }

    LaunchedEffect(Unit) {
        ClockLeftPadding.value = context.prefs("settings").getInt("Status_Bar_Time_LeftPadding", 0)
        ClockRightPadding.value = context.prefs("settings").getInt("Status_Bar_Time_RightPadding", 0)
        ClockTopPadding.value = context.prefs("settings").getInt("Status_Bar_Time_TopPadding", 0)
        ClockBottomPadding.value = context.prefs("settings").getInt("Status_Bar_Time_BottomPadding", 0)
        ClockStyleSelectedOption.value = context.prefs("settings").getInt("ClockStyleSelectedOption", 0)
        com_android_systemui_status_bar_clock = context.prefs("settings").getBoolean("com_android_systemui_status_bar_clock", false)
        ShowYears.value = context.prefs("settings").getBoolean("Status_Bar_Time_ShowYears", false)
        ShowMonth.value = context.prefs("settings").getBoolean("Status_Bar_Time_ShowMonth", false)
        ShowDay.value = context.prefs("settings").getBoolean("Status_Bar_Time_ShowDay", false)
        ShowWeek.value = context.prefs("settings").getBoolean("Status_Bar_Time_ShowWeek", false)
        ShowCNHour.value = context.prefs("settings").getBoolean("Status_Bar_Time_ShowCNHour", false)
        Showtime_period.value = context.prefs("settings").getBoolean("Status_Bar_Time_Showtime_period", false)
        ShowSeconds.value = context.prefs("settings").getBoolean("Status_Bar_Time_ShowSeconds", false)
        ShowMillisecond.value = context.prefs("settings").getBoolean("Status_Bar_Time_ShowMillisecond", false)
        HideSpace.value = context.prefs("settings").getBoolean("Status_Bar_Time_"+"HideSpace", false)
        DualRow.value = context.prefs("settings").getBoolean("Status_Bar_Time_"+"DualRow", false)
        ClockSize.value = context.prefs("settings").getInt("Status_Bar_Time_ClockSize",0)
        ClockUpdateSpeed.value = context.prefs("settings").getInt("Status_Bar_Time_ClockUpdateSpeed",0)
        Status_Bar_Time_gravitySelectedOption.value = context.prefs("settings").getInt("Status_Bar_Time_alignment", 0)
        customClock.value = context.prefs("settings").getString("Status_Bar_Time_CustomClockStyle", "HH:mm")
        customClockCache.value = context.prefs("settings").getString("Status_Bar_Time_CustomClockStyle", "HH:mm")
    }
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
    Scaffold(topBar = {
        TopAppBar(
            scrollBehavior = topappbarzt,
            title = stringResource(id = R.string.status_bar_clock),
            color = Color.Transparent,
            modifier = Modifier.hazeChild(
                state = hazeState,
                style = hazeStyle),
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                },
                    modifier = Modifier.padding(start = 18.dp)) {
                    Icon(
                        imageVector = MiuixIcons.ArrowBack,
                        contentDescription = null,
                        tint = MiuixTheme.colorScheme.onBackground
                    )
                }
            },
            actions = {
                // 如果你有其他操作按钮，这里可以添加
                IconButton(onClick = {
                    RestartAPP.value = true
                },
                    modifier = Modifier.padding(end = 18.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = null,
                        tint = MiuixTheme.colorScheme.onBackground
                    )
                }
            }
        )
    }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .haze(state = hazeState)
                .height(getWindowSize().height.dp)
                .background(MiuixTheme.colorScheme.background)
                .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal)),
            contentPadding = PaddingValues(top = padding.calculateTopPadding()),
            enableOverScroll = true,
            topAppBarScrollBehavior = topappbarzt
        ) {
            item {
                Column {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 6.dp,top = 15.dp)
                    ) {
                        SuperSwitch(
                            title = stringResource(R.string.status_bar_clock),
                            onCheckedChange = {
                                com_android_systemui_status_bar_clock = it
                                context.prefs("settings").edit { putBoolean("com_android_systemui_status_bar_clock", it) }
                            },
                            checked = com_android_systemui_status_bar_clock
                        )
                    }
                    AnimatedVisibility(
                        visible = !com_android_systemui_status_bar_clock
                    ) {
                        Column {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                color = Color.Red.copy(alpha = 0.1f)
                            ) {
                                BasicComponent(
                                    title = stringResource(R.string.no_start_func),
                                    titleColor = Color.Red
                                )
                            }
                        }
                    }
                    AnimatedVisibility(
                        visible = com_android_systemui_status_bar_clock,
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
                                    selectedIndex = ClockStyleSelectedOption.value,
                                    onSelectedIndexChange = { newOption ->
                                        ClockStyleSelectedOption.value = newOption
                                        CoroutineScope(Dispatchers.IO).launch {
                                            context.prefs("settings").edit { putInt("ClockStyleSelectedOption", newOption) }
                                        }
                                    }
                                )
                                addline()
                                Column {
                                    SuperArrow(
                                        title = ClockSizeTitle,
                                        summary = stringResource(R.string.clock_size_summary),
                                        onClick = {
                                            showClockSizeDialog.value = true
                                        },
                                        rightText = "${ClockSize.value}dp"
                                    )
                                    Slider(
                                        progress = (ClockSize.value / 20.0).toFloat(),
                                        onProgressChange = { newProgress ->
                                            ClockSize.value = (newProgress * 20.0).toInt()
                                            context.prefs("settings").edit { putInt("Status_Bar_Time_ClockSize", (newProgress * 20.0).toInt()) }
                                        },
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                                    )
                                }
                                addline()
                                Column {
                                    SuperArrow(
                                        title = stringResource(R.string.clock_update_time_title),
                                        summary = stringResource(R.string.clock_update_time_summary),
                                        onClick = {
                                            showclock_update_timeDialog.value = true
                                        },
                                        rightText = "${ClockUpdateSpeed.value}ms"
                                    )
                                    Slider(
                                        progress = (ClockUpdateSpeed.value / 2000.000).toFloat(),
                                        onProgressChange = { newProgress ->
                                            ClockUpdateSpeed.value = (newProgress * 2000).toInt()
                                            context.prefs("settings").edit { putInt("Status_Bar_Time_ClockUpdateSpeed", (newProgress * 2000).toInt()) }
                                        },
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                                    )
                                }
                                addline()
                                Text("dp To px", modifier = Modifier.padding(start = 15.dp, top = 16.dp))
                                val px = remember { mutableStateOf("0") }
                                TextField(value = px.value, onValueChange = { px.value = it }, modifier = Modifier.padding(start = 12.dp, end = 12.dp))
                                if (px.value.isNotEmpty()) {
                                    AnimatedVisibility(visible = px.value.isNotEmpty()) {
                                        SmallTitle("${px.value}dp = ${dpToPx(px.value.toFloat(),context)}px")
                                    }
                                }
                                addline()
                                Column {
                                    SuperArrow(
                                        title = stringResource(R.string.clock_top_margin),
                                        onClick = {
                                            showClockTopPaddingDialog.value = true
                                        },
                                        rightText = "${ClockTopPadding.value}px"
                                    )
                                    Slider(
                                        progress = (ClockTopPadding.value / 100.000).toFloat(),
                                        onProgressChange = { newProgress ->
                                            ClockTopPadding.value = (newProgress * 100).toInt()
                                            context.prefs("settings").edit { putInt("Status_Bar_Time_TopPadding", (newProgress * 100).toInt()) }
                                        },
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                                    )
                                }
                                addline()
                                Column {
                                    SuperArrow(
                                        title = stringResource(R.string.clock_bottom_margin),
                                        onClick = {
                                            showClockBottomPaddingDialog.value = true
                                        },
                                        rightText = "${ClockBottomPadding.value}px"
                                    )
                                    Slider(
                                        progress = (ClockBottomPadding.value / 100.000).toFloat(),
                                        onProgressChange = { newProgress ->
                                            ClockBottomPadding.value = (newProgress * 100).toInt()
                                            context.prefs("settings").edit { putInt("Status_Bar_Time_BottomPadding", (newProgress * 100).toInt()) }
                                        },
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                                    )
                                }
                                addline()
                                Column {
                                    SuperArrow(
                                        title = stringResource(R.string.clock_left_margin),
                                        onClick = {
                                            showClockLeftPaddingDialog.value = true
                                        },
                                        rightText = "${ClockLeftPadding.value}px"
                                    )
                                    Slider(
                                        progress = (ClockLeftPadding.value / 100.000).toFloat(),
                                        onProgressChange = { newProgress ->
                                            ClockLeftPadding.value = (newProgress * 100).toInt()
                                            context.prefs("settings").edit { putInt("Status_Bar_Time_LeftPadding", (newProgress * 100).toInt()) }
                                        },
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                                    )
                                }
                                addline()
                                Column {
                                    SuperArrow(
                                        title = stringResource(R.string.clock_right_margin),
                                        onClick = {
                                            showClockRightPaddingDialog.value = true
                                        },
                                        rightText = "${ClockRightPadding.value}px"
                                    )
                                    Slider(
                                        progress = (ClockRightPadding.value / 100.000).toFloat(),
                                        onProgressChange = { newProgress ->
                                            ClockRightPadding.value = (newProgress * 100).toInt()
                                            context.prefs("settings").edit { putInt("Status_Bar_Time_RightPadding", (newProgress * 100).toInt()) }
                                        },
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                                    )
                                }
                            }
                        }
                    }
                    AnimatedVisibility(visible = ClockStyleSelectedOption.value == 0 && com_android_systemui_status_bar_clock == true) {
                        Column {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                SuperSwitch(
                                    title = stringResource(R.string.show_years_title),
                                    summary = stringResource(R.string.show_years_summary),
                                    checked = ShowYears.value,
                                    onCheckedChange = {
                                        ShowYears.value = it
                                        CoroutineScope(Dispatchers.IO).launch {
                                            context.prefs("settings").edit { putBoolean("Status_Bar_Time_ShowYears", it) }
                                        }
                                    }
                                )
                                addline()
                                SuperSwitch(
                                    title = stringResource(R.string.show_month_title),
                                    summary = stringResource(R.string.show_month_summary),
                                    checked = ShowMonth.value,
                                    onCheckedChange = {
                                        ShowMonth.value = it
                                        CoroutineScope(Dispatchers.IO).launch {
                                            context.prefs("settings").edit { putBoolean("Status_Bar_Time_ShowMonth", it) }
                                        }
                                    }
                                )
                                addline()
                                SuperSwitch(
                                    title = stringResource(R.string.show_day_title),
                                    summary = stringResource(R.string.show_day_summary),
                                    checked = ShowDay.value,
                                    onCheckedChange = {
                                        ShowDay.value = it
                                        CoroutineScope(Dispatchers.IO).launch {
                                            context.prefs("settings").edit { putBoolean("Status_Bar_Time_ShowDay", it) }
                                        }
                                    }
                                )
                                addline()
                                SuperSwitch(
                                    title = stringResource(R.string.show_week_title),
                                    summary = stringResource(R.string.show_week_summary),
                                    checked = ShowWeek.value,
                                    onCheckedChange = {
                                        ShowWeek.value = it
                                        CoroutineScope(Dispatchers.IO).launch {
                                            context.prefs("settings").edit { putBoolean("Status_Bar_Time_ShowWeek", it) }
                                        }
                                    }
                                )
                                addline()
                                SuperSwitch(
                                    title = stringResource(R.string.show_cn_hour_title),
                                    summary = stringResource(R.string.show_cn_hour_summary),
                                    checked = ShowCNHour.value,
                                    onCheckedChange = {
                                        ShowCNHour.value = it
                                        CoroutineScope(Dispatchers.IO).launch {
                                            context.prefs("settings").edit { putBoolean("Status_Bar_Time_ShowCNHour", it) }
                                        }
                                    }
                                )
                                addline()
                                SuperSwitch(
                                    title = stringResource(R.string.showtime_period_title),
                                    summary = stringResource(R.string.showtime_period_summary),
                                    checked = Showtime_period.value,
                                    onCheckedChange = {
                                        Showtime_period.value = it
                                        CoroutineScope(Dispatchers.IO).launch {
                                            context.prefs("settings").edit { putBoolean("Status_Bar_Time_Showtime_period", it) }
                                        }
                                    }
                                )
                                addline()
                                SuperSwitch(
                                    title = stringResource(R.string.show_seconds_title),
                                    summary = stringResource(R.string.show_seconds_summary),
                                    checked = ShowSeconds.value,
                                    onCheckedChange = {
                                        ShowSeconds.value = it
                                        CoroutineScope(Dispatchers.IO).launch {
                                            context.prefs("settings").edit { putBoolean("Status_Bar_Time_ShowSeconds", it) }
                                        }
                                    }
                                )
                                addline()
                                SuperSwitch(
                                    title = stringResource(R.string.show_millisecond_title),
                                    summary = stringResource(R.string.show_millisecond_summary),
                                    checked = ShowMillisecond.value,
                                    onCheckedChange = {
                                        ShowMillisecond.value = it
                                        CoroutineScope(Dispatchers.IO).launch {
                                            context.prefs("settings").edit { putBoolean("Status_Bar_Time_ShowMillisecond", it) }
                                        }
                                    }
                                )
                                addline()
                                SuperSwitch(
                                    title = stringResource(R.string.hide_space_title),
                                    summary = stringResource(R.string.hide_space_summary),
                                    checked = HideSpace.value,
                                    onCheckedChange = {
                                        HideSpace.value = it
                                        CoroutineScope(Dispatchers.IO).launch {
                                            context.prefs("settings").edit { putBoolean("Status_Bar_Time_HideSpace", it) }
                                        }
                                    }
                                )
                                addline()
                                SuperSwitch(
                                    title = stringResource(R.string.dual_row_title),
                                    summary = stringResource(R.string.dual_row_summary),
                                    checked = DualRow.value,
                                    onCheckedChange = {
                                        DualRow.value = it
                                        CoroutineScope(Dispatchers.IO).launch {
                                            context.prefs("settings").edit { putBoolean("Status_Bar_Time_DualRow", it) }
                                        }
                                    }
                                )
                            }
                        }
                    }
                    AnimatedVisibility(visible = ClockStyleSelectedOption.value == 1 && com_android_systemui_status_bar_clock == true,
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
                                    selectedIndex = Status_Bar_Time_gravitySelectedOption.value,
                                    onSelectedIndexChange = { newOption ->
                                        Status_Bar_Time_gravitySelectedOption.value = newOption
                                        CoroutineScope(Dispatchers.IO).launch {
                                            context.prefs("settings").edit { putInt("Status_Bar_Time_alignment", newOption) }
                                        }
                                    }
                                )
                                addline()
                                SuperArrow(title = stringResource(R.string.clock_format)
                                    , rightText = customClock.value, onClick = {
                                        showCustomClockDialog.value = true
                                    })
                                SmallTitle(text = stringResource(R.string.status_bar_clock_custom_tips), insideMargin = DpSize(18.dp, 8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
    resetApp.AppRestartScreen(appList,RestartAPP)
    CustomClockDialog(showCustomClockDialog,customClockCache,customClock,focusManager)
    SettingIntDialog(context,showClockSizeDialog,ClockSizeTitle,ClockSize,focusManager,"Status_Bar_Time_ClockSize")
    SettingIntDialog(context,showclock_update_timeDialog,ClockUpdateSpeedTitle,ClockUpdateSpeed,focusManager,"Status_Bar_Time_ClockUpdateSpeed")
    SettingIntDialog(context,showClockTopPaddingDialog,ClockTopPaddingTitle,ClockTopPadding,focusManager,"Status_Bar_Time_TopPadding")
    SettingIntDialog(context,showClockBottomPaddingDialog,ClockBottomPaddingTitle,ClockBottomPadding,focusManager,"Status_Bar_Time_BottomPadding")
    SettingIntDialog(context,showClockLeftPaddingDialog,ClockLeftPaddingTitle,ClockLeftPadding,focusManager,"Status_Bar_Time_LeftPadding")
    SettingIntDialog(context,showClockRightPaddingDialog,ClockRightPaddingTitle,ClockRightPadding,focusManager,"Status_Bar_Time_RightPadding")
}

fun pxToDp(px: Float, context: Context): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        px,
        context.resources.displayMetrics
    ) / context.resources.displayMetrics.density
}

fun dpToPx(dp: Float, context: Context): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
    )
}

@Composable
fun CustomClockDialog(showCustomClockDialog: MutableState<Boolean>, customClockCache: MutableState<String>, CustomClock: MutableState<String>, focusManager: androidx.compose.ui.focus.FocusManager) {
    val context = LocalContext.current
    if (!showCustomClockDialog.value) return
    showDialog(content = {
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
                Button(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.cancel),
                    onClick = {
                        dismissDialog()
                        showCustomClockDialog.value = false
                    }
                )
                Spacer(Modifier.width(12.dp))
                Button(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.ok),
                    submit = true,
                    onClick = {
                        dismissDialog()
                        CustomClock.value = customClockCache.value
                        context.prefs("settings").edit { putString("Status_Bar_Time_CustomClockStyle", customClockCache.value) }
                        showCustomClockDialog.value = false
                    }
                )
            }
        }
    })

}

@Composable
fun SettingIntDialog(context: Context,
                     show: MutableState<Boolean>,
                     title: String,
                     set: MutableState<Int>,
                     focusManager: FocusManager,
                     saveName: String) {
    if (!show.value) return
    val cache = remember { mutableStateOf(set.value.toString()) }
    showDialog(content = {
        SuperDialog(title = stringResource(R.string.settings)+" "+title,
            show = show,
            onDismissRequest = {
                show.value = false
            }) {
            TextField(
                value = cache.value,
                onValueChange = { cache.value = it },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )
            AnimatedVisibility((cache.value.isEmpty())) {
                SmallTitle(stringResource(R.string.content_not_empty), textColor = Color.Red,
                    insideMargin = DpSize(0.dp, 8.dp))
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.cancel),
                    onClick = {
                        dismissDialog()
                        show.value = false
                    }
                )
                Spacer(Modifier.width(12.dp))
                Button(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.ok),
                    submit = true,
                    enabled = (cache.value.isNotEmpty()),
                    onClick = {
                        dismissDialog()
                        set.value = cache.value.toInt()
                        context.prefs("settings").edit { putInt(saveName, cache.value.toInt()) }
                        show.value = false
                    }
                )
            }
        }
    })
}
