package com.suqi8.oshin.ui.activity.feature

import android.content.pm.PackageManager
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.suqi8.oshin.models.Action
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.Dropdown
import com.suqi8.oshin.models.NoEnable
import com.suqi8.oshin.models.Picture
import com.suqi8.oshin.models.PlainText
import com.suqi8.oshin.models.RelatedLinks
import com.suqi8.oshin.models.ScreenItem
import com.suqi8.oshin.models.Slider
import com.suqi8.oshin.models.StringInput
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch
import com.suqi8.oshin.models.Title
import com.suqi8.oshin.models.TitledScreenItem
import com.suqi8.oshin.ui.activity.components.Card
import com.suqi8.oshin.ui.activity.components.FunNoEnable
import com.suqi8.oshin.ui.activity.components.FunPage
import com.suqi8.oshin.ui.activity.components.addline
import com.suqi8.oshin.ui.activity.components.funArrow
import com.suqi8.oshin.ui.activity.components.funDropdown
import com.suqi8.oshin.ui.activity.components.funPicSele
import com.suqi8.oshin.ui.activity.components.funSlider
import com.suqi8.oshin.ui.activity.components.funString
import com.suqi8.oshin.ui.activity.components.funSwitch
import com.suqi8.oshin.ui.activity.components.wantFind
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

/**
 * 通用的功能页面渲染器。
 * 它会根据 ViewModel 提供的 PageDefinition 动态构建整个页面。
 */
@Composable
fun featureScreen(
    navController: NavController,
    viewModel: featureViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pageDef = uiState.pageDefinition
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val topAppBarState = MiuixScrollBehavior(rememberTopAppBarState())

    // 自动滚动逻辑
    LaunchedEffect(uiState.isLoading, uiState.highlightKey) {
        if (!uiState.isLoading && uiState.highlightKey != null && pageDef != null) {
            // 计算目标 item 所在的 PageItem (通常是 CardDefinition) 的索引
            val targetIndex = uiState.highlightKey?.let { key ->
                pageDef.items.indexOfFirst { pageItem ->
                    if (pageItem is CardDefinition) {
                        pageItem.items.any { (it as? TitledScreenItem)?.key == key }
                    } else {
                        false // RelatedLinks 等其他类型暂不支持被高亮定位
                    }
                }
            } ?: -1

            if (targetIndex != -1) {
                coroutineScope.launch {
                    // 滚动到目标卡片
                    listState.animateScrollToItem(index = targetIndex)
                }
            }
        }
    }

    // 处理加载状态
    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // 处理页面定义未找到的情况
    if (pageDef == null) {
        FunPage(title = "Error", navController = navController, scrollBehavior = topAppBarState) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Page definition not found.")
            }
        }
        return
    }

    // 渲染主页面
    FunPage(
        title = resolveTitle(title = pageDef.title),
        appList = pageDef.appList,
        navController = navController,
        scrollBehavior = topAppBarState
    ) { padding ->
        val itemStates = uiState.itemStates
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .overScrollVertical()
                .scrollEndHaptic()
                .nestedScroll(topAppBarState.nestedScrollConnection),
            contentPadding = padding
        ) {
            itemsIndexed(pageDef.items) { _, pageItem ->
                when (pageItem) {
                    is CardDefinition -> {
                        pageItem.titleRes?.let {
                            SmallTitle(text = stringResource(it))
                        }
                        Card {
                            Column {
                                val itemCount = pageItem.items.size
                                pageItem.items.forEachIndexed { itemIndex, item ->
                                    // 可见性判断在这里，针对卡片内部的每一个 item
                                    val isVisible = viewModel.evaluateCondition(item.condition, itemStates)
                                    AnimatedVisibility(visible = isVisible) {
                                        Column {
                                            val isHighlighted = (item as? TitledScreenItem)?.key == uiState.highlightKey

                                            val topPadding = when {
                                                itemCount == 1 -> 4.dp
                                                itemIndex == 0 -> 2.dp
                                                else -> 0.dp
                                            }
                                            val bottomPadding = when {
                                                itemCount == 1 -> 4.dp
                                                itemIndex == itemCount - 1 -> 2.dp
                                                else -> 0.dp
                                            }
                                            val itemPadding = PaddingValues(top = topPadding, bottom = bottomPadding)

                                            AnimatedVisibility(visible = isVisible) {
                                                RenderScreenItem(
                                                    item = item,
                                                    viewModel = viewModel,
                                                    navController = navController,
                                                    isHighlighted = isHighlighted,
                                                    paddingValues = itemPadding
                                                )

                                                if (itemIndex < pageItem.items.lastIndex) {
                                                    addline()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    is RelatedLinks -> {
                        // RelatedLinks 作为一个整体，有自己的显示条件
                        val isVisible = viewModel.evaluateCondition(pageItem.condition, itemStates)
                        AnimatedVisibility(visible = isVisible) {
                            wantFind(
                                links = pageItem.links,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 根据 ScreenItem 的具体类型，选择并渲染对应的 "fun" 组件。
 */
@Composable
private fun RenderScreenItem(
    item: ScreenItem,
    viewModel: featureViewModel,
    navController: NavController,
    isHighlighted: Boolean,
    paddingValues: PaddingValues
) {
    val itemStates by viewModel.uiState.collectAsState()
    val highlightColor = remember { Animatable(Color.Transparent) }
    val coroutineScope = rememberCoroutineScope()
    val highlightColorPrimary = MiuixTheme.colorScheme.primary.copy(alpha = 0.25f)

    LaunchedEffect(isHighlighted) {
        if (isHighlighted) {
            coroutineScope.launch {
                repeat(2) { // 闪两次
                    // 动画亮起
                    highlightColor.animateTo(
                        targetValue = highlightColorPrimary,
                        animationSpec = tween(
                            durationMillis = 1000,  // 亮起速度
                            delayMillis = 0
                        )
                    )
                    // 动画熄灭
                    highlightColor.animateTo(
                        targetValue = Color.Transparent,
                        animationSpec = tween(
                            durationMillis = 1000,  // 渐灭速度
                            delayMillis = 100      // 熄灭前停顿
                        )
                    )
                }
            }
        }
    }

    Column(modifier = Modifier.background(highlightColor.value)) {
        when (item) {
            is Switch -> {
                val checked = itemStates.itemStates[item.key] as? Boolean ?: item.defaultValue
                funSwitch(
                    title = resolveTitle(title = item.title),
                    summary = item.summary?.let { stringResource(it) },
                    checked = checked,
                    externalPadding = paddingValues,
                    onCheckedChange = { newValue -> viewModel.updateState(item.key, newValue) }
                )
            }
            is Slider -> {
                val value = itemStates.itemStates[item.key] as? Float ?: item.defaultValue
                funSlider(
                    title = resolveTitle(title = item.title),
                    summary = item.summary?.let { stringResource(it) },
                    value = value,
                    valueRange = item.valueRange,
                    onValueChange = { newValue -> viewModel.updateState(item.key, newValue) },
                    unit = item.unit,
                    decimalPlaces = item.decimalPlaces,
                    externalPadding = paddingValues,
                )
            }
            is Dropdown -> {
                val selectedIndex = itemStates.itemStates[item.key] as? Int ?: item.defaultValue
                funDropdown(
                    title = resolveTitle(title = item.title),
                    summary = item.summary?.let { stringResource(it) },
                    selectedIndex = selectedIndex,
                    options = stringArrayResource(id = item.optionsRes).toList(),
                    externalPadding = paddingValues,
                    onSelectedIndexChange = { newIndex -> viewModel.updateState(item.key, newIndex) }
                )
            }
            is Action -> {
                funArrow(
                    title = resolveTitle(title = item.title),
                    summary = item.summary?.let { stringResource(it) },
                    externalPadding = paddingValues,
                    onClick = { navController.navigate("feature/${item.route}") }
                )
            }
            is NoEnable -> FunNoEnable()
            is Picture -> {
                // 从 itemStates 中获取当前显示的图片
                val imageBitmap = itemStates.itemStates[item.key] as? ImageBitmap
                funPicSele(
                    title = resolveTitle(title = item.title),
                    summary = item.summary?.let { stringResource(it) },
                    imageBitmap = imageBitmap,
                    externalPadding = paddingValues,
                    onImageSelected = { uri ->
                        // 当用户选择了新图片，通知 ViewModel 处理
                        viewModel.saveImageFromUri(item.key, item.targetPath, uri)
                    }
                )
            }
            is StringInput -> {
                val value = itemStates.itemStates[item.key] as? String ?: item.defaultValue
                funString(
                    title = resolveTitle(title = item.title),
                    summary = item.summary?.let { stringResource(it) },
                    value = value,
                    externalPadding = paddingValues,
                    onValueChange = { newValue -> viewModel.updateState(item.key, newValue) },
                    nullable = item.nullable
                )
            }
        }
    }
}

/**
 * 辅助 Composable，用于将灵活的 Title 模型解析为最终显示的字符串。
 */
@Composable
private fun resolveTitle(title: Title): String {
    return when (title) {
        is StringResource -> stringResource(title.id)
        is PlainText -> title.text
        is AppName -> getAppName(title.packageName)
    }
}

/**
 * 辅助 Composable，用于安全地获取应用名称。
 */
@Composable
private fun getAppName(packageName: String): String {
    val context = LocalContext.current
    return try {
        val pm = context.packageManager
        val appInfo = pm.getApplicationInfo(packageName, 0)
        pm.getApplicationLabel(appInfo).toString()
    } catch (e: PackageManager.NameNotFoundException) {
        packageName // 如果找不到应用，返回包名作为兜底
    }
}
