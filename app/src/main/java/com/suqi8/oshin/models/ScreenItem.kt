package com.suqi8.oshin.models

import androidx.annotation.ArrayRes
import androidx.annotation.StringRes

/**
 * 声明式 UI 的数据模型根接口。
 *
 * 列表中的每一项都代表一个可渲染的 UI 组件，例如开关、滑块或导航项等。
 * 所有 UI 项都支持条件显示机制。
 *
 * @property condition 此项的显示条件。若为 null 则始终显示；若不为 null，则仅在条件满足时显示
 *
 * @see TitledScreenItem 带有标题和摘要的 UI 项基类
 */
sealed interface ScreenItem {
    val condition: Condition?
}

/**
 * 带有标题和摘要的 UI 项基接口。
 *
 * 为可被搜索和导航的 UI 组件提供统一的接口，这些组件都具有唯一标识符、标题和可选摘要。
 *
 * @property title 组件的标题，使用灵活的 [Title] 模型
 * @property summary 组件的摘要文本资源 ID，可为 null
 * @property key 组件的唯一标识符，用于在 ViewModel 中追踪状态或存储偏好设置
 *
 * @see Title 标题模型
 * @see ScreenItem 父级接口
 */
sealed interface TitledScreenItem : ScreenItem {
    val title: Title
    val summary: Int?
    val key: String
}

/**
 * 开关项组件（对应 FunSwitch）。
 *
 * 用于呈现一个布尔值的开关控件，状态持久化到 SharedPreferences。
 * 支持标题、摘要和条件显示。
 *
 * @property key 用于 SharedPreferences 的键，用于持久化开关状态
 * @property title 开关的标题
 * @property summary 开关的摘要描述，可选
 * @property defaultValue 开关的默认值，默认为 false
 * @property condition 开关的显示条件
 *
 * @see TitledScreenItem 父级接口
 */
data class Switch(
    override val key: String,
    override val title: Title,
    @StringRes override val summary: Int? = null,
    val defaultValue: Boolean = false,
    override val condition: Condition? = null
) : TitledScreenItem

/**
 * 滑块项组件（对应 FunSlider）。
 *
 * 用于呈现一个浮点数值的滑块控件，支持自定义范围、单位和精度。
 * 数值持久化到 SharedPreferences。
 *
 * @property key 用于 SharedPreferences 的键
 * @property title 滑块的标题
 * @property summary 滑块的摘要描述，可选
 * @property defaultValue 默认值，默认为 0f
 * @property valueRange 滑块的值范围，默认为 0f..100f
 * @property unit 数值的单位，如 "dp"、"ms" 等，默认为空字符串
 * @property decimalPlaces 数值显示的小数位数，默认为 1
 * @property condition 滑块的显示条件
 *
 * @see TitledScreenItem 父级接口
 */
data class Slider(
    override val key: String,
    override val title: Title,
    @StringRes override val summary: Int? = null,
    val defaultValue: Float = 0f,
    val valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    val unit: String = "",
    val decimalPlaces: Int = 1,
    override val condition: Condition? = null
) : TitledScreenItem

/**
 * 下拉选择项组件（对应 FunDropdown）。
 *
 * 用于呈现一个选项列表的下拉菜单控件。选中的索引持久化到 SharedPreferences。
 *
 * @property key 用于 SharedPreferences 的键，存储选中项的索引
 * @property title 下拉菜单的标题
 * @property summary 下拉菜单的摘要描述，可选
 * @property optionsRes 选项列表的字符串数组资源 ID
 * @property defaultValue 默认选中的选项索引，默认为 0
 * @property condition 下拉菜单的显示条件
 *
 * @see TitledScreenItem 父级接口
 */
data class Dropdown(
    override val key: String,
    override val title: Title,
    @StringRes override val summary: Int? = null,
    @ArrayRes val optionsRes: Int,
    val defaultValue: Int = 0,
    override val condition: Condition? = null
) : TitledScreenItem

/**
 * 字符串输入项组件（对应 funString）。
 *
 * 用于呈现一个文本输入框，允许用户输入字符串。
 * 输入值持久化到 SharedPreferences。
 *
 * @property key 用于 SharedPreferences 的键
 * @property title 输入框的标题
 * @property summary 输入框的摘要描述，可选
 * @property defaultValue 默认的字符串值，默认为空字符串
 * @property nullable 是否允许输入为空字符串，默认为 false
 * @property condition 输入框的显示条件
 *
 * @see TitledScreenItem 父级接口
 */
data class StringInput(
    override val key: String,
    override val title: Title,
    @StringRes override val summary: Int? = null,
    val defaultValue: String = "",
    val nullable: Boolean = false,
    override val condition: Condition? = null
) : TitledScreenItem

/**
 * 导航动作项组件（对应 FunArrow）。
 *
 * 用于呈现一个可点击的导航项，点击后导航到指定的页面或功能。
 *
 * @property title 导航项的标题
 * @property summary 导航项的摘要描述，可选
 * @property route 点击后导航的目标路由 ID
 * @property condition 导航项的显示条件
 *
 * 注意：该类的 [key] 属性使用 [route] 作为唯一标识符。
 *
 * @see TitledScreenItem 父级接口
 */
data class Action(
    override val title: Title,
    @StringRes override val summary: Int? = null,
    val route: String,
    override val condition: Condition? = null
) : TitledScreenItem {
    override val key: String get() = route
}

/**
 * URL 跳转动作项组件。
 *
 * 用于呈现一个可点击的导航项，点击后跳转到指定的 URL。
 * 支持打开网页或调用其他应用。
 *
 * @property title 导航项的标题
 * @property summary 导航项的摘要描述，可选
 * @property url 点击后跳转的目标 URL
 * @property condition 导航项的显示条件
 *
 * 注意：该类的 [key] 属性使用 [url] 作为唯一标识符。
 *
 * @see TitledScreenItem 父级接口
 */
data class UrlAction(
    override val title: Title,
    @StringRes override val summary: Int? = null,
    val url: String,
    override val condition: Condition? = null
) : TitledScreenItem {
    override val key: String get() = url
}

/**
 * 图片选择项组件（对应 funPicSele）。
 *
 * 用于呈现一个图片选择控件，允许用户选择图片并复制到指定路径。
 *
 * @property key 用于在 ViewModel 中追踪状态的唯一键
 * @property title 图片选择项的标题
 * @property summary 图片选择项的摘要描述，可选
 * @property targetPath 用户选中的图片要复制到的最终文件路径
 * @property condition 图片选择项的显示条件
 *
 * @see TitledScreenItem 父级接口
 */
data class Picture(
    override val key: String,
    override val title: Title,
    @StringRes override val summary: Int? = null,
    val targetPath: String,
    override val condition: Condition? = null
) : TitledScreenItem

/**
 * 应用选择项组件（对应 funAppSele）。
 *
 * 用于呈现一个应用选择控件，允许用户从系统中选择一个或多个应用。
 * 选中的应用包名列表持久化到 SharedPreferences。
 *
 * @property key 用于 SharedPreferences 的键，存储选中应用的包名列表
 * @property title 应用选择项的标题
 * @property summary 应用选择项的摘要描述，可选
 * @property condition 应用选择项的显示条件
 *
 * @see TitledScreenItem 父级接口
 */
data class AppSelection(
    override val key: String,
    override val title: Title,
    @StringRes override val summary: Int? = null,
    override val condition: Condition? = null
) : TitledScreenItem
