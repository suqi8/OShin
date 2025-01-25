package com.suqi8.oshin.ui.activity.android

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.suqi8.oshin.GetAppName
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.funlistui.FunPage
import com.suqi8.oshin.ui.activity.funlistui.addline
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.extra.SuperArrow

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
                .padding(bottom = 6.dp,top = 15.dp)
        ) {
            SuperArrow(title = stringResource(id = R.string.package_manager_services),
                onClick = {
                    navController.navigate("android\\package_manager_services")
                })
            addline()
            SuperArrow(title = stringResource(id = R.string.oplus_system_services),
                onClick = {
                    navController.navigate("android\\oplus_system_services")
                })
        }
    }
}
