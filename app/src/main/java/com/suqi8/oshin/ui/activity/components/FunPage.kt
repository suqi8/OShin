package com.suqi8.oshin.ui.activity.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.highcapable.yukihookapi.hook.factory.prefs
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.suqi8.oshin.ui.components.LiquidButton
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeEffectScope
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Back
import top.yukonga.miuix.kmp.icon.icons.useful.Refresh
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical

@OptIn(ExperimentalHazeApi::class)
@Composable
fun FunPage(
    title: String,
    appList: List<String>? = listOf(),
    navController: NavController,
    content: @Composable () -> Unit
) {
    val topAppBarState = MiuixScrollBehavior(rememberTopAppBarState())
    val restartAPP = remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()
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
        topBar = {
            TopAppBar(
                scrollBehavior = topAppBarState,
                title = title,
                color = Color.Transparent,
                modifier = Modifier.hazeEffect(
                    state = hazeState,
                    style = hazeStyle, block = fun HazeEffectScope.() {
                        inputScale = HazeInputScale.Auto
                        progressive = HazeProgressive.verticalGradient(startIntensity = 1f, endIntensity = 0f)
                    }),
                navigationIcon = {
                    LiquidButton(
                        { navController.popBackStack() },
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .clickable {  }
                            .size(40.dp),
                        backdrop = backdrop
                    ) {
                        Icon(
                            imageVector = MiuixIcons.Useful.Back,
                            contentDescription = "Back",
                            Modifier.size(22.dp),
                            tint = MiuixTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    if (!appList.isNullOrEmpty()) {
                        LiquidButton(
                            { restartAPP.value = true },
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clickable {  }
                                .size(40.dp),
                            backdrop = backdrop
                        ) {
                            Icon(
                                imageVector = MiuixIcons.Useful.Refresh,
                                contentDescription = "Refresh",
                                Modifier.size(22.dp),
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
                .layerBackdrop(backdrop)
                .hazeSource(state = hazeState)
                .fillMaxSize()
        ) {
            Row {

            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MiuixTheme.colorScheme.background)
                    .overScrollVertical()
                    .nestedScroll(topAppBarState.nestedScrollConnection),
                contentPadding = padding,
                state = lazyListState
            ) {
                item {
                    content()
                    Spacer(Modifier.height(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()))
                }
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        if (!appList.isNullOrEmpty()) {
            if (restartAPP.value) {
                AppRestartScreen(appList, restartAPP, backdrop)
            }
        }
    }
}
