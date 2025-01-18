package com.suqi8.oshin.ui.activity.com.android.systemui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.funlistui.FunNoEnable
import com.suqi8.oshin.ui.activity.funlistui.FunSlider
import com.suqi8.oshin.ui.activity.funlistui.addline
import com.suqi8.oshin.ui.tools.resetApp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.extra.SuperDropdown
import top.yukonga.miuix.kmp.extra.SuperSwitch
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog
import java.io.File
import java.io.IOException

@SuppressLint("RtlHardcoded")
@Composable
fun hardware_indicator(navController: NavController) {
    val context = LocalContext.current
    val topappbarzt = MiuixScrollBehavior(rememberTopAppBarState())
    val restartAPP = remember { mutableStateOf(false) }
    val resetApp = resetApp()
    val focusManager = LocalFocusManager.current
    var isDebug = context.prefs("settings").getBoolean("Debug", false)

    val alpha = context.prefs("settings").getFloat("AppAlpha", 0.75f)
    val blurRadius: Dp = context.prefs("settings").getInt("AppblurRadius", 25).dp
    val noiseFactor = context.prefs("settings").getFloat("AppnoiseFactor", 0f)
    val containerColor: Color = MiuixTheme.colorScheme.background
    val hazeState = remember { HazeState() }
    val hazeStyle = remember(containerColor, alpha, blurRadius, noiseFactor) {
        HazeStyle(
            backgroundColor = containerColor,
            tint = HazeTint(containerColor.copy(alpha)),
            blurRadius = blurRadius,
            noiseFactor = noiseFactor
        )
    }

    val gravityOptions = listOf(
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
    // 使用 remember 来初始化并直接获取 SharedPreferences 配置
    val power_consumption_indicator = remember {
        mutableStateOf(
            context.prefs("systemui\\hardware_indicator")
                .getBoolean("power_consumption_indicator", false)
        )
    }

    val temperature_indicator = remember {
        mutableStateOf(
            context.prefs("systemui\\hardware_indicator")
                .getBoolean("temperature_indicator", false)
        )
    }

    val appList = listOf("com.android.systemui")
    val powerDisplay = listOf(
        stringResource(R.string.power),
        stringResource(R.string.current),
        stringResource(R.string.voltage)
    )

    // powerDisplay选择
    val powerDisplaySelect1 = remember {
        mutableIntStateOf(
            context.prefs("systemui\\hardware_indicator").getInt("powerDisplaySelect1", 0)
        )
    }
    val powerDisplaySelect2 = remember {
        mutableIntStateOf(
            context.prefs("systemui\\hardware_indicator").getInt("powerDisplaySelect2", 0)
        )
    }

    // 单位隐藏
    val hidePowerUnit = remember {
        mutableStateOf(
            context.prefs("systemui\\hardware_indicator").getBoolean("hidePowerUnit", false)
        )
    }
    val hideCurrentUnit = remember {
        mutableStateOf(
            context.prefs("systemui\\hardware_indicator").getBoolean("hideCurrentUnit", false)
        )
    }
    val hideVoltageUnit = remember {
        mutableStateOf(
            context.prefs("systemui\\hardware_indicator").getBoolean("hideVoltageUnit", false)
        )
    }

    // 双电池状态
    val isdualcell = remember {
        mutableStateOf(
            context.prefs("systemui\\hardware_indicator")
                .getBoolean("power_consumption_indicator_dual_cell", false)
        )
    }
    val power_consumption_indicator_dual_row = remember {
        mutableStateOf(
            context.prefs("systemui\\hardware_indicator")
                .getBoolean("power_consumption_indicator_dual_row", false)
        )
    }

    // 字体大小、更新时间
    val power_consumption_indicator_font_size = remember {
        mutableIntStateOf(
            context.prefs("systemui\\hardware_indicator")
                .getInt("power_consumption_indicator_font_size", 0)
        )
    }
    val power_consumption_indicator_update_time = remember {
        mutableIntStateOf(
            context.prefs("systemui\\hardware_indicator")
                .getInt("power_consumption_indicator_update_time", 0)
        )
    }

    // bold_text 和 alignment
    val power_consumption_indicator_bold_text = remember {
        mutableStateOf(
            context.prefs("systemui\\hardware_indicator")
                .getBoolean("power_consumption_indicator_bold_text", false)
        )
    }
    val power_consumption_indicator_absolute = remember {
        mutableStateOf(
            context.prefs("systemui\\hardware_indicator")
                .getBoolean("power_consumption_indicator_absolute", false)
        )
    }
    val power_consumption_indicator_alignment = remember {
        mutableIntStateOf(
            context.prefs("systemui\\hardware_indicator")
                .getInt("power_consumption_indicator_alignment", 0)
        )
    }

    // 温度显示相关

    val show_cpu_temp_data = remember { mutableStateOf(false) }
    val cpu_temp_source = remember {
        mutableIntStateOf(
            context.prefs("systemui\\hardware_indicator")
                .getInt("temperature_indicator_cpu_temp_source", 0)
        )
    }

    val temperatureDisplay = listOf(
        stringResource(R.string.battery_temperature),
        stringResource(R.string.cpu_temperature)
    )

    // 温度选择和更新时间
    val temperatureDisplaySelect1 = remember {
        mutableIntStateOf(
            context.prefs("systemui\\hardware_indicator")
                .getInt("temperature_indicator_display_select1", 0)
        )
    }
    val temperatureDisplaySelect2 = remember {
        mutableIntStateOf(
            context.prefs("systemui\\hardware_indicator")
                .getInt("temperature_indicator_display_select2", 0)
        )
    }
    val temperature_indicator_dual_row = remember {
        mutableStateOf(
            context.prefs("systemui\\hardware_indicator")
                .getBoolean("temperature_indicator_dual_row", false)
        )
    }
    val temperature_indicator_bold_text = remember {
        mutableStateOf(
            context.prefs("systemui\\hardware_indicator")
                .getBoolean("temperature_indicator_bold_text", false)
        )
    }
    val temperature_indicator_font_size = remember {
        mutableFloatStateOf(
            context.prefs("systemui\\hardware_indicator")
                .getFloat("temperature_indicator_font_size", 0f)
        )
    }
    val temperature_indicator_alignment = remember {
        mutableIntStateOf(
            context.prefs("systemui\\hardware_indicator")
                .getInt("temperature_indicator_alignment", 0)
        )
    }
    val temperature_indicator_updatetime = remember {
        mutableIntStateOf(
            context.prefs("systemui\\hardware_indicator")
                .getInt("temperature_indicator_update_time", 0)
        )
    }

    val hideBatteryUnit = remember { mutableStateOf(false) }
    val hideCpuUnit = remember { mutableStateOf(false) }
    Scaffold(topBar = {
        TopAppBar(
            scrollBehavior = topappbarzt,
            title = stringResource(id = R.string.hardware_indicator),
            color = Color.Transparent,
            modifier = Modifier.hazeChild(
                state = hazeState,
                style = hazeStyle
            ),
            navigationIcon = {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier.padding(start = 18.dp)
                ) {
                    Icon(
                        imageVector = MiuixIcons.ArrowBack,
                        contentDescription = null,
                        tint = MiuixTheme.colorScheme.onBackground
                    )
                }
            },
            actions = {
                // 如果你有其他操作按钮，这里可以添加
                IconButton(
                    onClick = {
                        restartAPP.value = true
                    },
                    modifier = Modifier.padding(end = 18.dp)
                ) {
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 6.dp, top = 15.dp)
                    ) {
                        SuperSwitch(
                            title = stringResource(R.string.power_consumption_indicator),
                            onCheckedChange = {
                                power_consumption_indicator.value = it
                                context.prefs("systemui\\hardware_indicator")
                                    .edit { putBoolean("power_consumption_indicator", it) }
                            },
                            checked = power_consumption_indicator.value
                        )
                    }
                    AnimatedVisibility(
                        visible = !power_consumption_indicator.value
                    ) {
                        FunNoEnable()
                    }
                    AnimatedVisibility(
                        visible = power_consumption_indicator.value
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
                                        context.prefs("systemui\\hardware_indicator").edit {
                                            putBoolean(
                                                "power_consumption_indicator_dual_cell",
                                                it
                                            )
                                        }
                                    },
                                    checked = isdualcell.value
                                )
                                addline()
                                SuperSwitch(
                                    title = stringResource(R.string.absolute_value),
                                    onCheckedChange = {
                                        power_consumption_indicator_absolute.value =
                                            it
                                        context.prefs("systemui\\hardware_indicator").edit {
                                            putBoolean(
                                                "power_consumption_indicator_absolute",
                                                it
                                            )
                                        }
                                    },
                                    checked = power_consumption_indicator_absolute.value
                                )
                                addline()
                                SuperSwitch(
                                    title = stringResource(R.string.bold_text),
                                    onCheckedChange = {
                                        power_consumption_indicator_bold_text.value =
                                            it
                                        context.prefs("systemui\\hardware_indicator").edit {
                                            putBoolean(
                                                "power_consumption_indicator_bold_text",
                                                it
                                            )
                                        }
                                    },
                                    checked = power_consumption_indicator_bold_text.value
                                )
                                addline()
                                SuperDropdown(
                                    title = stringResource(R.string.alignment),
                                    items = gravityOptions,
                                    selectedIndex = power_consumption_indicator_alignment.intValue,
                                    onSelectedIndexChange = { newOption ->
                                        power_consumption_indicator_alignment.intValue =
                                            newOption
                                        context.prefs("systemui\\hardware_indicator").edit {
                                            putInt(
                                                "power_consumption_indicator_alignment",
                                                newOption
                                            )
                                        }
                                    }
                                )
                                addline()
                                FunSlider(
                                    title = stringResource(R.string.update_time),
                                    category = "systemui\\hardware_indicator",
                                    key = "power_consumption_indicator_update_time",
                                    defValue = 1000,
                                    endtype = "ms",
                                    max = 2000f,
                                    min = 0f,
                                    decimalPlaces = 0,
                                    context = context
                                )
                                addline()
                                FunSlider(
                                    title = stringResource(R.string.font_size),
                                    category = "systemui\\hardware_indicator",
                                    key = "power_consumption_indicator_font_size",
                                    defValue = 8f,
                                    endtype = "sp",
                                    max = 20f,
                                    min = 0f,
                                    decimalPlaces = 1,
                                    context = context
                                )
                            }
                            SmallTitle(text = stringResource(R.string.display_content))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                SuperSwitch(
                                    title = stringResource(R.string.dual_row_title),
                                    onCheckedChange = {
                                        power_consumption_indicator_dual_row.value = it
                                        context.prefs("systemui\\hardware_indicator").edit {
                                            putBoolean(
                                                "power_consumption_indicator_dual_row",
                                                it
                                            )
                                        }
                                    },
                                    checked = power_consumption_indicator_dual_row.value
                                )
                                addline()
                                SuperDropdown(
                                    title = stringResource(R.string.first_line_content),
                                    items = powerDisplay,
                                    selectedIndex = powerDisplaySelect1.intValue,
                                    onSelectedIndexChange = {
                                        powerDisplaySelect1.intValue = it
                                        context.prefs("systemui\\hardware_indicator").edit {
                                            putInt(
                                                "powerDisplaySelect1",
                                                it
                                            )
                                        }
                                    }
                                )
                                AnimatedVisibility(visible = power_consumption_indicator_dual_row.value) {
                                    addline()
                                    SuperDropdown(
                                        title = stringResource(R.string.second_line_content),
                                        items = powerDisplay,
                                        selectedIndex = powerDisplaySelect2.intValue,
                                        onSelectedIndexChange = {
                                            powerDisplaySelect2.intValue = it
                                            context.prefs("systemui\\hardware_indicator").edit {
                                                putInt(
                                                    "powerDisplaySelect2",
                                                    it
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                            SmallTitle(text = stringResource(R.string.hide_unit))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                SuperSwitch(
                                    title = stringResource(R.string.power),
                                    onCheckedChange = {
                                        hidePowerUnit.value = it
                                        context.prefs("systemui\\hardware_indicator").edit {
                                            putBoolean(
                                                "hidePowerUnit",
                                                it
                                            )
                                        }
                                    },
                                    checked = hidePowerUnit.value
                                )
                                addline()
                                SuperSwitch(
                                    title = stringResource(R.string.current),
                                    onCheckedChange = {
                                        hideCurrentUnit.value = it
                                        context.prefs("systemui\\hardware_indicator").edit {
                                            putBoolean(
                                                "hideCurrentUnit",
                                                it
                                            )
                                        }
                                    },
                                    checked = hideCurrentUnit.value
                                )
                                addline()
                                SuperSwitch(
                                    title = stringResource(R.string.voltage),
                                    onCheckedChange = {
                                        hideVoltageUnit.value = it
                                        context.prefs("systemui\\hardware_indicator").edit {
                                            putBoolean(
                                                "hideVoltageUnit",
                                                it
                                            )
                                        }
                                    },
                                    checked = hideVoltageUnit.value
                                )
                            }
                        }
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 6.dp, top = 15.dp)
                    ) {
                        SuperSwitch(
                            title = stringResource(R.string.temperature_indicator),
                            onCheckedChange = {
                                temperature_indicator.value = it
                                context.prefs("systemui\\hardware_indicator").edit {
                                    putBoolean(
                                        "temperature_indicator",
                                        it
                                    )
                                }
                            },
                            checked = temperature_indicator.value
                        )
                    }
                    AnimatedVisibility(
                        visible = !temperature_indicator.value
                    ) {
                        FunNoEnable()
                    }
                    AnimatedVisibility(
                        visible = temperature_indicator.value
                    ) {
                        Column {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                SuperArrow(
                                    title = stringResource(R.string.show_cpu_temp_data),
                                    onClick = {
                                        show_cpu_temp_data.value = true
                                    })
                                addline()
                                FunSlider(
                                    title = stringResource(R.string.change_cpu_temp_source),
                                    summary = stringResource(R.string.enter_thermal_zone_number),
                                    category = "systemui\\hardware_indicator",
                                    key = "temperature_indicator_cpu_temp_source",
                                    defValue = 1,
                                    max = 100f,
                                    min = 0f,
                                    decimalPlaces = 0,
                                    context = context
                                )
                                addline()
                                SuperSwitch(
                                    title = stringResource(R.string.bold_text),
                                    onCheckedChange = {
                                        temperature_indicator_bold_text.value =
                                            it
                                        context.prefs("systemui\\hardware_indicator").edit {
                                            putBoolean(
                                                "temperature_indicator_bold_text",
                                                it
                                            )
                                        }
                                    },
                                    checked = temperature_indicator_bold_text.value
                                )
                                addline()
                                SuperDropdown(
                                    title = stringResource(R.string.alignment),
                                    items = gravityOptions,
                                    selectedIndex = temperature_indicator_alignment.intValue,
                                    onSelectedIndexChange = { newOption ->
                                        temperature_indicator_alignment.intValue =
                                            newOption
                                        context.prefs("systemui\\hardware_indicator").edit {
                                            putInt(
                                                "temperature_indicator_alignment",
                                                newOption
                                            )
                                        }
                                    }
                                )
                                addline()
                                FunSlider(
                                    title = stringResource(R.string.update_time),
                                    category = "systemui\\hardware_indicator",
                                    key = "temperature_indicator_update_time",
                                    defValue = 1000,
                                    endtype = "ms",
                                    max = 2000f,
                                    min = 0f,
                                    decimalPlaces = 0,
                                    context = context
                                )
                                addline()
                                FunSlider(
                                    title = stringResource(R.string.font_size),
                                    category = "systemui\\hardware_indicator",
                                    key = "temperature_indicator_font_size",
                                    defValue = 8f,
                                    endtype = "sp",
                                    max = 20f,
                                    min = 0f,
                                    decimalPlaces = 1,
                                    context = context
                                )
                            }
                            SmallTitle(text = stringResource(R.string.display_content))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                SuperSwitch(
                                    title = stringResource(R.string.dual_row_title),
                                    onCheckedChange = {
                                        temperature_indicator_dual_row.value =
                                            it
                                        context.prefs("systemui\\hardware_indicator").edit {
                                            putBoolean(
                                                "temperature_indicator_dual_row",
                                                it
                                            )
                                        }
                                    },
                                    checked = temperature_indicator_dual_row.value
                                )
                                addline()
                                SuperDropdown(
                                    title = stringResource(R.string.first_line_content),
                                    items = temperatureDisplay,
                                    selectedIndex = temperatureDisplaySelect1.intValue,
                                    onSelectedIndexChange = {
                                        temperatureDisplaySelect1.intValue = it
                                        context.prefs("systemui\\hardware_indicator").edit {
                                            putInt(
                                                "temperature_indicator_display_select1",
                                                it
                                            )
                                        }
                                    }
                                )
                                AnimatedVisibility(visible = temperature_indicator_dual_row.value) {
                                    addline()
                                    SuperDropdown(
                                        title = stringResource(R.string.second_line_content),
                                        items = temperatureDisplay,
                                        selectedIndex = temperatureDisplaySelect2.intValue,
                                        onSelectedIndexChange = {
                                            temperatureDisplaySelect2.intValue = it
                                            context.prefs("systemui\\hardware_indicator").edit {
                                                putInt(
                                                    "temperature_indicator_display_select2",
                                                    it
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                            SmallTitle(text = stringResource(R.string.hide_unit))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                SuperSwitch(
                                    title = stringResource(R.string.battery_temperature),
                                    onCheckedChange = {
                                        hideBatteryUnit.value = it
                                        context.prefs("systemui\\hardware_indicator").edit {
                                            putBoolean(
                                                "hideBatteryUnit",
                                                it
                                            )
                                        }
                                    },
                                    checked = hideBatteryUnit.value
                                )
                                addline()
                                SuperSwitch(
                                    title = stringResource(R.string.cpu_temperature),
                                    onCheckedChange = {
                                        hideCpuUnit.value = it
                                        context.prefs("systemui\\hardware_indicator").edit {
                                            putBoolean(
                                                "hideCpuUnit",
                                                it
                                            )
                                        }
                                    },
                                    checked = hideCpuUnit.value
                                )
                            }
                        }
                    }
                    Spacer(
                        Modifier.height(
                            WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                        )
                    )
                }
            }
        }
    }
    resetApp.AppRestartScreen(appList, restartAPP)
    cpu_temp_data(show_cpu_temp_data)
}

@Composable
fun cpu_temp_data(show: MutableState<Boolean>) {
    if (!show.value) return
    val temperatures = remember { getTemperatureList() }
    SuperDialog(title = stringResource(R.string.show_cpu_temp_data),
        show = show,
        onDismissRequest = {
            dismissDialog(show)
        }) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp)
        ) {
            items(temperatures) { temperatureInfo ->
                BasicComponent(
                    title = temperatureInfo.zoneName,
                    modifier = Modifier
                        .fillMaxWidth(),
                    summary = temperatureInfo.temperature
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.ok),
                enabled = true,
                onClick = {
                    dismissDialog(show)
                }
            )
        }
    }
}

fun getTemperatureList(): List<TemperatureInfo> {
    val temperatureList = mutableListOf<TemperatureInfo>()

    // /sys/class/thermal/thermal_zone* 文件路径
    val thermalZones =
        File("/sys/class/thermal/").listFiles { file -> file.name.startsWith("thermal_zone") }

    thermalZones?.forEach { zone ->
        val tempFile = File(zone, "temp")
        if (tempFile.exists() && tempFile.canRead()) {
            try {
                val temperature = tempFile.readText().trim().toIntOrNull()?.let {
                    // 将读取的温度值除以1000，转换为摄氏度
                    it / 1000.0
                }
                if (temperature != null && temperature in 30.0..100.0) {
                    temperatureList.add(TemperatureInfo(zone.name, "$temperature°C"))
                } else {
                    Log.d(
                        "TemperatureFilter",
                        "排除不合理温度: $temperature°C in zone ${zone.name}"
                    )
                }
            } catch (e: IOException) {
                // 处理读取失败的情况
                e.printStackTrace()
            }
        } else {
            // 文件不存在或不可读时处理
            val TAG = ""
            Log.d(TAG, "无法读取 $tempFile")
        }
    }
    return temperatureList
}

data class TemperatureInfo(val zoneName: String, val temperature: String)
