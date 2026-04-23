package com.suqi8.oshin.models

/**
 * 带标题和键的页面功能项接口，用于搜索和高亮定位。
 * Interface for screen items that carry a title, key and optional summary,
 * enabling search indexing and highlight navigation.
 */
interface TitledScreenItem {
    val title: Title
    val key: String
    val summary: Int?
    val condition: Condition?
}

/**
 * 页面内功能项的密封基类。
 * Sealed base class for all functional items within a feature page.
 */
sealed class ScreenItem {
    abstract val condition: Condition?
}

/**
 * 开关类型功能项。
 *
 * @param title 显示标题
 * @param key SharedPreferences 存储键
 * @param defaultValue 默认值，默认为 false
 * @param summary 摘要说明的资源 ID，可为 null
 * @param condition 控制此项可见性的条件，null 表示始终显示
 */
data class Switch(
    override val title: Title,
    override val key: String,
    val defaultValue: Boolean = false,
    override val summary: Int? = null,
    override val condition: Condition? = null
) : ScreenItem(), TitledScreenItem

/**
 * 滑块类型功能项。
 *
 * @param title 显示标题
 * @param key SharedPreferences 存储键
 * @param defaultValue 默认值，默认为 0f
 * @param valueRange 滑块的值域范围
 * @param unit 值的单位文本，可为 null
 * @param decimalPlaces 显示的小数位数
 * @param summary 摘要说明的资源 ID，可为 null
 * @param condition 控制此项可见性的条件，null 表示始终显示
 */
data class Slider(
    override val title: Title,
    override val key: String,
    val defaultValue: Float = 0f,
    val valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    val unit: String? = null,
    val decimalPlaces: Int = 1,
    override val summary: Int? = null,
    override val condition: Condition? = null
) : ScreenItem(), TitledScreenItem

/**
 * 下拉选择类型功能项。
 *
 * @param title 显示标题
 * @param key SharedPreferences 存储键
 * @param defaultValue 默认选中的索引，默认为 0
 * @param optionsRes 选项字符串数组的资源 ID
 * @param summary 摘要说明的资源 ID，可为 null
 * @param condition 控制此项可见性的条件，null 表示始终显示
 */
data class Dropdown(
    override val title: Title,
    override val key: String,
    val defaultValue: Int = 0,
    val optionsRes: Int,
    override val summary: Int? = null,
    override val condition: Condition? = null
) : ScreenItem(), TitledScreenItem

/**
 * 导航跳转类型功能项，点击后跳转到指定路由。
 * 其 key 与 route 相同。
 *
 * @param title 显示标题
 * @param route 跳转目标路由 ID
 * @param summary 摘要说明的资源 ID，可为 null
 * @param condition 控制此项可见性的条件，null 表示始终显示
 */
data class Action(
    override val title: Title,
    val route: String,
    override val summary: Int? = null,
    override val condition: Condition? = null
) : ScreenItem(), TitledScreenItem {
    override val key: String get() = route
}

/**
 * 图片选择类型功能项。
 *
 * @param title 显示标题
 * @param key SharedPreferences 存储键
 * @param targetPath 图片保存的文件路径
 * @param summary 摘要说明的资源 ID，可为 null
 * @param condition 控制此项可见性的条件，null 表示始终显示
 */
data class Picture(
    override val title: Title,
    override val key: String,
    val targetPath: String,
    override val summary: Int? = null,
    override val condition: Condition? = null
) : ScreenItem(), TitledScreenItem

/**
 * 字符串输入类型功能项。
 *
 * @param title 显示标题
 * @param key SharedPreferences 存储键
 * @param defaultValue 默认字符串值
 * @param nullable 是否允许值为空（空字符串等价于未设置）
 * @param summary 摘要说明的资源 ID，可为 null
 * @param condition 控制此项可见性的条件，null 表示始终显示
 */
data class StringInput(
    override val title: Title,
    override val key: String,
    val defaultValue: String = "",
    val nullable: Boolean = false,
    override val summary: Int? = null,
    override val condition: Condition? = null
) : ScreenItem(), TitledScreenItem

/**
 * 应用选择类型功能项，允许用户从已安装应用中选择一组应用。
 *
 * @param title 显示标题
 * @param key SharedPreferences 存储键（以逗号分隔的包名列表形式保存）
 * @param summary 摘要说明的资源 ID，可为 null
 * @param condition 控制此项可见性的条件，null 表示始终显示
 */
data class AppSelection(
    override val title: Title,
    override val key: String,
    override val summary: Int? = null,
    override val condition: Condition? = null
) : ScreenItem(), TitledScreenItem

/**
 * URL 跳转类型功能项，点击后在浏览器中打开指定 URL。
 * 其 key 与 url 相同。
 *
 * @param title 显示标题
 * @param url 要打开的 URL
 * @param summary 摘要说明的资源 ID，可为 null
 * @param condition 控制此项可见性的条件，null 表示始终显示
 */
data class UrlAction(
    override val title: Title,
    val url: String,
    override val summary: Int? = null,
    override val condition: Condition? = null
) : ScreenItem(), TitledScreenItem {
    override val key: String get() = url
}
