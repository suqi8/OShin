package com.suqi8.oshin.models

import androidx.annotation.StringRes

/**
 * 灵活的标题模型，支持多种标题来源形式。
 *
 * 该接口定义了应用中各类 UI 组件的标题表示方式，允许标题来自不同的来源，
 * 使得 UI 组件的配置更加灵活。
 *
 * @see StringResource 从资源文件加载的标题
 * @see AppName 从应用包名动态获取的标题
 * @see PlainText 纯文本标题
 */
sealed interface Title

/**
 * 字符串资源标题。
 *
 * 标题文本来自应用的字符串资源文件，支持多语言和资源管理。
 *
 * @property id 字符串资源的 ID
 */
data class StringResource(@StringRes val id: Int) : Title

/**
 * 应用名称标题。
 *
 * 标题文本从指定包名的应用动态获取其应用名称。
 *
 * @property packageName 应用的包名
 */
data class AppName(val packageName: String) : Title

/**
 * 纯文本标题。
 *
 * 标题文本直接提供为字符串，无需资源查询。
 *
 * @property text 标题的具体文本内容
 */
data class PlainText(val text: String) : Title
