package com.suqi8.oshin.ui.activity.com.android.systemui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.suqi8.oshin.ui.activity.components.Card
import com.suqi8.oshin.ui.activity.components.FunArrow
import com.suqi8.oshin.ui.activity.components.FunNoEnable
import com.suqi8.oshin.ui.activity.components.FunPage
import com.suqi8.oshin.ui.activity.components.FunSlider
import com.suqi8.oshin.ui.activity.components.FunSwich
import com.suqi8.oshin.ui.activity.components.addline
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.extra.SuperDropdown
import top.yukonga.miuix.kmp.utils.overScrollVertical
import java.io.File
import java.io.IOException

@SuppressLint("RtlHardcoded")
@Composable
fun hardware_indicator(navController: NavController) {
    val context = LocalContext.current
    val prefs = remember { context.prefs("systemui\\hardware_indicator") }

    // --- 通用设置数据 ---
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
    val displayContentOptions = listOf(
        stringResource(R.string.power),
        stringResource(R.string.current),
        stringResource(R.string.voltage),
        stringResource(R.string.cpu_temperature),
        stringResource(R.string.battery_temperature)
    )
    val show_cpu_temp_data = remember { mutableStateOf(false) }

    // --- 电量指示器状态 ---
    val powerIndicatorEnabled = remember { mutableStateOf(prefs.getBoolean("power_indicator_enabled", false)) }
    val powerIndicatorAlignment = remember { mutableIntStateOf(prefs.getInt("power_indicator_alignment", 0)) }
    val powerIndicatorDualRow = remember { mutableStateOf(prefs.getBoolean("power_indicator_dual_row", false)) }
    val powerIndicatorLine1Content = remember { mutableIntStateOf(prefs.getInt("power_indicator_line1_content", 0)) }
    val powerIndicatorLine2Content = remember { mutableIntStateOf(prefs.getInt("power_indicator_line2_content", 0)) }

    // --- 温度指示器状态 ---
    val tempIndicatorEnabled = remember { mutableStateOf(prefs.getBoolean("temp_indicator_enabled", false)) }
    val tempIndicatorAlignment = remember { mutableIntStateOf(prefs.getInt("temp_indicator_alignment", 0)) }
    val tempIndicatorDualRow = remember { mutableStateOf(prefs.getBoolean("temp_indicator_dual_row", false)) }
    val tempIndicatorLine1Content = remember { mutableIntStateOf(prefs.getInt("temp_indicator_line1_content", 0)) }
    val tempIndicatorLine2Content = remember { mutableIntStateOf(prefs.getInt("temp_indicator_line2_content", 0)) }


    FunPage(
        title = stringResource(id = R.string.hardware_indicator),
        appList = listOf("com.android.systemui"),
        navController = navController
    ) {
        Column {
            // --- 电量消耗指示器 ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 6.dp, top = 6.dp)
            ) {
                FunSwich(
                    title = stringResource(R.string.power_consumption_indicator),
                    category = "systemui\\hardware_indicator",
                    key = "power_indicator_enabled",
                    defValue = false,
                    onCheckedChange = { powerIndicatorEnabled.value = it }
                )
            }
            AnimatedVisibility(visible = !powerIndicatorEnabled.value) { FunNoEnable() }
            AnimatedVisibility(visible = powerIndicatorEnabled.value) {
                Column {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        FunSwich(
                            title = stringResource(R.string.dual_cell),
                            category = "systemui\\hardware_indicator",
                            key = "data_power_dual_cell",
                            defValue = false
                        )
                        addline()
                        FunSwich(
                            title = stringResource(R.string.absolute_value),
                            category = "systemui\\hardware_indicator",
                            key = "data_power_absolute_current",
                            defValue = false
                        )
                        addline()
                        FunSwich(
                            title = stringResource(R.string.bold_text),
                            category = "systemui\\hardware_indicator",
                            key = "power_indicator_bold",
                            defValue = false
                        )
                        addline()
                        SuperDropdown(
                            title = stringResource(R.string.alignment),
                            items = gravityOptions,
                            selectedIndex = powerIndicatorAlignment.intValue,
                            onSelectedIndexChange = { newOption ->
                                powerIndicatorAlignment.intValue = newOption
                                prefs.edit { putInt("power_indicator_alignment", newOption) }
                            }
                        )
                        addline()
                        FunSlider(
                            title = stringResource(R.string.update_time),
                            category = "systemui\\hardware_indicator",
                            key = "power_indicator_update_interval",
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
                            key = "power_indicator_font_size",
                            defValue = 8f,
                            endtype = "sp",
                            max = 20f,
                            min = 0f,
                            decimalPlaces = 1
                        )
                    }
                    SmallTitle(text = stringResource(R.string.display_content))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        FunSwich(
                            title = stringResource(R.string.dual_row_title),
                            category = "systemui\\hardware_indicator",
                            key = "power_indicator_dual_row",
                            defValue = false,
                            onCheckedChange = { powerIndicatorDualRow.value = it }
                        )
                        addline()
                        SuperDropdown(
                            title = stringResource(R.string.first_line_content),
                            items = displayContentOptions,
                            selectedIndex = powerIndicatorLine1Content.intValue,
                            onSelectedIndexChange = {
                                powerIndicatorLine1Content.intValue = it
                                prefs.edit { putInt("power_indicator_line1_content", it) }
                            }
                        )
                        AnimatedVisibility(visible = powerIndicatorDualRow.value) {
                            addline()
                            SuperDropdown(
                                title = stringResource(R.string.second_line_content),
                                items = displayContentOptions,
                                selectedIndex = powerIndicatorLine2Content.intValue,
                                onSelectedIndexChange = {
                                    powerIndicatorLine2Content.intValue = it
                                    prefs.edit { putInt("power_indicator_line2_content", it) }
                                }
                            )
                        }
                    }
                }
            }

            // --- 温度指示器 ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 6.dp, top = 15.dp)
            ) {
                FunSwich(
                    title = stringResource(R.string.temperature_indicator),
                    category = "systemui\\hardware_indicator",
                    key = "temp_indicator_enabled",
                    defValue = false,
                    onCheckedChange = { tempIndicatorEnabled.value = it }
                )
            }
            AnimatedVisibility(visible = !tempIndicatorEnabled.value) { FunNoEnable() }
            AnimatedVisibility(visible = tempIndicatorEnabled.value) {
                Column {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        FunArrow(
                            title = stringResource(R.string.show_cpu_temp_data),
                            onClick = { show_cpu_temp_data.value = true }
                        )
                        addline()
                        FunSlider(
                            title = stringResource(R.string.change_cpu_temp_source),
                            summary = stringResource(R.string.enter_thermal_zone_number),
                            category = "systemui\\hardware_indicator",
                            key = "data_temp_cpu_source",
                            defValue = 1,
                            max = 100f,
                            min = 0f,
                            decimalPlaces = 0
                        )
                        addline()
                        FunSwich(
                            title = stringResource(R.string.bold_text),
                            category = "systemui\\hardware_indicator",
                            key = "temp_indicator_bold",
                            defValue = false
                        )
                        addline()
                        SuperDropdown(
                            title = stringResource(R.string.alignment),
                            items = gravityOptions,
                            selectedIndex = tempIndicatorAlignment.intValue,
                            onSelectedIndexChange = { newOption ->
                                tempIndicatorAlignment.intValue = newOption
                                prefs.edit { putInt("temp_indicator_alignment", newOption) }
                            }
                        )
                        addline()
                        FunSlider(
                            title = stringResource(R.string.update_time),
                            category = "systemui\\hardware_indicator",
                            key = "temp_indicator_update_interval",
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
                            key = "temp_indicator_font_size",
                            defValue = 8f,
                            endtype = "sp",
                            max = 20f,
                            min = 0f,
                            decimalPlaces = 1
                        )
                    }
                    SmallTitle(text = stringResource(R.string.display_content))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        FunSwich(
                            title = stringResource(R.string.dual_row_title),
                            category = "systemui\\hardware_indicator",
                            key = "temp_indicator_dual_row",
                            defValue = false,
                            onCheckedChange = { tempIndicatorDualRow.value = it }
                        )
                        addline()
                        SuperDropdown(
                            title = stringResource(R.string.first_line_content),
                            items = displayContentOptions,
                            selectedIndex = tempIndicatorLine1Content.intValue,
                            onSelectedIndexChange = {
                                tempIndicatorLine1Content.intValue = it
                                prefs.edit { putInt("temp_indicator_line1_content", it) }
                            }
                        )
                        AnimatedVisibility(visible = tempIndicatorDualRow.value) {
                            addline()
                            SuperDropdown(
                                title = stringResource(R.string.second_line_content),
                                items = displayContentOptions,
                                selectedIndex = tempIndicatorLine2Content.intValue,
                                onSelectedIndexChange = {
                                    tempIndicatorLine2Content.intValue = it
                                    prefs.edit { putInt("temp_indicator_line2_content", it) }
                                }
                            )
                        }
                    }
                }
            }

            // --- 通用设置：隐藏单位 ---
            SmallTitle(text = stringResource(R.string.hide_unit))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                FunSwich(
                    title = stringResource(R.string.power),
                    category = "systemui\\hardware_indicator",
                    key = "unit_hide_power",
                    defValue = false
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.current),
                    category = "systemui\\hardware_indicator",
                    key = "unit_hide_current",
                    defValue = false
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.voltage),
                    category = "systemui\\hardware_indicator",
                    key = "unit_hide_voltage",
                    defValue = false
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.battery_temperature),
                    category = "systemui\\hardware_indicator",
                    key = "unit_hide_temp_battery",
                    defValue = false
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.cpu_temperature),
                    category = "systemui\\hardware_indicator",
                    key = "unit_hide_temp_cpu",
                    defValue = false
                )
            }
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
            show.value = false
        }) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp)
                .overScrollVertical()
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
                    show.value = false
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
