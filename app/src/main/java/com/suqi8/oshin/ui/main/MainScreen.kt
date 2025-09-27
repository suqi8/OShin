package com.suqi8.oshin.ui.main

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.BasicText
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
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kyant.capsule.ContinuousRoundedRectangle
import com.kyant.liquidglass.GlassStyle
import com.kyant.liquidglass.highlight.GlassHighlight
import com.kyant.liquidglass.liquidGlass
import com.kyant.liquidglass.liquidGlassProvider
import com.kyant.liquidglass.material.GlassMaterial
import com.kyant.liquidglass.material.simpleColorFilter
import com.kyant.liquidglass.refraction.InnerRefraction
import com.kyant.liquidglass.refraction.RefractionAmount
import com.kyant.liquidglass.refraction.RefractionHeight
import com.kyant.liquidglass.rememberLiquidGlassProviderState
import com.kyant.liquidglass.shadow.GlassShadow
import com.suqi8.oshin.Main_Function
import com.suqi8.oshin.Main_Home
import com.suqi8.oshin.Main_Module
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.about.Main_About
import com.suqi8.oshin.utils.BottomTabs
import com.suqi8.oshin.utils.BottomTabsScope
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

@OptIn(FlowPreview::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavController) {
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

    data class NavigationItem(
        val label: String,
        val icon: Int
    )

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

    val providerState = rememberLiquidGlassProviderState(
        backgroundColor = MiuixTheme.colorScheme.background
    )
    val commonGlassMaterial = GlassMaterial(
        blurRadius = 3.dp,
        alpha = 0.1f,
        colorFilter = simpleColorFilter(saturation = 1.5f)
    )
    val topAppBarStyle = GlassStyle(
        shape = ContinuousRoundedRectangle(28.dp),
        material = commonGlassMaterial,
        innerRefraction = InnerRefraction(
            height = RefractionHeight(8.dp),
            amount = RefractionAmount.Full
        ),
        highlight = GlassHighlight.Default.copy(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.5f)
        ),
        shadow = GlassShadow(elevation = 0.dp, brush = SolidColor(Color.Transparent.copy(alpha = 0.15f)), alpha = 0f)
    )
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AnimatedVisibility(pagerState.currentPage != 3) {
                TopAppBar(
                    scrollBehavior = currentScrollBehavior,
                    color = Color.Transparent,
                    title = when (pagerState.currentPage) {
                        0 -> stringResource(R.string.app_name)
                        1 -> stringResource(R.string.module)
                        2 -> stringResource(R.string.func)
                        else -> stringResource(R.string.about)
                    },
                    modifier = Modifier.liquidGlass(providerState, style = topAppBarStyle)
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = isBottomBarVisible,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                Column(
                    Modifier
                        .padding(32.dp, 8.dp)
                        .safeDrawingPadding()
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BottomTabs(
                            tabs = items,
                            selectedIndexState = targetPage,
                            liquidGlassProviderState = providerState,
                            background = MiuixTheme.colorScheme.surfaceContainer,
                            modifier = Modifier.weight(1f),
                            onTabSelected = { index ->
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                        ) { tab ->
                            BottomTabsScope().BottomTab({ color ->
                                Box(
                                    Modifier
                                        .size(24.dp)
                                        .paint(
                                            painterResource(tab.icon),
                                            colorFilter = ColorFilter.tint(color())
                                        )
                                )
                            }, { color ->
                                BasicText(tab.label, color = color)
                            }
                            )
                        }
                    }
                }
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .liquidGlassProvider(providerState)
        ) {
            AppHorizontalPager(
                modifier = Modifier.imePadding(),
                pagerState = pagerState,
                topAppBarScrollBehaviorList = topAppBarScrollBehaviorList,
                padding = padding,
                navController = navController,
            )
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
        userScrollEnabled = true,
        pageContent = { page ->
            when (page) {
                0 -> Main_Home(
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
