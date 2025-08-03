package com.suqi8.oshin

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.highcapable.yukihookapi.hook.factory.prefs
import com.kyant.liquidglass.GlassBorder
import com.kyant.liquidglass.GlassMaterial
import com.kyant.liquidglass.InnerRefraction
import com.kyant.liquidglass.LiquidGlassStyle
import com.kyant.liquidglass.LocalLiquidGlassProviderState
import com.kyant.liquidglass.liquidGlass
import com.kyant.liquidglass.liquidGlassProvider
import com.kyant.liquidglass.rememberLiquidGlassProviderState
import com.suqi8.oshin.ui.activity.about.Main_About
import com.suqi8.oshin.ui.activity.about.about_contributors
import com.suqi8.oshin.ui.activity.about.about_group
import com.suqi8.oshin.ui.activity.about.about_references
import com.suqi8.oshin.ui.activity.about.about_setting
import com.suqi8.oshin.ui.activity.android.android
import com.suqi8.oshin.ui.activity.android.oplus_services
import com.suqi8.oshin.ui.activity.android.package_manager_services
import com.suqi8.oshin.ui.activity.android.split_screen_multi_window
import com.suqi8.oshin.ui.activity.com.android.incallui.incallui
import com.suqi8.oshin.ui.activity.com.android.launcher.launcher
import com.suqi8.oshin.ui.activity.com.android.launcher.recent_task
import com.suqi8.oshin.ui.activity.com.android.mms.mms
import com.suqi8.oshin.ui.activity.com.android.phone.phone
import com.suqi8.oshin.ui.activity.com.android.settings.feature
import com.suqi8.oshin.ui.activity.com.android.settings.settings
import com.suqi8.oshin.ui.activity.com.android.systemui.controlCenter
import com.suqi8.oshin.ui.activity.com.android.systemui.hardware_indicator
import com.suqi8.oshin.ui.activity.com.android.systemui.notification
import com.suqi8.oshin.ui.activity.com.android.systemui.status_bar_clock
import com.suqi8.oshin.ui.activity.com.android.systemui.status_bar_wifi
import com.suqi8.oshin.ui.activity.com.android.systemui.statusbar_icon
import com.suqi8.oshin.ui.activity.com.android.systemui.systemui
import com.suqi8.oshin.ui.activity.com.coloros.ocrscanner.ocrscanner
import com.suqi8.oshin.ui.activity.com.coloros.oshare.oshare
import com.suqi8.oshin.ui.activity.com.coloros.phonemanager.phonemanager
import com.suqi8.oshin.ui.activity.com.coloros.securepay.securepay
import com.suqi8.oshin.ui.activity.com.finshell.wallet.wallet
import com.suqi8.oshin.ui.activity.com.heytap.health.health
import com.suqi8.oshin.ui.activity.com.heytap.quicksearchbox.quicksearchbox
import com.suqi8.oshin.ui.activity.com.heytap.speechassist.speechassist
import com.suqi8.oshin.ui.activity.com.mi.health.mihealth
import com.suqi8.oshin.ui.activity.com.oplus.appdetail.appdetail
import com.suqi8.oshin.ui.activity.com.oplus.battery.battery
import com.suqi8.oshin.ui.activity.com.oplus.exsystemservice.exsystemservice
import com.suqi8.oshin.ui.activity.com.oplus.games.games
import com.suqi8.oshin.ui.activity.com.oplus.notificationmanager.notificationmanager
import com.suqi8.oshin.ui.activity.com.oplus.ota.ota
import com.suqi8.oshin.ui.activity.com.oplus.phonemanager.oplusphonemanager
import com.suqi8.oshin.ui.activity.func.cpu_freq
import com.suqi8.oshin.ui.activity.func.romworkshop.Rom_workshop
import com.suqi8.oshin.ui.activity.hide_apps_notice
import com.suqi8.oshin.ui.activity.recent_update
import com.suqi8.oshin.ui.theme.AppTheme
import com.suqi8.oshin.utils.SpringEasing
import com.suqi8.oshin.utils.drawColoredShadow
import com.suqi8.oshin.utils.executeCommand
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import dev.chrisbanes.haze.ExperimentalHazeApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.NavigationItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.getWindowSize
import java.util.Locale
import kotlin.system.exitProcess

const val TAG = "OShin"
class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        val languageCode = newBase.prefs("settings").getInt("app_language", 0)

        val localeToSet = when (languageCode) {
            1 -> Locale.SIMPLIFIED_CHINESE
            2 -> Locale.ENGLISH
            3 -> Locale.JAPANESE
            4 -> Locale.forLanguageTag("ru")
            5 -> Locale.Builder().setLanguage("qaa").setExtension('x', "meme").build()
            else -> Locale.getDefault() // 跟随系统
        }

        val config = newBase.resources.configuration
        config.setLocale(localeToSet)
        val localizedContext = newBase.createConfigurationContext(config)
        super.attachBaseContext(localizedContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        if (!UMConfigure.isInit) {
            UMConfigure.preInit(this, "67c7dea68f232a05f127781e", "android")
        }

        setContent {
            val context = LocalContext.current
            val colorMode = remember {
                mutableIntStateOf(
                    context.prefs("settings").getInt("color_mode", 0) // 0: 跟随系统
                )
            }

            val darkMode = colorMode.intValue == 2 || (colorMode.intValue == 0 && isSystemInDarkTheme())

            DisposableEffect(darkMode) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT
                    ) { darkMode },
                    navigationBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT
                    ) { darkMode },
                )
                onDispose {}
            }

            window.isNavigationBarContrastEnforced = false

            AppTheme(colorMode = colorMode.intValue) {
                CompositionLocalProvider(LocalColorMode provides colorMode) {
                    Main0()
                }
            }
        }
    }
}

@Composable
fun Main0() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val windowWidth = getWindowSize().width

    val easing = SpringEasing.gentle()
    val duration = easing.durationMillis.toInt()

    val lspVersion = remember { mutableStateOf("") }
    val isPrivacyEnabled = remember { mutableStateOf(context.prefs("settings").getBoolean("privacy", true)) }

    LaunchedEffect(isPrivacyEnabled.value) {
        if (!isPrivacyEnabled.value) {
            UMConfigure.init(context, "67c7dea68f232a05f127781e", "android", UMConfigure.DEVICE_TYPE_PHONE, "")
            withContext(Dispatchers.IO) {
                val lsposedVersionName = executeCommand("awk -F= '/version=/ {print \$2}' /data/adb/modules/zygisk_lsposed/module.prop")
                lspVersion.value = lsposedVersionName
                val savedLspVersion = context.prefs("settings").getString("privacy_lspvername", "")
                if (lsposedVersionName.isNotEmpty() && lsposedVersionName != savedLspVersion) {
                    val eventData = mapOf("version_name" to lsposedVersionName)
                    MobclickAgent.onEvent(context, "lsposed_usage", eventData)
                    context.prefs("settings").edit {
                        putString("privacy_lspvername", lsposedVersionName)
                    }
                }
            }
        }
    }

    if (isPrivacyEnabled.value) {
        SuperDialog(
            show = isPrivacyEnabled,
            title = stringResource(R.string.privacy_title),
            onDismissRequest = {}
        ) {
            Text(stringResource(R.string.privacy_content))
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.exit),
                    onClick = {
                        exitProcess(0)
                    }
                )
                Spacer(Modifier.width(12.dp))
                TextButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.ok),
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                    onClick = {
                        isPrivacyEnabled.value = false
                        context.prefs("settings").edit { putBoolean("privacy", false) }
                    }
                )
            }
        }
    }


    Column {
        NavHost(
            navController = navController,
            startDestination = "Main",
            enterTransition = { slideInHorizontally(initialOffsetX = { windowWidth }, animationSpec = tween(duration, 0, easing = easing)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -windowWidth / 5 }, animationSpec = tween(duration, 0, easing = easing)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -windowWidth / 5 }, animationSpec = tween(duration, 0, easing = easing)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { windowWidth }, animationSpec = tween(duration, 0, easing = easing)) },
            sizeTransform = {
                SizeTransform(clip = false)
            }
        ) {
            composable("Main") { Main1(navController) }
            composable("recent_update") { recent_update(navController) }
            composable("android") { android(navController) }
            composable("android\\package_manager_services") { package_manager_services(navController = navController) }
            composable("android\\oplus_system_services") { oplus_services(navController = navController) }
            composable("android\\split_screen_multi_window") { split_screen_multi_window(navController = navController) }
            composable("systemui") { systemui(navController = navController) }
            composable("systemui\\status_bar_clock") { status_bar_clock(navController = navController) }
            composable("systemui\\hardware_indicator") { hardware_indicator(navController = navController) }
            composable("systemui\\statusbar_icon") { statusbar_icon(navController = navController) }
            composable("systemui\\notification") { notification(navController = navController) }
            composable("systemui\\status_bar_wifi") { status_bar_wifi(navController = navController) }
            composable("systemui\\controlCenter") { controlCenter(navController = navController) }
            composable("launcher") { launcher(navController = navController) }
            composable("launcher\\recent_task") { recent_task(navController = navController) }
            composable("about_setting") { about_setting(navController) }
            composable("about_group") { about_group(navController) }
            composable("about_references") { about_references(navController) }
            composable("about_contributors") { about_contributors(navController) }
            composable("settings") { settings(navController) }
            composable("settings\\feature") { feature(navController) }
            composable("battery") { battery(navController) }
            composable("speechassist") { speechassist(navController) }
            composable("ocrscanner") { ocrscanner(navController) }
            composable("games") { games(navController) }
            composable("wallet") { wallet(navController) }
            composable("phonemanager") { phonemanager(navController) }
            composable("oplusphonemanager") { oplusphonemanager(navController) }
            composable("mms") { mms(navController) }
            composable("securepay") { securepay(navController) }
            composable("health") { health(navController) }
            composable("appdetail") { appdetail(navController) }
            composable("func\\cpu_freq") { cpu_freq(navController) }
            composable("hide_apps_notice") { hide_apps_notice(navController) }
            composable("quicksearchbox") { quicksearchbox(navController) }
            composable("mihealth") { mihealth(navController) }
            composable("ota") { ota(navController) }
            composable("func\\romworkshop") { Rom_workshop(navController) }
            composable("oshare") { oshare(navController) }
            composable("incallui") { incallui(navController) }
            composable("notificationmanager") { notificationmanager(navController) }
            composable("exsystemservice") { exsystemservice(navController) }
            composable("phone") { phone(navController) }
        }
    }
}

@OptIn(FlowPreview::class, ExperimentalHazeApi::class, ExperimentalHazeApi::class,
    ExperimentalHazeApi::class
)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "InflateParams", "ResourceType")
@Composable
fun Main1(navController: NavController) {
    val topAppBarScrollBehavior0 = MiuixScrollBehavior(rememberTopAppBarState())
    val topAppBarScrollBehavior1 = MiuixScrollBehavior(rememberTopAppBarState())
    val topAppBarScrollBehavior2 = MiuixScrollBehavior(rememberTopAppBarState())
    val topAppBarScrollBehavior3 = MiuixScrollBehavior(rememberTopAppBarState())

    val topAppBarScrollBehaviorList = listOf(
        topAppBarScrollBehavior0, topAppBarScrollBehavior1, topAppBarScrollBehavior2, topAppBarScrollBehavior3
    )

    val pagerState = rememberPagerState(pageCount = { 4 },initialPage = 0)
    var targetPage by remember { mutableIntStateOf(pagerState.currentPage) }
    val coroutineScope = rememberCoroutineScope()
    val currentScrollBehavior = when (pagerState.currentPage) {
        0 -> topAppBarScrollBehaviorList[0]
        1 -> topAppBarScrollBehaviorList[1]
        2 -> topAppBarScrollBehaviorList[2]
        else -> topAppBarScrollBehaviorList[3]
    }

    val items = listOf(
        NavigationItem(stringResource(R.string.home), ImageVector.vectorResource(id = R.drawable.home)),
        NavigationItem(stringResource(R.string.module), ImageVector.vectorResource(id = R.drawable.module)),
        NavigationItem(stringResource(R.string.func), ImageVector.vectorResource(id = R.drawable.func)),
        NavigationItem(stringResource(R.string.about), ImageVector.vectorResource(id = R.drawable.about))
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.debounce(150).collectLatest {
            targetPage = pagerState.currentPage
        }
    }
    // 在 Composable 函数顶层创建 state
    val providerState = rememberLiquidGlassProviderState()
    val context = LocalContext.current

// 使用 CompositionLocalProvider 包裹 Scaffold
    CompositionLocalProvider(
        LocalLiquidGlassProviderState provides providerState
    ) {
        // 1. ✅ 提取可复用的玻璃材质，用于统一视觉风格
        val commonGlassMaterial = GlassMaterial(
            blurRadius = 0.3.dp,
            tint = MiuixTheme.colorScheme.background.copy(alpha = 0.1f),
            whitePoint = 0.1f,  // 高光
            chromaMultiplier = 1.2f // 增强透过玻璃看到的颜色饱和度
        )

        // 2. ✅ 为 TopAppBar 定义一个丰富的玻璃样式
        val topAppBarStyle = LiquidGlassStyle(
            shape = RoundedCornerShape(28.dp),
            material = commonGlassMaterial,
            innerRefraction = InnerRefraction.Default, // 添加默认的内部折射效果，产生凹凸感
            border = GlassBorder.Light(width = 1.dp)    // 添加一个细微的发光边框
        )

        // 3. ✅ 为 BottomBar 定义一个丰富的玻璃样式
        val bottomNavigationStyle = LiquidGlassStyle(
            shape = RoundedCornerShape(size = CardDefaults.CornerRadius),
            material = commonGlassMaterial,
            innerRefraction = InnerRefraction.Default,
            border = GlassBorder.Light(width = 1.dp)
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                AnimatedVisibility(pagerState.currentPage != 3) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(28.dp))) {
                        val enableBlur = context.prefs("settings").getBoolean("enable_blur", true)
                        TopAppBar(
                            scrollBehavior = currentScrollBehavior,
                            color = if (enableBlur) Color.Transparent else MiuixTheme.colorScheme.background,
                            title = when (pagerState.currentPage) {
                                0 -> stringResource(R.string.app_name)
                                1 -> stringResource(R.string.module)
                                2 -> stringResource(R.string.func)
                                else -> stringResource(R.string.about)
                            },
                            modifier = if (enableBlur) {
                                // 直接应用预设好的样式
                                Modifier.liquidGlass(style = topAppBarStyle)
                            } else Modifier
                        )
                    }
                }
            },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val enableBlur = context.prefs("settings").getBoolean("enable_blur", true)
                    Card(
                        // 4. ✅ 当玻璃效果启用时，不再需要额外的阴影
                        modifier = if (enableBlur) {
                            Modifier.padding(bottom = 10.dp)
                        } else {
                            Modifier
                                .padding(bottom = 10.dp)
                                .drawColoredShadow(
                                    MiuixTheme.colorScheme.onSurface,
                                    0.1f,
                                    borderRadius = CardDefaults.CornerRadius,
                                    shadowRadius = 10.dp
                                )
                        },
                        color = Color.Transparent // Card 必须透明才能透出内部的 NavigationBar 效果
                    ) {
                        NavigationBar(
                            items = items,
                            color = if (enableBlur) Color.Transparent else MiuixTheme.colorScheme.surfaceContainer,
                            modifier = if (enableBlur) {
                                // 直接应用预设好的样式
                                Modifier.liquidGlass(style = bottomNavigationStyle)
                            } else Modifier,
                            selected = targetPage,
                            onClick = { index ->
                                targetPage = index
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                        )
                    }
                }
            },
        ) { padding ->
            Box(
                modifier = Modifier
                    .liquidGlassProvider(providerState)
            ) {
                AppHorizontalPager(
                    modifier = Modifier.imePadding(),
                    pagerState = pagerState,
                    topAppBarScrollBehaviorList = topAppBarScrollBehaviorList,
                    padding = padding,
                    navController = navController,
                    context = context
                )
            }
        }
    }
}

@Composable
fun NavigationBar(
    items: List<NavigationItem>,
    selected: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MiuixTheme.colorScheme.background
) {
    require(items.size in 2..5) { "BottomBar must have between 2 and 5 items" }
    Surface(
        color = color
    ) {
        Column(
            modifier = modifier
                .background(color),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    val isSelected = selected == index
                    var isPressed by remember { mutableStateOf(false) }
                    val tint by animateColorAsState(
                        targetValue = when {
                            isPressed -> if (isSelected) {
                                MiuixTheme.colorScheme.onSurfaceContainer.copy(alpha = 0.6f)
                            } else {
                                MiuixTheme.colorScheme.onSurfaceContainerVariant.copy(alpha = 0.6f)
                            }

                            isSelected -> MiuixTheme.colorScheme.onSurfaceContainer
                            else -> MiuixTheme.colorScheme.onSurfaceContainerVariant
                        }
                    )
                    val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    Column(
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 20.dp)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        isPressed = true
                                        tryAwaitRelease()
                                        isPressed = false
                                    },
                                    onTap = { onClick(index) }
                                )
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            modifier = Modifier.size(28.dp),
                            imageVector = item.icon,
                            contentDescription = item.label,
                            colorFilter = ColorFilter.tint(tint)
                        )
                        Text(
                            modifier = Modifier,
                            text = item.label,
                            color = tint,
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp,
                            fontWeight = fontWeight
                        )
                    }
                }
            }
        }
    }
}

val LocalColorMode = compositionLocalOf<MutableState<Int>> { error("No ColorMode provided") }

@Composable
fun AppHorizontalPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    topAppBarScrollBehaviorList: List<ScrollBehavior>,
    padding: PaddingValues,
    navController: NavController,
    context: Context
) {
    HorizontalPager(
        modifier = modifier.background(MiuixTheme.colorScheme.background),
        state = pagerState,
        userScrollEnabled = true,
        pageContent = { page ->
            when (page) {
                0 -> Main_Home(
                    topAppBarScrollBehavior = topAppBarScrollBehaviorList[0],
                    padding = padding,
                    navController = navController
                )

                1 -> Main_Module(
                    topAppBarScrollBehavior = topAppBarScrollBehaviorList[1],
                    padding = padding,
                    navController = navController
                )

                2 -> Main_Function(
                    topAppBarScrollBehavior = topAppBarScrollBehaviorList[2],
                    padding = padding,
                    navController = navController
                )

                else -> Main_About(
                    topAppBarScrollBehavior = topAppBarScrollBehaviorList[3],
                    padding = padding,
                    context = context,
                    navController = navController
                )
            }
        }
    )
}
