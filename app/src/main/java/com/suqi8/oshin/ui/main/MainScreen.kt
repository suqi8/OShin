package com.suqi8.oshin.ui.main

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.highcapable.yukihookapi.hook.factory.prefs
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.suqi8.oshin.Main_Function
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.about.Main_About
import com.suqi8.oshin.ui.activity.components.BlurredTopBarBackground
import com.suqi8.oshin.ui.activity.components.BottomTabs
import com.suqi8.oshin.ui.home.MainHome
import com.suqi8.oshin.ui.module.Main_Module
import com.suqi8.oshin.ui.nav.path.NavPath
import com.suqi8.oshin.ui.nav.ui.NavStackScope
import com.suqi8.oshin.ui.softupdate.UpdateViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlin.math.abs

// 将 LocalColorMode 定义在与使用它的地方更近的文件中
val LocalColorMode = compositionLocalOf<MutableState<Int>> { error("No ColorMode provided") }

@OptIn(FlowPreview::class, ExperimentalSharedTransitionApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    navPath: NavPath,
    navStackScope: NavStackScope
) {
    val scope = rememberCoroutineScope()
    val topAppBarScrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val pagerState = rememberPagerState(pageCount = { 4 }, initialPage = 0)
    val context = LocalContext.current
    val backdrop = rememberLayerBackdrop()

    // 底部导航栏项目
    val bottomNavItems = listOf(
        NavigationItem(stringResource(R.string.home), ImageVector.vectorResource(R.drawable.home)),
        NavigationItem(stringResource(R.string.module), ImageVector.vectorResource(R.drawable.module)),
        NavigationItem(stringResource(R.string.func), ImageVector.vectorResource(R.drawable.func)),
        NavigationItem(stringResource(R.string.about), ImageVector.vectorResource(R.drawable.about))
    )

    // 底部栏可见性状态
    var isBottomBarVisible by rememberSaveable { mutableStateOf(true) }

    // 监听滚动行为控制底部栏显示/隐藏
    BottomBarVisibilityEffect(
        scrollBehavior = topAppBarScrollBehavior,
        onVisibilityChange = { isBottomBarVisible = it }
    )

    // 监听页面切换重置底部栏显示
    LaunchedEffect(pagerState.currentPage) {
        isBottomBarVisible = true
    }

    // 版本更新检查
    val activity = context as ComponentActivity
    val updateViewModel: UpdateViewModel = hiltViewModel(activity)
    val currentVersion = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "0.0.0"
        }.getOrDefault("0.0.0")
    }

    LaunchedEffect(Unit) {
        updateViewModel.autoCheckForUpdate(currentVersion)
    }

    val updateResult by updateViewModel.updateCheckResult.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                scrollBehavior = topAppBarScrollBehavior,
                color = Color.Transparent,
                modifier = Modifier.height(0.dp),
                title = ""
            )
        }
    ) { padding ->
        Box {
            AppHorizontalPager(
                modifier = Modifier
                    .layerBackdrop(backdrop)
                    .imePadding(),
                pagerState = pagerState,
                topAppBarScrollBehavior = topAppBarScrollBehavior,
                padding = padding,
                navPath = navPath,
                navStackScope = navStackScope
            )

            BlurredTopBarBackground(backdrop)

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter // 直接把所有子元素默认对齐到底部
            ) {
                AnimatedBottomBar(
                    isVisible = isBottomBarVisible,
                    items = bottomNavItems,
                    pagerState = pagerState,
                    scope = scope,
                    backdrop = backdrop
                )
            }

            UpdateAvailableDialog(
                release = updateResult,
                navPath = navPath,
                navStackScope = navStackScope,
                onDismiss = { updateViewModel.clearUpdateCheckResult() },
                updateViewModel = updateViewModel
            )
        }
    }
}

/**
 * 底部栏可见性控制效果
 */
@Composable
private fun BottomBarVisibilityEffect(
    scrollBehavior: ScrollBehavior,
    onVisibilityChange: (Boolean) -> Unit
) {
    LaunchedEffect(scrollBehavior) {
        var previousOffset = 0
        val threshold = 50

        snapshotFlow { scrollBehavior.state.contentOffset.toInt() }
            .collect { currentOffset ->
                // 接近顶部时始终显示
                if (currentOffset >= -5) {
                    onVisibilityChange(true)
                    previousOffset = currentOffset
                    return@collect
                }

                val delta = currentOffset - previousOffset

                // 滚动超过阈值时切换显示状态
                if (abs(delta) > threshold) {
                    onVisibilityChange(delta >= 0)
                    previousOffset = currentOffset
                }
            }
    }
}

/**
 * 带动画的底部导航栏
 */
@Composable
private fun AnimatedBottomBar(
    isVisible: Boolean,
    items: List<NavigationItem>,
    pagerState: PagerState,
    scope: CoroutineScope,
    backdrop: Backdrop
) {
    val context = LocalContext.current
    val bottomTabMode = remember { context.prefs("settings").getBoolean("bottomtab") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            if (!bottomTabMode) {
                BottomTabs(
                    modifier = Modifier,
                    tabs = items,
                    pagerState = pagerState,
                    onTabSelected = { screen ->
                        scope.launch {
                            pagerState.animateScrollToPage(screen)
                        }
                    },
                    backdrop = backdrop
                )
            } else {
                NavigationBar(
                    items = items,
                    selected = pagerState.currentPage,
                    onClick = { screen ->
                        scope.launch {
                            pagerState.animateScrollToPage(screen)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppHorizontalPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues,
    navPath: NavPath,
    navStackScope: NavStackScope
) {
    HorizontalPager(
        modifier = modifier.background(MiuixTheme.colorScheme.background),
        state = pagerState,
        beyondViewportPageCount = 3,
        userScrollEnabled = true,
        pageContent = { page ->
            Box(modifier = Modifier
                .rememberPageBlurModifier(pagerState, page)
                .fillMaxSize()) {
                when (page) {
                    0 -> MainHome(
                        topAppBarScrollBehavior = topAppBarScrollBehavior,
                        padding = padding,
                        navPath = navPath,
                        navStackScope = navStackScope
                    )

                    1 -> Main_Module(
                        topAppBarScrollBehavior = topAppBarScrollBehavior,
                        padding = padding,
                        navPath = navPath,
                        navStackScope = navStackScope
                    )

                    2 -> Main_Function(
                        topAppBarScrollBehavior = topAppBarScrollBehavior,
                        padding = padding,
                        navPath = navPath,
                        navStackScope = navStackScope
                    )

                    else -> Main_About(
                        topAppBarScrollBehavior = topAppBarScrollBehavior,
                        padding = padding,
                        navPath = navPath,
                        navStackScope = navStackScope
                    )
                }
            }
        }
    )
}

/**
 * 记住页面模糊效果的 Modifier
 */
@Composable
private fun Modifier.rememberPageBlurModifier(pagerState: PagerState, page: Int): Modifier {
    val maxBlurRadius = 16.dp
    val maxBlurRadiusPx = with(LocalDensity.current) { maxBlurRadius.toPx() }

    return remember(pagerState.currentPage, pagerState.currentPageOffsetFraction) {
        // 页面偏移量（正负表示左右偏移）
        val offset = runCatching { pagerState.getOffsetDistanceInPages(page) }.getOrDefault(0f)
        val blurPx = abs(offset) * maxBlurRadiusPx

        Modifier.graphicsLayer {
            if (blurPx > 0.1f) {
                renderEffect = BlurEffect(blurPx, blurPx, TileMode.Decal)
            }
        }
    }
}
