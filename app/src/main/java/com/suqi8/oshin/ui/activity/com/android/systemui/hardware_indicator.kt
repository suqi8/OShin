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
import com.highcapable.yukihookapi.hook.xposed.prefs.YukiHookPrefsBridge
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

    // --- 温度指示器状态 ---
    val tempIndicatorEnabled = remember { mutableStateOf(prefs.getBoolean("temp_indicator_enabled", false)) }


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
                IndicatorSettings(
                    prefs = prefs,
                    prefix = "power_indicator",
                    displayContentOptions = displayContentOptions,
                    gravityOptions = gravityOptions
                )
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
                IndicatorSettings(
                    prefs = prefs,
                    prefix = "temp_indicator",
                    displayContentOptions = displayContentOptions,
                    gravityOptions = gravityOptions
                )
            }

            // --- 全局数据源设置 ---
            SmallTitle(text = stringResource(R.string.data_source_settings))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
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
            }

            // --- 全局单位设置 ---
            SmallTitle(text = stringResource(R.string.unit_display_settings))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
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

/**
 * 可复用的指示器设置UI组件
 */
@Composable
fun IndicatorSettings(
    prefs: YukiHookPrefsBridge,
    prefix: String,
    displayContentOptions: List<String>,
    gravityOptions: List<String>
) {
    val dualRow = remember { mutableStateOf(prefs.getBoolean("${prefix}_dual_row", false)) }
    val line1Content = remember { mutableIntStateOf(prefs.getInt("${prefix}_line1_content", 0)) }
    val line2Content = remember { mutableIntStateOf(prefs.getInt("${prefix}_line2_content", 0)) }
    val alignment = remember { mutableIntStateOf(prefs.getInt("${prefix}_alignment", 0)) }

    Column {
        SmallTitle(text = stringResource(R.string.display_content))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            FunSwich(
                title = stringResource(R.string.dual_row_title),
                category = "systemui\\hardware_indicator",
                key = "${prefix}_dual_row",
                defValue = false,
                onCheckedChange = { dualRow.value = it }
            )
            addline()
            SuperDropdown(
                title = stringResource(R.string.first_line_content),
                items = displayContentOptions,
                selectedIndex = line1Content.intValue,
                onSelectedIndexChange = {
                    line1Content.intValue = it
                    prefs.edit { putInt("${prefix}_line1_content", it) }
                }
            )
            AnimatedVisibility(visible = dualRow.value) {
                addline()
                SuperDropdown(
                    title = stringResource(R.string.second_line_content),
                    items = displayContentOptions,
                    selectedIndex = line2Content.intValue,
                    onSelectedIndexChange = {
                        line2Content.intValue = it
                        prefs.edit { putInt("${prefix}_line2_content", it) }
                    }
                )
            }
        }
        SmallTitle(text = stringResource(R.string.appearance_and_update))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            FunSwich(
                title = stringResource(R.string.bold_text),
                category = "systemui\\hardware_indicator",
                key = "${prefix}_bold",
                defValue = false
            )
            addline()
            SuperDropdown(
                title = stringResource(R.string.alignment),
                items = gravityOptions,
                selectedIndex = alignment.intValue,
                onSelectedIndexChange = { newOption ->
                    alignment.intValue = newOption
                    prefs.edit { putInt("${prefix}_alignment", newOption) }
                }
            )
            addline()
            FunSlider(
                title = stringResource(R.string.update_time),
                category = "systemui\\hardware_indicator",
                key = "${prefix}_update_interval",
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
                key = "${prefix}_font_size",
                defValue = 8f,
                endtype = "sp",
                max = 20f,
                min = 0f,
                decimalPlaces = 1
            )
        }
    }
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
