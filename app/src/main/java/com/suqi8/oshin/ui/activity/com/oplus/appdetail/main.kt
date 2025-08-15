package com.suqi8.oshin.ui.activity.com.oplus.appdetail

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.components.FunPage
import com.suqi8.oshin.ui.activity.components.FunSwich
import com.suqi8.oshin.ui.activity.components.addline
import com.suqi8.oshin.utils.GetAppName
import com.suqi8.oshin.ui.activity.components.Card

@SuppressLint("SuspiciousIndentation")
@Composable
fun appdetail(navController: NavController) {
    FunPage(
        title = GetAppName(packageName = "com.oplus.appdetail"),
        appList = listOf("com.oplus.appdetail"),
        navController = navController
    ) {
        Card {
            FunSwich(
                title = stringResource(R.string.remove_recommendations),
                category = "appdetail",
                key = "remove_recommendations"
            )
            addline()
            FunSwich(
                title = stringResource(R.string.remove_installation_frequency_popup),
                category = "appdetail",
                key = "remove_installation_frequency_popup"
            )
            addline()
            FunSwich(
                title = stringResource(R.string.remove_attempt_installation_popup),
                category = "appdetail",
                key = "remove_attempt_installation_popup"
            )
            addline()
            FunSwich(
                title = stringResource(R.string.remove_version_check),
                category = "appdetail",
                key = "remove_version_check"
            )
            addline()
            FunSwich(
                title = stringResource(R.string.remove_security_check),
                category = "appdetail",
                key = "remove_security_check"
            )
        }
    }
}
