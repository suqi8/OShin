package io.github.suqi8.opatch

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.FirstBaseline
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

@SuppressLint("RtlHardcoded")
@Composable
fun Fun_com_android_systemui_hardware_indicator(navController: NavController) {
    val context = LocalContext.current
    val topappbarzt = MiuixScrollBehavior(top.yukonga.miuix.kmp.basic.rememberTopAppBarState())
    val RestartAPP = remember { mutableStateOf(false) }
    val resetApp = resetApp()
    val focusManager = LocalFocusManager.current
    var isDebug = context.prefs("settings").getBoolean("Debug", false)
    var com_android_systemui_power_consumption_indicator = remember { mutableStateOf(false) }
    var com_android_systemui_temperature_indicator = remember { mutableStateOf(false) }
    val appList = listOf("com.android.systemui")
    val powerDisplay = listOf(stringResource(R.string.power), stringResource(R.string.current),stringResource(R.string.voltage))
    val powerDisplaySelect1 = remember { mutableStateOf(0) }
    val powerDisplaySelect2 = remember { mutableStateOf(0) }
    val hidePowerUnit = remember { mutableStateOf(false) }
    val hideCurrentUnit = remember { mutableStateOf(false) }
    val hideVoltageUnit = remember { mutableStateOf(false) }
    val isdualcell = remember { mutableStateOf(false) }
    val power_consumption_indicator_dual_row = remember { mutableStateOf(false) }
    val power_consumption_indicator_font_size = remember { mutableStateOf(0) }
    val power_consumption_indicator_update_time = remember { mutableStateOf(0) }
    val Dialog_font_size_Title = stringResource(R.string.font_size)
    val Dialog_update_time_Title = stringResource(R.string.update_time)
    val show_update_time_Dialog = remember { mutableStateOf(false) }
    val show_font_size_Dialog = remember { mutableStateOf(false) }
    val com_android_systemui_power_consumption_indicator_bold_text = remember { mutableStateOf(false) }
    val com_android_systemui_power_consumption_indicator_absolute = remember { mutableStateOf(false) }

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
        com_android_systemui_power_consumption_indicator_absolute.value = context.prefs("settings").getBoolean("com_android_systemui_power_consumption_indicator_absolute", false)
        com_android_systemui_power_consumption_indicator_bold_text.value = context.prefs("settings").getBoolean("com_android_systemui_power_consumption_indicator_bold_text", false)
        power_consumption_indicator_font_size.value = context.prefs("settings").getInt("com_android_systemui_power_consumption_indicator_font_size", 0)
        power_consumption_indicator_update_time.value = context.prefs("settings").getInt("com_android_systemui_power_consumption_indicator_update_time", 0)
        power_consumption_indicator_dual_row.value = context.prefs("settings").getBoolean("com_android_systemui_power_consumption_indicator_dual_row", false)
        isdualcell.value = context.prefs("settings").getBoolean("com_android_systemui_power_consumption_indicator_dual_cell", false)
        com_android_systemui_power_consumption_indicator.value = context.prefs("settings").getBoolean("com_android_systemui_power_consumption_indicator", false)
        com_android_systemui_temperature_indicator.value = context.prefs("settings").getBoolean("com_android_systemui_temperature_indicator", false)
        powerDisplaySelect1.value = context.prefs("settings").getInt("com_android_systemui_powerDisplaySelect1", 0)
        powerDisplaySelect2.value = context.prefs("settings").getInt("com_android_systemui_powerDisplaySelect2", 0)
        hidePowerUnit.value = context.prefs("settings").getBoolean("com_android_systemui_hidePowerUnit", false)
        hideCurrentUnit.value = context.prefs("settings").getBoolean("com_android_systemui_hideCurrentUnit", false)
        hideVoltageUnit.value = context.prefs("settings").getBoolean("com_android_systemui_hideVoltageUnit", false)
    }
    Scaffold(topBar = {
        TopAppBar(
            scrollBehavior = topappbarzt,
            title = stringResource(id = R.string.hardware_indicator),
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
                            title = stringResource(R.string.power_consumption_indicator),
                            onCheckedChange = {
                                com_android_systemui_power_consumption_indicator.value = it
                                context.prefs("settings").edit { putBoolean("com_android_systemui_power_consumption_indicator", it) }
                            },
                            checked = com_android_systemui_power_consumption_indicator.value
                        )
                    }
                    AnimatedVisibility(
                        visible = !com_android_systemui_power_consumption_indicator.value
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
                        visible = com_android_systemui_power_consumption_indicator.value
                    ) {
                        Column {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                SuperSwitch(
                                    title = stringResource(R.string.dual_cell),
                                    onCheckedChange = {
                                        isdualcell.value = it
                                        context.prefs("settings").edit { putBoolean("com_android_systemui_power_consumption_indicator_dual_cell", it) }
                                    },
                                    checked = isdualcell.value
                                )
                                addline()
                                SuperSwitch(
                                    title = stringResource(R.string.absolute_value),
                                    onCheckedChange = {
                                        com_android_systemui_power_consumption_indicator_absolute.value = it
                                        context.prefs("settings").edit { putBoolean("com_android_systemui_power_consumption_indicator_absolute", it) }
                                    },
                                    checked = com_android_systemui_power_consumption_indicator_absolute.value
                                )
                                addline()
                                SuperSwitch(
                                    title = stringResource(R.string.bold_text),
                                    onCheckedChange = {
                                        com_android_systemui_power_consumption_indicator_bold_text.value = it
                                        context.prefs("settings").edit { putBoolean("com_android_systemui_power_consumption_indicator_bold_text", it) }
                                    },
                                    checked = com_android_systemui_power_consumption_indicator_bold_text.value
                                )
                                addline()
                                Column {
                                    SuperArrow(
                                        title = stringResource(R.string.update_time),
                                        onClick = {
                                            show_update_time_Dialog.value = true
                                        },
                                        rightText = "${power_consumption_indicator_update_time.value}ms"
                                    )
                                    Slider(
                                        progress = ((power_consumption_indicator_update_time.value / 2000.00).toFloat()),
                                        onProgressChange = { newProgress ->
                                            power_consumption_indicator_update_time.value =
                                                (newProgress * 2000.00).toInt()
                                            context.prefs("settings").edit { putInt("com_android_systemui_power_consumption_indicator_update_time", ((newProgress * 2000.00).toInt())) }
                                        },
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                                    )
                                }
                                addline()
                                Column {
                                    SuperArrow(
                                        title = stringResource(R.string.font_size),
                                        onClick = {
                                            show_font_size_Dialog.value = true
                                        },
                                        rightText = "${power_consumption_indicator_font_size.value}sp"
                                    )
                                    Slider(
                                        progress = ((power_consumption_indicator_font_size.value / 20.00).toFloat()),
                                        onProgressChange = { newProgress ->
                                            power_consumption_indicator_font_size.value =
                                                (newProgress * 20.00).toInt()
                                            context.prefs("settings").edit { putInt("com_android_systemui_power_consumption_indicator_font_size", ((newProgress * 20.00).toInt())) }
                                        },
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                                    )
                                }
                            }
                            SmallTitle(stringResource(R.string.display_content))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                SuperSwitch(
                                    title = stringResource(R.string.dual_row_title),
                                    onCheckedChange = {
                                        power_consumption_indicator_dual_row.value = it
                                        context.prefs("settings").edit { putBoolean("com_android_systemui_power_consumption_indicator_dual_row", it) }
                                    },
                                    checked = power_consumption_indicator_dual_row.value
                                )
                                addline()
                                SuperDropdown(
                                    title = stringResource(R.string.first_line_content),
                                    items = powerDisplay,
                                    selectedIndex = powerDisplaySelect1.value
                                ) {
                                    powerDisplaySelect1.value = it
                                    context.prefs("settings").edit { putInt("com_android_systemui_powerDisplaySelect1", it) }
                                }
                                AnimatedVisibility(visible = power_consumption_indicator_dual_row.value) {
                                    addline()
                                    SuperDropdown(
                                        title = stringResource(R.string.second_line_content),
                                        items = powerDisplay,
                                        selectedIndex = powerDisplaySelect2.value
                                    ) {
                                        powerDisplaySelect2.value = it
                                        context.prefs("settings").edit { putInt("com_android_systemui_powerDisplaySelect2", it) }
                                    }
                                }
                            }
                            SmallTitle(stringResource(R.string.hide_unit))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                SuperSwitch(
                                    title = stringResource(R.string.power),
                                    onCheckedChange = {
                                        hidePowerUnit.value = it
                                        context.prefs("settings").edit { putBoolean("com_android_systemui_hidePowerUnit", it) }
                                    },
                                    checked = hidePowerUnit.value
                                )
                                addline()
                                SuperSwitch(
                                    title = stringResource(R.string.current),
                                    onCheckedChange = {
                                        hideCurrentUnit.value = it
                                        context.prefs("settings").edit { putBoolean("com_android_systemui_hideCurrentUnit", it) }
                                    },
                                    checked = hideCurrentUnit.value
                                )
                                addline()
                                SuperSwitch(
                                    title = stringResource(R.string.voltage),
                                    onCheckedChange = {
                                        hideVoltageUnit.value = it
                                        context.prefs("settings").edit { putBoolean("com_android_systemui_hideVoltageUnit", it) }
                                    },
                                    checked = hideVoltageUnit.value
                                )
                            }
                        }
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 6.dp,top = 15.dp)
                    ) {
                        SuperSwitch(
                            title = stringResource(R.string.temperature_indicator),
                            onCheckedChange = {
                                com_android_systemui_temperature_indicator.value = it
                                context.prefs("settings").edit { putBoolean("com_android_systemui_temperature_indicator", it) }
                            },
                            checked = com_android_systemui_temperature_indicator.value
                        )
                    }
                    AnimatedVisibility(
                        visible = !com_android_systemui_temperature_indicator.value
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
                        visible = com_android_systemui_temperature_indicator.value,
                        enter = AnimTools().enterTransition(0),
                        exit = AnimTools().exitTransition(100)
                    ) {
                        Column {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                            }
                        }
                    }
                }
            }
        }
    }
    resetApp.AppRestartScreen(appList,RestartAPP)
    SettingIntDialog(context,show_font_size_Dialog,Dialog_font_size_Title,power_consumption_indicator_font_size,focusManager,"com_android_systemui_power_consumption_indicator_font_size")
    SettingIntDialog(context,show_update_time_Dialog,Dialog_update_time_Title,power_consumption_indicator_update_time,focusManager,"com_android_systemui_power_consumption_indicator_update_time")
}
