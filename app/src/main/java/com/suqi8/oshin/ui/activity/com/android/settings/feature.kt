package com.suqi8.oshin.ui.activity.com.android.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.funlistui.FunPage
import com.suqi8.oshin.ui.activity.funlistui.FunSwich
import com.suqi8.oshin.ui.activity.funlistui.addline
import top.yukonga.miuix.kmp.basic.Card

@SuppressLint("SuspiciousIndentation")
@Composable
fun feature(navController: NavController) {
    FunPage(
        title = stringResource(R.string.feature),
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
                FunSwich(
                    title = stringResource(R.string.force_enable_all_features),
                    summary = stringResource(R.string.enable_all_features_warning),
                    category = "settings\\feature",
                    key = "force_enable_all_features"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.demo_only_device),
                    category = "settings\\feature",
                    key = "retail_locked_terminal"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.retail_locked_terminal),
                    category = "settings\\feature",
                    key = "retail_locked_terminal"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_enable_karaoke),
                    category = "settings\\feature",
                    key = "force_enable_karaoke"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_enable_3d_camera_color),
                    category = "settings\\feature",
                    key = "force_enable_3d_camera_color"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_aon_explorer),
                    category = "settings\\feature",
                    key = "force_aon_explorer"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_enable_app_freeze),
                    category = "settings\\feature",
                    key = "force_enable_app_freeze"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.check_ble_audio_whitelist),
                    category = "settings\\feature",
                    key = "check_ble_audio_whitelist"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_breathing_light_sync),
                    category = "settings\\feature",
                    key = "force_breathing_light_sync"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_breathing_light_color),
                    category = "settings\\feature",
                    key = "force_breathing_light_color"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_support_wide_gamut),
                    category = "settings\\feature",
                    key = "force_support_wide_gamut"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_support_color_mode),
                    category = "settings\\feature",
                    key = "force_support_color_mode"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_support_hidden_app_feature),
                    category = "settings\\feature",
                    key = "force_support_hidden_app_feature"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_support_smart_case),
                    category = "settings\\feature",
                    key = "force_support_smart_case"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_dirac_audio),
                    category = "settings\\feature",
                    key = "force_dirac_audio"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_dolby_audio),
                    category = "settings\\feature",
                    key = "force_dolby_audio"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_dual_earbuds),
                    category = "settings\\feature",
                    key = "force_dual_earbuds"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_foldable_screen),
                    category = "settings\\feature",
                    key = "force_foldable_screen"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_fold_or_flip_screen),
                    category = "settings\\feature",
                    key = "force_fold_or_flip_screen"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.disable_display_remapping),
                    summary = stringResource(R.string.disable_ui_remap_when_unfolded),
                    category = "settings\\feature",
                    key = "disable_display_remapping"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.disable_gesture_navigation),
                    category = "settings\\feature",
                    key = "disable_gesture_navigation"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.disable_google_mobile_services),
                    category = "settings\\feature",
                    key = "disable_google_mobile_services"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.hide_storage_info),
                    category = "settings\\feature",
                    key = "hide_storage_info"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_holo_audio),
                    category = "settings\\feature",
                    key = "enable_holo_audio"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_hd_video),
                    category = "settings\\feature",
                    key = "force_hd_video"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.auto_grant_install),
                    category = "settings\\feature",
                    key = "auto_grant_install"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.disable_lock_wallpaper),
                    category = "settings\\feature",
                    key = "disable_lock_wallpaper"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.light_os),
                    category = "settings\\feature",
                    key = "light_os"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_multi_volume),
                    category = "settings\\feature",
                    key = "force_multi_volume"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_app_clone),
                    category = "settings\\feature",
                    key = "force_app_clone"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_adaptive_brightness),
                    category = "settings\\feature",
                    key = "force_adaptive_brightness"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.disable_ota),
                    category = "settings\\feature",
                    key = "disable_ota"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_audio_boost),
                    category = "settings\\feature",
                    key = "enable_audio_boost"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_ai_image),
                    category = "settings\\feature",
                    key = "enable_ai_image"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_osie_tech),
                    category = "settings\\feature",
                    key = "enable_osie_tech"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.force_shutdown_key),
                    category = "settings\\feature",
                    key = "force_shutdown_key"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.single_pulse_pwm),
                    category = "settings\\feature",
                    key = "single_pulse_pwm"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.disable_res_switch),
                    category = "settings\\feature",
                    key = "disable_res_switch"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.manual_refresh_rate),
                    category = "settings\\feature",
                    key = "manual_refresh_rate"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.default_smart_refresh),
                    category = "settings\\feature",
                    key = "default_smart_refresh"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.refresh_rate_notify),
                    category = "settings\\feature",
                    key = "refresh_rate_notify"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_sell_mode),
                    category = "settings\\feature",
                    key = "enable_sell_mode" // 保持 XML 的 name 不变
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_dual_sim),
                    category = "settings\\feature",
                    key = "enable_dual_sim"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_single_sim_check),
                    category = "settings\\feature",
                    key = "disable_single_sim_check"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_anti_voyeur),
                    category = "settings\\feature",
                    key = "enable_anti_voyeur"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_snc_content),
                    category = "settings\\feature",
                    key = "enable_snc_content"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_sound_combo),
                    category = "settings\\feature",
                    key = "enable_sound_combo"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_sound_settings),
                    category = "settings\\feature",
                    key = "enable_sound_settings"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_audio_input),
                    category = "settings\\feature",
                    key = "enable_audio_input"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_15k_resolution),
                    category = "settings\\feature",
                    key = "enable_1.5k_resolution"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_adfr),
                    category = "settings\\feature",
                    key = "enable_adfr"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_aod),
                    category = "settings\\feature",
                    key = "enable_aod"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_aon_face),
                    category = "settings\\feature",
                    key = "enable_aon_face"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_autolayout),
                    category = "settings\\feature",
                    key = "enable_autolayout"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_blade_colormode),
                    category = "settings\\feature",
                    key = "enable_blade_colormode"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_breeno_suggest),
                    category = "settings\\feature",
                    key = "enable_breeno_suggest"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_brightness_anim),
                    category = "settings\\feature",
                    key = "enable_brightness_anim"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_cinema_mode),
                    category = "settings\\feature",
                    key = "enable_cinema_mode"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_oled_colorful),
                    category = "settings\\feature",
                    key = "enable_oled_colorful"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_custom_color),
                    category = "settings\\feature",
                    key = "enable_custom_color"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_colorful_mode),
                    category = "settings\\feature",
                    key = "enable_colorful_mode"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_powersaving_color),
                    category = "settings\\feature",
                    key = "enable_powersaving_color"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_compact_window),
                    category = "settings\\feature",
                    key = "enable_compact_window"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_dc_backlight),
                    category = "settings\\feature",
                    key = "enable_dc_backlight"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_dynamic_brightness),
                    category = "settings\\feature",
                    key = "enable_dynamic_brightness"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_dirac_a2dp),
                    category = "settings\\feature",
                    key = "enable_dirac_a2dp"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_dynamic_fps),
                    category = "settings\\feature",
                    key = "enable_dynamic_fps"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_edge_anti_touch),
                    category = "settings\\feature",
                    key = "enable_edge_anti_touch"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_5g_support),
                    category = "settings\\feature",
                    key = "enable_5g_support"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_fold_remap),
                    category = "settings\\feature",
                    key = "disable_fold_remap"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_gt_mode),
                    category = "settings\\feature",
                    key = "enable_gt_mode"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_hdr_alwayson),
                    category = "settings\\feature",
                    key = "enable_hdr_alwayson"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_hdr_highlight),
                    category = "settings\\feature",
                    key = "enable_hdr_highlight"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_smart_color_temp2),
                    category = "settings\\feature",
                    key = "enable_smart_color_temp2"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_linear_vibration),
                    category = "settings\\feature",
                    key = "enable_linear_vibration"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_luxun_vibration),
                    category = "settings\\feature",
                    key = "enable_luxun_vibration"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_multi_led_breathing),
                    category = "settings\\feature",
                    key = "enable_multi_led_breathing"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_phone_limit),
                    category = "settings\\feature",
                    key = "enable_phone_limit"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_pixelworks_x7),
                    category = "settings\\feature",
                    key = "enable_pixelworks_x7"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_resolution_switch),
                    category = "settings\\feature",
                    key = "enable_resolution_switch"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_ringtone_vibration),
                    category = "settings\\feature",
                    key = "enable_ringtone_vibration"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_satellite_network),
                    category = "settings\\feature",
                    key = "enable_satellite_network"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_spatializer_speaker),
                    category = "settings\\feature",
                    key = "enable_spatializer_speaker"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_super_volume2x),
                    category = "settings\\feature",
                    key = "enable_super_volume2x"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_super_volume3x),
                    category = "settings\\feature",
                    key = "enable_super_volume3x"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_temp_adjust),
                    category = "settings\\feature",
                    key = "enable_temp_adjust"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_touchpad_split),
                    category = "settings\\feature",
                    key = "enable_touchpad_split"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_ultrasonic_fp),
                    category = "settings\\feature",
                    key = "enable_ultrasonic_fp"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_volume_boost),
                    category = "settings\\feature",
                    key = "enable_volume_boost"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_color_ball),
                    category = "settings\\feature",
                    key = "enable_color_ball"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_surround_effect),
                    category = "settings\\feature",
                    key = "enable_surround_effect"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_tablet_mode),
                    category = "settings\\feature",
                    key = "enable_tablet_mode"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_typec_menu),
                    category = "settings\\feature",
                    key = "enable_typec_menu"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_ultrasonic_security),
                    category = "settings\\feature",
                    key = "enable_ultrasonic_security"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_vibrator_style),
                    category = "settings\\feature",
                    key = "enable_vibrator_style"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_smart_screenoff),
                    category = "settings\\feature",
                    key = "enable_smart_screenoff"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_richtap_vibrate),
                    category = "settings\\feature",
                    key = "enable_richtap_vibrate"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_dirac_v2),
                    category = "settings\\feature",
                    key = "enable_dirac_v2"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_iris5_display),
                    category = "settings\\feature",
                    key = "enable_iris5_display"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_ring_haptic),
                    category = "settings\\feature",
                    key = "enable_ring_haptic"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_video_osie),
                    category = "settings\\feature",
                    key = "enable_video_osie"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_video_sr),
                    category = "settings\\feature",
                    key = "enable_video_sr"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.disable_deactivate_app),
                    category = "settings\\feature",
                    key = "disable_deactivate_app"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_haptic_preview),
                    category = "settings\\feature",
                    key = "disable_haptic_preview"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_modify_devname),
                    category = "settings\\feature",
                    key = "disable_modify_devname"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_super_sleep),
                    category = "settings\\feature",
                    key = "enable_super_sleep"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.disable_5g_reminder),
                    category = "settings\\feature",
                    key = "disable_5g_reminder"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_account_dialog),
                    category = "settings\\feature",
                    key = "disable_account_dialog"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_app_disable),
                    category = "settings\\feature",
                    key = "enable_app_disable"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_cmiit_auth),
                    category = "settings\\feature",
                    key = "hide_cmiit_auth"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_hyper_vision),
                    category = "settings\\feature",
                    key = "enable_hyper_vision"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_carrier),
                    category = "settings\\feature",
                    key = "disable_carrier"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.locale_uk_to_en),
                    category = "settings\\feature",
                    key = "locale_uk_to_en"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_clear_cache),
                    category = "settings\\feature",
                    key = "disable_clear_cache"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_colorful_real),
                    category = "settings\\feature",
                    key = "enable_colorful_real"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_confidential),
                    category = "settings\\feature",
                    key = "disable_confidential"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_cyberpunk),
                    category = "settings\\feature",
                    key = "enable_cyberpunk"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.auto_resolution),
                    category = "settings\\feature",
                    key = "auto_resolution"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_oem_unlock),
                    category = "settings\\feature",
                    key = "enable_oem_unlock"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_auto_rotate),
                    category = "settings\\feature",
                    key = "disable_auto_rotate"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_app_switch),
                    category = "settings\\feature",
                    key = "disable_app_switch"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_euex),
                    category = "settings\\feature",
                    key = "enable_euex"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.force_exp_version),
                    category = "settings\\feature",
                    key = "force_exp_version"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_film_finger),
                    category = "settings\\feature",
                    key = "enable_film_finger"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_finger_anim),
                    category = "settings\\feature",
                    key = "enable_finger_anim"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_fintech_nfc),
                    category = "settings\\feature",
                    key = "enable_fintech_nfc"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.force_flip_device),
                    category = "settings\\feature",
                    key = "force_flip_device"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_gesture),
                    category = "settings\\feature",
                    key = "disable_gesture"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.keep_gesture_up),
                    category = "settings\\feature",
                    key = "keep_gesture_up"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.more_gesture_up),
                    category = "settings\\feature",
                    key = "more_gesture_up"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_gota_update),
                    category = "settings\\feature",
                    key = "enable_gota_update"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_business_state),
                    category = "settings\\feature",
                    key = "enable_business_state"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_ultimate_clean),
                    category = "settings\\feature",
                    key = "enable_ultimate_clean"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_hw_version),
                    category = "settings\\feature",
                    key = "hide_hw_version"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_device_id),
                    category = "settings\\feature",
                    key = "hide_device_id"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.hide_ktv_loopback),
                    category = "settings\\feature",
                    key = "hide_ktv_loopback"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_mms_ringtone),
                    category = "settings\\feature",
                    key = "hide_mms_ringtone"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.move_dc_to_dev),
                    category = "settings\\feature",
                    key = "move_dc_to_dev"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_network_speed),
                    category = "settings\\feature",
                    key = "hide_network_speed"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_power_wake3),
                    category = "settings\\feature",
                    key = "hide_power_wake3"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_sim_signal),
                    category = "settings\\feature",
                    key = "hide_sim_signal"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_humming),
                    category = "settings\\feature",
                    key = "enable_humming"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_kernel_id),
                    category = "settings\\feature",
                    key = "show_kernel_id"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.ignore_repeat_click),
                    category = "settings\\feature",
                    key = "ignore_repeat_click"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.imei_sv_from_ota),
                    category = "settings\\feature",
                    key = "imei_sv_from_ota"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_light_func),
                    category = "settings\\feature",
                    key = "enable_light_func"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_marvel),
                    category = "settings\\feature",
                    key = "enable_marvel"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_portrait_center),
                    category = "settings\\feature",
                    key = "hide_portrait_center"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_video_beauty),
                    category = "settings\\feature",
                    key = "hide_video_beauty"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_2g3g),
                    category = "settings\\feature",
                    key = "show_2g3g"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_ocloud),
                    category = "settings\\feature",
                    key = "disable_ocloud"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.force_oh_device),
                    category = "settings\\feature",
                    key = "force_oh_device"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.only_hw_version),
                    category = "settings\\feature",
                    key = "only_hw_version"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_kddi_au),
                    category = "settings\\feature",
                    key = "enable_kddi_au"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_operator),
                    category = "settings\\feature",
                    key = "show_operator"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_privacy_email),
                    category = "settings\\feature",
                    key = "hide_privacy_email"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.keep_swipe_up),
                    category = "settings\\feature",
                    key = "keep_swipe_up"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_ota),
                    category = "settings\\feature",
                    key = "disable_ota"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_otg_alarm),
                    category = "settings\\feature",
                    key = "disable_otg_alarm"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.disable_otg_entry),
                    category = "settings\\feature",
                    key = "disable_otg_entry"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_pac_custom),
                    category = "settings\\feature",
                    key = "enable_pac_custom"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_privacy),
                    category = "settings\\feature",
                    key = "disable_privacy"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_fake_base),
                    category = "settings\\feature",
                    key = "hide_fake_base"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_rl_delete),
                    category = "settings\\feature",
                    key = "enable_rl_delete"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.force_rlm_device),
                    category = "settings\\feature",
                    key = "force_rlm_device"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_raise_wake),
                    category = "settings\\feature",
                    key = "enable_raise_wake"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_recent_task),
                    category = "settings\\feature",
                    key = "disable_recent_task"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.remove_cota_home),
                    category = "settings\\feature",
                    key = "remove_cota_home"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_resize_screen),
                    category = "settings\\feature",
                    key = "disable_resize_screen"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_rlm_feedback),
                    category = "settings\\feature",
                    key = "enable_rlm_feedback"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_screen_pin),
                    category = "settings\\feature",
                    key = "disable_screen_pin"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_search_index),
                    category = "settings\\feature",
                    key = "disable_search_index"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_seedling_exp),
                    category = "settings\\feature",
                    key = "enable_seedling_exp"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_custom_devname),
                    category = "settings\\feature",
                    key = "enable_custom_devname"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_cota_devname),
                    category = "settings\\feature",
                    key = "enable_cota_devname"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_set_password),
                    category = "settings\\feature",
                    key = "disable_set_password"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_all_anr),
                    category = "settings\\feature",
                    key = "hide_all_anr"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_brand_name),
                    category = "settings\\feature",
                    key = "show_brand_name"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_carrier_config),
                    category = "settings\\feature",
                    key = "show_carrier_config"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_carrier_update),
                    category = "settings\\feature",
                    key = "show_carrier_update"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_custom_details),
                    category = "settings\\feature",
                    key = "show_custom_details"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_data_usage),
                    category = "settings\\feature",
                    key = "hide_data_usage"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_diagnostic),
                    category = "settings\\feature",
                    key = "show_diagnostic"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_os_firstname),
                    category = "settings\\feature",
                    key = "show_os_firstname"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_hw_version),
                    category = "settings\\feature",
                    key = "show_hw_version"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_ims_status),
                    category = "settings\\feature",
                    key = "show_ims_status"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.show_kernel_time),
                    category = "settings\\feature",
                    key = "show_kernel_time"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_net_unlock),
                    category = "settings\\feature",
                    key = "show_net_unlock"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_never_timeout),
                    category = "settings\\feature",
                    key = "show_never_timeout"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_npu_detail),
                    category = "settings\\feature",
                    key = "hide_npu_detail"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_processor),
                    category = "settings\\feature",
                    key = "show_processor"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_processor_gen2),
                    category = "settings\\feature",
                    key = "show_processor_gen2"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.screen_size_cm),
                    category = "settings\\feature",
                    key = "screen_size_cm"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_sw_version),
                    category = "settings\\feature",
                    key = "show_sw_version"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.sw_instead_build),
                    category = "settings\\feature",
                    key = "sw_instead_build"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_uicc_unlock),
                    category = "settings\\feature",
                    key = "show_uicc_unlock"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_sim_lock),
                    category = "settings\\feature",
                    key = "enable_sim_lock"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_sim_toolkit),
                    category = "settings\\feature",
                    key = "hide_sim_toolkit"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.force_software_conf),
                    category = "settings\\feature",
                    key = "force_software_conf"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.special_side_finger),
                    category = "settings\\feature",
                    key = "special_side_finger"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_circle_search),
                    category = "settings\\feature",
                    key = "enable_circle_search"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_custom_ver),
                    category = "settings\\feature",
                    key = "show_custom_ver"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_electronic_label),
                    category = "settings\\feature",
                    key = "enable_electronic_label"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.fullscreen_apps),
                    category = "settings\\feature",
                    key = "fullscreen_apps"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.smart_gesture),
                    category = "settings\\feature",
                    key = "smart_gesture"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_imsi),
                    category = "settings\\feature",
                    key = "show_imsi"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_meid),
                    category = "settings\\feature",
                    key = "show_meid"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.member_rcc_show),
                    category = "settings\\feature",
                    key = "member_rcc_show"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.mini_capsule),
                    category = "settings\\feature",
                    key = "mini_capsule"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.number_recognition),
                    category = "settings\\feature",
                    key = "number_recognition"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.enable_oguard),
                    category = "settings\\feature",
                    key = "enable_oguard"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.oh_india_version),
                    category = "settings\\feature",
                    key = "oh_india_version"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.usb_tether_boot),
                    category = "settings\\feature",
                    key = "usb_tether_boot"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.quick_app_support),
                    category = "settings\\feature",
                    key = "quick_app_support"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.region_picker),
                    category = "settings\\feature",
                    key = "region_picker"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enable_roulette),
                    category = "settings\\feature",
                    key = "enable_roulette"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_wfc_dialog),
                    category = "settings\\feature",
                    key = "show_wfc_dialog"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.smart_touch),
                    category = "settings\\feature",
                    key = "smart_touch"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.smart_touch_v2),
                    category = "settings\\feature",
                    key = "smart_touch_v2"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.show_sms_number),
                    category = "settings\\feature",
                    key = "show_sms_number"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.ai_eye_protect),
                    category = "settings\\feature",
                    key = "ai_eye_protect"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_edge_panel),
                    category = "settings\\feature",
                    key = "disable_edge_panel"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_stable_plan),
                    category = "settings\\feature",
                    key = "disable_stable_plan"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_time_change),
                    category = "settings\\feature",
                    key = "disable_time_change"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_gaze_ringtone),
                    category = "settings\\feature",
                    key = "disable_gaze_ringtone"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_user_exp),
                    category = "settings\\feature",
                    key = "disable_user_exp"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_verify_dialog),
                    category = "settings\\feature",
                    key = "disable_verify_dialog"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.virtual_comm_device),
                    category = "settings\\feature",
                    key = "virtual_comm_device"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.virtual_comm_service),
                    category = "settings\\feature",
                    key = "virtual_comm_service"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_vowifi_setting),
                    category = "settings\\feature",
                    key = "disable_vowifi_setting"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.disable_volte_setting),
                    category = "settings\\feature",
                    key = "disable_volte_setting"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.volte_icon_off),
                    category = "settings\\feature",
                    key = "volte_icon_off"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.disable_wifi_setting),
                    category = "settings\\feature",
                    key = "disable_wifi_setting"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.hide_install_sources),
                    category = "settings\\feature",
                    key = "hide_install_sources"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.biometric_privacy),
                    category = "settings\\feature",
                    key = "biometric_privacy"
                )

                addline()
                FunSwich(
                    title = stringResource(R.string.upload_error_log),
                    category = "settings\\feature",
                    key = "upload_error_log"
                )
                addline()
                FunSwich(title = stringResource(R.string.dirac_sound), category = "settings\\feature", key = "dirac_sound")
                addline()
                FunSwich(title = stringResource(R.string.dolby_support), category = "settings\\feature", key = "dolby_sound")
                addline()
                FunSwich(title = stringResource(R.string.edge_panel), category = "settings\\feature", key = "edge_panel")
                addline()
                FunSwich(title = stringResource(R.string.resolution_pic), category = "settings\\feature", key = "resolution_pic")
                addline()
                FunSwich(title = stringResource(R.string.sharpness_switch), category = "settings\\feature", key = "sharpness_switch")
                addline()
                FunSwich(title = stringResource(R.string.hyper_mode), category = "settings\\feature", key = "hyper_mode")
                addline()
                FunSwich(title = stringResource(R.string.fluid_cloud), category = "settings\\feature", key = "fluid_cloud")
                addline()
                FunSwich(title = stringResource(R.string.linear_vibration), category = "settings\\feature", key = "linear_vibration")
                addline()
                FunSwich(title = stringResource(R.string.op7_vibration), category = "settings\\feature", key = "op7_vibration")
                addline()
                FunSwich(title = stringResource(R.string.palm_unlock), category = "settings\\feature", key = "palm_unlock")
                addline()
                FunSwich(title = stringResource(R.string.stealth_security), category = "settings\\feature", key = "stealth_security")
                addline()
                FunSwich(title = stringResource(R.string.pwm_reboot), category = "settings\\feature", key = "pwm_reboot")
                addline()
                FunSwich(title = stringResource(R.string.anti_voyeur), category = "settings\\feature", key = "anti_voyeur")
                addline()
                FunSwich(title = stringResource(R.string.enable_redpacket_helper), category = "settings\\feature", key = "enable_redpacket_helper")
            }
        }
    }
}
