package com.suqi8.oshin.models

import com.google.gson.annotations.SerializedName

/**
 * 定义一个视图的显示配置数据模型。
 *
 * @property id 视图的资源 ID 字符串，与 ViewNode.id 对应。
 * @property mode 视图的显示模式。
 */
data class ViewConfig(
    @SerializedName("id")
    val id: String,
    @SerializedName("mode")
    val mode: Int
) {
    companion object {
        /** 默认模式，遵循原始视图状态。 */
        const val MODE_NORMAL = 0

        /** 强制显示模式 (View.VISIBLE)。 */
        const val MODE_ALWAYS_VISIBLE = 1

        /** 强制隐藏模式 (View.GONE)，视图将不可见且不占据任何布局空间。 */
        const val MODE_ALWAYS_HIDDEN = 2

        /** 强制隐藏（占位）模式 (View.INVISIBLE)，视图将不可见但仍然占据其布局空间。 */
        const val MODE_ALWAYS_INVISIBLE = 3
    }
}
