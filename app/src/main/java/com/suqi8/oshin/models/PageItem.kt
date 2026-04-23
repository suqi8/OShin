package com.suqi8.oshin.models

/**
 * 功能页面内顶层元素的密封接口。
 * 每个 PageItem 可以有一个可选的显示条件。
 *
 * Sealed interface for top-level elements within a feature page.
 */
sealed interface PageItem {
    val condition: Condition?
}

/**
 * 卡片定义，包含一组功能项。
 *
 * @param items 卡片内的功能项列表
 * @param titleRes 卡片顶部小标题的资源 ID，null 表示无标题
 * @param condition 控制此卡片可见性的条件，null 表示始终显示
 */
data class CardDefinition(
    val items: List<ScreenItem>,
    val titleRes: Int? = null,
    override val condition: Condition? = null
) : PageItem

/**
 * 相关链接卡片，引导用户跳转到其他相关功能页。
 *
 * @param links 相关链接列表
 * @param condition 控制此卡片可见性的条件，null 表示始终显示
 */
data class RelatedLinks(
    val links: List<Link>,
    override val condition: Condition? = null
) : PageItem {

    /**
     * 单个相关链接。
     *
     * @param titleRes 链接文本的字符串资源 ID
     * @param route 跳转目标的路由 ID
     */
    data class Link(val titleRes: Int, val route: String)
}

/**
 * 功能未启用提示项，当指定条件不满足时显示"功能未启用"的提示。
 *
 * @param condition 触发显示此提示的条件，null 表示始终显示
 */
data class NoEnable(
    override val condition: Condition? = null
) : PageItem
