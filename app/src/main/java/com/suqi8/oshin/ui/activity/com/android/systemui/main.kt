package com.suqi8.oshin.ui.activity.com.android.systemui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.GetAppName
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.funlistui.FunPage
import com.suqi8.oshin.ui.activity.funlistui.FunSwich
import com.suqi8.oshin.ui.activity.funlistui.addline
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.extra.SuperArrow

@SuppressLint("SuspiciousIndentation")
@Composable
fun systemui(navController: NavController) {
    val context = LocalContext.current
    FunPage(
        title = GetAppName(packageName = "com.android.systemui"),
        appList = listOf("com.android.systemui"),
        navController = navController
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 6.dp, top = 15.dp)
        ) {
            SuperArrow(title = stringResource(id = R.string.status_bar_clock),
                onClick = {
                    navController.navigate("systemui\\status_bar_clock")
                })
            addline()
            SuperArrow(title = stringResource(id = R.string.hardware_indicator),
                onClick = {
                    navController.navigate("systemui\\hardware_indicator")
                })
            addline()
            SuperArrow(title = stringResource(id = R.string.status_bar_icon),
                onClick = {
                    navController.navigate("systemui\\statusbar_icon")
                })
            addline()
            SuperArrow(title = stringResource(id = R.string.status_bar_notification),
                onClick = {
                    navController.navigate("systemui\\notification")
                })
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 6.dp, top = 6.dp)
        ) {
            FunSwich(
                title = stringResource(R.string.hide_status_bar),
                category = "systemui",
                key = "hide_status_bar",
                defValue = false
            )
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 6.dp, top = 6.dp)
        ) {
            val enable_all_day_screen_off = remember { mutableStateOf(context.prefs("systemui").getBoolean("enable_all_day_screen_off", false)) }
            FunSwich(
                title = stringResource(R.string.enable_all_day_screen_off),
                category = "systemui",
                key = "enable_all_day_screen_off",
                defValue = false,
                onCheckedChange = {
                    enable_all_day_screen_off.value = it
                }
            )
            AnimatedVisibility(enable_all_day_screen_off.value) {
                addline()
                FunSwich(
                    title = stringResource(R.string.force_trigger_ltpo),
                    category = "systemui",
                    key = "force_trigger_ltpo",
                    defValue = true
                )
            }
        }
    }
}
