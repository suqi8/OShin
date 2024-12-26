package io.github.suqi8.opatch

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.highcapable.yukihookapi.hook.factory.prefs
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import io.github.suqi8.opatch.ui.activity.com.android.systemui.SettingFloatDialog
import io.github.suqi8.opatch.ui.tools.resetApp
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack
import top.yukonga.miuix.kmp.theme.MiuixTheme

@SuppressLint("SuspiciousIndentation")
@Composable
fun Fun_com_android_launcher(navController: NavController) {
    val context = LocalContext.current
    val one = MiuixScrollBehavior(top.yukonga.miuix.kmp.basic.rememberTopAppBarState())
    val appList = listOf("com.android.launcher")
    val restartAPP = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val resetApp = resetApp()
    val isDebug = context.prefs("settings").getBoolean("Debug", false)
    val showIconTextDialog = remember { mutableStateOf(false) }
    val iconTextTitle = stringResource(R.string.desktop_icon_and_text_size_multiplier)
    val iconText = remember { mutableFloatStateOf(1.0f) }

    LaunchedEffect(Unit) {
        iconText.floatValue = context.prefs("settings").getFloat("com_android_launcher_icon_text", 1.00f)
    }

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

    Scaffold(topBar = { GetAppIconAndName(packageName = "com.android.launcher") { appName, icon ->
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
            topAppBarScrollBehavior = one, modifier = Modifier.fillMaxSize().haze(state = hazeState)) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp, top = 15.dp)
                ) {
                    Column {
                        SuperArrow(
                            title = stringResource(R.string.desktop_icon_and_text_size_multiplier),
                            summary = stringResource(R.string.icon_size_limit_note),
                            onClick = {
                                showIconTextDialog.value = true
                            },
                            rightText = "${iconText.floatValue}x"
                        )
                        Slider(
                            progress = ((iconText.floatValue / 2.00).toFloat()),
                            onProgressChange = { newProgress ->
                                iconText.floatValue = (newProgress * 2.00).toFloat()
                                context.prefs("settings").edit { putFloat("com_android_launcher_icon_text", (((newProgress * 2.00).toFloat()))) }
                            },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                        )
                    }
                }
            }
        }
    }
    SettingFloatDialog(context,showIconTextDialog,iconTextTitle,iconText,focusManager,"com_android_launcher_icon_text")
    resetApp.AppRestartScreen(appList,restartAPP)
}
