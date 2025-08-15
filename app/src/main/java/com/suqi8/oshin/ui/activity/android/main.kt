package com.suqi8.oshin.ui.activity.android

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.components.Card
import com.suqi8.oshin.ui.activity.components.FunArrow
import com.suqi8.oshin.ui.activity.components.FunPage
import com.suqi8.oshin.ui.activity.components.FunSwich
import com.suqi8.oshin.ui.activity.components.WantFind
import com.suqi8.oshin.ui.activity.components.addline
import com.suqi8.oshin.utils.GetAppName

@Composable
fun android(navController: NavController) {
    FunPage(
        title = GetAppName(packageName = "android"),
        appList = listOf("android"),
        navController = navController
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 6.dp,top = 6.dp)
        ) {
            FunArrow(title = stringResource(id = R.string.package_manager_services),
                onClick = {
                    navController.navigate("android\\package_manager_services")
                })
            addline()
            FunArrow(title = stringResource(id = R.string.oplus_system_services),
                onClick = {
                    navController.navigate("android\\oplus_system_services")
                })
        }
        Card(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 6.dp,top = 6.dp)
        ) {
            FunArrow(title = stringResource(id = R.string.split_screen_multi_window),
                onClick = {
                    navController.navigate("android\\split_screen_multi_window")
                })
        }
        Card(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 6.dp,top = 6.dp)
        ) {
            FunSwich(
                title = stringResource(R.string.disable_72h_verify),
                category = "android",
                key = "DisablePinVerifyPer72h"
            )
            addline()
            FunSwich(
                title = stringResource(R.string.allow_untrusted_touch),
                category = "android",
                key = "AllowUntrustedTouch"
            )
        }
        WantFind(
            listOf(
                WantFind(stringResource(R.string.allow_turn_off_all_categories),"notificationmanager")
            ),
            navController
        )
    }
}
