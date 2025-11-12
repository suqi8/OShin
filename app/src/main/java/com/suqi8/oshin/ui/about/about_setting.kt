package com.suqi8.oshin.ui.about

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.components.Card
import com.suqi8.oshin.ui.activity.components.FunPage
import com.suqi8.oshin.ui.activity.components.FunSwitch
import com.suqi8.oshin.ui.activity.components.SuperDropdown
import com.suqi8.oshin.ui.activity.components.SuperSwitch
import com.suqi8.oshin.ui.activity.components.addline
import com.suqi8.oshin.ui.home.ModernSectionTitle
import com.suqi8.oshin.ui.main.LocalColorMode
import com.suqi8.oshin.ui.nav.path.NavPath
import com.suqi8.oshin.ui.nav.ui.NavStackScope
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic
import java.util.Locale

@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("LocalContextConfigurationRead")
@Composable
fun about_setting(
    navPath: NavPath,
    navStackScope: NavStackScope,
) {
    val context = LocalContext.current
    val colorModeState = LocalColorMode.current
    val colorMode = colorModeState.value

    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())

    FunPage(
        navPath = navPath,
        navStackScope = navStackScope,
        scrollBehavior = scrollBehavior,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .overScrollVertical()
                .scrollEndHaptic()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = padding
        ) {
            item {
                ModernSectionTitle(
                    title = stringResource(id = R.string.settings),
                    modifier = Modifier
                        .displayCutoutPadding()
                        .padding(top = padding.calculateTopPadding() + 72.dp, bottom = 8.dp)
                )
            }
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp)
                        .padding(top = 15.dp)
                ) {
                    val compositionResult =
                        rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.setting))
                    val progress =
                        animateLottieCompositionAsState(
                            composition = compositionResult.value,
                            iterations = LottieConstants.IterateForever
                        )
                    LottieAnimation(
                        composition = compositionResult.value,
                        progress = { progress.progress },
                        modifier = Modifier.padding(1.dp)
                    )
                }
            }
            item {
                Card {
                    SuperDropdown(
                        title = stringResource(R.string.Color_Mode),
                        items = listOf(
                            stringResource(R.string.Auto_Mode),
                            stringResource(R.string.Light_Mode),
                            stringResource(
                                R.string.Night_Mode
                            )
                        ),
                        selectedIndex = colorMode,
                        onSelectedIndexChange = {
                            colorModeState.value = it
                            context.prefs("settings").edit { putInt("color_mode", it) }
                        }
                    )
                    addline()
                    val context = LocalContext.current
                    // 1. 确保你的 R.array.language 数组里已经加上了“梗体中文”
                    //    假设它在第6个位置，也就是索引为 5
                    val languageArray = stringArrayResource(id = R.array.language).toList()
                    val selectedLanguageIndex = remember { mutableStateOf(context.prefs("settings").getInt("app_language", 0)) }
                    val recompose = currentRecomposeScope

                    // 2. 切换语言逻辑
                    fun changeLanguage(index: Int) {
                        // ✨ 在这里添加我们的自定义语言
                        val newLocale = when (index) {
                            1 -> Locale.SIMPLIFIED_CHINESE
                            2 -> Locale.ENGLISH
                            3 -> Locale.JAPANESE
                            4 -> Locale.Builder().setLanguage("ru").build()
                            5 -> Locale.Builder().setLanguage("qaa").setExtension('x', "meme").build()
                            6 -> Locale.KOREAN
                            else -> Locale.getDefault() // 跟随系统
                        }

                        val resources = context.resources
                        val config = Configuration(resources.configuration)
                        config.setLocale(newLocale)

                        // 更新应用配置
                        resources.updateConfiguration(config, resources.displayMetrics)

                        // 强制重组以刷新UI
                        recompose.invalidate()
                    }

                    SuperDropdown(
                        title = stringResource(R.string.app_language),
                        items = languageArray,
                        selectedIndex = selectedLanguageIndex.value,
                        onSelectedIndexChange = { index ->
                            selectedLanguageIndex.value = index
                            context.prefs("settings").edit { putInt("app_language", index) }
                            changeLanguage(index)
                        }
                    )
                    addline()
                    val updateChannelIndex = remember { mutableStateOf(context.prefs("settings").getInt("app_update_channel", 0)) }
                    SuperDropdown(
                        title = stringResource(R.string.update_channel),
                        items = listOf(
                            stringResource(R.string.update_page_tab_release), // "Release"
                            stringResource(R.string.update_page_tab_ci)      // "CI Build"
                        ),
                        selectedIndex = updateChannelIndex.value,
                        onSelectedIndexChange = { index ->
                            updateChannelIndex.value = index
                            context.prefs("settings").edit { putInt("app_update_channel", index) }
                        }
                    )
                    addline()
                    FunSwitch(
                        title = "Debug",
                        category = "settings",
                        key = "Debug"
                    )
                    addline()
                    FunSwitch(
                        title = stringResource(R.string.addline),
                        category = "settings",
                        key = "addline",
                        defValue = true
                    )
                    addline()
                    val componentName = ComponentName(context, "com.suqi8.oshin.Home")
                    val pm = context.packageManager
                    val isIconVisible = remember {
                        mutableStateOf(
                            try {
                                pm.getComponentEnabledSetting(componentName) != PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                            } catch (e: Exception) {
                                true
                            }
                        )
                    }
                    SuperSwitch(title = stringResource(R.string.hide_launcher_icon),
                        checked = !isIconVisible.value,
                        onCheckedChange = { hide ->
                            isIconVisible.value = !hide
                            pm.setComponentEnabledSetting(
                                componentName,
                                if (hide)
                                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                                else
                                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                                PackageManager.DONT_KILL_APP
                            )
                        })
                    addline()
                    FunSwitch(
                        title = stringResource(R.string.disable_bottom_bar_glass),
                        category = "settings",
                        key = "bottomtab"
                    )
                }
            }
        }
    }
}
