package com.suqi8.oshin.ui.activity.func

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kyant.expressa.m3.shape.CornerShape
import com.kyant.liquidglass.GlassStyle
import com.kyant.liquidglass.liquidGlass
import com.kyant.liquidglass.material.GlassMaterial
import com.kyant.liquidglass.refraction.InnerRefraction
import com.kyant.liquidglass.refraction.RefractionAmount
import com.kyant.liquidglass.refraction.RefractionHeight
import com.kyant.liquidglass.rememberLiquidGlassProviderState
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.funlistui.addline
import com.suqi8.oshin.utils.executeCommand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
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
import top.yukonga.miuix.kmp.utils.overScrollVertical


@Composable
fun cpu_freq(
    navController: NavController
) {
    val topAppBarState = MiuixScrollBehavior(rememberTopAppBarState())
    val lazyListState = rememberLazyListState()
    val liquidGlassProviderState = rememberLiquidGlassProviderState(MiuixTheme.colorScheme.surfaceContainer)

    val iconButtonLiquidGlassStyle =
        GlassStyle(
            CornerShape.full,
            innerRefraction = InnerRefraction(
                height = RefractionHeight(8.dp),
                amount = RefractionAmount.Full
            ),
            material = GlassMaterial(
                brush = SolidColor(MiuixTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f))
            )
        )
    Scaffold(
        topBar = {
            TopAppBar(
                scrollBehavior = topAppBarState,
                title = stringResource(R.string.cpu_freq_main),
                color = Color.Transparent,
                modifier = Modifier,
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = CornerShape.full,
                                ambientColor = MiuixTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                spotColor = MiuixTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            .liquidGlass(liquidGlassProviderState, iconButtonLiquidGlassStyle)
                            .clickable { navController.popBackStack() }
                            .size(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MiuixIcons.Useful.Back,
                            contentDescription = "Back",
                            Modifier.size(22.dp),
                            tint = MiuixTheme.colorScheme.onBackground
                        )
                    }
                }
            )
        }
    ) { padding ->
        val pullToRefreshState = rememberPullToRefreshState()
        var isRefreshing by rememberSaveable { mutableStateOf(false) }
        val scope = rememberCoroutineScope()
        val cpuFrequencies = remember { mutableStateOf<Map<Int, Triple<List<String>, Int, Int>>>(emptyMap()) }
        LaunchedEffect(isRefreshing) {
            if (isRefreshing) {
                isRefreshing = true
                cpuFrequencies.value = getAllCoresFrequencies()
                isRefreshing = false
            }
        }
        LaunchedEffect(cpuFrequencies.value.size) {
            if (cpuFrequencies.value.isEmpty()) {
                isRefreshing = true
                cpuFrequencies.value = getAllCoresFrequencies()
                isRefreshing = false
            }
        }
        PullToRefresh(
            modifier = Modifier.padding(
                padding
            ),
            pullToRefreshState = pullToRefreshState,
            isRefreshing = isRefreshing,
            onRefresh = { isRefreshing = true }
        ) {
            LazyColumn(
                modifier = Modifier
                    .overScrollVertical()
                    .fillMaxSize()
                    .background(MiuixTheme.colorScheme.background)
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
