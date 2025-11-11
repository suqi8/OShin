package com.suqi8.oshin.ui.module

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
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
import com.suqi8.oshin.ui.activity.components.SuperArrow
import com.suqi8.oshin.ui.activity.components.addline
import com.suqi8.oshin.ui.home.ModernSectionTitle
import com.suqi8.oshin.utils.GetAppIconAndName
import com.suqi8.oshin.utils.GetFuncRoute
import com.suqi8.oshin.utils.drawColoredShadow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.InputField
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SearchBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

// ========================================
// 主屏幕
// ========================================

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
    var searchExpanded by remember { mutableStateOf(false) }

    // 使用 ViewModel 保存的滚动位置
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = viewModel.scrollIndex,
        initialFirstVisibleItemScrollOffset = viewModel.scrollOffset
    )

    // 监听并保存滚动位置
    LaunchedEffect(listState) {
        snapshotFlow {
            listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
        }.collect { (index, offset) ->
            viewModel.saveScrollPosition(index, offset)
        }
    }

    // 同步搜索状态
    LaunchedEffect(uiState.isSearching) {
        searchExpanded = uiState.isSearching
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

        item(key = "searchbar") {
            ModuleSearchBar(
                query = uiState.searchQuery,
                expanded = searchExpanded,
                searchResults = uiState.searchResults,
                onQueryChange = viewModel::onSearchQueryChanged,
                onExpandedChange = {
                    searchExpanded = it
                    if (!it) {
                        viewModel.onSearchQueryChanged("")
                    }
                },
                navController = navController,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope
            )
        }

        if (!searchExpanded) {
            item {
                AppList(
                    appStyle = uiState.appStyle,
                    onStyleChange = viewModel::onAppStyleChanged,
                    moduleEntries = uiState.moduleEntries,
                    onAppNotFound = viewModel::onAppNotFound,
                    navController = navController,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    viewModel = viewModel
                )
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
                                titleColor = BasicComponentDefaults.titleColor(
                                    enabledColor = MiuixTheme.colorScheme.primary
                                ),
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
}

// ========================================
// Miuix 风格搜索栏
// ========================================

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ModuleSearchBar(
    query: String,
    expanded: Boolean,
    searchResults: List<SearchableItem>,
    onQueryChange: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    SearchBar(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        inputField = {
            InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = { onExpandedChange(false) },
                expanded = expanded,
                onExpandedChange = onExpandedChange
            )
        },
        outsideRightAction = {
            Text(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .clickable(
                        interactionSource = null,
                        indication = null
                    ) {
                        onExpandedChange(false)
                        onQueryChange("")
                    },
                text = stringResource(R.string.cancel),
                style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Bold),
                color = MiuixTheme.colorScheme.primary
            )
        },
        content = {
            SearchResultsList(
                results = searchResults,
                query = query,
                navController = navController,
                onItemClick = { onExpandedChange(false) },
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope
            )
        }
    )
}

// ========================================
// 应用列表
// ========================================

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun AppList(
    appStyle: Int,
    onStyleChange: () -> Unit,
    moduleEntries: List<ModuleEntry>,
    onAppNotFound: (String) -> Unit,
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: ModuleViewModel
) {
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

        ModuleCard {
            if (appStyle == 0) {
                // 网格布局
                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    moduleEntries.forEach { entry ->
                        AppItemFlow(
                            entry = entry,
                            onClick = { navController.navigate("feature/${entry.routeId}") },
                            onNotFound = onAppNotFound,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope,
                            viewModel = viewModel
                        )
                    }
                }
            } else {
                // 列表布局
                Column {
                    moduleEntries.forEachIndexed { index, entry ->
                        AppItemList(
                            entry = entry,
                            onClick = { navController.navigate("feature/${entry.routeId}") },
                            onNotFound = onAppNotFound,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope,
                            viewModel = viewModel
                        )
                        if (index < moduleEntries.size - 1) {
                            Divider()
                        }
                    }
                }
            }
        }
    }
}

// ========================================
// 搜索结果列表
// ========================================

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SearchResultsList(
    results: List<SearchableItem>,
    query: String,
    navController: NavController,
    onItemClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    com.suqi8.oshin.ui.activity.components.Card {
        if (results.isEmpty()) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "空空如也~",
                    fontFamily = FontFamily.Monospace,
                    color = MiuixTheme.colorScheme.onSurfaceVariantActions.copy(alpha = 0.6f)
                )
            }
        } else {
            Column {
                results.forEachIndexed { index, item ->
                    SearchResultItem(
                        item = item,
                        query = query,
                        onClick = {
                            navController.navigate("${item.route}?highlightKey=${item.key}")
                            onItemClick()
                        },
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    if (index < results.size - 1) {
                        addline()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SearchResultItem(
    item: SearchableItem,
    query: String,
    onClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val context = LocalContext.current
    val highlightColor = MiuixTheme.colorScheme.primary
    val titleAnnotated = highlightText(item.title, query, highlightColor)
    val summaryAnnotated = highlightText(item.summary, query, highlightColor)

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
                    animatedVisibilityScope = animatedVisibilityScope
                )
                .wrapContentHeight()
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = titleAnnotated,
                    fontSize = 16.sp,
                    color = MiuixTheme.colorScheme.onBackground
                )
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

// ========================================
// 应用项组件 - 列表样式
// ========================================

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun AppItemList(
    entry: ModuleEntry,
    onClick: () -> Unit,
    onNotFound: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: ModuleViewModel
) {
    val cachedInfo = remember(entry.packageName) {
        viewModel.getAppInfo(entry.packageName)
    }

    if (cachedInfo != null) {
        // 使用缓存
        AppItemListContent(
            appName = cachedInfo.name,
            icon = cachedInfo.icon,
            dominantColor = cachedInfo.dominantColor,
            packageName = entry.packageName,
            entry = entry,
            onClick = onClick,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope
        )
    } else {
        // 加载并缓存
        GetAppIconAndName(packageName = entry.packageName) { appName, icon ->
            if (appName != "noapp") {
                var dominantColor by remember { mutableStateOf<Color?>(null) }

                LaunchedEffect(icon) {
                    dominantColor = withContext(Dispatchers.IO) {
                        if (YukiHookAPI.Status.isModuleActive) {
                            extractDominantColor(icon)
                        } else {
                            Color.Red
                        }
                    }

                    dominantColor?.let { color ->
                        viewModel.cacheAppInfo(
                            entry.packageName,
                            AppInfo(appName, icon, color)
                        )
                    }
                }

                dominantColor?.let { color ->
                    AppItemListContent(
                        appName = appName,
                        icon = icon,
                        dominantColor = color,
                        packageName = entry.packageName,
                        entry = entry,
                        onClick = onClick,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
            } else {
                onNotFound(entry.packageName)
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun AppItemListContent(
    appName: String,
    icon: ImageBitmap,
    dominantColor: Color,
    packageName: String,
    entry: ModuleEntry,
    onClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    with(sharedTransitionScope) {
        Row(
            modifier = Modifier
                .clickable(onClick = onClick)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                colors = CardDefaults.defaultColors(color = dominantColor),
                modifier = Modifier
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = "item-${entry.routeId}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                    .drawColoredShadow(
                        dominantColor,
                        alpha = 1f,
                        borderRadius = 13.dp,
                        shadowRadius = 7.dp
                    )
            ) {
                Image(
                    bitmap = icon,
                    contentDescription = appName,
                    modifier = Modifier.size(45.dp)
                )
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
}

// ========================================
// 应用项组件 - 网格样式
// ========================================

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun AppItemFlow(
    entry: ModuleEntry,
    onClick: () -> Unit,
    onNotFound: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: ModuleViewModel
) {
    val cachedInfo = remember(entry.packageName) {
        viewModel.getAppInfo(entry.packageName)
    }

    if (cachedInfo != null) {
        // 使用缓存
        AppItemFlowContent(
            appName = cachedInfo.name,
            icon = cachedInfo.icon,
            dominantColor = cachedInfo.dominantColor,
            entry = entry,
            onClick = onClick,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope
        )
    } else {
        // 加载并缓存
        GetAppIconAndName(packageName = entry.packageName) { appName, icon ->
            if (appName != "noapp") {
                var dominantColor by remember { mutableStateOf<Color?>(null) }

                LaunchedEffect(icon) {
                    dominantColor = withContext(Dispatchers.IO) {
                        if (YukiHookAPI.Status.isModuleActive) {
                            extractDominantColor(icon)
                        } else {
                            Color.Red
                        }
                    }

                    dominantColor?.let { color ->
                        viewModel.cacheAppInfo(
                            entry.packageName,
                            AppInfo(appName, icon, color)
                        )
                    }
                }

                dominantColor?.let { color ->
                    AppItemFlowContent(
                        appName = appName,
                        icon = icon,
                        dominantColor = color,
                        entry = entry,
                        onClick = onClick,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
            } else {
                onNotFound(entry.packageName)
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun AppItemFlowContent(
    appName: String,
    icon: ImageBitmap,
    dominantColor: Color,
    entry: ModuleEntry,
    onClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    with(sharedTransitionScope) {
        Column(
            modifier = Modifier
                .width(65.dp)
                .clickable(onClick = onClick),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                colors = CardDefaults.defaultColors(color = dominantColor),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = "item-${entry.routeId}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    .drawColoredShadow(
                        dominantColor,
                        alpha = 1f,
                        borderRadius = 13.dp,
                        shadowRadius = 7.dp
                    )
            ) {
                Image(
                    bitmap = icon,
                    contentDescription = appName,
                    modifier = Modifier.size(50.dp)
                )
            }
            Text(
                text = appName,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                softWrap = false,
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 6.dp)
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(key = "title-${entry.routeId}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
            )
        }
    }
}

// ========================================
// UI 容器组件
// ========================================

@Composable
private fun ModuleCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        content = content
    )
}

@Composable
private fun Divider() {
    val context = LocalContext.current
    if (context.prefs("settings").getBoolean("addline", false)) {
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            thickness = 0.5.dp,
            color = MiuixTheme.colorScheme.dividerLine
        )
    }
}

// ========================================
// 工具函数
// ========================================

/**
 * 高亮搜索关键词
 */
@Composable
private fun highlightText(
    text: String,
    query: String,
    highlightColor: Color
): AnnotatedString {
    return buildAnnotatedString {
        if (query.isBlank() || !text.contains(query, ignoreCase = true)) {
            append(text)
            return@buildAnnotatedString
        }

        val regex = Regex(query, RegexOption.IGNORE_CASE)
        var lastIndex = 0

        regex.findAll(text).forEach { matchResult ->
            append(text.substring(lastIndex, matchResult.range.first))
            withStyle(
                style = SpanStyle(
                    color = highlightColor,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(matchResult.value)
            }
            lastIndex = matchResult.range.last + 1
        }

        if (lastIndex < text.length) {
            append(text.substring(lastIndex))
        }
    }
}

/**
 * 提取图片主色调
 */
private suspend fun extractDominantColor(icon: ImageBitmap): Color {
    return withContext(Dispatchers.IO) {
        Palette.from(icon.asAndroidBitmap())
            .generate()
            .dominantSwatch
            ?.rgb
            ?.let { Color(it) }
            ?: Color.White
    }
}
