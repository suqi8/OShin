package com.suqi8.oshin.ui.module

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import com.kyant.capsule.ContinuousRoundedRectangle
import com.suqi8.oshin.R
import com.suqi8.oshin.models.ModuleEntry
import com.suqi8.oshin.ui.activity.components.BasicComponentDefaults
import com.suqi8.oshin.ui.activity.components.SuperArrow
import com.suqi8.oshin.ui.activity.components.addline
import com.suqi8.oshin.ui.home.ModernSectionTitle
import com.suqi8.oshin.ui.nav.path.NavPath
import com.suqi8.oshin.ui.nav.transition.NavTransitionType
import com.suqi8.oshin.ui.nav.ui.NavStackScope
import com.suqi8.oshin.utils.drawColoredShadow
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.InputField
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SearchBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperBottomSheetDefaults.cornerRadius
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
    navPath: NavPath,
    navStackScope: NavStackScope,
    padding: PaddingValues,
    viewModel: ModuleViewModel = hiltViewModel()
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
                navPath = navPath,
                navStackScope = navStackScope
            )
        }

        if (!searchExpanded) {
            item {
                AppList(
                    appStyle = uiState.appStyle,
                    onStyleChange = viewModel::onAppStyleChanged,
                    moduleEntries = uiState.moduleEntries,
                    navPath = navPath,
                    navStackScope = navStackScope,
                    viewModel = viewModel
                )
            }

            item {
                AnimatedVisibility(visible = uiState.notInstalledApps.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                    ) {
                        SuperArrow(
                            title = stringResource(R.string.app_not_found_in_list),
                            titleColor = BasicComponentDefaults.titleColor(
                                enabledColor = MiuixTheme.colorScheme.primary
                            ),
                            onClick = {
                                val packages = uiState.notInstalledApps.joinToString(",")
                                //navController.navigate("hide_apps_notice/$packages")
                            }
                        )
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
    searchResults: List<SearchResultUiItem>,
    onQueryChange: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    navPath: NavPath,
    navStackScope: NavStackScope,
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
                navPath = navPath,
                navStackScope = navStackScope,
                onItemClick = { onExpandedChange(false) }
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
    navPath: NavPath,
    navStackScope: NavStackScope,
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
                            navStackScope = navStackScope,
                            onClick = {
                                navPath.push(
                                    item = "feature/${entry.routeId}",
                                    navTransitionType = NavTransitionType.Zoom // 或者你想要的任何动画
                                ) },
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
                            navStackScope = navStackScope,
                            onClick = {
                                navPath.push(
                                    item = "feature/${entry.routeId}",
                                    navTransitionType = NavTransitionType.Zoom) },
                            viewModel = viewModel
                        )
                        if (index < moduleEntries.size - 1) {
                            addline()
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
    results: List<SearchResultUiItem>,
    query: String,
    navPath: NavPath,
    navStackScope: NavStackScope,
    onItemClick: () -> Unit
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
                            val routeWithParams = "${item.item.route}?highlightKey=${item.item.key}"

                            // 2. 将整个字符串作为 item push 到 NavPath
                            navPath.push(
                                item = routeWithParams,
                                navTransitionType = NavTransitionType.Zoom // 或者你想要的任何动画
                            )
                            onItemClick()
                        }
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
    item: SearchResultUiItem,
    query: String,
    onClick: () -> Unit
) {
    val highlightColor = MiuixTheme.colorScheme.primary

    val searchableItem = item.item
    val featurePath = item.formattedRoute // <-- 直接使用

    val titleAnnotated = highlightText(searchableItem.title, query, highlightColor)
    val summaryAnnotated = highlightText(searchableItem.summary, query, highlightColor)

    Row(
        modifier = Modifier
            .fillMaxWidth()
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
            if (searchableItem.summary.isNotBlank()) {
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

// ========================================
// 应用项组件 - 列表样式
// ========================================

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun AppItemList(
    entry: ModuleEntry,
    navStackScope: NavStackScope,
    onClick: () -> Unit,
    viewModel: ModuleViewModel
) {
    val defaultColor = MiuixTheme.colorScheme.primary

    val appUiInfo by remember(entry.packageName) {
        derivedStateOf { viewModel.getAppInfo(entry.packageName) }
    }

    LaunchedEffect(entry.packageName, defaultColor) {
        if (appUiInfo == null) {
            viewModel.loadAppInfoWithColor(entry.packageName, defaultColor)
        }
    }

    // 3. 观察 "not found" 状态
    val isNotInstalled by viewModel.uiState.collectAsState().let { state ->
        remember(entry.packageName) {
            derivedStateOf { state.value.notInstalledApps.contains(entry.packageName) }
        }
    }

    if (!isNotInstalled) {
        if (appUiInfo != null) {
            // 状态 1: 已加载
            AppItemListContent(
                appName = appUiInfo!!.name,
                icon = appUiInfo!!.icon,
                dominantColor = appUiInfo!!.dominantColor,
                packageName = entry.packageName,
                entry = entry,
                navStackScope = navStackScope,
                onClick = onClick
            )
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
    navStackScope: NavStackScope,
    onClick: () -> Unit
) {
    val targetPath = "feature/${entry.routeId}"
    Row(
        modifier = navStackScope.zoomTransitionSource(
            modifier = Modifier.fillMaxWidth(), // 1. 将原有的 Modifier 传入
            path = targetPath,                  // 2. 提供目标路径
            color = dominantColor,              // 3. 提供背景颜色
            shape = ContinuousRoundedRectangle(cornerRadius)          // 4. 提供形状 (假设 Miuix Card 有这个属性)
        )
            .clickable(onClick = onClick)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            colors = CardDefaults.defaultColors(color = dominantColor),
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                .drawColoredShadow(
                    dominantColor,
                    alpha = 1f,
                    borderRadius = 13.dp,
                    shadowRadius = 7.dp,
                    roundedRect = false
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
                text = appName
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

// ========================================
// 应用项组件 - 网格样式
// ========================================

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun AppItemFlow(
    entry: ModuleEntry,
    navStackScope: NavStackScope,
    onClick: () -> Unit,
    viewModel: ModuleViewModel
) {
    val defaultColor = MiuixTheme.colorScheme.primary

    // 1. 从 ViewModel 的缓存中观察状态
    val appUiInfo by remember(entry.packageName) {
        derivedStateOf { viewModel.getAppInfo(entry.packageName) }
    }

    // 2. 触发 ViewModel 加载数据
    LaunchedEffect(entry.packageName, defaultColor) {
        if (appUiInfo == null) {
            viewModel.loadAppInfoWithColor(entry.packageName, defaultColor)
        }
    }

    // 3. 观察 "not found" 状态
    val isNotInstalled by viewModel.uiState.collectAsState().let { state ->
        remember(entry.packageName) {
            derivedStateOf { state.value.notInstalledApps.contains(entry.packageName) }
        }
    }

    // 4. 渲染 UI
    if (!isNotInstalled) {
        if (appUiInfo != null) {
            // 状态 1: 已加载
            AppItemFlowContent(
                appName = appUiInfo!!.name,
                icon = appUiInfo!!.icon,
                dominantColor = appUiInfo!!.dominantColor,
                entry = entry,
                navStackScope = navStackScope,
                onClick = onClick
            )
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
    navStackScope: NavStackScope,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(65.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val targetPath = "feature/${entry.routeId}"
        Card(
            colors = CardDefaults.defaultColors(color = dominantColor),
            modifier = navStackScope.zoomTransitionSource(
                modifier = Modifier.width(65.dp),
                path = targetPath,
                color = dominantColor,
                shape = ContinuousRoundedRectangle(cornerRadius) // 同样，如果不存在就用 RoundedCornerShape 替代
            )
                .padding(top = 10.dp)
                .drawColoredShadow(
                    dominantColor,
                    alpha = 1f,
                    borderRadius = 13.dp,
                    shadowRadius = 7.dp,
                    roundedRect = false
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
        )
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
