package com.suqi8.oshin.ui.activity.android

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.funlistui.FunPage
import com.suqi8.oshin.ui.activity.funlistui.FunSwich
import top.yukonga.miuix.kmp.basic.Card

@Composable
fun oplus_services(navController: NavController) {
    FunPage(
        title = stringResource(id = R.string.oplus_system_services),
        appList = listOf("android"),
        navController = navController
    ) {
        Column {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 6.dp, top = 15.dp)
            ) {
                FunSwich(
                    title = stringResource(R.string.oplus_root_check),
                    summary = stringResource(R.string.oplus_root_check_summary),
                    category = "android\\oplus_system_services",
                    key = "disable_root_check"
                )
            }
        }
    }
}
