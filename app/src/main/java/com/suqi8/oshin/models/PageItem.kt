package com.suqi8.oshin.models

import androidx.annotation.StringRes

/**
 * 页面级别的UI块，可以是设置项卡片，也可以是独立组件卡片。
 */
sealed interface PageItem

/**
 * CardDefinition 现在是一个页面级别的元素。
 */
data class CardDefinition(
    @StringRes val titleRes: Int? = null,
    val items: List<ScreenItem>
) : PageItem

/**
 * RelatedLinks 现在是一个独立的、页面级别的元素。
 */
data class RelatedLinks(
    val links: List<Link>,
    val condition: DisplayCondition? = null
) : PageItem {
    data class Link(
        @StringRes val titleRes: Int,
        val route: String
    )
}
