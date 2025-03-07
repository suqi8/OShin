package com.suqi8.oshin.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun AppTheme(
    colorMode: Int = 0,
    content: @Composable () -> Unit
) {
    val darkTheme = isSystemInDarkTheme()
    return MiuixTheme(
        colors = when (colorMode) {
            1 -> top.yukonga.miuix.kmp.theme.lightColorScheme()
            2 -> top.yukonga.miuix.kmp.theme.darkColorScheme()
            else -> if (darkTheme) top.yukonga.miuix.kmp.theme.darkColorScheme() else top.yukonga.miuix.kmp.theme.lightColorScheme()
        },
        content = content
    )
}
