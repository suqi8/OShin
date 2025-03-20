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
                    title = stringResource(R.string.retail_locked_terminal),
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
                addline()
                FunSwich(
                    title = stringResource(R.string.disable_gesture_navigation),
                    category = "settings",
                    key = "disable_gesture_navigation"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.disable_google_mobile_services),
                    category = "settings",
                    key = "disable_google_mobile_services"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.hide_storage_info),
                    category = "settings",
                    key = "hide_storage_info"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_holo_audio),
                    category = "settings",
                    key = "enable_holo_audio"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_hd_video),
                    category = "settings",
                    key = "force_hd_video"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.auto_grant_install),
                    category = "settings",
                    key = "auto_grant_install"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.disable_lock_wallpaper),
                    category = "settings",
                    key = "disable_lock_wallpaper"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.light_os),
                    category = "settings",
                    key = "light_os"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_multi_volume),
                    category = "settings",
                    key = "force_multi_volume"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_app_clone),
                    category = "settings",
                    key = "force_app_clone"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_adaptive_brightness),
                    category = "settings",
                    key = "force_adaptive_brightness"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.disable_ota),
                    category = "settings",
                    key = "disable_ota"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_audio_boost),
                    category = "settings",
                    key = "enable_audio_boost"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_ai_image),
                    category = "settings",
                    key = "enable_ai_image"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_osie_tech),
                    category = "settings",
                    key = "enable_osie_tech"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_shutdown_key),
                    category = "settings",
                    key = "force_shutdown_key"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.single_pulse_pwm),
                    category = "settings",
                    key = "single_pulse_pwm"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.disable_res_switch),
                    category = "settings",
                    key = "disable_res_switch"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.manual_refresh_rate),
                    category = "settings",
                    key = "manual_refresh_rate"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.default_smart_refresh),
                    category = "settings",
                    key = "default_smart_refresh"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.refresh_rate_notify),
                    category = "settings",
                    key = "refresh_rate_notify"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_sell_mode),
                    category = "settings",
                    key = "enable_sell_mode" // 保持 XML 的 name 不变
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_dual_sim),
                    category = "settings",
                    key = "enable_dual_sim"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_single_sim_check),
                    category = "settings",
                    key = "disable_single_sim_check"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_anti_voyeur),
                    category = "settings",
                    key = "enable_anti_voyeur"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_snc_content),
                    category = "settings",
                    key = "enable_snc_content"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_sound_combo),
                    category = "settings",
                    key = "enable_sound_combo"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_sound_settings),
                    category = "settings",
                    key = "enable_sound_settings"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_audio_input),
                    category = "settings",
                    key = "enable_audio_input"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_15k_resolution),
                    category = "settings",
                    key = "enable_1.5k_resolution"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_adfr),
                    category = "settings",
                    key = "enable_adfr"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_aod),
                    category = "settings",
                    key = "enable_aod"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_aon_face),
                    category = "settings",
                    key = "enable_aon_face"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_autolayout),
                    category = "settings",
                    key = "enable_autolayout"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_blade_colormode),
                    category = "settings",
                    key = "enable_blade_colormode"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_breeno_suggest),
                    category = "settings",
                    key = "enable_breeno_suggest"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_brightness_anim),
                    category = "settings",
                    key = "enable_brightness_anim"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_cinema_mode),
                    category = "settings",
                    key = "enable_cinema_mode"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_oled_colorful),
                    category = "settings",
                    key = "enable_oled_colorful"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_custom_color),
                    category = "settings",
                    key = "enable_custom_color"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_colorful_mode),
                    category = "settings",
                    key = "enable_colorful_mode"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_powersaving_color),
                    category = "settings",
                    key = "enable_powersaving_color"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_compact_window),
                    category = "settings",
                    key = "enable_compact_window"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_dc_backlight),
                    category = "settings",
                    key = "enable_dc_backlight"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_dynamic_brightness),
                    category = "settings",
                    key = "enable_dynamic_brightness"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_dirac_a2dp),
                    category = "settings",
                    key = "enable_dirac_a2dp"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_dynamic_fps),
                    category = "settings",
                    key = "enable_dynamic_fps"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_edge_anti_touch),
                    category = "settings",
                    key = "enable_edge_anti_touch"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_5g_support),
                    category = "settings",
                    key = "enable_5g_support"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_fold_remap),
                    category = "settings",
                    key = "disable_fold_remap"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_gt_mode),
                    category = "settings",
                    key = "enable_gt_mode"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_hdr_alwayson),
                    category = "settings",
                    key = "enable_hdr_alwayson"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_hdr_highlight),
                    category = "settings",
                    key = "enable_hdr_highlight"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_smart_color_temp2),
                    category = "settings",
                    key = "enable_smart_color_temp2"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_linear_vibration),
                    category = "settings",
                    key = "enable_linear_vibration"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_luxun_vibration),
                    category = "settings",
                    key = "enable_luxun_vibration"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_multi_led_breathing),
                    category = "settings",
                    key = "enable_multi_led_breathing"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_phone_limit),
                    category = "settings",
                    key = "enable_phone_limit"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_pixelworks_x7),
                    category = "settings",
                    key = "enable_pixelworks_x7"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_resolution_switch),
                    category = "settings",
                    key = "enable_resolution_switch"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_ringtone_vibration),
                    category = "settings",
                    key = "enable_ringtone_vibration"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_satellite_network),
                    category = "settings",
                    key = "enable_satellite_network"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_spatializer_speaker),
                    category = "settings",
                    key = "enable_spatializer_speaker"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_super_volume2x),
                    category = "settings",
                    key = "enable_super_volume2x"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_super_volume3x),
                    category = "settings",
                    key = "enable_super_volume3x"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_temp_adjust),
                    category = "settings",
                    key = "enable_temp_adjust"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_touchpad_split),
                    category = "settings",
                    key = "enable_touchpad_split"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_ultrasonic_fp),
                    category = "settings",
                    key = "enable_ultrasonic_fp"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_volume_boost),
                    category = "settings",
                    key = "enable_volume_boost"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_color_ball),
                    category = "settings",
                    key = "enable_color_ball"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_surround_effect),
                    category = "settings",
                    key = "enable_surround_effect"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_tablet_mode),
                    category = "settings",
                    key = "enable_tablet_mode"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_typec_menu),
                    category = "settings",
                    key = "enable_typec_menu"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_ultrasonic_security),
                    category = "settings",
                    key = "enable_ultrasonic_security"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_vibrator_style),
                    category = "settings",
                    key = "enable_vibrator_style"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_smart_screenoff),
                    category = "settings",
                    key = "enable_smart_screenoff"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_richtap_vibrate),
                    category = "settings",
                    key = "enable_richtap_vibrate"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_dirac_v2),
                    category = "settings",
                    key = "enable_dirac_v2"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_iris5_display),
                    category = "settings",
                    key = "enable_iris5_display"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_ring_haptic),
                    category = "settings",
                    key = "enable_ring_haptic"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_video_osie),
                    category = "settings",
                    key = "enable_video_osie"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_video_sr),
                    category = "settings",
                    key = "enable_video_sr"
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
