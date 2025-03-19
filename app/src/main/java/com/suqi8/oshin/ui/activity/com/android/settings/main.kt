package com.suqi8.oshin.ui.activity.com.android.settings

import android.annotation.SuppressLint
import android.os.Environment
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
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
import com.suqi8.oshin.ui.activity.funlistui.FunPage
import com.suqi8.oshin.ui.activity.funlistui.FunPicSele
import com.suqi8.oshin.ui.activity.funlistui.FunSlider
import com.suqi8.oshin.ui.activity.funlistui.FunString
import com.suqi8.oshin.ui.activity.funlistui.FunSwich
import com.suqi8.oshin.ui.activity.funlistui.WantFind
import com.suqi8.oshin.ui.activity.funlistui.addline
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.SmallTitle

@SuppressLint("SuspiciousIndentation")
@Composable
fun settings(navController: NavController) {
    FunPage(
        title = GetAppName(packageName = "com.android.settings"),
        appList = listOf("com.android.settings"),
        navController = navController
    ) {
        Column {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 6.dp, top = 6.dp)
            ) {
                val context = LocalContext.current
                FunString(
                    title = stringResource(R.string.custom_display_model),
                    summary = stringResource(R.string.hint_empty_content_default),
                    category = "settings",
                    key = "custom_display_model",
                    defValue = "",
                    nullable = true
                )
                addline()
                val ota_card_bg = remember { mutableStateOf(context.prefs("settings").getBoolean("enable_ota_card_bg", false)) }
                FunSwich(
                    title = stringResource(R.string.enable_ota_card_bg),
                    category = "settings",
                    key = "enable_ota_card_bg",
                    onCheckedChange = {
                        ota_card_bg.value = it
                    }
                )
                AnimatedVisibility(ota_card_bg.value) {
                    Column {
                        addline()
                        FunPicSele(
                            title = stringResource(R.string.select_background_btn),
                            category = "settings",
                            key = "ota_card_bg",
                            route = "${Environment.getExternalStorageDirectory()}/.OShin/settings/ota_card.png"
                        )
                        addline()
                        FunSlider(
                            title = stringResource(R.string.corner_radius_title),
                            category = "settings",
                            key = "ota_corner_radius",
                            defValue = 0f,
                            endtype = "px",
                            max = 300f,
                            min = 0f,
                            decimalPlaces = 1
                        )
                    }
                }
                addline()
                FunSwich(
                    title = stringResource(R.string.force_show_nfc_security_chip),
                    category = "settings",
                    key = "force_show_nfc_security_chip"
                )
            }
            SmallTitle(stringResource(R.string.feature))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 6.dp)
            ) {
                FunSwich(
                    title = stringResource(R.string.force_enable_all_features),
                    summary = stringResource(R.string.enable_all_features_warning),
                    category = "settings",
                    key = "force_enable_all_features"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.demo_only_device),
                    category = "settings",
                    key = "retail_locked_terminal"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.demo_only_device),
                    category = "settings",
                    key = "retail_locked_terminal"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_enable_karaoke),
                    category = "settings",
                    key = "force_enable_karaoke"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_enable_3d_camera_color),
                    category = "settings",
                    key = "force_enable_3d_camera_color"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_aon_explorer),
                    category = "settings",
                    key = "force_aon_explorer"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_enable_app_freeze),
                    category = "settings",
                    key = "force_enable_app_freeze"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.check_ble_audio_whitelist),
                    category = "settings",
                    key = "check_ble_audio_whitelist"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_breathing_light_sync),
                    category = "settings",
                    key = "force_breathing_light_sync"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_breathing_light_color),
                    category = "settings",
                    key = "force_breathing_light_color"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_breathing_light_color),
                    category = "settings",
                    key = "force_breathing_light_color"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_support_wide_gamut),
                    category = "settings",
                    key = "force_support_wide_gamut"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_support_color_mode),
                    category = "settings",
                    key = "force_support_color_mode"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_support_hidden_app_feature),
                    category = "settings",
                    key = "force_support_hidden_app_feature"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_support_smart_case),
                    category = "settings",
                    key = "force_support_smart_case"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_dirac_audio),
                    category = "settings",
                    key = "force_dirac_audio"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_dolby_audio),
                    category = "settings",
                    key = "force_dolby_audio"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_dual_earbuds),
                    category = "settings",
                    key = "force_dual_earbuds"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_foldable_screen),
                    category = "settings",
                    key = "force_foldable_screen"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_fold_or_flip_screen),
                    category = "settings",
                    key = "force_fold_or_flip_screen"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.disable_display_remapping),
                    summary = stringResource(R.string.disable_ui_remap_when_unfolded),
                    category = "settings",
                    key = "disable_display_remapping"
                )
            }
            WantFind(
                listOf(
                    WantFind(stringResource(R.string.auto_start_max_limit),"battery")
                ),
                navController
            )
        }
    }
}
