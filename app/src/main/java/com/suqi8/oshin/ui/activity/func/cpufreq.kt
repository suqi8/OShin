package com.suqi8.oshin.ui.activity.func

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.components.Card
import com.suqi8.oshin.ui.activity.components.FunPage
import com.suqi8.oshin.ui.activity.components.SuperDropdown
import com.suqi8.oshin.ui.activity.components.addline
import com.suqi8.oshin.utils.executeCommand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.PullToRefresh
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.rememberPullToRefreshState
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun cpu_freq(
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val topAppBarState = MiuixScrollBehavior(rememberTopAppBarState())

    FunPage(
        title = stringResource(R.string.cpu_freq_main),
        navController = navController,
        scrollBehavior = topAppBarState,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        animationKey = "func\\cpu_freq"
    ) { padding ->
        val pullToRefreshState = rememberPullToRefreshState()
        var isRefreshing by rememberSaveable { mutableStateOf(false) }
        val scope = rememberCoroutineScope()
        val cpuFrequencies =
            remember { mutableStateOf<Map<Int, Triple<List<String>, Int, Int>>>(emptyMap()) }
        LaunchedEffect(isRefreshing) {
            if (cpuFrequencies.value.isEmpty()) isRefreshing = true
            if (isRefreshing) {
                cpuFrequencies.value = getAllCoresFrequencies()
                isRefreshing = false
            }
        }

        PullToRefresh(
            modifier = Modifier,
            pullToRefreshState = pullToRefreshState,
            isRefreshing = isRefreshing,
            onRefresh = { isRefreshing = true }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .overScrollVertical()
                    .scrollEndHaptic()
                    .nestedScroll(topAppBarState.nestedScrollConnection),
                contentPadding = padding
            ) {
                item {
                    AnimatedVisibility(!cpuFrequencies.value.isEmpty()) {
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
                                        title = stringResource(R.string.cpu_group_max_freq),
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
                                        title = stringResource(R.string.cpu_group_min_freq),
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
        }
    }
}

fun setCpuFrequency(core: Int, frequency: String, isMax: Boolean) {
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

suspend fun getAllCoresFrequencies(): Map<Int, Triple<List<String>, Int, Int>> =
    coroutineScope {
        val coreCount = withContext(Dispatchers.IO) {
            executeCommand("ls /sys/devices/system/cpu/ | grep -E 'cpu[0-9]+' | wc -l")
                .toIntOrNull() ?: 0
        }

        (0 until coreCount).map { i ->
            async(Dispatchers.IO) {
                val freqPath = "/sys/devices/system/cpu/cpu$i/cpufreq/scaling_available_frequencies"
                val maxFreqPath = "/sys/devices/system/cpu/cpu$i/cpufreq/scaling_max_freq"
                val minFreqPath = "/sys/devices/system/cpu/cpu$i/cpufreq/scaling_min_freq"

                val freqResult = executeCommand("cat $freqPath")
                val maxFreqValue = executeCommand("cat $maxFreqPath").trim()
                val minFreqValue = executeCommand("cat $minFreqPath").trim()

                if (freqResult.isNotEmpty() && freqResult != "0") {
                    val frequencies = freqResult.split("\\s+".toRegex()).map { it.trim() }
                    val maxIndex = frequencies.indexOf(maxFreqValue).takeIf { it >= 0 } ?: 0
                    val minIndex = frequencies.indexOf(minFreqValue).takeIf { it >= 0 } ?: 0
                    i to Triple(frequencies, maxIndex, minIndex)
                } else null
            }
        }.awaitAll()
            .filterNotNull()
            .toMap()
    }
