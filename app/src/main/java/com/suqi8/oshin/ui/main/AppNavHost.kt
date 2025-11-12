package com.suqi8.oshin.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.suqi8.oshin.ui.about.about_contributors
import com.suqi8.oshin.ui.about.about_group
import com.suqi8.oshin.ui.about.about_references
import com.suqi8.oshin.ui.about.about_setting
import com.suqi8.oshin.ui.activity.HideAppsNotice
import com.suqi8.oshin.ui.activity.feature.featureScreen
import com.suqi8.oshin.ui.activity.func.cpu_freq
import com.suqi8.oshin.ui.activity.func.romworkshop.RomWorkshop
import com.suqi8.oshin.ui.nav.path.NavPath
import com.suqi8.oshin.ui.nav.transition.NavAnimationSpecs
import com.suqi8.oshin.ui.nav.transition.NavItemAppearance
import com.suqi8.oshin.ui.nav.transition.NavTransitionType
import com.suqi8.oshin.ui.nav.ui.NavStack
import top.yukonga.miuix.kmp.utils.BackHandler

// 我们不再需要 Compose Animation 库的特定 API，可以移除
// @OptIn(ExperimentalAnimationApi::class, ExperimentalSharedTransitionApi::class)

@Composable
fun AppNavHost() {
    // 1. 移除 rememberNavController()

    // 2. 创建动画所需的 CoroutineScope
    val animationScope = rememberCoroutineScope()

    // 3. 创建并记住 NavPath 实例
    val navPath = remember {
        NavPath(
            // 定义初始页面堆栈。这里我们把 "Main" 作为第一个页面。
            // 动画类型可以随便给一个，因为第一个页面没有进入动画。
            initialItems = listOf("Main" to NavTransitionType.Zoom),
            animationScope = animationScope,
            navAnimationSpecs = NavAnimationSpecs.Default // 使用默认动画参数
        )
    }

    // 保留你原来的 Dialogs
    VerifyDialog()
    PrivacyDialog()

    val canPop by remember {
        derivedStateOf {
            // 只有当“未退出”的页面数量大于 1 时，才允许 pop
            navPath.states.count { it.transition.appearance < NavItemAppearance.Exiting } > 1
        }
    }

    BackHandler(enabled = canPop) {
        navPath.pop() // 调用 NavPath 自己的 pop
    }

    // 4. 使用 NavStack 替换 SharedTransitionLayout 和 NavHost
    NavStack(
        path = navPath,
        modifier = Modifier.fillMaxSize()
    ) { navStackScope, item ->
        // 'item' 是当前需要显示的页面的路由标识（比如 "Main"）
        // 我们在这里根据 item 决定显示哪个 Composable
        // 注意：现在我们需要手动传递 navPath 和 navStackScope
        when (item) {
            "Main" -> MainScreen(
                navPath = navPath,
                navStackScope = navStackScope
            )
            "about_setting" -> about_setting(
                navPath = navPath,
                navStackScope = navStackScope
            )
            "about_group" -> about_group(
                navPath = navPath,
                navStackScope = navStackScope
            )
            "about_references" -> about_references(
                navPath = navPath,
                navStackScope = navStackScope
            )
            "about_contributors" -> about_contributors(
                navPath = navPath,
                navStackScope = navStackScope
            )
            "func\\cpu_freq" -> cpu_freq(
                navPath = navPath,
                navStackScope = navStackScope
            )
            "func\\romworkshop" -> RomWorkshop(
                navPath = navPath,
                navStackScope = navStackScope
            )

            // 暂时注释掉其他复杂的页面，我们先保证基础页面能工作
            // "software_update" -> SoftwareUpdatePage(...)
            // "feature/systemui\\status_bar\\StatusBarLayout" -> StatusBarLayout(...)

            // 之后我们会处理带参数的路由
            else -> {
                if (item is String && item.startsWith("feature/")) {
                    val categoryId = item.substringAfter("feature/").substringBefore("?")
                    val highlightKey = if (item.contains("?highlightKey=")) item.substringAfter("?highlightKey=") else null

                    featureScreen(
                        navPath = navPath,
                        navStackScope = navStackScope,
                        categoryId = categoryId, // 传递解析出的参数
                        highlightKey = highlightKey
                    )
                } else if (item is String && item.startsWith("hide_apps_notice/")) {
                    val packages = item.substringAfter("hide_apps_notice/")
                    HideAppsNotice(
                        navPath = navPath,
                        navStackScope = navStackScope,
                        packages = packages
                    )
                }
            }
        }
    }
}
