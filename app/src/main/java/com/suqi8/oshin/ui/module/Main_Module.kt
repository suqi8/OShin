package com.suqi8.oshin.ui.module

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.palette.graphics.Palette
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.R
import com.suqi8.oshin.models.ModuleEntry
import com.suqi8.oshin.ui.activity.components.BasicComponentDefaults
import com.suqi8.oshin.ui.activity.components.Card
import com.suqi8.oshin.ui.activity.components.CardDefaults
import com.suqi8.oshin.ui.activity.components.SuperArrow
import com.suqi8.oshin.ui.home.ModernSectionTitle
import com.suqi8.oshin.utils.GetAppIconAndName
import com.suqi8.oshin.utils.GetFuncRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Search
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

// --- 主屏幕入口 ---

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Main_Module(
    topAppBarScrollBehavior: ScrollBehavior,
    navController: NavController,
    padding: PaddingValues,
    viewModel: ModuleViewModel = hiltViewModel(),
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val uiState by viewModel.uiState.collectAsState()

    // ===== 使用 ViewModel 中保存的滚动位置 =====
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = viewModel.scrollIndex,
        initialFirstVisibleItemScrollOffset = viewModel.scrollOffset
    )

    // ===== 监听滚动变化并保存到 ViewModel =====
    LaunchedEffect(listState) {
        snapshotFlow {
            listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
        }.collect { (index, offset) ->
            viewModel.saveScrollPosition(index, offset)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MiuixTheme.colorScheme.background)
            .overScrollVertical()
            .scrollEndHaptic()
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        contentPadding = padding,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ModernSectionTitle(
                title = stringResource(id = R.string.module),
                modifier = Modifier
                    .displayCutoutPadding()
                    .padding(top = padding.calculateTopPadding() + 80.dp)
            )
        }
        item {
            HUDSearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            Column {
                if (uiState.isSearching) {
                    SearchContent(
                        features = uiState.searchResults,
                        query = uiState.searchQuery,
                        navController = navController,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                } else {
                    AppListContent(
                        appStyle = uiState.appStyle,
                        onStyleChange = viewModel::onAppStyleChanged,
                        moduleEntries = uiState.moduleEntries,
                        onAppNotFound = viewModel::onAppNotFound,
                        navController = navController,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        viewModel = viewModel // ===== 传递 viewModel =====
                    )
                }
            }
        }

        item {
            AnimatedVisibility(visible = uiState.notInstalledApps.isNotEmpty()) {
                with(sharedTransitionScope) {
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .sharedBounds(
                                sharedContentState = rememberSharedContentState(key = "hide_apps_notice"),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                    ) {
                        SuperArrow(
                            title = stringResource(R.string.app_not_found_in_list),
                            titleColor = BasicComponentDefaults.titleColor(enabledColor = MiuixTheme.colorScheme.primary),
                            onClick = {
                                val packages = uiState.notInstalledApps.joinToString(",")
                                navController.navigate("hide_apps_notice/$packages")
                            }
                        )
                    }
                }
            }
        }
    }
}


// --- 子组件 ---

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppListContent(
    appStyle: Int,
    onStyleChange: () -> Unit,
    moduleEntries: List<ModuleEntry>,
    onAppNotFound: (String) -> Unit,
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: ModuleViewModel = hiltViewModel()
) {
    val installedEntries = moduleEntries

    Column(Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = stringResource(R.string.switch_style),
                color = MiuixTheme.colorScheme.primary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable(onClick = onStyleChange)
            )
        }

        Card(modifier = Modifier.padding(horizontal = 0.dp)) {
            if (appStyle == 0) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    installedEntries.forEach { entry ->
                        FunctionAppFlow(
                            packageName = entry.packageName,
                            onClick = { navController.navigate("feature/${entry.routeId}") },
                            onResult = onAppNotFound,
                            entry = entry,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope,
                            viewModel = viewModel // ===== 传递 viewModel =====
                        )
                    }
                }
            } else {
                Column {
                    installedEntries.forEachIndexed { index, entry ->
                        FunctionApp(
                            packageName = entry.packageName,
                            onClick = { navController.navigate("feature/${entry.routeId}") },
                            onResult = onAppNotFound,
                            entry = entry,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope,
                            viewModel = viewModel // ===== 传递 viewModel =====
                        )
                        if (index < installedEntries.size - 1) {
                            addline()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SearchContent(
    features: List<SearchableItem>,
    query: String,
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    Card(modifier = Modifier.padding(horizontal = 16.dp)) {
        if (features.isEmpty()) {
            Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = "空空如也~",
                    fontFamily = FontFamily.Monospace,
                    color = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        } else {
            Column {
                features.forEachIndexed { index, item ->
                    SearchListItem(
                        item = item,
                        query = query,
                        highlightColor = MiuixTheme.colorScheme.primary,
                        onClick = {
                            navController.navigate("${item.route}?highlightKey=${item.key}")
                        },
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    if (index < features.size - 1) {
                        addline()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SearchListItem(
    item: SearchableItem,
    query: String,
    highlightColor: Color,
    onClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val context = LocalContext.current
    val titleAnnotated = highlightMatches(item.title, query, highlightColor)
    val summaryAnnotated = highlightMatches(item.summary, query, highlightColor)
    val routeId = remember(item.route) {
        item.route.substringAfter("feature/")
    }
    val featurePath = remember(routeId) {
        GetFuncRoute(routeId, context)
    }

    with(sharedTransitionScope) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = item.key),
                    animatedVisibilityScope = animatedVisibilityScope,
                    // placeHolderSize = animatedSize
                )
                .wrapContentHeight()
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = titleAnnotated, fontSize = 16.sp, color = MiuixTheme.colorScheme.onBackground)
                if (item.summary.isNotBlank()) {
                    Text(
                        text = summaryAnnotated,
                        fontSize = 12.sp,
                        color = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
                Text(
                    text = featurePath,
                    fontSize = 11.sp,
                    color = MiuixTheme.colorScheme.onSurfaceVariantActions.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}


// --- 通用辅助组件 ---

@Composable
fun HUDSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Card(
        modifier = modifier
    ) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .height(48.dp),
            textStyle = MiuixTheme.textStyles.main.copy(
                color = MiuixTheme.colorScheme.onBackground,
                fontFamily = FontFamily.Monospace
            ),
            singleLine = true,
            cursorBrush = SolidColor(MiuixTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            decorationBox = { innerTextField ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = MiuixIcons.Useful.Search,
                        contentDescription = "Search"
                    )
                    Spacer(Modifier.width(12.dp))
                    Box(Modifier.weight(1f)) {
                        if (query.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.Search),
                                color = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        innerTextField()
                    }
                }
            }
        )
    }
}

@Composable
fun HUDModuleContainer(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    // 使用首页风格的渐变背景
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF6366F1).copy(alpha = 0.08f),
                        Color(0xFF8B5CF6).copy(alpha = 0.04f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = Color(0xFF6366F1).copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp)
    ) {
        Column { content() }
    }
}

@Composable
fun SectionTitle(titleResId: Int) {
    val primaryColor = MiuixTheme.colorScheme.primary.copy(alpha = 0.5f)
    val textColor = MiuixTheme.colorScheme.onBackground
    var animated by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { animated = true }

    val lineWidth by animateFloatAsState(
        targetValue = if (animated) 1f else 0.00001f,
        animationSpec = tween(durationMillis = 700)
    )
    val textAlpha by animateFloatAsState(
        targetValue = if (animated) 1f else 0f,
        animationSpec = tween(durationMillis = 500, delayMillis = 200)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(lineWidth)
                .height(1.dp)
                .background(Brush.horizontalGradient(listOf(Color.Transparent, primaryColor)))
        )
        Text(
            text = " ${stringResource(id = titleResId)} ",
            fontSize = 20.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = textColor.copy(alpha = textAlpha),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Box(
            modifier = Modifier
                .weight(lineWidth)
                .height(1.dp)
                .background(Brush.horizontalGradient(listOf(primaryColor, Color.Transparent)))
        )
    }
}

// 缓存颜色以避免重复计算
private val colorCache = mutableMapOf<String, Color>()

@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun FunctionApp(
    packageName: String,
    onClick: () -> Unit,
    onResult: (String) -> Unit,
    entry: ModuleEntry,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: ModuleViewModel = hiltViewModel() // 添加 ViewModel 参数
) {
    // 先检查缓存
    val cachedInfo = remember(packageName) { viewModel.getAppInfo(packageName) }

    if (cachedInfo != null) {
        // 直接使用缓存数据渲染，无需加载
        with(sharedTransitionScope) {
            Row(
                modifier = Modifier
                    .clickable(onClick = onClick)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    colors = CardDefaults.defaultColors(color = cachedInfo.dominantColor),
                    modifier = Modifier
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "item-${entry.routeId}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                        .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                        .drawColoredShadow(
                            cachedInfo.dominantColor,
                            1f,
                            borderRadius = 13.dp,
                            shadowRadius = 7.dp,
                        )
                ) {
                    Image(
                        bitmap = cachedInfo.icon,
                        contentDescription = cachedInfo.name,
                        modifier = Modifier.size(45.dp)
                    )
                }
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(
                        text = cachedInfo.name,
                        modifier = Modifier.sharedElement(
                            sharedContentState = rememberSharedContentState(key = "title-${entry.routeId}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    )
                    Text(
                        text = packageName,
                        fontSize = MiuixTheme.textStyles.subtitle.fontSize,
                        fontWeight = FontWeight.Medium,
                        color = MiuixTheme.colorScheme.onBackgroundVariant
                    )
                }
            }
        }
    } else {
        // 缓存中没有，需要加载
        GetAppIconAndName(packageName = packageName) { appName, icon ->
            if (appName != "noapp") {
                val defaultColor = MiuixTheme.colorScheme.surface
                val dominantColor = remember { mutableStateOf(colorCache[packageName] ?: defaultColor) }
                val isLoading = remember { mutableStateOf(dominantColor.value == defaultColor) }

                LaunchedEffect(icon) {
                    if (isLoading.value) {
                        val newColor = withContext(Dispatchers.IO) {
                            if (YukiHookAPI.Status.isModuleActive) getAutoColor(icon) else Color.Red
                        }
                        dominantColor.value = newColor
                        colorCache[packageName] = newColor
                        isLoading.value = false

                        // ===== 关键：保存到 ViewModel 缓存 =====
                        viewModel.cacheAppInfo(
                            packageName,
                            AppInfo(appName, icon, newColor)
                        )
                    }
                }

                with(sharedTransitionScope) {
                    Row(
                        modifier = Modifier
                            .clickable(onClick = onClick)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            colors = CardDefaults.defaultColors(color = dominantColor.value),
                            modifier = Modifier
                                .sharedBounds(
                                    sharedContentState = rememberSharedContentState(key = "item-${entry.routeId}"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                                .drawColoredShadow(
                                    dominantColor.value,
                                    1f,
                                    borderRadius = 13.dp,
                                    shadowRadius = 7.dp,
                                )
                        ) {
                            Image(bitmap = icon, contentDescription = appName, modifier = Modifier.size(45.dp))
                        }
                        Column(modifier = Modifier.padding(start = 16.dp)) {
                            Text(
                                text = appName,
                                modifier = Modifier.sharedElement(
                                    sharedContentState = rememberSharedContentState(key = "title-${entry.routeId}"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                            )
                            Text(
                                text = packageName,
                                fontSize = MiuixTheme.textStyles.subtitle.fontSize,
                                fontWeight = FontWeight.Medium,
                                color = MiuixTheme.colorScheme.onBackgroundVariant
                            )
                        }
                    }
                }
            } else {
                onResult(packageName)
            }
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun FunctionAppFlow(
    packageName: String,
    onClick: () -> Unit,
    onResult: (String) -> Unit,
    entry: ModuleEntry,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: ModuleViewModel = hiltViewModel() // 添加 ViewModel 参数
) {
    // 先检查缓存
    val cachedInfo = remember(packageName) { viewModel.getAppInfo(packageName) }

    if (cachedInfo != null) {
        // 直接使用缓存数据渲染
        with(sharedTransitionScope) {
            Column(
                modifier = Modifier
                    .width(65.dp)
                    .clickable(onClick = onClick),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    colors = CardDefaults.defaultColors(color = cachedInfo.dominantColor),
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "item-${entry.routeId}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                        .drawColoredShadow(
                            cachedInfo.dominantColor,
                            1f,
                            borderRadius = 13.dp,
                            shadowRadius = 7.dp,
                        )
                ) {
                    Image(
                        bitmap = cachedInfo.icon,
                        contentDescription = cachedInfo.name,
                        modifier = Modifier.size(50.dp)
                    )
                }
                Text(
                    text = cachedInfo.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false,
                    modifier = Modifier.padding(top = 10.dp, bottom = 6.dp)
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(key = "title-${entry.routeId}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                )
            }
        }
    } else {
        // 缓存中没有，需要加载
        GetAppIconAndName(packageName = packageName) { appName, icon ->
            if (appName != "noapp") {
                val defaultColor = MiuixTheme.colorScheme.surface
                val dominantColor = remember { mutableStateOf(colorCache[packageName] ?: defaultColor) }
                val isLoading = remember { mutableStateOf(dominantColor.value == defaultColor) }

                LaunchedEffect(icon) {
                    if (isLoading.value) {
                        val newColor = withContext(Dispatchers.IO) {
                            if (YukiHookAPI.Status.isModuleActive) getAutoColor(icon) else Color.Red
                        }
                        dominantColor.value = newColor
                        colorCache[packageName] = newColor
                        isLoading.value = false

                        // ===== 关键：保存到 ViewModel 缓存 =====
                        viewModel.cacheAppInfo(
                            packageName,
                            AppInfo(appName, icon, newColor)
                        )
                    }
                }

                with(sharedTransitionScope) {
                    Column(
                        modifier = Modifier
                            .width(65.dp)
                            .clickable(onClick = onClick),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            colors = CardDefaults.defaultColors(color = dominantColor.value),
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .sharedBounds(
                                    sharedContentState = rememberSharedContentState(key = "item-${entry.routeId}"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                                .drawColoredShadow(
                                    dominantColor.value,
                                    1f,
                                    borderRadius = 13.dp,
                                    shadowRadius = 7.dp,
                                )
                        ) {
                            Image(bitmap = icon, contentDescription = appName, modifier = Modifier.size(50.dp))
                        }
                        Text(
                            text = appName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            softWrap = false,
                            modifier = Modifier.padding(top = 10.dp, bottom = 6.dp)
                                .sharedElement(
                                    sharedContentState = rememberSharedContentState(key = "title-${entry.routeId}"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                        )
                    }
                }
            } else {
                onResult(packageName)
            }
        }
    }
}

// --- 工具函数和类 ---

class CutCornerShape(private val cut: Dp) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val cutPx = with(density) { cut.toPx() }
        val path = Path().apply {
            moveTo(cutPx, 0f)
            lineTo(size.width - cutPx, 0f)
            lineTo(size.width, cutPx)
            lineTo(size.width, size.height - cutPx)
            lineTo(size.width - cutPx, size.height)
            lineTo(cutPx, size.height)
            lineTo(0f, size.height - cutPx)
            lineTo(0f, cutPx)
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun highlightMatches(text: String, query: String, highlightColor: Color): AnnotatedString {
    return buildAnnotatedString {
        if (query.isBlank() || !text.contains(query, ignoreCase = true)) {
            append(text)
            return@buildAnnotatedString
        }
        val regex = Regex(query, RegexOption.IGNORE_CASE)
        var lastIndex = 0
        regex.findAll(text).forEach { matchResult ->
            append(text.substring(lastIndex, matchResult.range.first))
            withStyle(style = SpanStyle(color = highlightColor, fontWeight = FontWeight.Bold)) {
                append(matchResult.value)
            }
            lastIndex = matchResult.range.last + 1
        }
        if (lastIndex < text.length) {
            append(text.substring(lastIndex))
        }
    }
}

suspend fun getAutoColor(icon: ImageBitmap): Color {
    return withContext(Dispatchers.IO) {
        Palette.from(icon.asAndroidBitmap()).generate().dominantSwatch?.rgb?.let { Color(it) } ?: Color.White
    }
}

@Composable
fun addline() {
    val context = LocalContext.current
    if (context.prefs("settings").getBoolean("addline", false))
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            thickness = 0.5.dp,
            color = MiuixTheme.colorScheme.dividerLine
        )
}

@SuppressLint("UseKtx")
fun Modifier.drawColoredShadow(
    color: Color,
    alpha: Float = 0.2f,
    borderRadius: Dp = 0.dp,
    shadowRadius: Dp = 20.dp,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp
): Modifier = this.drawBehind {
    this.drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        val transparentColor = color.copy(alpha = 0f).toArgb()
        val shadowColor = color.copy(alpha = alpha).toArgb()

        canvas.save()

        frameworkPaint.color = transparentColor
        frameworkPaint.setShadowLayer(
            shadowRadius.toPx(),
            offsetX.toPx(),
            offsetY.toPx(),
            shadowColor
        )

        canvas.drawRoundRect(
            left = 0f,
            top = 0f,
            right = this.size.width,
            bottom = this.size.height,
            radiusX = borderRadius.toPx(),
            radiusY = borderRadius.toPx(),
            paint = paint
        )

        canvas.restore()
    }
}
