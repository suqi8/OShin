package com.suqi8.oshin.ui.activity

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.suqi8.oshin.R
import com.suqi8.oshin.notInstalledApps
import com.suqi8.oshin.ui.activity.components.Card
import com.suqi8.oshin.ui.activity.components.FunPage
import top.yukonga.miuix.kmp.basic.Text

@Composable
fun hide_apps_notice(navController: NavController) {
    FunPage(
        title = stringResource(R.string.help),
        navController = navController
    ) {
        Card() {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(stringResource(R.string.hide_apps_notice, notInstalledApps.value.size))
                Text(notInstalledApps.value.joinToString(separator = ", "))
            }
        }
    }
}
