package io.github.suqi8.opatch

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.highcapable.yukihookapi.hook.factory.prefs
import io.github.suqi8.opatch.application.RestartApp
import io.github.suqi8.opatch.tools.AnimTools
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperDropdown
import top.yukonga.miuix.kmp.extra.SuperSwitch
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack
import top.yukonga.miuix.kmp.theme.MiuixTheme

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
    var com_android_systemui_status_bar_clock by remember {
        mutableStateOf(false)
    }
    var ClockSize by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        ClockStyleSelectedOption.value = context.prefs("settings").getInt("ClockStyleSelectedOption")
        com_android_systemui_status_bar_clock = context.prefs("settings").getBoolean("com_android_systemui_status_bar_clock")
        ShowYears.value = context.prefs("settings").getBoolean("Status_Bar_Time_ShowYears")
        ShowMonth.value = context.prefs("settings").getBoolean("Status_Bar_Time_ShowMonth")
        ShowDay.value = context.prefs("settings").getBoolean("Status_Bar_Time_ShowDay")
        ShowWeek.value = context.prefs("settings").getBoolean("Status_Bar_Time_ShowWeek")
        ShowCNHour.value = context.prefs("settings").getBoolean("Status_Bar_Time_ShowCNHour")
        Showtime_period.value = context.prefs("settings").getBoolean("Status_Bar_Time_Showtime_period")
        ShowSeconds.value = context.prefs("settings").getBoolean("Status_Bar_Time_ShowSeconds")
        HideSpace.value = context.prefs("settings").getBoolean("Status_Bar_Time_"+"HideSpace")
        DualRow.value = context.prefs("settings").getBoolean("Status_Bar_Time_"+"DualRow")
        ClockSize = context.prefs("settings").getInt("Status_Bar_Time_ClockSize")
    }
    Scaffold(topBar = {
        TopAppBar(
            scrollBehavior = topappbarzt,
            title = stringResource(id = R.string.status_bar_clock),
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
                    RestartApp(context,"com.tencent.mobileqq")
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
                .background(MiuixTheme.colorScheme.background)
                .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal)),
            contentPadding = PaddingValues(top = padding.calculateTopPadding()),
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
                                Column {
                                    SuperArrow(title = stringResource(R.string.clock_size),
                                        summary = stringResource(R.string.clock_size_summary),
                                        onClick = {

                                        }, rightText = "${ClockSize}dp")
                                    Slider(
                                        progress = (ClockSize/15.0).toFloat(),
                                        onProgressChange = { newProgress -> ClockSize = (newProgress*15.0).toInt()
                                            context.prefs("settings").edit { putInt("Status_Bar_Time_ClockSize", (newProgress*15.0).toInt()) }
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
                                    title = "显示年份",
                                    checked = ShowYears.value,
                                    onCheckedChange = {
                                        ShowYears.value = it
                                        CoroutineScope(Dispatchers.IO).launch {
                                            context.prefs("settings").edit { putBoolean("Status_Bar_Time_ShowYears", it) }
                                        }
                                    }
                                )
                                SuperSwitch(
                                    title = "显示月份",
                                    checked = ShowMonth.value,
                                    onCheckedChange = {
                                        ShowMonth.value = it
                                        CoroutineScope(Dispatchers.IO).launch {
                                            context.prefs("settings").edit { putBoolean("Status_Bar_Time_"+"ShowMonth", it) }
                                        }
                                    }
                                )
                                SuperSwitch(
                                    title = "显示日期",
                                    checked = ShowDay.value,
                                    onCheckedChange = {
                                        ShowDay.value = it
                                        CoroutineScope(Dispatchers.IO).launch {
                                            context.prefs("settings").edit { putBoolean("Status_Bar_Time_"+"ShowDay", it) }
                                        }
                                    }
                                )
                                SuperSwitch(
                                    title = "显示星期",
                                    checked = ShowWeek.value,
                                    onCheckedChange = {
                                        ShowWeek.value = it
                                        CoroutineScope(Dispatchers.IO).launch {
                                            context.prefs("settings").edit { putBoolean("Status_Bar_Time_"+"ShowWeek", it) }
                                        }
                                    }
                                )
                                SuperSwitch(
                                    title = "显示时辰",
                                    checked = ShowCNHour.value,
                                    onCheckedChange = {
                                        ShowCNHour.value = it
                                        CoroutineScope(Dispatchers.IO).launch {
                                            context.prefs("settings").edit { putBoolean("Status_Bar_Time_"+"ShowCNHour", it) }
                                        }
                                    }
                                )
                                SuperSwitch(
                                    title = "显示时段",
                                    checked = Showtime_period.value,
                                    onCheckedChange = {
                                        Showtime_period.value = it
                                        CoroutineScope(Dispatchers.IO).launch {
                                            context.prefs("settings").edit { putBoolean("Status_Bar_Time_"+"Showtime_period", it) }
                                        }
                                    }
                                )
                                SuperSwitch(
                                    title = "显示秒数",
                                    checked = ShowSeconds.value,
                                    onCheckedChange = {
                                        ShowSeconds.value = it
                                        CoroutineScope(Dispatchers.IO).launch {
                                            context.prefs("settings").edit { putBoolean("Status_Bar_Time_"+"ShowSeconds", it) }
                                        }
                                    }
                                )
                                SuperSwitch(
                                    title = "隐藏间隔",
                                    checked = HideSpace.value,
                                    onCheckedChange = {
                                        HideSpace.value = it
                                        CoroutineScope(Dispatchers.IO).launch {
                                            context.prefs("settings").edit { putBoolean("Status_Bar_Time_"+"HideSpace", it) }
                                        }
                                    }
                                )
                                SuperSwitch(
                                    title = "双排显示",
                                    checked = DualRow.value,
                                    onCheckedChange = {
                                        DualRow.value = it
                                        CoroutineScope(Dispatchers.IO).launch {
                                            context.prefs("settings").edit { putBoolean("Status_Bar_Time_"+"DualRow", it) }
                                        }
                                    }
                                )
                            }
                        }
                    }
                    AnimatedVisibility(visible = ClockStyleSelectedOption.value == 1 && com_android_systemui_status_bar_clock == true,
                        enter = AnimTools().enterTransition(0),
                        exit = AnimTools().exitTransition(100)) { }
                }
            }
        }
    }
}

