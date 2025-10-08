package com.suqi8.oshin.ui.activity.about

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.components.Card
import com.suqi8.oshin.ui.activity.components.FunPage
import com.suqi8.oshin.ui.activity.components.addline
import com.suqi8.oshin.ui.main.LocalColorMode
import top.yukonga.miuix.kmp.extra.SuperDropdown
import top.yukonga.miuix.kmp.extra.SuperSwitch
import java.util.Locale

@SuppressLint("LocalContextConfigurationRead")
@Composable
fun about_setting(
    navController: NavController
) {
    val context = LocalContext.current
    val colorModeState = LocalColorMode.current
    val colorMode = colorModeState.value
    FunPage(
        title = stringResource(id = R.string.settings),
        navController = navController
    ) {
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
            /*funSwich(
                title = "Debug",
                category = "settings",
                key = "Debug"
            )
            addline()
            funSwich(
                title = stringResource(R.string.addline),
                category = "settings",
                key = "addline"
            )
            addline()*/
            val componentName = ComponentName(context, "com.suqi8.oshin.Home")
            val pm = context.packageManager
            val ishide = remember {
                mutableStateOf(
                    try {
                        val state = pm.getComponentEnabledSetting(componentName)
                        state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                    } catch (e: PackageManager.NameNotFoundException) {
                        false
                    }
                )
            }
            SuperSwitch(title = stringResource(R.string.hide_launcher_icon),
                checked = !ishide.value,
                onCheckedChange = {
                    ishide.value = !ishide.value
                    context.packageManager.setComponentEnabledSetting(
                        componentName,
                        if (ishide.value)
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                        else
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP
                    )
                })
            /*addline()
            FunSwich(
                title = stringResource(R.string.enable_blur),
                category = "settings",
                key = "enable_blur",
                defValue = true
            )*/
        }
    }
}
