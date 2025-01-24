package com.suqi8.oshin.ui.activity.com.android.systemui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.funlistui.FunNoEnable
import com.suqi8.oshin.ui.activity.funlistui.FunSwich
import com.suqi8.oshin.ui.tools.resetApp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeEffect
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.SuperDropdown
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack
import top.yukonga.miuix.kmp.theme.MiuixTheme

@SuppressLint("RtlHardcoded")
@Composable
fun statusbar_icon(navController: NavController) {
    val context = LocalContext.current
    val TopAppBarState = MiuixScrollBehavior(rememberTopAppBarState())
    val RestartAPP = remember { mutableStateOf(false) }
    val resetApp = resetApp()
    val appList = listOf("com.android.systemui")
    val showlist = listOf(stringResource(R.string.default_), stringResource(R.string.hide))
    val show_Wifi_icon = remember { mutableIntStateOf(context.prefs("systemui\\statusbar_icon").getInt("show_Wifi_icon", 0)) }
    val show_Wifi_arrow = remember { mutableIntStateOf(context.prefs("systemui\\statusbar_icon").getInt("show_Wifi_arrow", 0)) }

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

    Scaffold(topBar = {
        TopAppBar(
            scrollBehavior = TopAppBarState,
            title = stringResource(id = R.string.status_bar_icon),
            color = Color.Transparent,
            modifier = Modifier.hazeEffect(
                state = hazeState,
                style = hazeStyle
            ),
            navigationIcon = {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier.padding(start = 18.dp)
                ) {
                    Icon(
                        imageVector = MiuixIcons.ArrowBack,
                        contentDescription = null,
                        tint = MiuixTheme.colorScheme.onBackground
                    )
                }
            },
            actions = {
                // 如果你有其他操作按钮，这里可以添加
                IconButton(
                    onClick = {
                        RestartAPP.value = true
                    },
                    modifier = Modifier.padding(end = 18.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = null,
                        tint = MiuixTheme.colorScheme.onBackground
                    )
                }
            }
        )
    }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .haze(state = hazeState)
                .background(MiuixTheme.colorScheme.background)
                .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal)),
            contentPadding = PaddingValues(top = padding.calculateTopPadding()),
            topAppBarScrollBehavior = TopAppBarState
        ) {
            item {
                Column {
                    val statusbar_icon = remember { mutableStateOf(context.prefs("settings").getBoolean("statusbar_icon", false)) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 6.dp, top = 15.dp)
                    ) {
                        FunSwich(
                            title = stringResource(R.string.status_bar_icon),
                            category = "systemui\\statusbar_icon",
                            key = "statusbar_icon",
                            defValue = false,
                            onCheckedChange = {
                                statusbar_icon.value = it
                            }
                        )
                    }
                    AnimatedVisibility(
                        visible = !statusbar_icon.value
                    ) {
                        FunNoEnable()
                    }
                    AnimatedVisibility(
                        visible = statusbar_icon.value
                    ) {
                        Column {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row {
                                    SuperDropdown(title = stringResource(R.string.wifi_icon),
                                        items = showlist,
                                        selectedIndex = show_Wifi_icon.intValue,
                                        onSelectedIndexChange = {
                                            show_Wifi_icon.intValue = it
                                            context.prefs("systemui\\statusbar_icon").edit {
                                                putInt(
                                                    "icon_show_Wifi_icon",
                                                    it
                                                )
                                            }
                                        })
                                }
                                SuperDropdown(title = stringResource(R.string.wifi_arrow),
                                    items = showlist,
                                    selectedIndex = show_Wifi_arrow.intValue,
                                    onSelectedIndexChange = {
                                        show_Wifi_arrow.intValue = it
                                        context.prefs("systemui\\statusbar_icon").edit {
                                            putInt(
                                                "show_Wifi_arrow",
                                                it
                                            )
                                        }
                                    })
                            }
                        }
                    }
                }
            }
        }
    }
    resetApp.AppRestartScreen(appList, RestartAPP)
}
