package com.suqi8.oshin.ui.main

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.highcapable.yukihookapi.hook.factory.prefs
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.suqi8.oshin.Main_Function
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.about.Main_About
import com.suqi8.oshin.ui.components.BottomTabs
import com.suqi8.oshin.ui.home.MainHome
import com.suqi8.oshin.ui.module.Main_Module
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeEffectScope
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlin.math.abs

// 将 LocalColorMode 定义在与使用它的地方更近的文件中
val LocalColorMode = compositionLocalOf<MutableState<Int>> { error("No ColorMode provided") }

data class NavigationItem(
    val label: String,
    val icon: Int
)

@OptIn(FlowPreview::class, ExperimentalHazeApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavController) {

    val scope = rememberCoroutineScope()
    val topAppBarScrollBehavior0 = MiuixScrollBehavior(rememberTopAppBarState())
    val topAppBarScrollBehavior1 = MiuixScrollBehavior(rememberTopAppBarState())
    val topAppBarScrollBehavior2 = MiuixScrollBehavior(rememberTopAppBarState())
    val topAppBarScrollBehavior3 = MiuixScrollBehavior(rememberTopAppBarState())

    val topAppBarScrollBehaviorList = listOf(
        topAppBarScrollBehavior0, topAppBarScrollBehavior1, topAppBarScrollBehavior2, topAppBarScrollBehavior3
    )

    val pagerState = rememberPagerState(pageCount = { 4 }, initialPage = 0)
    val targetPage = remember { mutableIntStateOf(pagerState.currentPage) }
    val coroutineScope = rememberCoroutineScope()

    val currentScrollBehavior = topAppBarScrollBehaviorList[pagerState.currentPage]

    val items = listOf(
        NavigationItem(stringResource(R.string.home), R.drawable.home),
        NavigationItem(stringResource(R.string.module), R.drawable.module),
        NavigationItem(stringResource(R.string.func), R.drawable.func),
        NavigationItem(stringResource(R.string.about), R.drawable.about)
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.debounce(150).collectLatest {
            targetPage.intValue = pagerState.currentPage
        }
    }
    var isBottomBarVisible by remember { mutableStateOf(true) }
    LaunchedEffect(currentScrollBehavior) {
        var previousOffset = 0
        val threshold = 50

        snapshotFlow { currentScrollBehavior.state.contentOffset.toInt() }
            .collect { currentOffset ->
                if (currentOffset >= -5) {
                    isBottomBarVisible = true
                    previousOffset = currentOffset
                    return@collect
                }

                val delta = currentOffset - previousOffset

                if (abs(delta) > threshold) {
                    isBottomBarVisible = delta >= 0
                    previousOffset = currentOffset
                }
            }
    }
    LaunchedEffect(pagerState.currentPage) {
        isBottomBarVisible = true
    }

    val backdrop = rememberLayerBackdrop()

    val context = LocalContext.current
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AnimatedVisibility(pagerState.currentPage != 3) {
                TopAppBar(
                    modifier = Modifier.hazeEffect(
                        state = hazeState,
                        style = hazeStyle, block = fun HazeEffectScope.() {
                            inputScale = HazeInputScale.Auto
                            progressive = HazeProgressive.verticalGradient(startIntensity = 1f, endIntensity = 0f)
                        }),
                    scrollBehavior = currentScrollBehavior,
                    color = Color.Transparent,
                    title = when (pagerState.currentPage) {
                        0 -> stringResource(R.string.app_name)
                        1 -> stringResource(R.string.module)
                        2 -> stringResource(R.string.func)
                        else -> stringResource(R.string.about)
                    }
                )
            }
        }
    ) { padding ->
        Box() {
            AppHorizontalPager(
                modifier = Modifier.layerBackdrop(backdrop).hazeSource(state = hazeState).imePadding(),
                pagerState = pagerState,
                topAppBarScrollBehaviorList = topAppBarScrollBehaviorList,
                padding = padding,
                navController = navController,
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .safeDrawingPadding()
            ) {
                AnimatedVisibility(
                    visible = isBottomBarVisible,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    BottomTabs(
                        modifier = Modifier.align(Alignment.BottomStart),
                        tabs = items,
                        pagerState = pagerState,
                        onTabSelected = { screen ->
                            scope.launch {
                                pagerState.animateScrollToPage(screen)
                            }
                        },
                        backdrop = backdrop
                    )
                }
            }
        }
    }
}


@Composable
fun AppHorizontalPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    topAppBarScrollBehaviorList: List<ScrollBehavior>,
    padding: PaddingValues,
    navController: NavController
) {
    HorizontalPager(
        modifier = modifier.background(MiuixTheme.colorScheme.background),
        state = pagerState,
        beyondViewportPageCount = 3,
        userScrollEnabled = true,
        pageContent = { page ->
            when (page) {
                0 -> MainHome(
                    topAppBarScrollBehavior = topAppBarScrollBehaviorList[0],
                    padding = padding,
                    navController = navController
                )

                1 -> Main_Module(
                    topAppBarScrollBehavior = topAppBarScrollBehaviorList[1],
                    padding = padding,
                    navController = navController
                )

                2 -> Main_Function(
                    topAppBarScrollBehavior = topAppBarScrollBehaviorList[2],
                    padding = padding,
                    navController = navController
                )

                else -> Main_About(
                    topAppBarScrollBehavior = topAppBarScrollBehaviorList[3],
                    padding = padding,
                    context = LocalContext.current,
                    navController = navController
                )
            }
        }
    )
}
