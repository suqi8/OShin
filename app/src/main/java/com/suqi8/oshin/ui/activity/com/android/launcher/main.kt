package com.suqi8.oshin.ui.activity.com.android.launcher

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
import com.suqi8.oshin.ui.activity.components.Card
import com.suqi8.oshin.ui.activity.components.FunArrow
import com.suqi8.oshin.ui.activity.components.FunDropdown
import com.suqi8.oshin.ui.activity.components.FunPage
import com.suqi8.oshin.ui.activity.components.FunSlider
import com.suqi8.oshin.ui.activity.components.FunSwich
import com.suqi8.oshin.ui.activity.components.addline
import com.suqi8.oshin.utils.GetAppName

@SuppressLint("SuspiciousIndentation")
@Composable
fun launcher(navController: NavController) {
    FunPage(
        title = GetAppName(packageName = "com.android.launcher"),
        appList = listOf("com.android.launcher"),
        navController = navController
    ) {
        Card {
            FunArrow(title = stringResource(id = R.string.recent_tasks),
                onClick = {
                    navController.navigate("launcher\\recent_task")
                })
        }
        Card {
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
            addline()
            FunSwich(
                title = stringResource(R.string.force_enable_fold_dock),
                category = "launcher",
                key = "force_enable_fold_dock"
            )
            addline()
            FunSlider(
                title = stringResource(R.string.adjust_dock_transparency),
                category = "launcher",
                key = "dock_transparency",
                defValue = 1f,
                endtype = "f",
                max = 10f,
                min = 0f,
                decimalPlaces = 2
            )
            addline()
            FunSwich(
                title = stringResource(R.string.force_enable_dock_blur),
                summary = stringResource(R.string.force_enable_dock_blur_undevice),
                category = "launcher",
                key = "force_enable_dock_blur"
            )
            addline()
            FunSlider(
                title = stringResource(R.string.set_anim_level),
                category = "launcher",
                key = "set_anim_level",
                max = 4f,
                min = -1f,
                defValue = -1,
                decimalPlaces = 0
            )
        }
    }
}
