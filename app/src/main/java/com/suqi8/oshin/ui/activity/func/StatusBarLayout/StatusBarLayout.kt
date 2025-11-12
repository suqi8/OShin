package com.suqi8.oshin.ui.activity.func.StatusBarLayout

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.components.FunPage
import com.suqi8.oshin.ui.activity.components.SuperArrow
import com.suqi8.oshin.ui.mainscreen.home.ModernSectionTitle
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.basic.ArrowRight
import top.yukonga.miuix.kmp.icon.icons.useful.Play
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

private val ViewNode.expandableId: String
    get() = id.ifBlank { "group_${this.hashCode()}" }

/**
 * 视图控制器屏幕的主 Composable 函数。
 * @param navController 用于导航。
 * @param viewModel 关联的 ViewModel 实例。
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun StatusBarLayout(
    navController: NavController,
    viewModel: StatusBarLayoutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())

    // 控制配置对话框显示的状态
    val showDialog = remember { mutableStateOf(false) }
    // 存储当前被选中的节点，用于对话框
    var selectedNode by remember { mutableStateOf<ViewNode?>(null) }

    // 当对话框关闭时，发送指令取消视图高亮
    LaunchedEffect(showDialog.value) {
        if (!showDialog.value) {
            viewModel.clearHighlight()
        }
    }

    FunPage(
        appList = listOf("com.android.systemui"),
        navController = navController,
        scrollBehavior = scrollBehavior
    ) { paddingValues ->
        when {
            uiState.isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "loading")
            }
            uiState.viewTree == null -> Box(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.view_controller_load_failed))
            }
            else -> {
                // 使用标准的 LazyListState，不再需要排序功能
                val listState = rememberLazyListState()

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .overScrollVertical()
                        .scrollEndHaptic()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    contentPadding = paddingValues
                ) {
                    item {
                        ModernSectionTitle(
                            title = stringResource(id = R.string.status_bar_layout),
                            modifier = Modifier
                                .displayCutoutPadding()
                                .padding(top = 72.dp, bottom = 8.dp)
                        )
                    }
                    // 列表项直接绑定到 ViewModel 中的 visibleNodes
                    items(viewModel.visibleNodes, key = { it.uniqueKey }) { itemInfo ->
                        // 列表项的根布局，现在是一个简单的 Column
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MiuixTheme.colorScheme.background)
                        ) {
                            NodeItem(
                                node = itemInfo.node,
                                level = itemInfo.level,
                                isExpanded = uiState.expandedNodes.contains(itemInfo.node.expandableId),
                                onToggle = { viewModel.toggleNode(itemInfo.node.expandableId) },
                                onItemClick = { clickedNode ->
                                    if (clickedNode.id.isNotBlank()) {
                                        selectedNode = clickedNode
                                        showDialog.value = true
                                        viewModel.highlightView(clickedNode.hashCodeValue)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // 当 selectedNode 不为空时，渲染配置对话框
    selectedNode?.let { node ->
        ViewConfigDialog(
            show = showDialog.value,
            node = node,
            currentConfigMode = uiState.configs[node.id] ?: ViewConfig.MODE_NORMAL,
            onDismiss = { showDialog.value = false },
            onConfigChange = { mode ->
                viewModel.updateConfig(node.id, mode)
                showDialog.value = false
            }
        )
    }
}

/**
 * 递归地渲染视图树的子节点。
 * @param nodes 要渲染的子节点列表。
 * @param level 当前的层级深度。
 * @param uiState 全局UI状态，用于获取展开信息。
 * @param onNodeClick 节点点击事件的回调。
 * @param onToggle 节点展开/折叠事件的回调。
 */
@Composable
private fun RenderSubTree(
    nodes: List<ViewNode>,
    level: Int,
    uiState: StatusBarLayoutUiState,
    onNodeClick: (ViewNode) -> Unit,
    onToggle: (String) -> Unit
) {
    nodes.forEach { node ->
        NodeItem(
            node = node,
            level = level,
            isExpanded = uiState.expandedNodes.contains(node.expandableId),
            onToggle = { onToggle(node.expandableId) },
            onItemClick = onNodeClick
        )
        // 如果子节点也是展开状态，则继续递归渲染其子树
        if (uiState.expandedNodes.contains(node.expandableId)) {
            RenderSubTree(
                nodes = node.children,
                level = level + 1,
                uiState = uiState,
                onNodeClick = onNodeClick,
                onToggle = onToggle
            )
        }
    }
}

/**
 * 列表中的单个节点项 Composable。
 * @param node 要渲染的节点数据。
 * @param level 节点的层级，用于计算缩进。
 * @param isExpanded 节点当前是否为展开状态。
 * @param onToggle 点击展开/折叠箭头时的回调。
 * @param onItemClick 点击整个节点项时的回调。
 */
@Composable
private fun NodeItem(
    node: ViewNode,
    level: Int,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onItemClick: (ViewNode) -> Unit
) {
    if (node.id.isBlank() && node.children.isEmpty() && node.type.isBlank()) return

    val simpleClassName = node.type.substringAfterLast('.').ifBlank { "ViewGroup" }
    val simpleId = node.id.ifBlank { "No ID" }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(node) }
            .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 根据层级显示缩进
        HierarchyIndicator(level = level)

        // 展开/折叠箭头的旋转动画
        val rotationAngle by animateFloatAsState(
            targetValue = if (isExpanded) 90f else 0f,
            animationSpec = tween(durationMillis = 200),
            label = "arrowRotation"
        )

        // 如果节点有子节点，则显示箭头
        if (node.children.isNotEmpty()) {
            Icon(
                imageVector = MiuixIcons.Basic.ArrowRight,
                contentDescription = "Toggle",
                modifier = Modifier
                    .size(24.dp)
                    .rotate(rotationAngle)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onToggle() }
            )
        } else {
            // 如果没有子节点，用空白占位以保持对齐
            Spacer(modifier = Modifier.width(24.dp))
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 显示节点信息（类型、ID、尺寸）
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = simpleClassName,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = simpleId,
                    fontSize = 12.sp,
                    color = MiuixTheme.colorScheme.onBackgroundVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                val dimensions = if (node.width > 0 && node.height > 0) {
                    " | ${node.width}x${node.height}dp"
                } else ""
                Text(
                    text = dimensions,
                    fontSize = 12.sp,
                    color = MiuixTheme.colorScheme.onBackgroundVariant,
                    maxLines = 1
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))

        // 显示可见性标记 (V, I, G)
        VisibilityBadge(visibility = node.visibility)
    }
}

/**
 * 一个简单的 Composable，用于根据层级生成空白间隔，实现缩进效果。
 */
@Composable
private fun HierarchyIndicator(level: Int, spacePerLevel: Dp = 12.dp) {
    Spacer(modifier = Modifier.width(level * spacePerLevel))
}

/**
 * 显示视图可见性状态的徽章 (V/I/G)。
 */
@Composable
fun VisibilityBadge(visibility: String) {
    val (text, color) = when (visibility) {
        "Visible" -> "V" to Color(0xFF4CAF50)
        "Invisible" -> "I" to Color(0xFFFFA000)
        "Gone" -> "G" to Color(0xFFF44336)
        else -> "" to Color.Transparent
    }
    if (text.isNotEmpty()) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 10.sp,
            modifier = Modifier
                .background(color, shape = RoundedCornerShape(4.dp))
                .padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

/**
 * 用于配置单个视图显示模式的对话框。
 */
@Composable
private fun ViewConfigDialog(
    show: Boolean,
    node: ViewNode,
    currentConfigMode: Int,
    onDismiss: () -> Unit,
    onConfigChange: (Int) -> Unit
) {
    val configOptions = listOf(
        R.string.view_controller_mode_normal to ViewConfig.MODE_NORMAL,
        R.string.view_controller_mode_visible to ViewConfig.MODE_ALWAYS_VISIBLE,
        R.string.view_controller_mode_hidden to ViewConfig.MODE_ALWAYS_HIDDEN,
        R.string.view_controller_mode_invisible to ViewConfig.MODE_ALWAYS_INVISIBLE
    )

    val showState = remember { mutableStateOf(true) }
    LaunchedEffect(show) { showState.value = show }

    SuperDialog(
        show = showState,
        title = node.type.substringAfterLast('.').ifBlank { "ViewGroup" },
        summary = node.id,
        onDismissRequest = onDismiss
    ) {
        Column {
            configOptions.forEach { (textResId, mode) ->
                val text = stringResource(textResId)
                SuperArrow(
                    title = text,
                    leftAction = {
                        if (currentConfigMode == mode) {
                            Icon(
                                imageVector = MiuixIcons.Useful.Play,
                                contentDescription = "Selected",
                                tint = MiuixTheme.colorScheme.primary
                            )
                        }
                    },
                    onClick = { onConfigChange(mode) }
                )
            }
        }
    }
}
