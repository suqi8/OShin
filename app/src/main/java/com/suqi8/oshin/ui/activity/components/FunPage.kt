package com.suqi8.oshin.ui.activity.components

import android.graphics.RenderEffect
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawPlainBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.effect
import com.suqi8.oshin.ui.components.LiquidButton
import com.suqi8.oshin.utils.hasShortcut
import com.suqi8.oshin.utils.launchApp
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeState
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

@OptIn(ExperimentalHazeApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun FunPage(
    title: String,
    appList: List<String> = emptyList(),
    navController: NavController,
    content: @Composable () -> Unit
) {
    val topAppBarState = MiuixScrollBehavior(rememberTopAppBarState())
    FunPage(
        title = title,
        appList = appList,
        navController = navController,
        scrollBehavior = topAppBarState
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .overScrollVertical()
                .scrollEndHaptic()
                .nestedScroll(topAppBarState.nestedScrollConnection),
            contentPadding = padding
        ) {
            item { content() }
        }
    }
}

@OptIn(ExperimentalHazeApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun FunPage(
    title: String = "",
    appList: List<String> = emptyList(),
    navController: NavController,
    scrollBehavior: ScrollBehavior,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    animationKey: String? = null,
    action: @Composable () -> Unit = {},
    content: @Composable (padding: PaddingValues) -> Unit
) {
    val restartAPP = remember { mutableStateOf(false) }
    val backdrop = rememberLayerBackdrop()
    val hazeState = remember { HazeState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = title,
                color = Color.Transparent,
                scrollBehavior = scrollBehavior,
                modifier = Modifier.height(0.dp)
            )
        },
        modifier = sharedTransitionScope?.let { scope ->
            animationKey?.let { key ->
                animatedVisibilityScope?.let { animScope ->
                    with(scope) {
                        Modifier.sharedBounds(
                            sharedContentState = rememberSharedContentState(key = key),
                            animatedVisibilityScope = animScope
                        )
                    }
                }
            }
        } ?: Modifier
    ) { padding ->
        val background = MiuixTheme.colorScheme.background

        Box(Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .layerBackdrop(backdrop)
                    .background(background)
                    .fillMaxSize()
            ) {
                content(padding)
                /*if (sharedTransitionScope?.isTransitionActive == true) {
                    Box(Modifier.fillMaxSize().pointerInput(Unit) {})
                }*/
            }

            // --- 顶部按钮栏 ---
            Column(Modifier.align(Alignment.TopStart)) {
                TopButtons(navController, appList, backdrop, restartAPP, action)
            }

            // --- 顶部模糊栏 ---
            if (title == "") {
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
            }
        }
    }

    if (appList.isNotEmpty() && restartAPP.value) {
        AppRestartScreen(appList, restartAPP, backdrop)
    }
}

@Composable
private fun TopButtons(
    navController: NavController,
    appList: List<String>,
    backdrop: Backdrop,
    restartAPP: MutableState<Boolean>,
    action: @Composable () -> Unit
) {
    val context = LocalContext.current
    var showShortcut by remember { mutableStateOf(false) }

    LaunchedEffect(appList) {
        withContext(Dispatchers.IO) {
            if (appList.size == 1 && hasShortcut(context, appList.first())) {
                showShortcut = true
            }
        }
    }

    Row(
        Modifier
            .displayCutoutPadding()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LiquidButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.size(40.dp),
            backdrop = backdrop
        ) {
            Icon(
                imageVector = MiuixIcons.Useful.Back,
                contentDescription = "Back",
                modifier = Modifier.size(22.dp),
                tint = MiuixTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        action()

        if (showShortcut) {
            LiquidButton(
                onClick = { launchApp(context, appList.first()) },
                modifier = Modifier.size(40.dp),
                backdrop = backdrop
            ) {
                Icon(
                    imageVector = MiuixIcons.Useful.Play,
                    contentDescription = "Open Shortcut",
                    modifier = Modifier.size(22.dp),
                    tint = MiuixTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        if (appList.isNotEmpty()) {
            LiquidButton(
                onClick = { restartAPP.value = true },
                modifier = Modifier.size(40.dp),
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
}
