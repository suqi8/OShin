package io.github.suqi8.opatch

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.highcapable.yukihookapi.hook.factory.prefs
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import io.github.suqi8.opatch.ui.theme.AppTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.HorizontalPager
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.getWindowSize

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val colorMode = remember { mutableIntStateOf(0) }
            val darkMode = colorMode.intValue == 2 || (isSystemInDarkTheme() && colorMode.intValue == 0)
            val context = LocalContext.current
            // 读取存储的 colorMode
            LaunchedEffect(Unit) {
                getColorMode(context).collect { savedIndex ->
                    colorMode.intValue = savedIndex
                }
            }
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

@Composable
fun Main0(modifier: Modifier,context: Context,colorMode: MutableState<Int> = remember { mutableIntStateOf(0) }) {
    val navController = rememberNavController()
    val windowWidth = getWindowSize().width
    val easing = CubicBezierEasing(0.12f, 0.88f, 0.2f, 1f)
    Column {
        NavHost(navController = navController, startDestination = "Main",enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { windowWidth },
                    animationSpec = tween(durationMillis = 500, easing = easing)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -windowWidth / 5 },
                    animationSpec = tween(durationMillis = 500, easing = easing)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -windowWidth / 5 },
                    animationSpec = tween(durationMillis = 500, easing = easing)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { windowWidth },
                    animationSpec = tween(durationMillis = 500, easing = easing)
                )
            }) {
            composable("Main") { Main1(modifier = modifier, context = context,navController,colorMode) }
            composable("Fun_android") { Fun_android(navController) }
            composable("Fun_android_package_manager_services") { Fun_android_package_manager_services(navController = navController)}
            composable("Fun_com_android_systemui") { Fun_com_android_systemui(navController = navController)}
            composable("Fun_com_android_systemui_status_bar_clock") { Fun_com_android_systemui_status_bar_clock(navController = navController) }
            composable("Fun_com_android_systemui_hardware_indicator") { Fun_com_android_systemui_hardware_indicator(navController = navController) }
        }
    }
}

@OptIn(FlowPreview::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "InflateParams", "ResourceType")
@Composable
fun Main1(modifier: Modifier,context: Context,navController: NavController,colorMode: MutableState<Int>) {
    val alpha: MutableFloatState = remember { mutableFloatStateOf(0.75f) }
    val blurRadius: MutableState<Dp> = remember { mutableStateOf(25.dp) }
    val noiseFactor = remember { mutableFloatStateOf(0f) }
    val containerColor: Color = MiuixTheme.colorScheme.background
    val hazeState = remember { HazeState() }
    val hazeStyle = remember(containerColor, alpha.value, blurRadius.value, noiseFactor.value) {
        HazeStyle(
            backgroundColor = containerColor,
            tint = HazeTint.Color(containerColor.copy(alpha.value)),
            blurRadius = blurRadius.value,
            noiseFactor = noiseFactor.value
        )
    }
    LaunchedEffect(Unit) {
        alpha.value = context.prefs("settings").getFloat("AppAlpha", 0.75f)
        blurRadius.value = context.prefs("settings").getInt("AppblurRadius", 25).dp
        noiseFactor.value = context.prefs("settings").getFloat("AppnoiseFactor", 0f)
    }
    val topAppBarScrollBehavior0 = MiuixScrollBehavior(top.yukonga.miuix.kmp.basic.rememberTopAppBarState())
    val topAppBarScrollBehavior1 = MiuixScrollBehavior(top.yukonga.miuix.kmp.basic.rememberTopAppBarState())
    val topAppBarScrollBehavior2 = MiuixScrollBehavior(top.yukonga.miuix.kmp.basic.rememberTopAppBarState())

    val topAppBarScrollBehaviorList = listOf(
        topAppBarScrollBehavior0, topAppBarScrollBehavior1, topAppBarScrollBehavior2
    )

    val pagerState = rememberPagerState(pageCount = { 3 },initialPage = 1)
    var targetPage by remember { mutableIntStateOf(pagerState.currentPage) }
    val coroutineScope = rememberCoroutineScope()
    val currentScrollBehavior = when (pagerState.currentPage) {
        0 -> topAppBarScrollBehaviorList[0]
        1 -> topAppBarScrollBehaviorList[1]
        else -> topAppBarScrollBehaviorList[2]
    }

    val items = listOf(
        NavigationItem(stringResource(R.string.func), ImageVector.vectorResource(id = R.drawable.func)),
        NavigationItem(stringResource(R.string.home), ImageVector.vectorResource(id = R.drawable.home)),
        NavigationItem(stringResource(R.string.about), ImageVector.vectorResource(id = R.drawable.about))
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.debounce(150).collectLatest {
            targetPage = pagerState.currentPage
        }
    }
    Scaffold(modifier = Modifier.fillMaxSize(),/*enableBottomBarBlur = true,
        enableTopBarBlur = true, alpha = 0.2f, */bottomBar = {
        /*Box(modifier = Modifier.hazeChild(state = hazeState)) {

        }*/
        NavigationBar(
            items = items,
            color = Color.Transparent,
            modifier = Modifier.hazeChild(
                state = hazeState,
                style = hazeStyle),
            selected = targetPage,
            onClick = { index ->
                targetPage = index
                coroutineScope.launch {
                    pagerState.animateScrollToPage(index)
                }
            }
        )
    }, topBar = {
        TopAppBar(scrollBehavior = currentScrollBehavior,color = Color.Transparent,title = when (pagerState.currentPage) {
            0 -> stringResource(R.string.func)
            1 -> stringResource(R.string.home)
            else -> stringResource(R.string.about)
        }, modifier = Modifier.hazeChild(
            state = hazeState,
            style = hazeStyle), navigationIcon = {
            IconButton(onClick = { /* do something */ }) {
                Image(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = null)
            }
        })
    }) { padding ->
        Box(modifier = Modifier.haze(
            state = hazeState)) {
            AppHorizontalPager(
                modifier = Modifier.imePadding(),
                pagerState = pagerState,
                topAppBarScrollBehaviorList = topAppBarScrollBehaviorList,
                padding = padding,
                navController = navController,
                colorMode = colorMode,
                context = context,
                alpha = alpha,
                blurRadius = blurRadius,
                noiseFactor = noiseFactor
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
fun AppHorizontalPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    topAppBarScrollBehaviorList: List<ScrollBehavior>,
    padding: PaddingValues,
    navController: NavController,
    colorMode: MutableState<Int>,
    context: Context,
    alpha: MutableFloatState,
    blurRadius: MutableState<Dp>,
    noiseFactor: MutableFloatState
) {
    HorizontalPager(
        modifier = modifier,
        pagerState = pagerState,
        userScrollEnabled = true,
        pageContent = { page ->
            when (page) {
                0 -> Main_Function(
                    topAppBarScrollBehavior = topAppBarScrollBehaviorList[0],
                    padding = padding,
                    navController = navController
                )

                1 -> Main_Home(
                    topAppBarScrollBehavior = topAppBarScrollBehaviorList[1],
                    padding = padding
                )

                else -> Main_About(
                    topAppBarScrollBehavior = topAppBarScrollBehaviorList[2],
                    padding = padding,
                    colorMode = colorMode,
                    context = context,
                    navController = navController,
                    alpha = alpha,
                    blur = blurRadius,
                    noise = noiseFactor
                )
            }
        }
    )
}

suspend fun saveColorMode(context: Context, selectedIndex: Int) {
    val colorModeKey = intPreferencesKey("color_mode")
    context.dataStore.edit { preferences ->
        preferences[colorModeKey] = selectedIndex
    }
}

fun getColorMode(context: Context): Flow<Int> {
    val colorModeKey = intPreferencesKey("color_mode")
    return context.dataStore.data.map { preferences ->
        preferences[colorModeKey] ?: 0 // 默认值为 0（Auto_Mode）
    }
}

suspend fun saveSetting(context: Context, name: String, data: String) {
    context.dataStore.edit { preferences ->
        val KEY = stringPreferencesKey(name)
        preferences[KEY] = data
    }
}

suspend fun getSetting(context: Context, name: String): String? {
    val preferences = context.dataStore.data.first()
    val KEY = stringPreferencesKey(name)
    return preferences[KEY]
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
