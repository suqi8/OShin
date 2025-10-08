package com.suqi8.oshin.ui.activity

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import dev.chrisbanes.haze.ExperimentalHazeApi

@OptIn(ExperimentalHazeApi::class)
@Composable
fun recent_update(navController: NavController) {
    /*val context = LocalContext.current
    val topAppBarState = MiuixScrollBehavior(rememberTopAppBarState())
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
    val lazyListState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState(firstVisibleItemIndex = 0) // 初始位置
    }
    Scaffold(topBar = {
        TopAppBar(
            scrollBehavior = topAppBarState,
            title = stringResource(R.string.recent_update),
            color = if (context.prefs("settings").getBoolean("enable_blur", true)) Color.Transparent else MiuixTheme.colorScheme.background,
            modifier = if (context.prefs("settings").getBoolean("enable_blur", true)) {
                Modifier.hazeEffect(
                    state = hazeState,
                    style = hazeStyle, block = fun HazeEffectScope.() {
                        inputScale = HazeInputScale.Auto
                        if (context.prefs("settings").getBoolean("enable_gradient_blur", true)) progressive = HazeProgressive.verticalGradient(startIntensity = 1f, endIntensity = 0f)
                    })
            } else Modifier,
            navigationIcon = {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier.padding(start = 18.dp)
                ) {
                    Icon(
                        imageVector = MiuixIcons.Useful.Back,
                        contentDescription = null,
                        tint = MiuixTheme.colorScheme.onBackground
                    )
                }
            }
        )
        Image(painter = painterResource(R.drawable.osu),contentDescription = null, modifier = Modifier.fillMaxWidth())
    }) { padding ->
        val recentFeatureState = remember { mutableStateOf<List<FeatureItem>?>(null) }
        val context = LocalContext.current
        LaunchedEffect(Unit) {
            recentFeatureState.value = withContext(Dispatchers.IO) {
                features(context)
                    .takeIf { it.isNotEmpty() }
                    ?.toList()
                    ?.reversed()
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .overScrollVertical()
                .hazeSource(state = hazeState)
                .background(MiuixTheme.colorScheme.background)
                .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal))
                .nestedScroll(topAppBarState.nestedScrollConnection),
            contentPadding = padding,
            state = lazyListState
        ) {
            recentFeatureState.value?.let { list ->
                itemsIndexed(list) { index, feature ->
                    val onClick by remember(feature.category) {
                        mutableStateOf({ navController.navigate(feature.category) })
                    }
                    val route = rememberSaveable { mutableStateOf("") }
                    if (route.value == "") {
                        LaunchedEffect(Unit) {
                            route.value = GetFuncRoute(feature.category, context)
                        }
                    }
                    FunArrow(
                        title = feature.title,
                        summary = if (feature.summary != null) feature.summary + "\n" + route.value else route.value,
                        onClick = onClick
                    )
                    if (index != list.lastIndex) {
                        addline()
                    }
                }
            }
            item {
                Spacer(Modifier.height(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()))
            }
        }
    }*/
}
