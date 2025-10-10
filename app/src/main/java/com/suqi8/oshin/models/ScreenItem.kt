package com.suqi8.oshin.models

import androidx.annotation.ArrayRes
import androidx.annotation.StringRes

/**
 * 声明式UI的数据模型根接口。
 * 列表中的每一项都代表一个UI组件，例如一个开关、一个滑块或一个静态提示块。
 */
sealed interface ScreenItem {
    /**
     * 此项的显示条件。如果为 null，则始终显示。
     * 如果不为 null，则仅在条件满足时显示。
     */
    val condition: DisplayCondition?
}

/**
 * 描述一个图片选择项 (对应 funPicSele)。
 * @param key 用于在 ViewModel 中追踪状态的唯一键。
 * @param title 标题。
 * @param summaryRes 可选的摘要。
 * @param targetPath 图片被选中后，要复制到的最终文件路径。
 */
data class Picture(
    override val key: String,
    override val title: Title,
    @StringRes override val summary: Int? = null,
    val targetPath: String,
    override val condition: DisplayCondition? = null
) : TitledScreenItem

/**
 * 一个带有标题和摘要的UI项的接口，用于统一可搜索的项。
 */
sealed interface TitledScreenItem : ScreenItem {
    val title: Title
    val summary: Int?
    val key: String
}

/**
 * 描述一个UI组件的显示条件。
 * @param dependencyKey 所依赖的功能项的 key。
 * @param operator 比较操作符。
 * @param requiredValue 所依赖项需要满足的值。
 */
data class DisplayCondition(
    val dependencyKey: String,
    val operator: Operator = Operator.EQUALS,
    val requiredValue: Any
)

/**
 * 定义了可用的比较操作符。
 */
enum class Operator {
    EQUALS,         // 等于
    NOT_EQUALS,     // 不等于
    // 未来扩展: GREATER_THAN, LESS_THAN, CONTAINS 等
}

/**
 * 描述一个开关项 (对应 FunSwitch)。
 * @param key 用于 SharedPreferences 的键。
 * @param title 标题，使用灵活的 Title 模型。
 * @param summaryRes 可选的摘要字符串资源 ID。
 * @param defaultValue 开关的默认值。
 */
data class Switch(
    override val key: String,
    override val title: Title,
    @StringRes override val summary: Int? = null,
    val defaultValue: Boolean = false,
    override val condition: DisplayCondition? = null
) : TitledScreenItem

/**
 * 描述一个滑块项 (对应 FunSlider)。
 * @param key 用于 SharedPreferences 的键。
 * @param title 标题。
 * @param summaryRes 可选的摘要字符串资源 ID。
 * @param defaultValue 默认值。
 * @param valueRange 值的范围。
 * @param unit 数值单位，如 "dp", "ms" 等。
 * @param decimalPlaces 小数位数。
 */
data class Slider(
    override val key: String,
    override val title: Title,
    @StringRes override val summary: Int? = null,
    val defaultValue: Float = 0f,
    val valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    val unit: String = "",
    val decimalPlaces: Int = 1,
    override val condition: DisplayCondition? = null
) : TitledScreenItem

/**
 * 描述一个下拉选择项 (对应 FunDropdown)。
 * @param key 用于 SharedPreferences 的键。
 * @param title 标题。
 * @param summaryRes 可选的摘要字符串资源 ID。
 * @param optionsRes 选项列表的字符串数组资源 ID。
 * @param defaultValue 默认选中的索引。
 */
data class Dropdown(
    override val key: String,
    override val title: Title,
    @StringRes override val summary: Int? = null,
    @ArrayRes val optionsRes: Int,
    val defaultValue: Int = 0,
    override val condition: DisplayCondition? = null
) : TitledScreenItem

/**
 * 描述一个字符串输入项 (对应 funString)。
 * @param key 用于 SharedPreferences 的键。
 * @param title 标题。
 * @param summaryRes 可选的摘要。
 * @param defaultValue 默认的字符串值。
 * @param nullable 是否允许输入为空。
 */
data class StringInput(
    override val key: String,
    override val title: Title,
    @StringRes override val summary: Int? = null,
    val defaultValue: String = "",
    val nullable: Boolean = false,
    override val condition: DisplayCondition? = null
) : TitledScreenItem

/**
 * 描述一个可点击的箭头导航项 (对应 FunArrow)。
 * @param title 标题。
 * @param summaryRes 可选的摘要字符串资源 ID。
 * @param route 点击后导航的目标路由ID。
 */
data class Action(
    override val title: Title,
    @StringRes override val summary: Int? = null,
    val route: String,
    override val condition: DisplayCondition? = null
) : TitledScreenItem {
    override val key: String get() = route
}
