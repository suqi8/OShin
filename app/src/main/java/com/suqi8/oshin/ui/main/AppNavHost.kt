package com.suqi8.oshin.ui.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SizeTransform
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.suqi8.oshin.ui.about.about_contributors
import com.suqi8.oshin.ui.about.about_group
import com.suqi8.oshin.ui.about.about_references
import com.suqi8.oshin.ui.about.about_setting
import com.suqi8.oshin.ui.activity.HideAppsNotice
import com.suqi8.oshin.ui.activity.feature.featureScreen
import com.suqi8.oshin.ui.activity.func.StatusBarLayout.StatusBarLayout
import com.suqi8.oshin.ui.activity.func.cpu_freq
import com.suqi8.oshin.ui.activity.func.feature.OplusSettingsScreen
import com.suqi8.oshin.ui.activity.func.romworkshop.RomWorkshop
import com.suqi8.oshin.utils.SpringEasing
import top.yukonga.miuix.kmp.utils.getWindowSize

@OptIn(ExperimentalAnimationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val windowWidth = getWindowSize().width

    val easing = SpringEasing.stiff()
    val duration = easing.durationMillis.toInt()
    VerifyDialog()
    PrivacyDialog()


    Column {
        SharedTransitionLayout {
            NavHost(
                navController = navController,
                startDestination = "Main",
                /*enterTransition = { slideInHorizontally(initialOffsetX = { windowWidth }, animationSpec = tween(duration, 0, easing = easing)) },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -windowWidth / 5 }, animationSpec = tween(duration, 0, easing = easing)) },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -windowWidth / 5 }, animationSpec = tween(duration, 0, easing = easing)) },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { windowWidth }, animationSpec = tween(duration, 0, easing = easing)) },*/
                sizeTransform = {
                    SizeTransform(clip = false)
                }
            ) {
                composable("Main") {
                    val sharedTransitionScope = this@SharedTransitionLayout
                    val animatedVisibilityScope = this
                    MainScreen(
                        navController = navController,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
                composable("about_setting") {
                    val sharedTransitionScope = this@SharedTransitionLayout
                    val animatedVisibilityScope = this
                    about_setting(
                        navController = navController,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
                composable("about_group") {
                    val sharedTransitionScope = this@SharedTransitionLayout
                    val animatedVisibilityScope = this
                    about_group(
                        navController = navController,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
                composable("about_references") {
                    val sharedTransitionScope = this@SharedTransitionLayout
                    val animatedVisibilityScope = this
                    about_references(
                        navController = navController,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
                composable("about_contributors") {
                    val sharedTransitionScope = this@SharedTransitionLayout
                    val animatedVisibilityScope = this
                    about_contributors(
                        navController = navController,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
                composable("func\\cpu_freq") {
                    val sharedTransitionScope = this@SharedTransitionLayout
                    val animatedVisibilityScope = this
                    cpu_freq(
                        navController = navController,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
                composable("func\\romworkshop") {
                    val sharedTransitionScope = this@SharedTransitionLayout
                    val animatedVisibilityScope = this
                    RomWorkshop(
                        navController = navController,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
                composable("feature/systemui\\status_bar\\StatusBarLayout") {
                    StatusBarLayout(navController)
                }
                composable("feature/settings\\oplus_settings") {
                    OplusSettingsScreen(navController)
                }
                composable(
                    route = "hide_apps_notice/{packages}",
                    arguments = listOf(navArgument("packages") {
                        type = NavType.StringType
                        nullable = true
                    })
                ) { backStackEntry ->
                    val sharedTransitionScope = this@SharedTransitionLayout
                    val animatedVisibilityScope = this
                    HideAppsNotice(
                        navController = navController,
                        packages = backStackEntry.arguments?.getString("packages"),
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
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
                    val sharedTransitionScope = this@SharedTransitionLayout
                    val animatedVisibilityScope = this

                    // 从导航参数中安全地获取 categoryId
                    val categoryId = backStackEntry.arguments?.getString("categoryId")

                    if (categoryId != null) {
                        featureScreen(
                            navController = navController,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    }
                }
            }
        }
    }
}
