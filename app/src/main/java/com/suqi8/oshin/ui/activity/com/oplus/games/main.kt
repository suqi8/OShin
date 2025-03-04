package com.suqi8.oshin.ui.activity.com.oplus.games

import android.annotation.SuppressLint
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
import com.suqi8.oshin.ui.activity.funlistui.FunSwich
import com.suqi8.oshin.ui.activity.funlistui.addline
import top.yukonga.miuix.kmp.basic.Card

@SuppressLint("SuspiciousIndentation")
@Composable
fun games(navController: NavController) {
    FunPage(
        title = GetAppName(packageName = "com.oplus.games"),
        appList = listOf("com.oplus.games"),
        navController = navController
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 6.dp, top = 6.dp)
        ) {
            FunSwich(
                title = stringResource(R.string.enable_ultra_combo),
                category = "games",
                key = "ultra_combo"
            )
            addline()
            FunSwich(
                title = stringResource(R.string.enable_hok_ai_v1),
                category = "games",
                key = "hok_ai_v1"
            )
            addline()
            FunSwich(
                title = stringResource(R.string.enable_hok_ai_v2),
                summary = stringResource(R.string.realme_gt7pro_feature_unlock_device_restriction),
                category = "games",
                key = "hok_ai_v2"
            )
            addline()
            FunSwich(
                title = stringResource(R.string.enable_hok_ai_v3),
                category = "games",
                key = "hok_ai_v3"
            )
            addline()
            FunSwich(
                title = stringResource(R.string.hok_ai_assistant_remove_pkg_restriction),
                summary = stringResource(R.string.ai_assistant_global_display),
                category = "games",
                key = "hok_ai_remove_pkg_restriction"
            )
        }
    }
}
