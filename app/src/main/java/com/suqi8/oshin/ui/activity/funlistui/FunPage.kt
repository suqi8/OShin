package com.suqi8.oshin.ui.activity.funlistui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kyant.liquidglass.GlassStyle
import com.kyant.liquidglass.liquidGlass
import com.kyant.liquidglass.liquidGlassProvider
import com.kyant.liquidglass.material.GlassMaterial
import com.kyant.liquidglass.refraction.InnerRefraction
import com.kyant.liquidglass.refraction.RefractionAmount
import com.kyant.liquidglass.refraction.RefractionHeight
import com.kyant.liquidglass.rememberLiquidGlassProviderState
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
    val lazyListState = rememberLazyListState()
    val liquidGlassProviderState = rememberLiquidGlassProviderState(MiuixTheme.colorScheme.surfaceContainer)

    val iconButtonLiquidGlassStyle =
        GlassStyle(
            RoundedCornerShape(50),
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
                title = title,
                color = Color.Transparent,
                modifier = Modifier,
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .liquidGlass(liquidGlassProviderState, iconButtonLiquidGlassStyle)
                            .clickable { navController.popBackStack() }
                            .size(48.dp),
                        contentAlignment = Alignment.Center
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
                        Box(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .liquidGlass(liquidGlassProviderState, iconButtonLiquidGlassStyle)
                                .clickable {
                                    restartAPP.value = true
                                }
                                .size(48.dp),
                            contentAlignment = Alignment.Center
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
                .liquidGlassProvider(liquidGlassProviderState)
                .fillMaxSize()
        ) {
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
    Box(modifier = Modifier.fillMaxSize().liquidGlassProvider(liquidGlassProviderState)) {
        if (!appList.isNullOrEmpty()) {
            if (restartAPP.value) {
                AppRestartScreen(appList, restartAPP,liquidGlassProviderState)
            }
        }
    }
}
