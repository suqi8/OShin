package com.suqi8.oshin.ui.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.suqi8.oshin.ui.activity.about.about_contributors
import com.suqi8.oshin.ui.activity.about.about_group
import com.suqi8.oshin.ui.activity.about.about_references
import com.suqi8.oshin.ui.activity.about.about_setting
import com.suqi8.oshin.ui.activity.android.android
import com.suqi8.oshin.ui.activity.android.oplus_services
import com.suqi8.oshin.ui.activity.android.package_manager_services
import com.suqi8.oshin.ui.activity.android.split_screen_multi_window
import com.suqi8.oshin.ui.activity.com.android.incallui.incallui
import com.suqi8.oshin.ui.activity.com.android.launcher.launcher
import com.suqi8.oshin.ui.activity.com.android.launcher.recent_task
import com.suqi8.oshin.ui.activity.com.android.mms.mms
import com.suqi8.oshin.ui.activity.com.android.phone.phone
import com.suqi8.oshin.ui.activity.com.android.settings.settings
import com.suqi8.oshin.ui.activity.com.android.systemui.controlCenter
import com.suqi8.oshin.ui.activity.com.android.systemui.hardware_indicator
import com.suqi8.oshin.ui.activity.com.android.systemui.notification
import com.suqi8.oshin.ui.activity.com.android.systemui.status_bar_clock
import com.suqi8.oshin.ui.activity.com.android.systemui.status_bar_wifi
import com.suqi8.oshin.ui.activity.com.android.systemui.statusbar_icon
import com.suqi8.oshin.ui.activity.com.android.systemui.systemui
import com.suqi8.oshin.ui.activity.com.coloros.ocrscanner.ocrscanner
import com.suqi8.oshin.ui.activity.com.coloros.oshare.oshare
import com.suqi8.oshin.ui.activity.com.coloros.phonemanager.phonemanager
import com.suqi8.oshin.ui.activity.com.coloros.securepay.securepay
import com.suqi8.oshin.ui.activity.com.finshell.wallet.wallet
import com.suqi8.oshin.ui.activity.com.heytap.health.health
import com.suqi8.oshin.ui.activity.com.heytap.quicksearchbox.quicksearchbox
import com.suqi8.oshin.ui.activity.com.heytap.speechassist.speechassist
import com.suqi8.oshin.ui.activity.com.mi.health.mihealth
import com.suqi8.oshin.ui.activity.com.oplus.appdetail.appdetail
import com.suqi8.oshin.ui.activity.com.oplus.battery.battery
import com.suqi8.oshin.ui.activity.com.oplus.exsystemservice.exsystemservice
import com.suqi8.oshin.ui.activity.com.oplus.games.games
import com.suqi8.oshin.ui.activity.com.oplus.notificationmanager.notificationmanager
import com.suqi8.oshin.ui.activity.com.oplus.ota.ota
import com.suqi8.oshin.ui.activity.com.oplus.padconnect.padconnect
import com.suqi8.oshin.ui.activity.com.oplus.phonemanager.oplusphonemanager
import com.suqi8.oshin.ui.activity.func.cpu_freq
import com.suqi8.oshin.ui.activity.func.romworkshop.Rom_workshop
import com.suqi8.oshin.ui.activity.hide_apps_notice
import com.suqi8.oshin.ui.activity.recent_update
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
            composable("recent_update") { recent_update(navController) }
            composable("android") { android(navController) }
            composable("android\\package_manager_services") { package_manager_services(navController = navController) }
            composable("android\\oplus_system_services") { oplus_services(navController = navController) }
            composable("android\\split_screen_multi_window") { split_screen_multi_window(navController = navController) }
            composable("systemui") { systemui(navController = navController) }
            composable("systemui\\status_bar_clock") { status_bar_clock(navController = navController) }
            composable("systemui\\hardware_indicator") { hardware_indicator(navController = navController) }
            composable("systemui\\statusbar_icon") { statusbar_icon(navController = navController) }
            composable("systemui\\notification") { notification(navController = navController) }
            composable("systemui\\status_bar_wifi") { status_bar_wifi(navController = navController) }
            composable("systemui\\controlCenter") { controlCenter(navController = navController) }
            composable("launcher") { launcher(navController = navController) }
            composable("launcher\\recent_task") { recent_task(navController = navController) }
            composable("about_setting") { about_setting(navController) }
            composable("about_group") { about_group(navController) }
            composable("about_references") { about_references(navController) }
            composable("about_contributors") { about_contributors(navController) }
            composable("settings") { settings(navController) }
            composable("battery") { battery(navController) }
            composable("speechassist") { speechassist(navController) }
            composable("ocrscanner") { ocrscanner(navController) }
            composable("games") { games(navController) }
            composable("wallet") { wallet(navController) }
            composable("phonemanager") { phonemanager(navController) }
            composable("oplusphonemanager") { oplusphonemanager(navController) }
            composable("mms") { mms(navController) }
            composable("securepay") { securepay(navController) }
            composable("health") { health(navController) }
            composable("appdetail") { appdetail(navController) }
            composable("func\\cpu_freq") { cpu_freq(navController) }
            composable("hide_apps_notice") { hide_apps_notice(navController) }
            composable("quicksearchbox") { quicksearchbox(navController) }
            composable("mihealth") { mihealth(navController) }
            composable("ota") { ota(navController) }
            composable("func\\romworkshop") { Rom_workshop(navController) }
            composable("oshare") { oshare(navController) }
            composable("incallui") { incallui(navController) }
            composable("notificationmanager") { notificationmanager(navController) }
            composable("exsystemservice") { exsystemservice(navController) }
            composable("phone") { phone(navController) }
            composable("padconnect") { padconnect(navController) }
        }
    }
}
