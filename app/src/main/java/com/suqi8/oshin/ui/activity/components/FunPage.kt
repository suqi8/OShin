package com.suqi8.oshin.ui.activity.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.suqi8.oshin.ui.components.LiquidButton
import com.suqi8.oshin.utils.hasShortcut
import com.suqi8.oshin.utils.launchApp
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeEffectScope
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Back
import top.yukonga.miuix.kmp.icon.icons.useful.Play
import top.yukonga.miuix.kmp.icon.icons.useful.Refresh
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

// --- 版本 1 (兼容旧页面) ---
// 这个版本接收一个无参数的 content lambda，并为其内部创建一个 LazyColumn。
// 所有老的页面都会自动调用这个版本。
@OptIn(ExperimentalHazeApi::class)
@Composable
fun FunPage(
    title: String,
    appList: List<String> = emptyList(),
    navController: NavController,
    content: @Composable () -> Unit // <-- 无参数的 lambda
) {
    // 为旧页面创建它们自己的滚动状态
    val topAppBarState = MiuixScrollBehavior(rememberTopAppBarState())

    // 直接调用新版本的 funPage，并为其构建滚动环境
    FunPage(
        title = title,
        appList = appList,
        navController = navController,
        scrollBehavior = topAppBarState
    ) { padding ->
        // 在内部创建一个 LazyColumn，模拟旧版本的行为
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .overScrollVertical()
                .scrollEndHaptic()
                .nestedScroll(topAppBarState.nestedScrollConnection),
            contentPadding = padding
        ) {
            item {
                content() // 将旧页面的内容放在 item 中
            }
        }
    }
}


// --- 版本 2 (新架构主版本) ---
// 这个版本接收一个带 PaddingValues 参数的 content lambda。
// 我们的 featureScreen 会自动调用这个版本。
@OptIn(ExperimentalHazeApi::class)
@Composable
fun FunPage(
    title: String,
    appList: List<String> = emptyList(),
    navController: NavController,
    scrollBehavior: ScrollBehavior,
    content: @Composable (padding: PaddingValues) -> Unit
) {
    val context = LocalContext.current
    val restartAPP = remember { mutableStateOf(false) }
    val backdrop = rememberLayerBackdrop()
    val hazeState = remember { HazeState() }
    val hazeStyle = HazeStyle(
        backgroundColor = MiuixTheme.colorScheme.background,
        tint = HazeTint(MiuixTheme.colorScheme.background.copy(0.75f)),
        blurRadius = 25.dp,
        noiseFactor = 0f
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = title,
                // 1. 使用透明背景
                color = Color.Transparent,
                // 2. 应用 Haze 模糊效果
                modifier = Modifier.hazeEffect(
                    state = hazeState,
                    style = hazeStyle,
                    block = fun HazeEffectScope.() {
                        inputScale = HazeInputScale.Auto
                        progressive = HazeProgressive.verticalGradient(startIntensity = 1f, endIntensity = 0f)
                    }
                ),
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    // 3. 使用 LiquidButton
                    LiquidButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(40.dp),
                        backdrop = backdrop
                    ) {
                        Icon(
                            imageVector = MiuixIcons.Useful.Back,
                            contentDescription = "Back",
                            modifier = Modifier.size(22.dp),
                            tint = MiuixTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    var showShortcut by remember { mutableStateOf(false) }

                    LaunchedEffect(appList) {
                        withContext(Dispatchers.IO) { // 后台线程
                            if (appList.size == 1 && hasShortcut(context, appList.first())) showShortcut = true
                        }
                    }
                    AnimatedVisibility(visible = showShortcut) {
                        LiquidButton(
                            onClick = { launchApp(context, appList.first()) },
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(40.dp),
                            backdrop = backdrop
                        ) {
                            Icon(
                                imageVector = MiuixIcons.Useful.Play,
                                contentDescription = "Open Shortcut",
                                modifier = Modifier.size(22.dp),
                                tint = MiuixTheme.colorScheme.onBackground
                            )
                        }
                    }

                    if (appList.isNotEmpty()) {
                        LiquidButton(
                            onClick = { restartAPP.value = true },
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(40.dp),
                            backdrop = backdrop
                        ) {
                            Icon(
                                imageVector = MiuixIcons.Useful.Refresh,
                                contentDescription = "Refresh",
                                modifier = Modifier.size(22.dp),
                                tint = MiuixTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            Modifier
                // 5. 应用 Backdrop 和 HazeSource 以支持按钮效果和模糊
                .layerBackdrop(backdrop)
                .hazeSource(state = hazeState)
                .fillMaxSize()
                .background(MiuixTheme.colorScheme.background)
        ) {
            content(padding)
        }
    }

    if (appList.isNotEmpty() && restartAPP.value) {
         AppRestartScreen(appList, restartAPP, backdrop)
    }
}
