package com.suqi8.oshin.ui.activity.funlistui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.highcapable.yukihookapi.hook.factory.prefs
import com.kyant.liquidglass.GlassBorder
import com.kyant.liquidglass.GlassMaterial
import com.kyant.liquidglass.InnerRefraction
import com.kyant.liquidglass.LiquidGlassStyle
import com.kyant.liquidglass.LocalLiquidGlassProviderState
import com.kyant.liquidglass.liquidGlass
import com.kyant.liquidglass.liquidGlassProvider
import com.kyant.liquidglass.rememberLiquidGlassProviderState
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Back
import top.yukonga.miuix.kmp.icon.icons.useful.Refresh
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical

@Composable
fun FunPage(
    title: String,
    appList: List<String>? = listOf(),
    navController: NavController,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val topAppBarState = MiuixScrollBehavior(rememberTopAppBarState())
    val restartAPP = remember { mutableStateOf(false) }
    val resetApp = resetApp()
    val lazyListState = rememberLazyListState()
    val providerState = rememberLiquidGlassProviderState()
    val enableBlur = context.prefs("settings").getBoolean("enable_blur", true)

    val liquidGlassStyle = LiquidGlassStyle(
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
        material = GlassMaterial(
            blurRadius = 0.dp,
            whitePoint = 0f,
            chromaMultiplier = 1.2f
        ),
        innerRefraction = InnerRefraction.Default,
        border = GlassBorder.Light(width = 1.dp)
    )

    CompositionLocalProvider(
        LocalLiquidGlassProviderState provides providerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    scrollBehavior = topAppBarState,
                    title = title,
                    color = if (enableBlur) Color.Transparent else MiuixTheme.colorScheme.background,
                    modifier = if (enableBlur) {
                        Modifier.liquidGlass(style = liquidGlassStyle)
                    } else Modifier,
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.padding(start = 18.dp)
                        ) {
                            Icon(
                                imageVector = MiuixIcons.Useful.Back,
                                contentDescription = "Back",
                                tint = MiuixTheme.colorScheme.onBackground
                            )
                        }
                    },
                    actions = {
                        if (!appList.isNullOrEmpty()) {
                            IconButton(
                                onClick = { restartAPP.value = true },
                                modifier = Modifier.padding(end = 18.dp)
                            ) {
                                Icon(
                                    imageVector = MiuixIcons.Useful.Refresh,
                                    contentDescription = "Refresh",
                                    tint = MiuixTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                )
            }
        ) { padding ->
            // ✅ 核心改动：将 provider 移到 LazyColumn 的父级 Box 上
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MiuixTheme.colorScheme.background)
                    .liquidGlassProvider(providerState)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
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
    }

    if (!appList.isNullOrEmpty()) {
        if (restartAPP.value) {
            resetApp.AppRestartScreen(appList, restartAPP)
        }
    }
}
