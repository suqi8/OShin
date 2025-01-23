package com.suqi8.oshin.ui.activity.com.android.systemui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
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
import com.suqi8.oshin.GetAppIconAndName
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.funlistui.FunSwich
import com.suqi8.oshin.ui.activity.funlistui.addline
import com.suqi8.oshin.ui.tools.resetApp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack
import top.yukonga.miuix.kmp.theme.MiuixTheme

@SuppressLint("SuspiciousIndentation")
@Composable
fun systemui(navController: NavController) {
    val context = LocalContext.current
    val one = MiuixScrollBehavior(top.yukonga.miuix.kmp.basic.rememberTopAppBarState())
    val appList = listOf("com.android.systemui")
    val restartAPP = remember { mutableStateOf(false) }
    val resetApp = resetApp()
    val isDebug = context.prefs("settings").getBoolean("Debug", false)

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

    Scaffold(topBar = { GetAppIconAndName(packageName = "com.android.systemui") { appName, icon ->
        TopAppBar(
            color = Color.Transparent,
            modifier = Modifier.hazeChild(
                state = hazeState,
                style = hazeStyle),
            title = appName,
            scrollBehavior = one,
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
            }, actions = {
                IconButton(onClick = {
                    restartAPP.value = true
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
    } }) {padding ->
        LazyColumn(contentPadding = PaddingValues(top = padding.calculateTopPadding()),
            topAppBarScrollBehavior = one, modifier = Modifier
                .fillMaxSize()
                .haze(state = hazeState)) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp, top = 15.dp)
                ) {
                    SuperArrow(title = stringResource(id = R.string.status_bar_clock),
                        onClick = {
                            navController.navigate("systemui\\status_bar_clock")
                        })
                    addline()
                    SuperArrow(title = stringResource(id = R.string.hardware_indicator),
                        onClick = {
                            navController.navigate("systemui\\hardware_indicator")
                        })
                    addline()
                    SuperArrow(title = stringResource(id = R.string.status_bar_icon),
                        onClick = {
                            navController.navigate("systemui\\statusbar_icon")
                        })
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp, top = 15.dp)
                ) {
                    FunSwich(
                        title = stringResource(R.string.hide_status_bar),
                        category = "systemui",
                        key = "hide_status_bar",
                        defValue = false
                    )
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp, top = 15.dp)
                ) {
                    val enable_all_day_screen_off = remember { mutableStateOf(context.prefs("systemui").getBoolean("enable_all_day_screen_off", false)) }
                    FunSwich(
                        title = stringResource(R.string.enable_all_day_screen_off),
                        category = "systemui",
                        key = "enable_all_day_screen_off",
                        defValue = false,
                        onCheckedChange = {
                            enable_all_day_screen_off.value = it
                        }
                    )
                    AnimatedVisibility(enable_all_day_screen_off.value) {
                        addline()
                        FunSwich(
                            title = stringResource(R.string.force_trigger_ltpo),
                            category = "systemui",
                            key = "force_trigger_ltpo",
                            defValue = true
                        )
                    }
                }
            }
        }
    }
    resetApp.AppRestartScreen(appList,restartAPP)
}
