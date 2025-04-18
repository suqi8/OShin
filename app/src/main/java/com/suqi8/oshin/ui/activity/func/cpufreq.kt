package com.suqi8.oshin.ui.activity.func

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.R
import com.suqi8.oshin.executeCommand
import com.suqi8.oshin.ui.activity.funlistui.addline
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeEffectScope
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.PullToRefresh
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberPullToRefreshState
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.SuperDropdown
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme

@OptIn(ExperimentalHazeApi::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun cpu_freq(navController: NavController) {
    val context = LocalContext.current
    val topAppBarState = MiuixScrollBehavior(rememberTopAppBarState())
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
    val lazyListState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState(firstVisibleItemIndex = 0) // 初始位置
    }
    Scaffold(topBar = {
        TopAppBar(
            scrollBehavior = topAppBarState,
            title = stringResource(R.string.cpu_freq_main),
            color = if (context.prefs("settings").getBoolean("enable_blur", true)) Color.Transparent else MiuixTheme.colorScheme.background,
            modifier = if (context.prefs("settings").getBoolean("enable_blur", true)) {
                Modifier.hazeEffect(
                    state = hazeState,
                    style = hazeStyle, block = fun HazeEffectScope.() {
                        inputScale = HazeInputScale.Auto
                        if (context.prefs("settings").getBoolean("enable_gradient_blur", true)) progressive = HazeProgressive.verticalGradient(startIntensity = 1f, endIntensity = 0f)
                    })
            } else Modifier,
            navigationIcon = {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier.padding(start = 18.dp)
                ) {
                    Icon(
                        imageVector = MiuixIcons.Useful.Back,
                        contentDescription = null,
                        tint = MiuixTheme.colorScheme.onBackground
                    )
                }
            }
        )
        Image(painter = painterResource(R.drawable.osu),contentDescription = null, modifier = Modifier.fillMaxWidth())
    }) { padding ->
        val pullToRefreshState = rememberPullToRefreshState()
        var isRefreshing by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()
        var cpuFrequencies = remember { mutableStateOf<Map<Int, Triple<List<String>, Int, Int>>>(emptyMap()) }
        LaunchedEffect(pullToRefreshState.isRefreshing) {
            if (pullToRefreshState.isRefreshing) {
                isRefreshing = true
                cpuFrequencies.value = getAllCoresFrequencies()
                pullToRefreshState.completeRefreshing {
                    isRefreshing = false
                }
            }
        }
        LaunchedEffect(cpuFrequencies.value.size) {
            if (cpuFrequencies.value.isEmpty()) {
                isRefreshing = true
                cpuFrequencies.value = getAllCoresFrequencies()
                pullToRefreshState.completeRefreshing {
                    isRefreshing = false
                }
            }
        }
        PullToRefresh(
            modifier = Modifier.padding(
                top = padding.calculateTopPadding()
            ),
            pullToRefreshState = pullToRefreshState
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .hazeSource(state = hazeState)
                    .background(MiuixTheme.colorScheme.background)
                    .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                    .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal))
                    .nestedScroll(topAppBarState.nestedScrollConnection),
                state = lazyListState
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
                    Spacer(Modifier.height(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()))
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

