package com.suqi8.oshin.models

/**
 * 代表一个可以以多种方式提供的文本标题。
 * Represents a text title that can be provided in multiple ways.
 */
sealed class Title

/**
 * 通过 Android 字符串资源 ID 提供的标题。
 */
data class StringResource(val id: Int) : Title()

/**
 * 通过纯文本字符串直接提供的标题。
 */
data class PlainText(val text: String) : Title()

/**
 * 通过应用包名动态获取应用名称作为标题。
 */
data class AppName(val packageName: String) : Title()
