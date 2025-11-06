package com.suqi8.oshin.ui.main

import android.annotation.SuppressLint
import android.graphics.RenderEffect
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawPlainBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.effect
import com.suqi8.oshin.Main_Function
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.about.Main_About
import com.suqi8.oshin.ui.components.BottomTabs
import com.suqi8.oshin.ui.home.MainHome
import com.suqi8.oshin.ui.module.Main_Module
import com.suqi8.oshin.ui.softupdate.GitHubRelease
import com.suqi8.oshin.ui.softupdate.UpdateViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlin.math.abs

// 将 LocalColorMode 定义在与使用它的地方更近的文件中
val LocalColorMode = compositionLocalOf<MutableState<Int>> { error("No ColorMode provided") }

data class NavigationItem(
    val label: String,
    val icon: Int
)

@OptIn(FlowPreview::class, ExperimentalSharedTransitionApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val scope = rememberCoroutineScope()
    val topAppBarScrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())

    val pagerState = rememberPagerState(pageCount = { 4 }, initialPage = 0)
    val currentScrollBehavior = topAppBarScrollBehavior

    val items = listOf(
        NavigationItem(stringResource(R.string.home), R.drawable.home),
        NavigationItem(stringResource(R.string.module), R.drawable.module),
        NavigationItem(stringResource(R.string.func), R.drawable.func),
        NavigationItem(stringResource(R.string.about), R.drawable.about)
    )
    val context = LocalContext.current
    val currentVersion = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "0.0.0"
        } catch (e: Exception) {
            "0.0.0"
        }
    }
    val activity = context as ComponentActivity
    val updateViewModel: UpdateViewModel = hiltViewModel(activity)
    LaunchedEffect(Unit) {
        updateViewModel.autoCheckForUpdate(currentVersion)
    }

    val updateResult by updateViewModel.updateCheckResult.collectAsState()

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.debounce(150).collectLatest {
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                scrollBehavior = currentScrollBehavior,
                color = Color.Transparent,
                modifier = Modifier.height(0.dp),
                title = ""
            )
        }
    ) { padding ->
        Box {
            val background = MiuixTheme.colorScheme.background

            AppHorizontalPager(
                modifier = Modifier.layerBackdrop(backdrop).imePadding(),
                pagerState = pagerState,
                topAppBarScrollBehavior = topAppBarScrollBehavior,
                padding = padding,
                navController = navController,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope
            )
            Box(
                Modifier
                    .height(72.dp)
                    .fillMaxWidth()
                    .drawPlainBackdrop(
                        backdrop = backdrop,
                        shape = { RectangleShape },
                        effects = {
                            blur(4f.dp.toPx())
                            effect(
                                RenderEffect.createRuntimeShaderEffect(
                                    obtainRuntimeShader(
                                        "AlphaMask",
                                        """
uniform shader content;

uniform float2 size;
layout(color) uniform half4 tint;
uniform float tintIntensity;

half4 main(float2 coord) {
float blurAlpha = smoothstep(size.y, size.y * 0.2, coord.y);
float tintAlpha = smoothstep(size.y, size.y * 0.2, coord.y);
return mix(content.eval(coord) * blurAlpha, tint * tintAlpha, tintIntensity);
}"""
                                    ).apply {
                                        setFloatUniform("size", size.width, size.height)
                                        setColorUniform("tint", background.value.toLong())
                                        setFloatUniform("tintIntensity", 0.8f)
                                    },
                                    "content"
                                )
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {}

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
            UpdateAvailableDialog(
                release = updateResult,
                navController = navController,
                onDismiss = { updateViewModel.clearUpdateCheckResult() },
                updateViewModel = updateViewModel
            )
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
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    HorizontalPager(
        modifier = modifier.background(MiuixTheme.colorScheme.background),
        state = pagerState,
        beyondViewportPageCount = 3,
        userScrollEnabled = true,
        pageContent = { page ->
            val maxBlurRadius = 16.dp
            val maxBlurRadiusPx = with(LocalDensity.current) { maxBlurRadius.toPx() }
            val offset = try {
                pagerState.getOffsetDistanceInPages(page)
            } catch (e: IndexOutOfBoundsException) {
                0f
            }
            val isScrolling = pagerState.isScrollInProgress
            val blurPx = if (isScrolling) {
                // 如果正在滚动，就根据偏移量计算模糊度
                abs(offset) * maxBlurRadiusPx
            } else {
                // 如果已经停止滚动 (即使用户停在两页之间)，则不模糊
                0f
            }
            // 4. 创建一个动态的 Modifier
            val pageModifier = Modifier.graphicsLayer {
                // 只有在模糊值有意义时才应用效果
                if (blurPx > 0.1f) {
                    renderEffect = BlurEffect(
                        blurPx,
                        blurPx,
                        TileMode.Decal // Decal 模式在边缘处理上最干净
                    )
                }
            }
            Box(
                modifier = pageModifier.fillMaxSize()
            ) {
                when (page) {
                    0 -> MainHome(
                        topAppBarScrollBehavior = topAppBarScrollBehavior,
                        padding = padding,
                        navController = navController,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )

                    1 -> Main_Module(
                        topAppBarScrollBehavior = topAppBarScrollBehavior,
                        padding = padding,
                        navController = navController,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )

                    2 -> Main_Function(
                        topAppBarScrollBehavior = topAppBarScrollBehavior,
                        padding = padding,
                        navController = navController,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )

                    else -> Main_About(
                        topAppBarScrollBehavior = topAppBarScrollBehavior,
                        padding = padding,
                        navController = navController,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
            }
        }
    )
}

@Composable
private fun UpdateAvailableDialog(
    release: GitHubRelease?,
    navController: NavController,
    onDismiss: () -> Unit,
    updateViewModel: UpdateViewModel
) {
    val show = remember(release) { mutableStateOf(release != null) }

    SuperDialog(
        show = show,
        title = stringResource(R.string.update_page_status_new_version),
        summary = stringResource(R.string.update_available_dialog_summary, release?.name ?: ""),
        onDismissRequest = onDismiss
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.cancel),
                onClick = {
                    onDismiss()
                    show.value = false
                }
            )
            Spacer(Modifier.width(12.dp))
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.update_available_dialog_now),
                colors = ButtonDefaults.textButtonColorsPrimary(),
                onClick = {
                    onDismiss()
                    show.value = false
                    updateViewModel.setAutoDownloadFlag()
                    navController.navigate("software_update")
                }
            )
        }
    }
}
