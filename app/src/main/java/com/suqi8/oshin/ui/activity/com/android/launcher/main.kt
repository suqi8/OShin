package com.suqi8.oshin.ui.activity.com.android.launcher

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.GetAppName
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.funlistui.FunDropdown
import com.suqi8.oshin.ui.activity.funlistui.FunPage
import com.suqi8.oshin.ui.activity.funlistui.FunSlider
import com.suqi8.oshin.ui.activity.funlistui.FunSwich
import com.suqi8.oshin.ui.activity.funlistui.addline
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.extra.SuperArrow

@SuppressLint("SuspiciousIndentation")
@Composable
fun launcher(navController: NavController) {
    FunPage(
        title = GetAppName(packageName = "com.android.launcher"),
        appList = listOf("com.android.launcher"),
        navController = navController
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 6.dp, top = 6.dp)
        ) {
            SuperArrow(title = stringResource(id = R.string.recent_tasks),
                onClick = {
                    navController.navigate("launcher\\recent_task")
                })
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 6.dp, top = 6.dp)
        ) {
            FunSlider(
                title = stringResource(R.string.desktop_icon_and_text_size_multiplier),
                summary = stringResource(R.string.icon_size_limit_note),
                category = "launcher",
                key = "icon_text",
                defValue = 1.0f,
                endtype = "x",
                max = 2f,
                min = 0f,
                decimalPlaces = 1
            )
            addline()
            val context = LocalContext.current
            val force_enable_fold_mode = remember { mutableStateOf(context.prefs("launcher").getBoolean("force_enable_fold_mode", false)) }
            FunSwich(
                title = stringResource(R.string.force_enable_fold_mode),
                category = "launcher",
                key = "force_enable_fold_mode",
                onCheckedChange = {
                    force_enable_fold_mode.value = it
                }
            )
            AnimatedVisibility(force_enable_fold_mode.value) {
                addline()
                FunDropdown(
                    title = stringResource(R.string.fold_mode),
                    category = "launcher",
                    key = "fold_mode",
                    selectedList = listOf(stringResource(R.string.unfold), stringResource(R.string.fold))
                )
            }
        }
    }
}
