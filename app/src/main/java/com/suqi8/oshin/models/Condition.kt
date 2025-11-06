package com.suqi8.oshin.models

import com.suqi8.oshin.models.Operator.EQUALS
import com.suqi8.oshin.models.Operator.NOT_EQUALS


/**
 * 条件判断的基础接口。
 *
 * 用于声明式定义 UI 元素的显示条件。支持简单条件和复合条件的组合。
 *
 * @see SimpleCondition 单一条件判断
 * @see AndCondition 多条件与逻辑
 */
sealed interface Condition

/**
 * 简单条件，基于单一依赖项和操作符的条件判断。
 *
 * 用于比较特定键的值是否满足条件，支持相等和不相等两种操作符。
 *
 * @property dependencyKey 依赖项的键，对应某个 ScreenItem 的 key
 * @property operator 比较操作符，默认为 [Operator.EQUALS]
 * @property requiredValue 期望的值，默认为 true
 *
 * @see Operator 可用的操作符枚举
 */
data class SimpleCondition(
    val dependencyKey: String,
    val operator: Operator = EQUALS,
    val requiredValue: Any = true
) : Condition

/**
 * 复合条件，表示多个条件的与逻辑（全部满足）。
 *
 * 仅当所有包含的条件都满足时，该复合条件才为真。
 *
 * @property conditions 条件列表，支持嵌套的复合条件
 *
 * @see Condition 条件接口
 */
data class AndCondition(
    val conditions: List<Condition>
) : Condition

/**
 * 条件比较操作符枚举。
 *
 * @property EQUALS 相等判断
 * @property NOT_EQUALS 不相等判断
 */
enum class Operator {
    EQUALS,      // 值相等
    NOT_EQUALS   // 值不相等
}
