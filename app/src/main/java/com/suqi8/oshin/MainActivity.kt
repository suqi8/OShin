package com.suqi8.oshin

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.ui.main.AppNavHost
import com.suqi8.oshin.ui.main.LocalColorMode
import com.suqi8.oshin.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

const val TAG = "OShin"
private const val APP_LANGUAGE_PREF_KEY = "app_language"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        // 从 SharedPreferences 中读取语言设置
        val languageCode = newBase.prefs("settings").getInt(APP_LANGUAGE_PREF_KEY, 0)

        // 使用 when 表达式直接返回 Locale 对象，更简洁
        val localeToSet = when (languageCode) {
            1 -> Locale.SIMPLIFIED_CHINESE
            2 -> Locale.ENGLISH
            3 -> Locale.JAPANESE
            4 -> Locale.forLanguageTag("ru")
            5 -> Locale.Builder().setLanguage("qaa").setExtension('x', "meme").build()
            6 -> Locale.KOREAN
            else -> null // 使用 null 代表系统默认
        }

        // 如果 localeToSet 不为 null，则应用它
        val context = if (localeToSet != null) {
            val config = newBase.resources.configuration
            config.setLocale(localeToSet)
            newBase.createConfigurationContext(config)
        } else {
            newBase
        }

        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val colorMode = remember {
                mutableIntStateOf(
                    context.prefs("settings").getInt("color_mode", 0)
                )
            }

            val darkMode = colorMode.intValue == 2 || (colorMode.intValue == 0 && isSystemInDarkTheme())

            DisposableEffect(darkMode) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT
                    ) { darkMode },
                    navigationBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT
                    ) { darkMode },
                )
                onDispose {}
            }

            window.isNavigationBarContrastEnforced = false

            AppTheme(colorMode = colorMode.intValue) {
                CompositionLocalProvider(LocalColorMode provides colorMode) {
                    // 主内容现在只是一个对 AppNavHost 的简单调用
                    AppNavHost()
                }
            }
        }
    }
}
