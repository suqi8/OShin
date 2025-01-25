package com.suqi8.oshin.ui.activity.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.R
import com.suqi8.oshin.saveColorMode
import com.suqi8.oshin.ui.activity.funlistui.FunPage
import com.suqi8.oshin.ui.activity.funlistui.FunSwich
import com.suqi8.oshin.ui.activity.funlistui.addline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperDropdown

@Composable
fun about_setting(
    navController: NavController, alpha: MutableState<Float>,
    blur: MutableState<Dp>,
    noise: MutableState<Float>, colorMode: MutableState<Int>,
) {
    val context = LocalContext.current
    val showAlphaDialog = remember { mutableStateOf(false) }
    val showBlurDialog = remember { mutableStateOf(false) }
    val showNoiseDialog = remember { mutableStateOf(false) }
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
                progress = progress.progress,
                modifier = Modifier.padding(1.dp)
            )
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 6.dp, top = 6.dp)
        ) {
            SuperDropdown(
                title = stringResource(R.string.Color_Mode),
                items = listOf(
                    stringResource(R.string.Auto_Mode),
                    stringResource(R.string.Light_Mode),
                    stringResource(
                        R.string.Night_Mode
                    )
                ),
                selectedIndex = colorMode.value,
                onSelectedIndexChange = {
                    colorMode.value = it
                    CoroutineScope(Dispatchers.IO).launch {
                        saveColorMode(context, it)
                    }
                }
            )
            addline()
            FunSwich(
                title = "Debug",
                category = "settings",
                key = "Debug"
            )
            addline()
            FunSwich(
                title = stringResource(R.string.addline),
                category = "settings",
                key = "addline"
            )
            addline()
            Column {
                SuperArrow(
                    title = stringResource(R.string.alpha_setting),
                    onClick = {
                        showAlphaDialog.value = true
                    },
                    rightText = "${alpha.value}f"
                )
                Slider(
                    progress = alpha.value,
                    onProgressChange = { newProgress ->
                        alpha.value = newProgress
                        context.prefs("settings").edit { putFloat("AppAlpha", newProgress) }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                )
            }
            addline()
            Column {
                SuperArrow(
                    title = stringResource(R.string.blur_radius_setting),
                    onClick = {
                        showBlurDialog.value = true
                    },
                    rightText = "${blur.value}"
                )
                Slider(
                    progress = (blur.value.value / 100f),
                    onProgressChange = { newProgress ->
                        blur.value =
                            (newProgress * 100).dp
                        context.prefs("settings")
                            .edit { putInt("AppblurRadius", ((newProgress * 100).toInt())) }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                )
            }
            addline()
            Column {
                SuperArrow(
                    title = stringResource(R.string.noise_factor_setting),
                    onClick = {
                        showNoiseDialog.value = true
                    },
                    rightText = "${noise.value}f"
                )
                Slider(
                    progress = noise.value,
                    onProgressChange = { newProgress ->
                        noise.value = newProgress
                        context.prefs("settings")
                            .edit { putFloat("AppnoiseFactor", newProgress) }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                )
            }
            /*addline()
            SuperSwitch(title = stringResource(R.string.feature_auto_color_picking_enabled),
                summary = stringResource(R.string.feature_auto_color_picking_warning),
                checked = auto_color.value,
                onCheckedChange = {
                    auto_color.value = it
                    context.prefs("settings").edit { putBoolean("auto_color", it) }
                })*/
        }
    }
}
