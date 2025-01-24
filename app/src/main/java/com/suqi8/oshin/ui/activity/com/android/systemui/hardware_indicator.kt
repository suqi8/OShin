package com.suqi8.oshin.ui.activity.com.android.systemui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.funlistui.FunNoEnable
import com.suqi8.oshin.ui.activity.funlistui.FunPage
import com.suqi8.oshin.ui.activity.funlistui.FunSlider
import com.suqi8.oshin.ui.activity.funlistui.FunSwich
import com.suqi8.oshin.ui.activity.funlistui.addline
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.extra.SuperDropdown
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog
import java.io.File
import java.io.IOException

@SuppressLint("RtlHardcoded")
@Composable
fun hardware_indicator(navController: NavController) {
    val context = LocalContext.current
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
    val powerDisplay = listOf(stringResource(R.string.power), stringResource(R.string.current), stringResource(R.string.voltage))
    val powerDisplaySelect1 = remember { mutableIntStateOf(context.prefs("systemui\\hardware_indicator").getInt("powerDisplaySelect1", 0)) }
    val powerDisplaySelect2 = remember { mutableIntStateOf(context.prefs("systemui\\hardware_indicator").getInt("powerDisplaySelect2", 0)) }
    val power_consumption_indicator_alignment = remember { mutableIntStateOf(context.prefs("systemui\\hardware_indicator").getInt("power_consumption_indicator_alignment", 0)) }
    val show_cpu_temp_data = remember { mutableStateOf(false) }
    val temperatureDisplay = listOf(stringResource(R.string.battery_temperature), stringResource(R.string.cpu_temperature))
    val temperatureDisplaySelect1 = remember { mutableIntStateOf(context.prefs("systemui\\hardware_indicator").getInt("temperature_indicator_display_select1", 0)) }
    val temperatureDisplaySelect2 = remember { mutableIntStateOf(context.prefs("systemui\\hardware_indicator").getInt("temperature_indicator_display_select2", 0)) }
    val temperature_indicator_alignment = remember { mutableIntStateOf(context.prefs("systemui\\hardware_indicator").getInt("temperature_indicator_alignment", 0)) }
    FunPage(
        title = stringResource(id = R.string.hardware_indicator),
        appList = listOf("com.android.systemui"),
        navController = navController
    ) {
        Column {
            val power_consumption_indicator = remember {
                mutableStateOf(
                    context.prefs("systemui\\hardware_indicator")
                        .getBoolean("power_consumption_indicator", false)
                )
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 6.dp, top = 15.dp)
            ) {
                FunSwich(
                    title = stringResource(R.string.power_consumption_indicator),
                    category = "systemui\\hardware_indicator",
                    key = "power_consumption_indicator",
                    defValue = false,
                    onCheckedChange = {
                        power_consumption_indicator.value = it
                    }
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
                        FunSwich(
                            title = stringResource(R.string.dual_cell),
                            category = "systemui\\hardware_indicator",
                            key = "power_consumption_indicator_dual_cell",
                            defValue = false
                        )
                        addline()
                        FunSwich(
                            title = stringResource(R.string.absolute_value),
                            category = "systemui\\hardware_indicator",
                            key = "power_consumption_indicator_absolute",
                            defValue = false
                        )
                        addline()
                        FunSwich(
                            title = stringResource(R.string.bold_text),
                            category = "systemui\\hardware_indicator",
                            key = "power_consumption_indicator_bold_text",
                            defValue = false
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
                            decimalPlaces = 0
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
                            decimalPlaces = 1
                        )
                    }
                    SmallTitle(text = stringResource(R.string.display_content))
                    val power_consumption_indicator_dual_row = remember {
                        mutableStateOf(context.prefs("systemui\\hardware_indicator").getBoolean("power_consumption_indicator_dual_row", false))
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        FunSwich(
                            title = stringResource(R.string.dual_row_title),
                            category = "systemui\\hardware_indicator",
                            key = "power_consumption_indicator_dual_row",
                            defValue = false,
                            onCheckedChange = {
                                power_consumption_indicator_dual_row.value = it
                            }
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
                        FunSwich(
                            title = stringResource(R.string.power),
                            category = "systemui\\hardware_indicator",
                            key = "hidePowerUnit",
                            defValue = false
                        )
                        addline()
                        FunSwich(
                            title = stringResource(R.string.current),
                            category = "systemui\\hardware_indicator",
                            key = "hideCurrentUnit",
                            defValue = false
                        )
                        addline()
                        FunSwich(
                            title = stringResource(R.string.voltage),
                            category = "systemui\\hardware_indicator",
                            key = "hideVoltageUnit",
                            defValue = false
                        )
                    }
                }
            }
            val temperature_indicator = remember {
                mutableStateOf(context.prefs("systemui\\hardware_indicator").getBoolean("temperature_indicator", false))
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 6.dp, top = 15.dp)
            ) {
                FunSwich(
                    title = stringResource(R.string.temperature_indicator),
                    category = "systemui\\hardware_indicator",
                    key = "temperature_indicator",
                    defValue = false,
                    onCheckedChange = {
                        temperature_indicator.value = it
                    }
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
                            decimalPlaces = 0
                        )
                        addline()
                        FunSwich(
                            title = stringResource(R.string.bold_text),
                            category = "systemui\\hardware_indicator",
                            key = "temperature_indicator_bold_text",
                            defValue = false
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
                            decimalPlaces = 0
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
                            decimalPlaces = 1
                        )
                    }
                    SmallTitle(text = stringResource(R.string.display_content))
                    val temperature_indicator_dual_row = remember {
                        mutableStateOf(context.prefs("systemui\\hardware_indicator").getBoolean("temperature_indicator_dual_row", false))
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        FunSwich(
                            title = stringResource(R.string.dual_row_title),
                            category = "systemui\\hardware_indicator",
                            key = "temperature_indicator_dual_row",
                            defValue = false,
                            onCheckedChange = {
                                temperature_indicator_dual_row.value = it
                            }
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
                        FunSwich(
                            title = stringResource(R.string.battery_temperature),
                            category = "systemui\\hardware_indicator",
                            key = "hideBatteryUnit",
                            defValue = false
                        )
                        addline()
                        FunSwich(
                            title = stringResource(R.string.cpu_temperature),
                            category = "systemui\\hardware_indicator",
                            key = "hideCpuUnit",
                            defValue = false
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
