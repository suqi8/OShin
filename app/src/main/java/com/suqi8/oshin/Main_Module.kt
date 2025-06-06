package com.suqi8.oshin

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlendModeColorFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.suqi8.oshin.ui.activity.funlistui.addline
import com.suqi8.oshin.ui.activity.module.AppInfoItem
import com.suqi8.oshin.ui.activity.module.ModuleUiState
import com.suqi8.oshin.ui.activity.module.ModuleViewModel
import com.suqi8.oshin.utils.drawColoredShadow
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Search
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape
import top.yukonga.miuix.kmp.utils.overScrollVertical


// =================================================================================
// 主屏幕 Composable (The Main Screen)
// =================================================================================

@Composable
fun Main_Module(
    padding: PaddingValues,
    topAppBarScrollBehavior: ScrollBehavior,
    navController: NavController,
    viewModel: ModuleViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    AnimatedContent(
        targetState = uiState.isSearchExpanded,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith
                    fadeOut(animationSpec = tween(300))
        }
    ) { isSearching ->
        if (isSearching) {
            SearchScreenContent(
                uiState = uiState,
                padding = padding,
                onSearchQueryChanged = viewModel::onSearchQueryChanged,
                onExpandedChanged = viewModel::onSearchExpandedChanged,
                onFeatureClick = { category ->
                    viewModel.onSearchExpandedChanged(false)
                    navController.navigate(category)
                }
            )
        } else {
            MainScreenContent(
                uiState = uiState,
                padding = padding,
                topAppBarScrollBehavior = topAppBarScrollBehavior,
                onSearchQueryChanged = viewModel::onSearchQueryChanged,
                onExpandedChanged = viewModel::onSearchExpandedChanged,
                onAppStyleChanged = viewModel::onAppStyleChanged,
                navController = navController
            )
        }
    }
}


@Composable
private fun MainScreenContent(
    uiState: ModuleUiState,
    padding: PaddingValues,
    topAppBarScrollBehavior: ScrollBehavior,
    onSearchQueryChanged: (String) -> Unit,
    onExpandedChanged: (Boolean) -> Unit,
    onAppStyleChanged: () -> Unit,
    navController: NavController
) {
    Column(modifier = Modifier.fillMaxSize().padding(top = padding.calculateTopPadding())) {
        // 搜索栏只作为入口
        SearchBar(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            query = uiState.searchValue,
            onQueryChange = onSearchQueryChanged,
            onSearch = {}, // 在主页点击键盘搜索不做任何事
            expanded = uiState.isSearchExpanded,
            onExpandedChange = onExpandedChanged, // 点击或获取焦点时，会触发状态改变，切换到SearchScreenContent
            label = stringResource(R.string.Search),
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize().nestedScroll(topAppBarScrollBehavior.nestedScrollConnection).overScrollVertical(),
            contentPadding = PaddingValues(bottom = padding.calculateBottomPadding())
        ) {
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp), horizontalAlignment = Alignment.End) {
                    Text(text = stringResource(R.string.switch_style), color = MiuixTheme.colorScheme.primary, fontSize = 12.sp, modifier = Modifier.clickable(onClick = onAppStyleChanged))
                }
                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).padding(bottom = 6.dp)) {
                    AnimatedContent(targetState = uiState.appStyle, label = "AppStyleAnimation") { style ->
                        if (style == 0) {
                            AppFlowGrid(appList = uiState.appList, navController = navController)
                        } else {
                            AppList(appList = uiState.appList, navController = navController)
                        }
                    }
                }
            }
            item {
                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)) {
                    SuperArrow(title = stringResource(R.string.app_not_found_in_list), onClick = { navController.navigate("hide_apps_notice") })
                }
            }
        }
    }
}

@Composable
private fun SearchScreenContent(
    uiState: ModuleUiState,
    padding: PaddingValues,
    onSearchQueryChanged: (String) -> Unit,
    onExpandedChanged: (Boolean) -> Unit,
    onFeatureClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(top = padding.calculateTopPadding())) {
        SearchBar(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp),
            query = uiState.searchValue,
            onQueryChange = onSearchQueryChanged,
            onSearch = { /* Can perform search action */ },
            expanded = uiState.isSearchExpanded,
            onExpandedChange = onExpandedChanged,
            isSearchScreen = true,
            outsideRightAction = {
                Text(
                    modifier = Modifier.padding(start = 12.dp).clickable { onExpandedChanged(false) },
                    text = stringResource(R.string.cancel),
                    color = MiuixTheme.colorScheme.primary
                )
            }
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            if (uiState.filteredFeatures.isEmpty() && uiState.searchValue.isNotBlank()) {
                item { Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) { Text(text = "空空如也~") } }
            }
            itemsIndexed(uiState.filteredFeatures, key = { _, feature -> feature.title + feature.summary }) { index, feature ->
                val summaryText = remember(feature.summary, feature.displayPath) {
                    if (feature.summary != null) "${feature.summary}\n${feature.displayPath}" else feature.displayPath
                }
                SearchList(
                    title = highlightMatches(feature.title, uiState.searchValue),
                    summary = highlightMatches(summaryText, uiState.searchValue),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onFeatureClick(feature.category) }
                )
                if (index < uiState.filteredFeatures.size - 1) {
                    addline()
                }
            }
        }
    }
}

@Composable
private fun AppFlowGrid(appList: List<AppInfoItem>, navController: NavController) {
    FlowRow(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize().padding(vertical = 10.dp)) {
        appList.forEach { app ->
            FunctionAppFlow(app = app, navController = navController)
        }
    }
}

@Composable
private fun AppList(appList: List<AppInfoItem>, navController: NavController) {
    Column {
        appList.forEachIndexed { index, app ->
            FunctionApp(app = app, navController = navController)
            if (index < appList.size - 1) {
                addline()
            }
        }
    }
}

@Composable
private fun FunctionApp(app: AppInfoItem, navController: NavController) {
    Row(
        modifier = Modifier.clickable { navController.navigate(app.activityName) }.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(color = app.dominantColor, modifier = Modifier
            .padding(16.dp)
            .drawColoredShadow(
                color = app.dominantColor,
                alpha = 1f,
                borderRadius = 13.dp,
                shadowRadius = 7.dp,
                roundedRect = false
            )
        ) {
            if (app.icon != null) {
                Image(bitmap = app.icon, contentDescription = app.appName, modifier = Modifier.size(45.dp))
            } else {
                Spacer(modifier = Modifier.size(45.dp).background(Color.Gray))
            }
        }
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(text = app.appName)
            Text(
                text = app.packageName,
                fontSize = MiuixTheme.textStyles.subtitle.fontSize,
                fontWeight = FontWeight.Medium,
                color = MiuixTheme.colorScheme.onBackgroundVariant
            )
        }
    }
}

@Composable
private fun FunctionAppFlow(app: AppInfoItem, navController: NavController) {
    Column(
        modifier = Modifier.clickable { navController.navigate(app.activityName) }.width(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            color = app.dominantColor,
            modifier = Modifier.padding(top = 10.dp)
                .drawColoredShadow(app.dominantColor, 1f, borderRadius = 13.dp, shadowRadius = 7.dp, roundedRect = false)
        ) {
            if (app.icon != null) {
                Image(bitmap = app.icon, contentDescription = app.appName, modifier = Modifier.size(50.dp))
            } else {
                Spacer(modifier = Modifier.size(50.dp).background(Color.Gray))
            }
        }
        Text(
            text = app.appName,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            softWrap = false,
            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
        )
    }
}


// =================================================================================
// 您的自定义UI组件和辅助函数 (Your Custom UI & Helper Functions)
// =================================================================================

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    isSearchScreen: Boolean = false,
    outsideRightAction: @Composable (() -> Unit)? = null,
) {
    val focusRequester = remember { FocusRequester() }

    // 在搜索屏幕，让输入框自动获取焦点
    LaunchedEffect(isSearchScreen) {
        if (isSearchScreen) {
            focusRequester.requestFocus()
        }
    }

    Surface(modifier = modifier.zIndex(1f), color = Color.Transparent) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(1f)) {
                InputField(
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = onSearch,
                    expanded = expanded,
                    onExpandedChange = onExpandedChange,
                    label = label,
                    focusRequester = focusRequester, // 传入FocusRequester
                    leadingIcon = {
                        Image(imageVector = MiuixIcons.Useful.Search, contentDescription = null, colorFilter = BlendModeColorFilter(MiuixTheme.colorScheme.onSurfaceContainer, BlendMode.SrcIn), modifier = Modifier.padding(horizontal = 12.dp))
                    }
                )
            }
            AnimatedVisibility(visible = expanded) {
                outsideRightAction?.invoke()
            }
        }
    }

    BackHandler(enabled = expanded) { onExpandedChange(false) }
}

@Composable
fun InputField(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    focusRequester: FocusRequester, // 接收FocusRequester
    modifier: Modifier = Modifier,
    label: String = "",
    leadingIcon: @Composable (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val focusManager = LocalFocusManager.current

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged { if (it.isFocused && !expanded) onExpandedChange(true) } // 仅在未展开时通过焦点来展开
            .semantics { onClick { focusRequester.requestFocus(); true } },
        singleLine = true,
        textStyle = MiuixTheme.textStyles.main,
        cursorBrush = SolidColor(MiuixTheme.colorScheme.primary),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch(query) }),
        interactionSource = interactionSource,
        decorationBox = @Composable { innerTextField ->
            val shape = remember { SmoothRoundedCornerShape(50.dp) }
            Box(
                modifier = Modifier.background(color = MiuixTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.75f), shape = shape),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    leadingIcon?.invoke()
                    Box(modifier = Modifier.padding(vertical = 12.dp).padding(end = 12.dp)) {
                        if (query.isEmpty() && !focused) {
                            Text(text = label, color = MiuixTheme.colorScheme.onSurfaceContainerHigh)
                        }
                        innerTextField()
                    }
                }
            }
        }
    )

    LaunchedEffect(expanded) {
        if (!expanded && focused) {
            focusManager.clearFocus()
        }
    }
}

fun highlightMatches(text: String, query: String): AnnotatedString {
    if (query.isBlank()) return AnnotatedString(text)
    val builder = AnnotatedString.Builder()
    var lastIndex = 0
    val regex = Regex(query, RegexOption.IGNORE_CASE)
    regex.findAll(text).forEach { matchResult ->
        builder.append(text.substring(lastIndex, matchResult.range.first))
        builder.pushStyle(SpanStyle(color = Color.Red, fontWeight = FontWeight.Bold))
        builder.append(matchResult.value)
        builder.pop()
        lastIndex = matchResult.range.last + 1
    }
    builder.append(text.substring(lastIndex))
    return builder.toAnnotatedString()
}

// 假设的 SearchList Composable
@Composable
fun SearchList(title: AnnotatedString, summary: AnnotatedString, modifier: Modifier, onClick: () -> Unit) {
    Column(modifier = modifier.clickable(onClick = onClick).padding(16.dp)) {
        Text(text = title)
        if (summary.isNotBlank()) {
            Text(text = summary, fontSize = 12.sp, color = Color.Gray)
        }
    }
}
