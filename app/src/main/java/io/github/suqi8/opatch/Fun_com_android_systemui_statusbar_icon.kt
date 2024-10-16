package io.github.suqi8.opatch

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.Gravity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.highcapable.yukihookapi.hook.factory.prefs
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import io.github.suqi8.opatch.application.RestartApp
import io.github.suqi8.opatch.tools.AnimTools
import io.github.suqi8.opatch.ui.tools.resetApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.extra.SuperDropdown
import top.yukonga.miuix.kmp.extra.SuperSwitch
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.showDialog
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.Properties

@SuppressLint("RtlHardcoded")
@Composable
fun Fun_com_android_systemui_statusbar_icon(navController: NavController) {
    val context = LocalContext.current
    val topappbarzt = MiuixScrollBehavior(rememberTopAppBarState())
    val RestartAPP = remember { mutableStateOf(false) }
    val resetApp = resetApp()
    val appList = listOf("com.android.systemui")
    val com_android_systemui_statusbar_icon = remember { mutableStateOf(false) }
    val showlist = listOf(stringResource(R.string.default_),stringResource(R.string.hide))
    val show_Wifi_icon = remember { mutableStateOf(0) }
    val show_Wifi_arrow = remember { mutableStateOf(0) }

    val alpha = context.prefs("settings").getFloat("AppAlpha", 0.75f)
    val blurRadius: Dp = context.prefs("settings").getInt("AppblurRadius", 25).dp
    val noiseFactor = context.prefs("settings").getFloat("AppnoiseFactor", 0f)
    val containerColor: Color = MiuixTheme.colorScheme.background
    val hazeState = remember { HazeState() }
    val hazeStyle = remember(containerColor, alpha, blurRadius, noiseFactor) {
        HazeStyle(
            backgroundColor = containerColor,
            tint = HazeTint.Color(containerColor.copy(alpha)),
            blurRadius = blurRadius,
            noiseFactor = noiseFactor
        )
    }

    LaunchedEffect(Unit) {
        com_android_systemui_statusbar_icon.value = context.prefs("settings").getBoolean("com_android_systemui_statusbar_icon", false)
        show_Wifi_icon.value = context.prefs("settings").getInt("com_android_systemui_statusbar_icon_show_Wifi_icon", 0)
        show_Wifi_arrow.value = context.prefs("settings").getInt("com_android_systemui_statusbar_icon_show_Wifi_arrow", 0)
    }
    Scaffold(topBar = {
        TopAppBar(
            scrollBehavior = topappbarzt,
            title = stringResource(id = R.string.status_bar_icon),
            color = Color.Transparent,
            modifier = Modifier.hazeChild(
                state = hazeState,
                style = hazeStyle),
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                },
                    modifier = Modifier.padding(start = 18.dp)) {
                    Icon(
                        imageVector = MiuixIcons.ArrowBack,
                        contentDescription = null,
                        tint = MiuixTheme.colorScheme.onBackground
                    )
                }
            },
            actions = {
                // 如果你有其他操作按钮，这里可以添加
                IconButton(onClick = {
                    RestartAPP.value = true
                },
                    modifier = Modifier.padding(end = 18.dp)) {
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
            topAppBarScrollBehavior = topappbarzt
        ) {
            item {
                Column {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 6.dp, top = 15.dp)
                    ) {
                        SuperSwitch(
                            title = stringResource(R.string.status_bar_icon),
                            onCheckedChange = {
                                com_android_systemui_statusbar_icon.value = it
                                context.prefs("settings").edit { putBoolean("com_android_systemui_statusbar_icon", it) }
                            },
                            checked = com_android_systemui_statusbar_icon.value
                        )
                    }
                    AnimatedVisibility(
                        visible = !com_android_systemui_statusbar_icon.value
                    ) {
                        Column {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                color = Color.Red.copy(alpha = 0.1f)
                            ) {
                                BasicComponent(
                                    title = stringResource(R.string.no_start_func),
                                    titleColor = Color.Red
                                )
                            }
                        }
                    }
                    AnimatedVisibility(
                        visible = com_android_systemui_statusbar_icon.value
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
                                        selectedIndex = show_Wifi_icon.value,
                                        onSelectedIndexChange = {
                                            show_Wifi_icon.value = it
                                            context.prefs("settings").edit { putInt("com_android_systemui_statusbar_icon_show_Wifi_icon", it) }
                                        })
                                }
                                SuperDropdown(title = stringResource(R.string.wifi_arrow),
                                    items = showlist,
                                    selectedIndex = show_Wifi_arrow.value,
                                    onSelectedIndexChange = {
                                        show_Wifi_arrow.value = it
                                        context.prefs("settings").edit { putInt("com_android_systemui_statusbar_icon_show_Wifi_arrow", it) }
                                    })
                            }
                        }
                    }
                }
            }
        }
    }
    resetApp.AppRestartScreen(appList,RestartAPP)
}
