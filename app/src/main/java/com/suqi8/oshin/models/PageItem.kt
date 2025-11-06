package com.suqi8.oshin.models

import androidx.annotation.StringRes

/**
 * 页面级别的 UI 元素基础接口。
 *
 * 所有页面级别的组件（如卡片、链接组、提示等）都应实现此接口。
 * 该接口提供统一的条件显示机制，允许每个页面元素独立控制其可见性。
 *
 * @property condition 该页面元素的显示条件。当条件不满足时，该元素不应被渲染。
 *
 * @see CardDefinition 设置项卡片
 * @see RelatedLinks 相关链接卡片
 * @see NoEnable 未启用提示卡片
 */
sealed interface PageItem {
    val condition: Condition?
}

/**
 * 设置项卡片，用于在页面中展示一组相关的设置项。
 *
 * 该类型的卡片通常包含一个标题和多个可交互的设置项。
 * 整个卡片的显示可以通过 [condition] 属性进行条件控制。
 *
 * @property titleRes 卡片标题的字符串资源 ID。若为 null 则不显示标题。
 * @property items 该卡片包含的所有设置项列表。
 * @property condition 卡片的显示条件。若条件不满足，整个卡片将被隐藏。
 *
 * @see ScreenItem 设置项的详细定义
 * @see PageItem 页面级别元素接口
 */
data class CardDefinition(
    @StringRes val titleRes: Int? = null,
    val items: List<ScreenItem>,
    override val condition: Condition? = null
) : PageItem

/**
 * 相关链接卡片，用于在页面中展示一组相关的导航链接。
 *
 * 该类型的卡片提供快速导航功能，帮助用户访问相关的页面或功能。
 * 每条链接包含标题和目标路由。整个卡片的显示可通过 [condition] 进行条件控制。
 *
 * @property links 该卡片包含的所有链接列表。
 * @property condition 卡片的显示条件。若条件不满足，整个卡片将被隐藏。
 *
 * @see Link 单条链接的定义
 * @see PageItem 页面级别元素接口
 */
data class RelatedLinks(
    val links: List<Link>,
    override val condition: Condition? = null
) : PageItem {

    /**
     * 表示一条导航链接。
     *
     * @property titleRes 链接显示文本的字符串资源 ID。
     * @property route 链接目标的路由地址。用于导航到指定页面或功能。
     */
    data class Link(
        @StringRes val titleRes: Int,
        val route: String
    )
}

/**
 * 未启用提示卡片，用于在页面中显示功能暂未启用的提示。
 *
 * 该类型的卡片通常用于展示某些功能因条件不满足而暂时不可用的状态提示。
 * 它是一个独立的、页面级别的提示元素。
 *
 * @property condition 卡片的显示条件。通常用于控制该提示何时出现。
 *
 * @see PageItem 页面级别元素接口
 */
data class NoEnable(
    override val condition: Condition? = null
) : PageItem
