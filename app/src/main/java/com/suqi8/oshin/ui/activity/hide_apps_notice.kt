package com.suqi8.oshin.ui.activity

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.suqi8.oshin.R
import com.suqi8.oshin.notInstallList
import com.suqi8.oshin.ui.activity.funlistui.FunPage
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text

@Composable
fun hide_apps_notice(navController: NavController) {
    FunPage(
        title = stringResource(R.string.help),
        navController = navController
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 6.dp,top = 15.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(stringResource(R.string.hide_apps_notice, notInstallList.value.size))
                Text(notInstallList.value.joinToString(separator = ", "))
            }
        }
    }
}
