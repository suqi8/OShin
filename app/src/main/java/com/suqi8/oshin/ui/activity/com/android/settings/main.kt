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
                addline()
                FunSwich(
                    title = stringResource(R.string.disable_deactivate_app),
                    category = "settings",
                    key = "disable_deactivate_app"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_haptic_preview),
                    category = "settings",
                    key = "disable_haptic_preview"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_modify_devname),
                    category = "settings",
                    key = "disable_modify_devname"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_super_sleep),
                    category = "settings",
                    key = "enable_super_sleep"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.disable_5g_reminder),
                    category = "settings",
                    key = "disable_5g_reminder"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_account_dialog),
                    category = "settings",
                    key = "disable_account_dialog"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_app_disable),
                    category = "settings",
                    key = "enable_app_disable"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_cmiit_auth),
                    category = "settings",
                    key = "hide_cmiit_auth"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_hyper_vision),
                    category = "settings",
                    key = "enable_hyper_vision"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_carrier),
                    category = "settings",
                    key = "disable_carrier"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.locale_uk_to_en),
                    category = "settings",
                    key = "locale_uk_to_en"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_clear_cache),
                    category = "settings",
                    key = "disable_clear_cache"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_colorful_real),
                    category = "settings",
                    key = "enable_colorful_real"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_confidential),
                    category = "settings",
                    key = "disable_confidential"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_cyberpunk),
                    category = "settings",
                    key = "enable_cyberpunk"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.auto_resolution),
                    category = "settings",
                    key = "auto_resolution"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_oem_unlock),
                    category = "settings",
                    key = "enable_oem_unlock"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_auto_rotate),
                    category = "settings",
                    key = "disable_auto_rotate"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_app_switch),
                    category = "settings",
                    key = "disable_app_switch"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_euex),
                    category = "settings",
                    key = "enable_euex"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.force_exp_version),
                    category = "settings",
                    key = "force_exp_version"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_film_finger),
                    category = "settings",
                    key = "enable_film_finger"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_finger_anim),
                    category = "settings",
                    key = "enable_finger_anim"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_fintech_nfc),
                    category = "settings",
                    key = "enable_fintech_nfc"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.force_flip_device),
                    category = "settings",
                    key = "force_flip_device"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_gesture),
                    category = "settings",
                    key = "disable_gesture"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.keep_gesture_up),
                    category = "settings",
                    key = "keep_gesture_up"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.more_gesture_up),
                    category = "settings",
                    key = "more_gesture_up"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_gota_update),
                    category = "settings",
                    key = "enable_gota_update"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_business_state),
                    category = "settings",
                    key = "enable_business_state"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_ultimate_clean),
                    category = "settings",
                    key = "enable_ultimate_clean"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_hw_version),
                    category = "settings",
                    key = "hide_hw_version"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_device_id),
                    category = "settings",
                    key = "hide_device_id"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.hide_ktv_loopback),
                    category = "settings",
                    key = "hide_ktv_loopback"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_mms_ringtone),
                    category = "settings",
                    key = "hide_mms_ringtone"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.move_dc_to_dev),
                    category = "settings",
                    key = "move_dc_to_dev"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_network_speed),
                    category = "settings",
                    key = "hide_network_speed"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_power_wake3),
                    category = "settings",
                    key = "hide_power_wake3"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_sim_signal),
                    category = "settings",
                    key = "hide_sim_signal"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_humming),
                    category = "settings",
                    key = "enable_humming"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_kernel_id),
                    category = "settings",
                    key = "show_kernel_id"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.ignore_repeat_click),
                    category = "settings",
                    key = "ignore_repeat_click"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.imei_sv_from_ota),
                    category = "settings",
                    key = "imei_sv_from_ota"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_light_func),
                    category = "settings",
                    key = "enable_light_func"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_marvel),
                    category = "settings",
                    key = "enable_marvel"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_portrait_center),
                    category = "settings",
                    key = "hide_portrait_center"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_video_beauty),
                    category = "settings",
                    key = "hide_video_beauty"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_2g3g),
                    category = "settings",
                    key = "show_2g3g"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_ocloud),
                    category = "settings",
                    key = "disable_ocloud"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.force_oh_device),
                    category = "settings",
                    key = "force_oh_device"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.only_hw_version),
                    category = "settings",
                    key = "only_hw_version"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_kddi_au),
                    category = "settings",
                    key = "enable_kddi_au"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_operator),
                    category = "settings",
                    key = "show_operator"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_privacy_email),
                    category = "settings",
                    key = "hide_privacy_email"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.keep_swipe_up),
                    category = "settings",
                    key = "keep_swipe_up"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_ota),
                    category = "settings",
                    key = "disable_ota"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_otg_alarm),
                    category = "settings",
                    key = "disable_otg_alarm"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.disable_otg_entry),
                    category = "settings",
                    key = "disable_otg_entry"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_pac_custom),
                    category = "settings",
                    key = "enable_pac_custom"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_privacy),
                    category = "settings",
                    key = "disable_privacy"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_fake_base),
                    category = "settings",
                    key = "hide_fake_base"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_rl_delete),
                    category = "settings",
                    key = "enable_rl_delete"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.force_rlm_device),
                    category = "settings",
                    key = "force_rlm_device"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_raise_wake),
                    category = "settings",
                    key = "enable_raise_wake"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_recent_task),
                    category = "settings",
                    key = "disable_recent_task"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.remove_cota_home),
                    category = "settings",
                    key = "remove_cota_home"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_resize_screen),
                    category = "settings",
                    key = "disable_resize_screen"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_rlm_feedback),
                    category = "settings",
                    key = "enable_rlm_feedback"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_screen_pin),
                    category = "settings",
                    key = "disable_screen_pin"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_search_index),
                    category = "settings",
                    key = "disable_search_index"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_seedling_exp),
                    category = "settings",
                    key = "enable_seedling_exp"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_custom_devname),
                    category = "settings",
                    key = "enable_custom_devname"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_cota_devname),
                    category = "settings",
                    key = "enable_cota_devname"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_set_password),
                    category = "settings",
                    key = "disable_set_password"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_all_anr),
                    category = "settings",
                    key = "hide_all_anr"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_brand_name),
                    category = "settings",
                    key = "show_brand_name"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_carrier_config),
                    category = "settings",
                    key = "show_carrier_config"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_carrier_update),
                    category = "settings",
                    key = "show_carrier_update"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_custom_details),
                    category = "settings",
                    key = "show_custom_details"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_data_usage),
                    category = "settings",
                    key = "hide_data_usage"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_diagnostic),
                    category = "settings",
                    key = "show_diagnostic"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_os_firstname),
                    category = "settings",
                    key = "show_os_firstname"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_hw_version),
                    category = "settings",
                    key = "show_hw_version"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_ims_status),
                    category = "settings",
                    key = "show_ims_status"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.show_kernel_time),
                    category = "advanced",
                    key = "show_kernel_time"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_net_unlock),
                    category = "advanced",
                    key = "show_net_unlock"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_never_timeout),
                    category = "display",
                    key = "show_never_timeout"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_npu_detail),
                    category = "system",
                    key = "hide_npu_detail"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_processor),
                    category = "about",
                    key = "show_processor"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_processor_gen2),
                    category = "about",
                    key = "show_processor_gen2"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.screen_size_cm),
                    category = "display",
                    key = "screen_size_cm"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_sw_version),
                    category = "about",
                    key = "show_sw_version"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.sw_instead_build),
                    category = "about",
                    key = "sw_instead_build"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_uicc_unlock),
                    category = "security",
                    key = "show_uicc_unlock"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_sim_lock),
                    category = "security",
                    key = "enable_sim_lock"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_sim_toolkit),
                    category = "system",
                    key = "hide_sim_toolkit"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.force_software_conf),
                    category = "debug",
                    key = "force_software_conf"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.special_side_finger),
                    category = "biometrics",
                    key = "special_side_finger"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_circle_search),
                    category = "features",
                    key = "enable_circle_search"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_custom_ver),
                    category = "system",
                    key = "show_custom_ver"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_electronic_label),
                    category = "about",
                    key = "enable_electronic_label"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.fullscreen_apps),
                    category = "display",
                    key = "fullscreen_apps"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.smart_gesture),
                    category = "gesture",
                    key = "smart_gesture"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_imsi),
                    category = "network",
                    key = "show_imsi"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_meid),
                    category = "network",
                    key = "show_meid"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.member_rcc_show),
                    category = "members",
                    key = "member_rcc_show"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.mini_capsule),
                    category = "features",
                    key = "mini_capsule"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.number_recognition),
                    category = "messaging",
                    key = "number_recognition"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_oguard),
                    category = "security",
                    key = "enable_oguard"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.oh_india_version),
                    category = "region",
                    key = "oh_india_version"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.usb_tether_boot),
                    category = "network",
                    key = "usb_tether_boot"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.quick_app_support),
                    category = "apps",
                    key = "quick_app_support"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.region_picker),
                    category = "region",
                    key = "region_picker"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_roulette),
                    category = "features",
                    key = "enable_roulette"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_wfc_dialog),
                    category = "network",
                    key = "show_wfc_dialog"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.smart_touch),
                    category = "gesture",
                    key = "smart_touch"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.smart_touch_v2),
                    category = "gesture",
                    key = "smart_touch_v2"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_sms_number),
                    category = "messaging",
                    key = "show_sms_number"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.ai_eye_protect),
                    category = "display",
                    key = "ai_eye_protect"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_edge_panel),
                    category = "system",
                    key = "disable_edge_panel"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_stable_plan),
                    category = "system",
                    key = "disable_stable_plan"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_time_change),
                    category = "datetime",
                    key = "disable_time_change"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_gaze_ringtone),
                    category = "sound",
                    key = "disable_gaze_ringtone"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_user_exp),
                    category = "privacy",
                    key = "disable_user_exp"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_verify_dialog),
                    category = "security",
                    key = "disable_verify_dialog"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.virtual_comm_device),
                    category = "network",
                    key = "virtual_comm_device"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.virtual_comm_service),
                    category = "network",
                    key = "virtual_comm_service"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_vowifi_setting),
                    category = "network",
                    key = "disable_vowifi_setting"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.disable_volte_setting),
                    category = "network",
                    key = "disable_volte_setting"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.volte_icon_off),
                    category = "statusbar",
                    key = "volte_icon_off"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_wifi_setting),
                    category = "network",
                    key = "disable_wifi_setting"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_install_sources),
                    category = "security",
                    key = "hide_install_sources"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.biometric_privacy),
                    category = "privacy",
                    key = "biometric_privacy"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.upload_error_log),
                    category = "system",
                    key = "upload_error_log"
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
