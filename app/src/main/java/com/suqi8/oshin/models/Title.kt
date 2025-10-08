package com.suqi8.oshin.models

import androidx.annotation.StringRes

/**
 * 定义一个灵活的标题模型，可以是静态资源、动态应用名或纯文本。
 */
sealed interface Title

data class StringResource(@StringRes val id: Int) : Title
data class AppName(val packageName: String) : Title
data class PlainText(val text: String) : Title
