package io.github.suqi8.opatch.ui.activity.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.highcapable.yukihookapi.hook.factory.prefs
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import io.github.suqi8.opatch.R
import io.github.suqi8.opatch.addline
import io.github.suqi8.opatch.saveColorMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperDropdown
import top.yukonga.miuix.kmp.extra.SuperSwitch
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun about_setting(
    navController: NavController, alpha: MutableState<Float>,
    blur: MutableState<Dp>,
    noise: MutableState<Float>, colorMode: MutableState<Int>,
) {
    val isDebug = remember { mutableStateOf(false) }
    val auto_color = remember { mutableStateOf(true) }
    val addline = remember { mutableStateOf(false) }
    val showAlphaDialog = remember { mutableStateOf(false) }
    val showBlurDialog = remember { mutableStateOf(false) }
    val showNoiseDialog = remember { mutableStateOf(false) }

    val context = LocalContext.current
    val one = MiuixScrollBehavior(top.yukonga.miuix.kmp.basic.rememberTopAppBarState())
    val containerColor: Color = MiuixTheme.colorScheme.background
    val hazeState = remember { HazeState() }
    val hazeStyle = remember(containerColor, alpha.value, blur.value, noise.value) {
        HazeStyle(
            backgroundColor = containerColor,
            tint = HazeTint(containerColor.copy(alpha.value)),
            blurRadius = blur.value,
            noiseFactor = noise.value
        )
    }
    LaunchedEffect(Unit) {
        auto_color.value = context.prefs("settings").getBoolean("auto_color", false)
        isDebug.value = context.prefs("settings").getBoolean("Debug", false)
        addline.value = context.prefs("settings").getBoolean("addline", false)
    }

    Scaffold(topBar = {
        TopAppBar(
            title = stringResource(R.string.settings),
            color = Color.Transparent,
            modifier = Modifier.hazeChild(
                state = hazeState,
                style = hazeStyle
            ),
            scrollBehavior = one,
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
            }
        )
    }) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(top = padding.calculateTopPadding()),
            topAppBarScrollBehavior = one, modifier = Modifier
                .fillMaxSize()
                .haze(state = hazeState)
        ) {
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
                        animateLottieCompositionAsState(composition = compositionResult.value,
                                iterations = LottieConstants.IterateForever)
                    LottieAnimation(
                        composition = compositionResult.value,
                        progress = progress.value,
                        modifier = Modifier
                    )
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp)
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
                    SuperSwitch(
                        title = "Debug",
                        checked = isDebug.value,
                        onCheckedChange = {
                            isDebug.value = it
                            context.prefs("settings").edit { putBoolean("Debug", it) }
                        }
                    )
                    addline()
                    SuperSwitch(title = stringResource(R.string.addline),
                        checked = addline.value,
                        onCheckedChange = {
                            addline.value = it
                            context.prefs("settings").edit { putBoolean("addline", it) }
                        })
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
                    addline()
                    SuperSwitch(title = stringResource(R.string.feature_auto_color_picking_enabled),
                        summary = stringResource(R.string.feature_auto_color_picking_warning),
                        checked = auto_color.value,
                        onCheckedChange = {
                            auto_color.value = it
                            context.prefs("settings").edit { putBoolean("auto_color", it) }
                        })
                }
            }
        }
    }
}
