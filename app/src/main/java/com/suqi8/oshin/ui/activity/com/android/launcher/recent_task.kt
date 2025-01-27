package com.suqi8.oshin.ui.activity.com.android.launcher

import android.annotation.SuppressLint
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

@SuppressLint("SuspiciousIndentation")
@Composable
fun recent_task(navController: NavController) {
    FunPage(
        title = stringResource(R.string.recent_tasks),
        appList = listOf("com.android.launcher"),
        navController = navController
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 6.dp, top = 15.dp)
        ) {
            FunSwich(
                title = stringResource(R.string.force_display_memory),
                category = "launcher\\recent_task",
                key = "force_display_memory",
                defValue = false
            )
        }
    }
}
