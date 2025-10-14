package com.suqi8.oshin.ui.activity.func.StatusBarLayout

import android.graphics.Rect

// 在你的 com.suqi8.oshin.ui.activity.func.StatusBarLayout.ViewNode 文件中
data class ViewNode(
    val id: String,
    val type: String,
    val children: List<ViewNode>,

    // ✨ [新增] 添加可见性和边界信息
    val visibility: String, // "Visible", "Invisible", "Gone"
    val bounds: Rect? // Rect 是一个可以包含 left, top, right, bottom 的类
) {
    // 方便计算宽高的辅助属性
    val width: Int get() = bounds?.width() ?: 0
    val height: Int get() = bounds?.height() ?: 0
}
