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
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.ui.activity.about.Main_About
import com.suqi8.oshin.ui.activity.about.about_contributors
import com.suqi8.oshin.ui.activity.about.about_donors
import com.suqi8.oshin.ui.activity.about.about_group
import com.suqi8.oshin.ui.activity.about.about_references
import com.suqi8.oshin.ui.activity.about.about_setting
import com.suqi8.oshin.ui.activity.android.android
import com.suqi8.oshin.ui.activity.android.oplus_services
import com.suqi8.oshin.ui.activity.android.package_manager_services
import com.suqi8.oshin.ui.activity.android.split_screen_multi_window
import com.suqi8.oshin.ui.activity.com.android.launcher.launcher
import com.suqi8.oshin.ui.activity.com.android.launcher.recent_task
import com.suqi8.oshin.ui.activity.com.android.mms.mms
import com.suqi8.oshin.ui.activity.com.android.settings.feature
import com.suqi8.oshin.ui.activity.com.android.settings.settings
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
import com.suqi8.oshin.ui.activity.com.oplus.games.games
import com.suqi8.oshin.ui.activity.com.oplus.ota.ota
import com.suqi8.oshin.ui.activity.com.oplus.phonemanager.oplusphonemanager
import com.suqi8.oshin.ui.activity.func.cpu_freq
import com.suqi8.oshin.ui.activity.func.romworkshop.Rom_workshop
import com.suqi8.oshin.ui.activity.hide_apps_notice
import com.suqi8.oshin.ui.activity.recent_update
import com.suqi8.oshin.ui.theme.AppTheme
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeEffectScope
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
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
import top.yukonga.miuix.kmp.utils.MiuixPopupUtils.Companion.dismissDialog
import top.yukonga.miuix.kmp.utils.getWindowSize
import java.util.Locale
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.system.exitProcess

const val TAG = "OShin"
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false
        UMConfigure.preInit(this,"67c7dea68f232a05f127781e","android")
        UMConfigure.setProcessEvent(true)
        setContent {
            val context = LocalContext.current
            val language = context.prefs("settings").getInt("app_language", 0)
            val locale = when (language) {
                1 -> Locale.SIMPLIFIED_CHINESE
                2 -> Locale.ENGLISH
                3 -> Locale.JAPANESE
                else -> Locale.getDefault()
            }

            val config = resources.configuration
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)

            val colorMode = remember { mutableIntStateOf(context.prefs("settings").getInt("color_mode", 0)) }
            val darkMode = colorMode.intValue == 2 || (isSystemInDarkTheme() && colorMode.intValue == 0)
            DisposableEffect(darkMode) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT) { darkMode },
                    navigationBarStyle = SystemBarStyle.auto(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT) { darkMode },
                )

                window.isNavigationBarContrastEnforced = false  // Xiaomi moment, this code must be here

                onDispose {}
            }

            AppTheme(colorMode = colorMode.intValue) {
                Main0(colorMode = colorMode, context = context, modifier = Modifier)
            }
        }
    }
}

@Immutable
class SpringEasing @JvmOverloads constructor(
    private val damping: Float = 0.85f,
    private val response: Float = 0.3f,
    private val mass: Float = 1.0f,
    private val acceleration: Float = 0.0f
) : Easing {
    var duration: Long = 1000L
        private set
    private var g = 0.0
    private var inputScale = 1.0f
    private var omega = 0.0
    private var p = 0.0
    private var q = 0.0
    private lateinit var solution: SpringSolution
    private var velocity = 0.0f
    private var xStar = 0.0
    private var zeta = 0.0

    init {
        updateParameters()
    }

    override fun transform(fraction: Float): Float {
        if (fraction == 1.0f) {
            return 1.0f
        }
        val f2 = fraction * this.inputScale
        val x = solution.x(f2).toFloat()
        this.velocity = solution.dX(f2).toFloat()
        return x
    }

    private fun updateParameters() {
        val d = damping.toDouble()
        this.zeta = d
        val d2 = 6.283185307179586 / this.response
        this.omega = d2
        val f = this.mass
        val d3 = (((d * 2.0) * d2) * f) / f
        this.p = d3
        val d4 = ((d2 * d2) * f) / f
        this.q = d4
        val d5 = acceleration.toDouble()
        this.g = d5
        val d6 = ((-d5) / d4) + 1.0
        this.xStar = d6
        val d7 = (d3 * d3) - (d4 * 4.0)
        val d8 = 0.0 - d6
        this.solution = if (d7 > 0.0) {
            OverDampingSolution(d7, d8, d3, velocity.toDouble(), d6 )
        } else if (d7 == 0.0) {
            CriticalDampingSolution(d8, d3, velocity.toDouble(), d6)
        } else {
            UnderDampingSolution(d7, d8, d3, velocity.toDouble(), d6)
        }
        val solveDuration = (solveDuration(d7) * 1000.0).toLong()
        this.duration = solveDuration
        this.inputScale = (solveDuration.toFloat()) / 1000.0f
    }

    private fun solveDuration(d: Double): Double {
        var d2: Double
        var d3 = 0.0
        val d4 = if (d >= 0.0) 0.001 else 1.0E-4
        val d5 = this.g
        var d6 = 1.0
        if (d5 == 0.0) {
            var f = 0.0f
            while (abs(d3 - 1.0) > d4) {
                f += 0.001f
                d3 = solution.x(f)
                val dX = solution.dX(f)
                if (abs(d3 - 1.0) <= d4 && dX <= 5.0E-4) {
                    break
                }
            }
            return f.toDouble()
        }
        val solve = solution.solve(0.0, this.q, d5, this.xStar)
        val d7 = this.q
        val d8 = this.xStar
        val d9 = d7 * d8 * d8
        val d10 = (solve - d9) * d4
        var d11 = 1.0
        var solve2 = solution.solve(1.0, d7, this.g, d8)
        var d12 = 0.0
        while (true) {
            d2 = d9 + d10
            if (solve2 <= d2) {
                break
            }
            val d13 = d11 + d6
            d12 = d11
            d6 = 1.0
            d11 = d13
            solve2 = solution.solve(d13, this.q, this.g, this.xStar)
        }
        do {
            val d14 = (d12 + d11) / 2.0
            if (solution.solve(d14, this.q, this.g, this.xStar) > d2) {
                d12 = d14
            } else {
                d11 = d14
            }
        } while (d11 - d12 >= d4)
        return d11
    }

    internal abstract inner class SpringSolution {
        abstract fun dX(f: Float): Double

        abstract fun x(f: Float): Double

        fun solve(d: Double, d2: Double, d3: Double, d4: Double): Double {
            val f = d.toFloat()
            val x = x(f)
            val dX = dX(f)
            return (((d2 * x) * x) + (dX * dX)) - ((d3 * 2.0) * (x - d4))
        }
    }

    internal inner class CriticalDampingSolution(
        d2: Double,
        d3: Double,
        d4: Double,
        d5: Double
    ) :
        SpringSolution() {
        private val c1: Double
        private val c2: Double
        private val r: Double
        private val xStar: Double

        init {
            val d6 = (-d3) / 2.0
            this.r = d6
            this.c1 = d2
            this.c2 = d4 - (d2 * d6)
            this.xStar = d5
        }

        override fun x(f: Float): Double {
            val d = f.toDouble()
            return ((this.c1 + (this.c2 * d)) * exp(this.r * d)) + this.xStar
        }

        override fun dX(f: Float): Double {
            val d = this.c1
            val d2 = this.r
            val d3 = this.c2
            val d4 = f.toDouble()
            return ((d * d2) + (d3 * ((d2 * d4) + 1.0))) * exp(d2 * d4)
        }
    }

    internal inner class OverDampingSolution(
        d: Double,
        d2: Double,
        d3: Double,
        d4: Double,
        d5: Double
    ) :
        SpringSolution() {
        private val c1: Double
        private val c2: Double
        private val r1: Double
        private val r2: Double
        private val xStar: Double

        init {
            val sqrt = sqrt(d)
            val d6 = (sqrt - d3) / 2.0
            this.r1 = d6
            val d7 = ((-sqrt) - d3) / 2.0
            this.r2 = d7
            this.c1 = (d4 - (d2 * d7)) / sqrt
            this.c2 = (-(d4 - (d6 * d2))) / sqrt
            this.xStar = d5
        }

        override fun x(f: Float): Double {
            val d = f.toDouble()
            return (this.c1 * exp(this.r1 * d)) + (this.c2 * exp(this.r2 * d)) + this.xStar
        }

        override fun dX(f: Float): Double {
            val d = this.c1
            val d2 = this.r1
            val d3 = f.toDouble()
            val exp = d * d2 * exp(d2 * d3)
            val d4 = this.c2
            val d5 = this.r2
            return exp + (d4 * d5 * exp(d5 * d3))
        }
    }

    internal inner class UnderDampingSolution(
        d: Double,
        d2: Double,
        d3: Double,
        d4: Double,
        d5: Double
    ) :
        SpringSolution() {
        private val alpha: Double
        private val beta: Double
        private val c1: Double
        private val c2: Double
        private val xStar: Double

        init {
            val d6 = (-d3) / 2.0
            this.alpha = d6
            val sqrt = sqrt(-d) / 2.0
            this.beta = sqrt
            this.c1 = d2
            this.c2 = (d4 - (d2 * d6)) / sqrt
            this.xStar = d5
        }

        override fun x(f: Float): Double {
            val d = f.toDouble()
            return (exp(this.alpha * d) * ((this.c1 * cos(this.beta * d)) + (this.c2 * sin(
                this.beta * d
            )))) + this.xStar
        }

        override fun dX(f: Float): Double {
            val d = f.toDouble()
            val exp = exp(this.alpha * d)
            val d2 = this.c1 * this.alpha
            val d3 = this.c2
            val d4 = this.beta
            val cos = (d2 + (d3 * d4)) * cos(d4 * d)
            val d5 = this.c2 * this.alpha
            val d6 = this.c1
            val d7 = this.beta
            return exp * (cos + ((d5 - (d6 * d7)) * sin(d7 * d)))
        }
    }
}
val lspVersion = mutableStateOf("")
@Composable
fun Main0(modifier: Modifier,context: Context,colorMode: MutableState<Int> = remember { mutableIntStateOf(0) }) {
    val navController = rememberNavController()
    val windowWidth = getWindowSize().width
    val easing = SpringEasing(0.95f, 0.4f)//CubicBezierEasing(0.4f, 0.95f, 0.2f, 1f)
    val duration = easing.duration.toInt()
    val alpha: MutableFloatState = remember { mutableFloatStateOf(0.75f) }
    val blurRadius: MutableState<Dp> = remember { mutableStateOf(25.dp) }
    val noiseFactor = remember { mutableFloatStateOf(0f) }
    val containerColor: Color = MiuixTheme.colorScheme.background
    val hazeState = remember { HazeState() }
    val hazeStyle =
        remember(containerColor, alpha.floatValue, blurRadius.value, noiseFactor.floatValue) {
            HazeStyle(
                backgroundColor = containerColor,
                tint = HazeTint(containerColor.copy(alpha.floatValue)),
                blurRadius = blurRadius.value,
                noiseFactor = noiseFactor.floatValue
            )
        }
    LaunchedEffect(Unit) {
        alpha.floatValue = context.prefs("settings").getFloat("AppAlpha", 0.75f)
        blurRadius.value = context.prefs("settings").getInt("AppblurRadius", 25).dp
        noiseFactor.floatValue = context.prefs("settings").getFloat("AppnoiseFactor", 0f)
    }

    val privacy = rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(privacy.value) {
        privacy.value = context.prefs("settings").getBoolean("privacy",true)
        if (!context.prefs("settings").getBoolean("privacy",true)) {
            UMConfigure.init(context, "67c7dea68f232a05f127781e", "android", UMConfigure.DEVICE_TYPE_PHONE, "");
            withContext(Dispatchers.IO) {
                val lsposed_versionname = executeCommand("awk -F= '/version=/ {print \$2}' /data/adb/modules/zygisk_lsposed/module.prop")
                lspVersion.value = lsposed_versionname
                if (lsposed_versionname != "" && context.prefs("settings").getString("privacy_lspvername","") != lsposed_versionname) {
                    MobclickAgent.onEvent(context,"lsposed_usage", mapOf("version_name" to lsposed_versionname))
                    context.prefs("settings").edit { putString("privacy_lspvername",lsposed_versionname) }
                }
                MobclickAgent.onEvent(context,"lsposed_usage", mapOf("lsposed_versionname" to lsposed_versionname))
            }
        }
    }
    SuperDialog(
        show = privacy,
        title = stringResource(R.string.privacy_title),
        //summary = stringResource(R.string.privacy_content),
        onDismissRequest = {

        }
    ) {
        Text(stringResource(R.string.privacy_content))
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
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
                    dismissDialog(privacy)
                    context.prefs("settings").edit { putBoolean("privacy",false) }
                }
            )
        }
    }

    Column {
        NavHost(navController = navController, startDestination = "Main",enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { windowWidth },
                    animationSpec = tween(duration, 0, easing = easing)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -windowWidth / 5 },
                    animationSpec = tween(duration, 0, easing = easing)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -windowWidth / 5 },
                    animationSpec = tween(duration, 0, easing = easing)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { windowWidth },
                    animationSpec = tween(duration, 0, easing = easing)
                )
            },
            sizeTransform = {
                    SizeTransform(clip = true)  // 允许页面在过渡时进行缩放，但不裁剪内容
                }
        ) {
            composable("Main") { Main1(context = context,navController, hazeState, hazeStyle, colorMode) }
            composable("recent_update") { recent_update(navController) }
            composable("android") { android(navController) }
            composable("android\\package_manager_services") { package_manager_services(navController = navController) }
            composable("android\\oplus_system_services") { oplus_services(navController = navController) }
            composable("android\\split_screen_multi_window") { split_screen_multi_window(navController = navController) }
            composable("systemui") { systemui(navController = navController)}
            composable("systemui\\status_bar_clock") { status_bar_clock(navController = navController) }
            composable("systemui\\hardware_indicator") { hardware_indicator(navController = navController) }
            composable("systemui\\statusbar_icon") { statusbar_icon(navController = navController) }
            composable("systemui\\notification") { notification(navController = navController) }
            composable("systemui\\status_bar_wifi") { status_bar_wifi(navController = navController) }
            composable("launcher") { launcher(navController = navController) }
            composable("launcher\\recent_task") { recent_task(navController = navController) }
            composable("about_setting") { about_setting(navController,alpha,blurRadius,noiseFactor,colorMode) }
            composable("about_group") { about_group(navController) }
            composable("about_references") { about_references(navController) }
            composable("about_contributors") { about_contributors(navController) }
            composable("about_donors") { about_donors(navController) }
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
        }
    }
}

@OptIn(FlowPreview::class, ExperimentalHazeApi::class, ExperimentalHazeApi::class,
    ExperimentalHazeApi::class
)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "InflateParams", "ResourceType")
@Composable
fun Main1(context: Context,navController: NavController,
          hazeState: HazeState, hazeStyle: HazeStyle,
          colorMode: MutableState<Int>) {
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
    Scaffold(modifier = Modifier.fillMaxSize(),bottomBar = {
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()), horizontalAlignment = Alignment.CenterHorizontally) {
            Card(modifier = Modifier.padding(bottom = 10.dp).drawColoredShadow(
                MiuixTheme.colorScheme.onSurface,
                0.1f,
                borderRadius = CardDefaults.CornerRadius,
                shadowRadius = 10.dp,
                offsetX = 0.dp,
                offsetY = 0.dp,
                roundedRect = false
            )) {
                NavigationBar(
                    items = items,
                    color = if (context.prefs("settings").getBoolean("enable_blur", true)) Color.Transparent else MiuixTheme.colorScheme.surfaceContainer,
                    modifier = if (context.prefs("settings").getBoolean("enable_blur", true)) {
                        Modifier.hazeEffect(
                            state = hazeState,
                            style = hazeStyle, block = fun HazeEffectScope.() {
                                inputScale = HazeInputScale.Auto
                            }
                        )
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
    }, topBar = {
        AnimatedVisibility(pagerState.currentPage != 3) {
            Box(modifier = Modifier.clip(RoundedCornerShape(28.dp))) {
                TopAppBar(scrollBehavior = currentScrollBehavior,color = if (context.prefs("settings").getBoolean("enable_blur", true)) Color.Transparent else MiuixTheme.colorScheme.background,
                    title = when (pagerState.currentPage) {
                        0 -> stringResource(R.string.app_name)
                        1 -> stringResource(R.string.module)
                        2 -> stringResource(R.string.func)
                        else -> stringResource(R.string.about)
                    }, modifier = if (context.prefs("settings").getBoolean("enable_blur", true)) {
                        Modifier.hazeEffect(
                            state = hazeState,
                            style = hazeStyle, block = fun HazeEffectScope.() {
                                inputScale = HazeInputScale.Auto
                                if (context.prefs("settings").getBoolean("enable_gradient_blur", true)) progressive = HazeProgressive.verticalGradient(startIntensity = 1f, endIntensity = 0f)
                            })
                    } else Modifier, navigationIcon = {
                        /*Image(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = null,
                            modifier = Modifier.size(50.dp))*/
                        Card(modifier = Modifier
                            .size(55.dp).padding(10.dp)) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                /*Image(
                                    painter = painterResource(id = R.drawable.icon_background_newyear),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().graphicsLayer(scaleX = 1.5f, scaleY = 1.5f)
                                    *//*.offset(y = (-20).dp)*//*,
                        contentScale = ContentScale.Crop
                    )*/
                                Image(
                                    painter = painterResource(id = R.drawable.icon),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().graphicsLayer(scaleX = 1.5f, scaleY = 1.5f).clickable {
                                        //context.prefs("settings").edit { putBoolean("privacy",true) }
                                    }
                                    /*.offset(y = (-20).dp)*/,
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    })
                Image(painter = painterResource(R.drawable.osu),contentDescription = null, modifier = Modifier.fillMaxWidth())
            }
        }
    }) { padding ->
        Box(modifier = Modifier.hazeSource(
            state = hazeState)
        ) {
            AppHorizontalPager(
                modifier = Modifier.imePadding(),
                pagerState = pagerState,
                topAppBarScrollBehaviorList = topAppBarScrollBehaviorList,
                padding = padding,
                navController = navController,
                context = context,
                colorMode = colorMode
            )
        }
        /*Column(modifier = Modifier.padding(Padding)) {
            NavHost(navController = navController1, startDestination = "Main_Home") {
                composable("Main_Function") { Main_Function(navController) }
                composable("Main_Home") { Main_Home() }
                composable("Main_About") { Main_About(navController) }
            }
        }*/
    }
}

@Composable
fun NavigationBar(
    items: List<NavigationItem>,
    selected: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MiuixTheme.colorScheme.surfaceContainer
) {
    require(items.size in 2..5) { "BottomBar must have between 2 and 5 items" }
    Surface(
        color = color
    ) {
        Column(
            modifier = modifier
                .background(Color.Transparent),
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

@Composable
fun AppHorizontalPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    topAppBarScrollBehaviorList: List<ScrollBehavior>,
    padding: PaddingValues,
    navController: NavController,
    context: Context,
    colorMode: MutableState<Int>
) {
    HorizontalPager(
        modifier = modifier,
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
                    navController = navController,
                    colorMode = colorMode
                )
            }
        }
    )
}
