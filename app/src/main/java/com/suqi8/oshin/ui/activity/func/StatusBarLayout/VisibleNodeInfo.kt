package com.suqi8.oshin.ui.activity.func.StatusBarLayout

/**
 * 用于在UI上展示的、包含层级信息的单个节点数据类。
 * @property node 原始的 ViewNode 数据。
 * @property level 节点在树中的层级深度。
 * @property uniqueKey 为 Compose LazyColumn 提供的唯一且稳定的键。
 */
data class VisibleNodeInfo(
    val node: ViewNode,
    val level: Int,
    val uniqueKey: String
)
