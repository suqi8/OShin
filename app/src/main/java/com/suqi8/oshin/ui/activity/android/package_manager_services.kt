package com.suqi8.oshin.ui.activity.android

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.extra.SuperSwitch
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog

@Composable
fun package_manager_services(navController: NavController) {
    val downgr = remember { mutableStateOf(false) }
    val authcreak = remember { mutableStateOf(false) }
    val digestCreak = remember { mutableStateOf(false) }
    val usePreSig = remember { mutableStateOf(false) }
    val enhancedMode = remember { mutableStateOf(false) }
    val bypassBlock = remember { mutableStateOf(false) }
    val disable_verification_agent = remember { mutableStateOf(false) }
    val shared_user = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val topappbarzt = MiuixScrollBehavior(top.yukonga.miuix.kmp.basic.rememberTopAppBarState())
    val appList = listOf("android")
    val restartAPP = remember { mutableStateOf(false) }
    val resetApp = resetApp()
    val showUsePreSigDialog = remember { mutableStateOf(false) }
    resetApp.AppRestartScreen(appList,restartAPP)
    LaunchedEffect(Unit) {
        downgr.value = context.prefs("corepatch").getBoolean("downgrade", false)
        authcreak.value = context.prefs("corepatch").getBoolean("authcreak", false)
        digestCreak.value = context.prefs("corepatch").getBoolean("digestCreak", false)
        usePreSig.value = context.prefs("corepatch").getBoolean("UsePreSig", false)
        enhancedMode.value = context.prefs("corepatch").getBoolean("enhancedMode", false)
        bypassBlock.value = context.prefs("corepatch").getBoolean("bypassBlock", false)
        disable_verification_agent.value = context.prefs("corepatch").getBoolean("disableVerificationAgent", false)
        shared_user.value = context.prefs("corepatch").getBoolean("sharedUser", false)
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

    Scaffold(topBar = {
        TopAppBar(
            scrollBehavior = topappbarzt,
            color = Color.Transparent,
            modifier = Modifier.hazeChild(
                state = hazeState,
                style = hazeStyle),
            title = stringResource(id = R.string.package_manager_services),
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
                    SmallTitle(
                        text = stringResource(R.string.common_settings)
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 6.dp)
                    ) {
                        SuperSwitch(
                            title = stringResource(R.string.downgr),
                            summary = stringResource(R.string.downgr_summary),
                            onCheckedChange = {
                                downgr.value = it
                                context.prefs("corepatch").edit { putBoolean("downgrade", it) }
                            },
                            checked = downgr.value
                        )
                        addline()
                        SuperSwitch(
                            title = stringResource(R.string.authcreak),
                            summary = stringResource(R.string.authcreak_summary),
                            onCheckedChange = {
                                authcreak.value = it
                                context.prefs("corepatch").edit { putBoolean("authcreak", it) }
                            },
                            checked = authcreak.value
                        )
                        addline()
                        SuperSwitch(
                            title = stringResource(R.string.digestCreak),
                            summary = stringResource(R.string.digestCreak_summary),
                            onCheckedChange = {
                                digestCreak.value = it
                                context.prefs("corepatch").edit { putBoolean("digestCreak", it) }
                            },
                            checked = digestCreak.value
                        )
                        addline()
                        SuperSwitch(
                            title = stringResource(R.string.UsePreSig),
                            summary = stringResource(R.string.UsePreSig_summary),
                            onCheckedChange = {
                                showUsePreSigDialog.value = it
                                usePreSig.value = it
                                context.prefs("corepatch").edit { putBoolean("UsePreSig", it) }
                            },
                            checked = usePreSig.value
                        )
                        addline()
                        SuperSwitch(
                            title = stringResource(R.string.enhancedMode),
                            summary = stringResource(R.string.enhancedMode_summary),
                            onCheckedChange = {
                                enhancedMode.value = it
                                context.prefs("corepatch").edit { putBoolean("enhancedMode", it) }
                            },
                            checked = enhancedMode.value
                        )
                    }
                    SmallTitle(
                        text = stringResource(R.string.other_settings)
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 6.dp)
                    ) {
                        SuperSwitch(
                            title = stringResource(R.string.bypassBlock),
                            summary = stringResource(R.string.bypassBlock_summary),
                            onCheckedChange = {
                                bypassBlock.value = it
                                context.prefs("corepatch").edit { putBoolean("bypassBlock", it) }
                            },
                            checked = bypassBlock.value
                        )
                        addline()
                        SuperSwitch(
                            title = stringResource(R.string.shared_user_title),
                            summary = stringResource(R.string.shared_user_summary),
                            onCheckedChange = {
                                shared_user.value = it
                                context.prefs("corepatch").edit { putBoolean("sharedUser", it) }
                            },
                            checked = shared_user.value
                        )
                        addline()
                        SuperSwitch(
                            title = stringResource(R.string.disable_verification_agent_title),
                            summary = stringResource(R.string.disable_verification_agent_summary),
                            onCheckedChange = {
                                disable_verification_agent.value = it
                                context.prefs("corepatch").edit { putBoolean("disableVerificationAgent", it) }
                            },
                            checked = disable_verification_agent.value
                        )
                    }
                    Spacer(
                        Modifier.height(
                            WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                        )
                    )
                }
            }
        }
    }
    UsePreSig(showUsePreSigDialog)
}

@Composable
fun UsePreSig(showDialog: MutableState<Boolean>) {
    if (!showDialog.value) return
    SuperDialog(title = stringResource(R.string.warn),
        titleColor = Color.Red,
        summary = stringResource(R.string.usepresig_warn),
        show = showDialog,
        onDismissRequest = {
            dismissDialog(showDialog)
        }) {
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.ok),
                enabled = true,
                onClick = {
                    dismissDialog(showDialog)
                }
            )
        }
    }
}
