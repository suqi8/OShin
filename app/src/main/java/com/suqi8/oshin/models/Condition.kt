package com.suqi8.oshin.models

/**
 * 条件比较运算符。
 * Comparison operators for conditions.
 */
enum class Operator {
    EQUALS,
    NOT_EQUALS
}

/**
 * 用于控制 UI 元素可见性的条件基类。
 * Base sealed class for conditions that control the visibility of UI elements.
 */
sealed class Condition

/**
 * 简单条件：当指定键的当前值满足运算符和期望值时为真。
 *
 * @param dependencyKey 依赖的功能项的 key
 * @param operator 比较运算符，默认为 EQUALS
 * @param requiredValue 期望的值，默认为 true
 */
data class SimpleCondition(
    val dependencyKey: String,
    val operator: Operator = Operator.EQUALS,
    val requiredValue: Any = true
) : Condition()

/**
 * AND 条件：当所有子条件都满足时为真。
 *
 * @param conditions 需要同时满足的条件列表
 */
data class AndCondition(
    val conditions: List<Condition>
) : Condition()
