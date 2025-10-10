package com.suqi8.oshin.ui.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.suqi8.oshin.ui.activity.HideAppsNotice
import com.suqi8.oshin.ui.activity.about.about_contributors
import com.suqi8.oshin.ui.activity.about.about_group
import com.suqi8.oshin.ui.activity.about.about_references
import com.suqi8.oshin.ui.activity.about.about_setting
import com.suqi8.oshin.ui.activity.feature.featureScreen
import com.suqi8.oshin.ui.activity.func.cpu_freq
import com.suqi8.oshin.ui.activity.func.romworkshop.RomWorkshop
import com.suqi8.oshin.utils.SpringEasing
import top.yukonga.miuix.kmp.utils.getWindowSize

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val windowWidth = getWindowSize().width

    val easing = SpringEasing.gentle()
    val duration = easing.durationMillis.toInt()
    VerifyDialog()
    PrivacyDialog()


    Column {
        NavHost(
            navController = navController,
            startDestination = "Main",
            enterTransition = { slideInHorizontally(initialOffsetX = { windowWidth }, animationSpec = tween(duration, 0, easing = easing)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -windowWidth / 5 }, animationSpec = tween(duration, 0, easing = easing)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -windowWidth / 5 }, animationSpec = tween(duration, 0, easing = easing)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { windowWidth }, animationSpec = tween(duration, 0, easing = easing)) },
            sizeTransform = {
                SizeTransform(clip = false)
            }
        ) {
            composable("Main") { MainScreen(navController) }
            composable("about_setting") { about_setting(navController) }
            composable("about_group") { about_group(navController) }
            composable("about_references") { about_references(navController) }
            composable("about_contributors") { about_contributors(navController) }
            composable("func\\cpu_freq") { cpu_freq(navController) }
            composable(
                route = "hide_apps_notice/{packages}",
                arguments = listOf(navArgument("packages") {
                    type = NavType.StringType
                    nullable = true
                })
            ) { backStackEntry ->
                HideAppsNotice(
                    navController = navController,
                    packages = backStackEntry.arguments?.getString("packages")
                )
            }
            composable("func\\romworkshop") { RomWorkshop(navController) }
            composable(
                route = "feature/{categoryId}?highlightKey={highlightKey}",
                // 定义 categoryId 参数为字符串类型
                arguments = listOf(
                    navArgument("categoryId") { type = NavType.StringType },
                    navArgument("highlightKey") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                // 从导航参数中安全地获取 categoryId
                val categoryId = backStackEntry.arguments?.getString("categoryId")

                if (categoryId != null) {
                    featureScreen(
                        navController = navController
                    )
                }
            }
        }
    }
}
