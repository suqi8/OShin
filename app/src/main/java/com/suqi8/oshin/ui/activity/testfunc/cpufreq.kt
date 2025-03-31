package com.suqi8.oshin.ui.activity.testfunc

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.R
import com.suqi8.oshin.executeCommand
import com.suqi8.oshin.ui.activity.funlistui.FunNoEnable
import com.suqi8.oshin.ui.activity.funlistui.FunPage
import com.suqi8.oshin.ui.activity.funlistui.FunSwich
import com.suqi8.oshin.ui.activity.funlistui.addline
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.extra.SuperDropdown

@SuppressLint("SuspiciousIndentation")
@Composable
fun cpu_freq(navController: NavController) {
    val context = LocalContext.current
    val cpu_freq_main = remember { mutableStateOf(context.prefs("cpu_freq").getBoolean("cpu_freq_main", false)) }
    FunPage(
        title = stringResource(R.string.cpu_freq_main),
        navController = navController
    ) {
        val scope = rememberCoroutineScope()
        var cpuFrequencies = remember { mutableStateOf<Map<Int, Triple<List<String>, Int, Int>>>(emptyMap()) }
        LaunchedEffect(Unit) {
            cpuFrequencies.value = getAllCoresFrequencies()
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 6.dp, top = 6.dp)
        ) {
            FunSwich(
                title = stringResource(R.string.cpu_freq_main),
                summary = stringResource(R.string.frequency_activation_hint),
                category = "cpu_freq",
                key = "cpu_freq_main",
                onCheckedChange = {
                    cpu_freq_main.value = it
                }
            )
        }
        AnimatedVisibility(visible = !cpu_freq_main.value) { FunNoEnable() }
        AnimatedVisibility(visible = cpu_freq_main.value && !cpuFrequencies.value.isEmpty()) {
            Column {
                fun updateCpuFrequencies(core: Int, maxIndex: Int, minIndex: Int) {
                    cpuFrequencies.value = cpuFrequencies.value.toMutableMap().apply {
                        this[core] = Triple(this[core]!!.first, maxIndex, minIndex)
                    }
                }
                cpuFrequencies.value.forEach { (core, data) ->
                    val (freqs, maxIndex, minIndex) = data
                    var selectedMaxIndex by remember { mutableStateOf(maxIndex) }
                    var selectedMinIndex by remember { mutableStateOf(minIndex) }
                    SmallTitle("CPU$core")
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 6.dp)
                    ) {
                        SuperDropdown(
                            title = stringResource(R.string.cpu_group_max_freq, core),
                            items = freqs,
                            selectedIndex = selectedMaxIndex,
                            onSelectedIndexChange = {
                                selectedMaxIndex = it
                                scope.launch {
                                    updateCpuFrequencies(core, selectedMaxIndex, selectedMinIndex)
                                    setCpuFrequency(core, freqs[it], isMax = true)
                                }
                            }
                        )
                        addline()
                        SuperDropdown(
                            title = stringResource(R.string.cpu_group_min_freq, core),
                            items = freqs,
                            selectedIndex = selectedMinIndex,
                            onSelectedIndexChange = {
                                selectedMinIndex = it
                                scope.launch {
                                    updateCpuFrequencies(core, selectedMaxIndex, selectedMinIndex)
                                    setCpuFrequency(core, freqs[it], isMax = false)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

suspend fun setCpuFrequency(core: Int, frequency: String, isMax: Boolean) {
    val path = if (isMax) {
        "/sys/devices/system/cpu/cpu$core/cpufreq/scaling_max_freq"
    } else {
        "/sys/devices/system/cpu/cpu$core/cpufreq/scaling_min_freq"
    }

    val command = "echo $frequency > $path"

    val result = executeCommand(command)
    if (result == "0") {
        Log.e("CPU", "写入失败: $command")
    } else {
        Log.d("CPU", "成功写入: $command")
    }
}

suspend fun getAllCoresFrequencies(): Map<Int, Triple<List<String>, Int, Int>> {
    val cpuFrequencies = mutableMapOf<Int, Triple<List<String>, Int, Int>>()

    // 获取 CPU 核心数
    val coreCount = executeCommand("ls /sys/devices/system/cpu/ | grep -E 'cpu[0-9]+' | wc -l").toIntOrNull() ?: 0

    for (i in 0 until coreCount) {
        val freqPath = "/sys/devices/system/cpu/cpu$i/cpufreq/scaling_available_frequencies"
        val maxFreqPath = "/sys/devices/system/cpu/cpu$i/cpufreq/scaling_max_freq"
        val minFreqPath = "/sys/devices/system/cpu/cpu$i/cpufreq/scaling_min_freq"

        val freqResult = executeCommand("cat $freqPath")
        val maxFreqValue = executeCommand("cat $maxFreqPath").trim()
        val minFreqValue = executeCommand("cat $minFreqPath").trim()

        if (freqResult.isNotEmpty() && freqResult != "0") {
            val frequencies = freqResult.split("\\s+".toRegex()).map { it.trim() }

            // 找到 maxFreqValue 和 minFreqValue 在 frequencies 中的索引
            val maxIndex = frequencies.indexOf(maxFreqValue).takeIf { it >= 0 } ?: 0
            val minIndex = frequencies.indexOf(minFreqValue).takeIf { it >= 0 } ?: 0

            cpuFrequencies[i] = Triple(frequencies, maxIndex, minIndex)
        }
    }

    return cpuFrequencies
}

