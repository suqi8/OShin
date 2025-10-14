package com.suqi8.oshin.ui.activity.func.StatusBarLayout

/**
 * 定义 ViewControllerScreen 的整体UI状态。
 * @property isLoading 是否正在加载视图树。
 * @property viewTree 根 ViewNode，代表整个视图树结构。
 * @property configs 当前已保存的视图配置 Map (Key: viewId, Value: mode)。
 * @property expandedNodes 记录当前已展开的节点ID集合。
 */
data class StatusBarLayoutUiState(
    val isLoading: Boolean = true,
    val viewTree: ViewNode? = null,
    val configs: Map<String, Int> = emptyMap(),
    val expandedNodes: Set<String> = emptySet()
)
