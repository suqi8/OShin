package com.suqi8.oshin.ui.activity

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.components.FunPage

@Composable
fun hide_apps_notice(navController: NavController) {
    FunPage(
        title = stringResource(R.string.help),
        navController = navController
    ) {
        /*Card() {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(stringResource(R.string.hide_apps_notice, notInstalledApps.value.size))
                Text(notInstalledApps.value.joinToString(separator = ", "))
            }
        }*/
    }
}
