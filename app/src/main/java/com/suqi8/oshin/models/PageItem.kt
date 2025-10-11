package com.suqi8.oshin.models

import androidx.annotation.StringRes

/**
 * 页面级别的UI块，可以是设置项卡片，也可以是独立组件卡片。
 */
sealed interface PageItem {
    // 将 condition 提升到顶层，所有页面元素都可以有自己的显示条件
    val condition: Condition?
}
/**
 * CardDefinition 现在是一个页面级别的元素。
 */
data class CardDefinition(
    @StringRes val titleRes: Int? = null,
    val items: List<ScreenItem>,
    override val condition: Condition? = null
) : PageItem

/**
 * RelatedLinks 现在是一个独立的、页面级别的元素。
 */
data class RelatedLinks(
    val links: List<Link>,
    override val condition: Condition? = null
) : PageItem {
    data class Link(
        @StringRes val titleRes: Int,
        val route: String
    )
}

/**
 * 代表一个“未启用”的独立提示卡片。
 */
data class NoEnable(
    override val condition: Condition? = null // 实现接口属性
) : PageItem
