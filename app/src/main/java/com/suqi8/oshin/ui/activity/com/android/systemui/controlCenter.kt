package com.suqi8.oshin.ui.activity.com.android.systemui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.components.FunPage
import com.suqi8.oshin.ui.activity.components.FunSwich
import com.suqi8.oshin.ui.activity.components.addline
import com.suqi8.oshin.ui.activity.components.Card

@SuppressLint("SuspiciousIndentation")
@Composable
fun controlCenter(navController: NavController) {
    FunPage(
        title = stringResource(R.string.control_center),
        appList = listOf("com.android.systemui"),
        navController = navController
    ) {
        Card {
            val context = LocalContext.current
            val enlarge_media_cover = remember { mutableStateOf(context.prefs("systemui\\controlCenter").getBoolean("enlarge_media_cover",false)) }
            FunSwich(
                title = stringResource(R.string.enlarge_media_cover),
                summary = stringResource(R.string.media_cover_background_description),
                category = "systemui\\controlCenter",
                key = "enlarge_media_cover",
                defValue = false,
                onCheckedChange = {
                    enlarge_media_cover.value = it
                }
            )
            AnimatedVisibility(enlarge_media_cover.value) {
                addline()
                FunSwich(
                    title = stringResource(R.string.qs_media_auto_color_label),
                    category = "systemui\\controlCenter",
                    key = "qs_media_auto_color_label",
                    defValue = false
                )
            }
        }
    }
}
