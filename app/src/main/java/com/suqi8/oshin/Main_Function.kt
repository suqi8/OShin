package com.suqi8.oshin

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewTreeObserver
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlendModeColorFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.palette.graphics.Palette
import com.highcapable.yukihookapi.YukiHookAPI
import com.suqi8.oshin.ui.activity.funlistui.SearchList
import com.suqi8.oshin.ui.activity.funlistui.addline
import com.suqi8.oshin.utils.GetFuncRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Search
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape
import java.text.Collator
import java.util.Locale

fun features(context: Context) = listOf(
    item(title = context.getString(R.string.downgr),
        summary = context.getString(R.string.downgr_summary),
        category = "android\\package_manager_services"),
    item(title = context.getString(R.string.authcreak),
        summary = context.getString(R.string.authcreak_summary),
        category = "android\\package_manager_services"),
    item(
        title = context.getString(R.string.digestCreak),
        summary = context.getString(R.string.digestCreak_summary),
        category = "android\\package_manager_services"),
    item(title = context.getString(R.string.UsePreSig),
        summary = context.getString(R.string.UsePreSig_summary),
        category = "android\\package_manager_services"),
    item(title = context.getString(R.string.enhancedMode),
        summary = context.getString(R.string.enhancedMode_summary),
        category = "android\\package_manager_services"),
    item(title = context.getString(R.string.bypassBlock),
        summary = context.getString(R.string.bypassBlock_summary),
        category = "android\\package_manager_services"),
    item(title = context.getString(R.string.shared_user_title),
        summary = context.getString(R.string.shared_user_summary),
        category = "android\\package_manager_services"),
    item(title = context.getString(R.string.disable_verification_agent_title),
        summary = context.getString(R.string.disable_verification_agent_summary),
        category = "android\\package_manager_services"),
    item(title = context.getString(R.string.package_manager_services),
        category = "android\\package_manager_services"),
    item(title = context.getString(R.string.oplus_system_services),
        category = "android\\oplus_system_services"),
    item(title = context.getString(R.string.oplus_root_check),
        summary = context.getString(R.string.oplus_root_check_summary),
        category = "android\\oplus_system_services"),
    item(title = context.getString(R.string.desktop_icon_and_text_size_multiplier),
        summary = context.getString(R.string.icon_size_limit_note),
        category = "launcher"),
    item(title = context.getString(R.string.power_consumption_indicator),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.dual_cell),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.absolute_value),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.bold_text),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.alignment),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.update_time),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.font_size),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.dual_row_title),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.first_line_content),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.second_line_content),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.power),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.current),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.voltage),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.temperature_indicator),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.show_cpu_temp_data),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.change_cpu_temp_source),
        summary = context.getString(R.string.enter_thermal_zone_number),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.bold_text),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.alignment),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.update_time),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.font_size),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.dual_row_title),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.first_line_content),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.second_line_content),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.battery_temperature),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.cpu_temperature),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.status_bar_clock),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.hardware_indicator),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.status_bar_icon),
        category = "systemui\\statusbar_icon"),
    item(title = context.getString(R.string.hide_status_bar),
        category = "systemui"),
    item(title = context.getString(R.string.enable_all_day_screen_off),
        category = "systemui"),
    item(title = context.getString(R.string.force_trigger_ltpo),
        category = "systemui"),
    item(title = context.getString(R.string.status_bar_clock),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.clock_style),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.clock_size),
        summary = context.getString(R.string.clock_size_summary),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.clock_update_time_title),
        summary = context.getString(R.string.clock_update_time_summary),
        category = "systemui\\status_bar_clock"),
    item(title = "dp To px",
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.clock_top_margin),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.clock_bottom_margin),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.clock_left_margin),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.clock_right_margin),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.show_years_title),
        summary = context.getString(R.string.show_years_summary),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.show_month_title),
        summary = context.getString(R.string.show_month_summary),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.show_day_title),
        summary = context.getString(R.string.show_day_summary),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.show_week_title),
        summary = context.getString(R.string.show_week_summary),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.show_cn_hour_title),
        summary = context.getString(R.string.show_cn_hour_summary),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.showtime_period_title),
        summary = context.getString(R.string.showtime_period_summary),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.show_seconds_title),
        summary = context.getString(R.string.show_seconds_summary),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.show_millisecond_title),
        summary = context.getString(R.string.show_millisecond_summary),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.hide_space_title),
        summary = context.getString(R.string.hide_space_summary),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.dual_row_title),
        summary = context.getString(R.string.dual_row_summary),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.alignment),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.clock_format),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.clock_format_example),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.status_bar_icon),
        category = "systemui\\statusbar_icon"),
    item(title = context.getString(R.string.wifi_icon),
        category = "systemui\\statusbar_icon"),
    item(title = context.getString(R.string.wifi_arrow),
        category = "systemui\\statusbar_icon"),
    item(title = context.getString(R.string.force_display_memory),
        category = "launcher\\recent_task"),
    item(title = context.getString(R.string.recent_tasks),
        category = "launcher\\recent_task"),
    item(title = context.getString(R.string.status_bar_notification),
        category = "systemui\\notification"),
    item(title = context.getString(R.string.remove_developer_options_notification),
        summary = context.getString(R.string.notification_restriction_message),
        category = "systemui\\notification"),
    item(title = context.getString(R.string.low_battery_fluid_cloud_off),
        category = "battery"),
    item(title = context.getString(R.string.remove_and_do_not_disturb_notification),
        summary = context.getString(R.string.notification_restriction_message),
        category = "systemui\\notification"),
    item(title = context.getString(R.string.force_enable_xiaobu_call),
        category = "speechassist"),
    item(title = context.getString(R.string.remove_full_screen_translation_restriction),
        category = "ocrscanner"),
    item(title = context.getString(R.string.enable_ultra_combo),
        category = "games"),
    item(title = context.getString(R.string.enable_hok_ai_v1),
        category = "games"),
    item(title = context.getString(R.string.enable_hok_ai_v2),
        summary = context.getString(R.string.realme_gt7pro_feature_unlock_device_restriction),
        category = "games"),
    item(title = context.getString(R.string.enable_hok_ai_v3),
        category = "games"),
    item(title = context.getString(R.string.hok_ai_assistant_remove_pkg_restriction),
        summary = context.getString(R.string.ai_assistant_global_display),
        category = "games"),
    item(title = context.getString(R.string.feature_disable_cloud_control),
        category = "games"),
    item(title = context.getString(R.string.remove_package_restriction),
        category = "games"),
    item(title = context.getString(R.string.enable_all_features),
        summary = context.getString(R.string.enable_all_features_warning),
        category = "games"),
    item(title = context.getString(R.string.enable_pubg_ai),
        category = "games"),
    item(title = context.getString(R.string.auto_start_max_limit),
        summary = context.getString(R.string.auto_start_default_hint),
        category = "battery"),
    item(title = context.getString(R.string.split_screen_multi_window),
        category = "android\\split_screen_multi_window"),
    item(title = context.getString(R.string.remove_all_small_window_restrictions),
        category = "android\\split_screen_multi_window",
    ),
    item(title = context.getString(R.string.force_multi_window_mode),
    category = "android\\split_screen_multi_window"),
    item(title = context.getString(R.string.max_simultaneous_small_windows),
        category = "android\\split_screen_multi_window",
        summary = context.getString(R.string.default_value_hint_negative_one)),
    item(title = context.getString(R.string.small_window_corner_radius),
        category = "android\\split_screen_multi_window",
        summary = context.getString(R.string.default_value_hint_negative_one)),
    item(title = context.getString(R.string.small_window_focused_shadow),
        category = "android\\split_screen_multi_window",
        summary = context.getString(R.string.default_value_hint_negative_one)),
    item(title = context.getString(R.string.small_window_unfocused_shadow),
        category = "android\\split_screen_multi_window",
        summary = context.getString(R.string.default_value_hint_negative_one)),
    item(title = context.getString(R.string.custom_display_model),
        summary = context.getString(R.string.hint_empty_content_default),
        category = "settings"),
    item(title = context.getString(R.string.remove_swipe_page_ads),
        summary = context.getString(R.string.clear_wallet_data_notice),
        category = "wallet"),
    item(title = context.getString(R.string.enable_ota_card_bg),
        category = "settings"),
    item(title = context.getString(R.string.select_background_btn),
        category = "settings"),
    item(title = context.getString(R.string.corner_radius_title),
        category = "settings"),
    item(title = context.getString(R.string.force_enable_fold_mode),
        category = "launcher"),
    item(title = context.getString(R.string.fold_mode),
            category = "launcher"),
    item(title = context.getString(R.string.force_enable_fold_dock),
    category = "launcher"),
    item(title = context.getString(R.string.adjust_dock_transparency),
        category = "launcher"),
    item(title = context.getString(R.string.force_enable_dock_blur),
        summary = context.getString(R.string.force_enable_dock_blur_undevice),
        category = "launcher"),
    item(title = context.getString(R.string.remove_game_filter_root_detection),
        category = "games"),
    item(title = context.getString(R.string.remove_all_popup_delays),
        summary = context.getString(R.string.remove_all_popup_delays_eg),
        category = "phonemanager"),
    item(title = context.getString(R.string.remove_all_popup_delays),
        summary = context.getString(R.string.remove_all_popup_delays_eg),
        category = "oplusphonemanager"),
    item(title = context.getString(R.string.remove_message_ads),
        category = "mms"),
    item(title = context.getString(R.string.force_show_nfc_security_chip),
        category = "settings"),
    item(title = context.getString(R.string.security_payment_remove_risky_fluid_cloud),
        category = "securepay"),
    item(title = context.getString(R.string.custom_score),
        summary = context.getString(R.string.default_value_hint_negative_one),
        category = "phonemanager"),
    item(title = context.getString(R.string.custom_prompt_content),
    category = "phonemanager"),
    item(title = context.getString(R.string.custom_animation_duration),
        summary = context.getString(R.string.default_value_hint_negative_one),
        category = "phonemanager"),
    item(title = context.getString(R.string.custom_score),
        summary = context.getString(R.string.default_value_hint_negative_one),
        category = "oplusphonemanager"),
    item(title = context.getString(R.string.custom_prompt_content),
        category = "oplusphonemanager"),
    item(title = context.getString(R.string.custom_animation_duration),
        summary = context.getString(R.string.default_value_hint_negative_one),
        category = "oplusphonemanager"),
    item(title = context.getString(R.string.feature),
        category = "settings\\feature"),
    item(title = context.getString(R.string.force_enable_all_features),summary = context.getString(R.string.enable_all_features_warning),category = "settings\\feature"),
    item(title = context.getString(R.string.demo_only_device),category = "settings\\feature"),
    item(title = context.getString(R.string.retail_locked_terminal),category = "settings\\feature"),
    item(title = context.getString(R.string.force_enable_karaoke),category = "settings\\feature"),
    item(title = context.getString(R.string.force_enable_3d_camera_color),category = "settings\\feature"),
    item(title = context.getString(R.string.force_aon_explorer),category = "settings\\feature"),
    item(title = context.getString(R.string.force_enable_app_freeze),category = "settings\\feature"),
    item(title = context.getString(R.string.check_ble_audio_whitelist),category = "settings\\feature"),
    item(title = context.getString(R.string.force_breathing_light_sync),category = "settings\\feature"),
    item(title = context.getString(R.string.force_breathing_light_color),category = "settings\\feature"),
    item(title = context.getString(R.string.force_support_wide_gamut),category = "settings\\feature"),
    item(title = context.getString(R.string.force_support_color_mode),category = "settings\\feature"),
    item(title = context.getString(R.string.force_support_hidden_app_feature),category = "settings\\feature"),
    item(title = context.getString(R.string.force_support_smart_case),category = "settings\\feature"),
    item(title = context.getString(R.string.force_dirac_audio),category = "settings\\feature"),
    item(title = context.getString(R.string.force_dolby_audio),category = "settings\\feature"),
    item(title = context.getString(R.string.force_dual_earbuds),category = "settings\\feature"),
    item(title = context.getString(R.string.force_foldable_screen),category = "settings\\feature"),
    item(title = context.getString(R.string.force_fold_or_flip_screen),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_display_remapping),summary = context.getString(R.string.disable_ui_remap_when_unfolded),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_gesture_navigation),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_google_mobile_services),category = "settings\\feature"),
    item(title = context.getString(R.string.hide_storage_info),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_holo_audio),category = "settings\\feature"),
    item(title = context.getString(R.string.force_hd_video),category = "settings\\feature"),
    item(title = context.getString(R.string.auto_grant_install),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_lock_wallpaper),category = "settings\\feature"),
    item(title = context.getString(R.string.light_os),category = "settings\\feature"),
    item(title = context.getString(R.string.force_multi_volume),category = "settings\\feature"),
    item(title = context.getString(R.string.force_app_clone),category = "settings\\feature"),
    item(title = context.getString(R.string.force_adaptive_brightness),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_ota),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_audio_boost),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_ai_image),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_osie_tech),category = "settings\\feature"),
    item(title = context.getString(R.string.force_shutdown_key),category = "settings\\feature"),
    item(title = context.getString(R.string.single_pulse_pwm),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_res_switch),category = "settings\\feature"),
    item(title = context.getString(R.string.manual_refresh_rate),category = "settings\\feature"),
    item(title = context.getString(R.string.default_smart_refresh),category = "settings\\feature"),
    item(title = context.getString(R.string.refresh_rate_notify),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_sell_mode),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_dual_sim),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_single_sim_check),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_anti_voyeur),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_snc_content),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_sound_combo),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_sound_settings),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_audio_input),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_15k_resolution),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_adfr),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_aod),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_aon_face),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_autolayout),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_blade_colormode),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_breeno_suggest),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_brightness_anim),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_cinema_mode),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_oled_colorful),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_custom_color),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_colorful_mode),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_powersaving_color),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_compact_window),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_dc_backlight),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_dynamic_brightness),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_dirac_a2dp),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_dynamic_fps),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_edge_anti_touch),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_5g_support),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_fold_remap),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_gt_mode),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_hdr_alwayson),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_hdr_highlight),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_smart_color_temp2),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_linear_vibration),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_luxun_vibration),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_multi_led_breathing),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_phone_limit),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_pixelworks_x7),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_resolution_switch),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_ringtone_vibration),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_satellite_network),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_spatializer_speaker),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_super_volume2x),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_super_volume3x),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_temp_adjust),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_touchpad_split),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_ultrasonic_fp),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_volume_boost),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_color_ball),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_surround_effect),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_tablet_mode),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_typec_menu),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_ultrasonic_security),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_vibrator_style),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_smart_screenoff),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_richtap_vibrate),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_dirac_v2),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_iris5_display),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_ring_haptic),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_video_osie),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_video_sr),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_deactivate_app),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_haptic_preview),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_modify_devname),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_super_sleep),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_5g_reminder),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_account_dialog),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_app_disable),category = "settings\\feature"),
    item(title = context.getString(R.string.hide_cmiit_auth),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_hyper_vision),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_carrier),category = "settings\\feature"),
    item(title = context.getString(R.string.locale_uk_to_en),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_clear_cache),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_colorful_real),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_confidential),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_cyberpunk),category = "settings\\feature"),
    item(title = context.getString(R.string.auto_resolution),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_oem_unlock),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_auto_rotate),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_app_switch),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_euex),category = "settings\\feature"),
    item(title = context.getString(R.string.force_exp_version),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_film_finger),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_finger_anim),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_fintech_nfc),category = "settings\\feature"),
    item(title = context.getString(R.string.force_flip_device),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_gesture),category = "settings\\feature"),
    item(title = context.getString(R.string.keep_gesture_up),category = "settings\\feature"),
    item(title = context.getString(R.string.more_gesture_up),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_gota_update),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_business_state),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_ultimate_clean),category = "settings\\feature"),
    item(title = context.getString(R.string.hide_hw_version),category = "settings\\feature"),
    item(title = context.getString(R.string.hide_device_id),category = "settings\\feature"),
    item(title = context.getString(R.string.hide_ktv_loopback),category = "settings\\feature"),
    item(title = context.getString(R.string.hide_mms_ringtone),category = "settings\\feature"),
    item(title = context.getString(R.string.move_dc_to_dev),category = "settings\\feature"),
    item(title = context.getString(R.string.hide_network_speed),category = "settings\\feature"),
    item(title = context.getString(R.string.hide_power_wake3),category = "settings\\feature"),
    item(title = context.getString(R.string.hide_sim_signal),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_humming),category = "settings\\feature"),
    item(title = context.getString(R.string.show_kernel_id),category = "settings\\feature"),
    item(title = context.getString(R.string.ignore_repeat_click),category = "settings\\feature"),
    item(title = context.getString(R.string.imei_sv_from_ota),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_light_func),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_marvel),category = "settings\\feature"),
    item(title = context.getString(R.string.hide_portrait_center),category = "settings\\feature"),
    item(title = context.getString(R.string.hide_video_beauty),category = "settings\\feature"),
    item(title = context.getString(R.string.show_2g3g),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_ocloud),category = "settings\\feature"),
    item(title = context.getString(R.string.force_oh_device),category = "settings\\feature"),
    item(title = context.getString(R.string.only_hw_version),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_kddi_au),category = "settings\\feature"),
    item(title = context.getString(R.string.show_operator),category = "settings\\feature"),
    item(title = context.getString(R.string.hide_privacy_email),category = "settings\\feature"),
    item(title = context.getString(R.string.keep_swipe_up),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_ota),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_otg_alarm),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_otg_entry),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_pac_custom),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_privacy),category = "settings\\feature"),
    item(title = context.getString(R.string.hide_fake_base),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_rl_delete),category = "settings\\feature"),
    item(title = context.getString(R.string.force_rlm_device),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_raise_wake),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_recent_task),category = "settings\\feature"),
    item(title = context.getString(R.string.remove_cota_home),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_resize_screen),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_rlm_feedback),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_screen_pin),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_search_index),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_seedling_exp),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_custom_devname),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_cota_devname),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_set_password),category = "settings\\feature"),
    item(title = context.getString(R.string.hide_all_anr),category = "settings\\feature"),
    item(title = context.getString(R.string.show_brand_name),category = "settings\\feature"),
    item(title = context.getString(R.string.show_carrier_config),category = "settings\\feature"),
    item(title = context.getString(R.string.show_carrier_update),category = "settings\\feature"),
    item(title = context.getString(R.string.show_custom_details),category = "settings\\feature"),
    item(title = context.getString(R.string.hide_data_usage),category = "settings\\feature"),
    item(title = context.getString(R.string.show_diagnostic),category = "settings\\feature"),
    item(title = context.getString(R.string.show_os_firstname),category = "settings\\feature"),
    item(title = context.getString(R.string.show_hw_version),category = "settings\\feature"),
    item(title = context.getString(R.string.show_ims_status),category = "settings\\feature"),
    item(title = context.getString(R.string.show_kernel_time),category = "settings\\feature"),
    item(title = context.getString(R.string.show_net_unlock),category = "settings\\feature"),
    item(title = context.getString(R.string.show_never_timeout),category = "settings\\feature"),
    item(title = context.getString(R.string.hide_npu_detail),category = "settings\\feature"),
    item(title = context.getString(R.string.show_processor),category = "settings\\feature"),
    item(title = context.getString(R.string.show_processor_gen2),category = "settings\\feature"),
    item(title = context.getString(R.string.screen_size_cm),category = "settings\\feature"),
    item(title = context.getString(R.string.show_sw_version),category = "settings\\feature"),
    item(title = context.getString(R.string.sw_instead_build),category = "settings\\feature"),
    item(title = context.getString(R.string.show_uicc_unlock),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_sim_lock),category = "settings\\feature"),
    item(title = context.getString(R.string.hide_sim_toolkit),category = "settings\\feature"),
    item(title = context.getString(R.string.force_software_conf),category = "settings\\feature"),
    item(title = context.getString(R.string.special_side_finger),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_circle_search),category = "settings\\feature"),
    item(title = context.getString(R.string.show_custom_ver),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_electronic_label),category = "settings\\feature"),
    item(title = context.getString(R.string.fullscreen_apps),category = "settings\\feature"),
    item(title = context.getString(R.string.smart_gesture),category = "settings\\feature"),
    item(title = context.getString(R.string.show_imsi),category = "settings\\feature"),
    item(title = context.getString(R.string.show_meid),category = "settings\\feature"),
    item(title = context.getString(R.string.member_rcc_show),category = "settings\\feature"),
    item(title = context.getString(R.string.mini_capsule),category = "settings\\feature"),
    item(title = context.getString(R.string.number_recognition),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_oguard),category = "settings\\feature"),
    item(title = context.getString(R.string.oh_india_version),category = "settings\\feature"),
    item(title = context.getString(R.string.usb_tether_boot),category = "settings\\feature"),
    item(title = context.getString(R.string.quick_app_support),category = "settings\\feature"),
    item(title = context.getString(R.string.region_picker),category = "settings\\feature"),
    item(title = context.getString(R.string.enable_roulette),category = "settings\\feature"),
    item(title = context.getString(R.string.show_wfc_dialog),category = "settings\\feature"),
    item(title = context.getString(R.string.smart_touch),category = "settings\\feature"),
    item(title = context.getString(R.string.smart_touch_v2),category = "settings\\feature"),
    item(title = context.getString(R.string.show_sms_number),category = "settings\\feature"),
    item(title = context.getString(R.string.ai_eye_protect),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_edge_panel),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_stable_plan),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_time_change),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_gaze_ringtone),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_user_exp),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_verify_dialog),category = "settings\\feature"),
    item(title = context.getString(R.string.virtual_comm_device),category = "settings\\feature"),
    item(title = context.getString(R.string.virtual_comm_service),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_vowifi_setting),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_volte_setting),category = "settings\\feature"),
    item(title = context.getString(R.string.volte_icon_off),category = "settings\\feature"),
    item(title = context.getString(R.string.disable_wifi_setting),category = "settings\\feature"),
    item(title = context.getString(R.string.hide_install_sources),category = "settings\\feature"),
    item(title = context.getString(R.string.biometric_privacy),category = "settings\\feature"),
    item(title = context.getString(R.string.upload_error_log),category = "settings\\feature"),
    item(title = context.getString(R.string.dirac_sound), category = "settings\\feature"),
    item(title = context.getString(R.string.dolby_support), category = "settings\\feature"),
    item(title = context.getString(R.string.edge_panel), category = "settings\\feature"),
    item(title = context.getString(R.string.resolution_pic), category = "settings\\feature"),
    item(title = context.getString(R.string.sharpness_switch), category = "settings\\feature"),
    item(title = context.getString(R.string.hyper_mode), category = "settings\\feature"),
    item(title = context.getString(R.string.fluid_cloud), category = "settings\\feature"),
    item(title = context.getString(R.string.linear_vibration), category = "settings\\feature"),
    item(title = context.getString(R.string.op7_vibration), category = "settings\\feature"),
    item(title = context.getString(R.string.palm_unlock), category = "settings\\feature"),
    item(title = context.getString(R.string.stealth_security), category = "settings\\feature"),
    item(title = context.getString(R.string.pwm_reboot), category = "settings\\feature"),
    item(title = context.getString(R.string.anti_voyeur), category = "settings\\feature"),
    item(title = context.getString(R.string.enable_redpacket_helper), category = "settings\\feature"),
    item(title = context.getString(R.string.disable_root_dialog),
        category = "health"),
    item(title = context.getString(R.string.remove_recommendations),
        category = "appdetail"),
    item(title = context.getString(R.string.network_speed_indicator),
        category = "systemui\\status_bar_wifi"),
    item(title = context.getString(R.string.network_speed_indicator),
        category = "systemui\\status_bar_wifi"),
    item(title = context.getString(R.string.network_speed_style),
        category = "systemui\\status_bar_wifi"),
    item(title = context.getString(R.string.speed_font_size),
        summary = context.getString(R.string.default_value_hint_negative_one),
        category = "systemui\\status_bar_wifi"),
    item(title = context.getString(R.string.unit_font_size),
        summary = context.getString(R.string.default_value_hint_negative_one),
        category = "systemui\\status_bar_wifi"),
    item(title = context.getString(R.string.upload_font_size),
        summary = context.getString(R.string.default_value_hint_negative_one),
        category = "systemui\\status_bar_wifi"),
    item(title = context.getString(R.string.download_font_size),
        summary = context.getString(R.string.default_value_hint_negative_one),
        category = "systemui\\status_bar_wifi"),
    item(title = context.getString(R.string.slow_speed_threshold),
        category = "systemui\\status_bar_wifi"),
    item(title = context.getString(R.string.hide_on_slow),
        category = "systemui\\status_bar_wifi"),
    item(title = context.getString(R.string.hide_when_both_slow),
        category = "systemui\\status_bar_wifi"),
    item(title = context.getString(R.string.icon_indicator),
        category = "systemui\\status_bar_wifi"),
    item(title = context.getString(R.string.position_speed_indicator_front),
        category = "systemui\\status_bar_wifi"),
    item(title = context.getString(R.string.hide_space),
        category = "systemui\\status_bar_wifi"),
    item(title = context.getString(R.string.hide_bs),
        category = "systemui\\status_bar_wifi"),
    item(title = context.getString(R.string.swap_upload_download),
        category = "systemui\\status_bar_wifi"),
    item(title = context.getString(R.string.disable_72h_verify),
        category = "android"),
    item(title = context.getString(R.string.allow_untrusted_touch),
        category = "android"),
    item(title = context.getString(R.string.remove_app_recommendation_ads),
        category = "quicksearchbox"),
    item(title = context.getString(R.string.accessibility_service_authorize),
        category = "settings"),
    item(title = context.getString(R.string.accessibility_service_direct),
        category = "settings"),
    item(title = context.getString(R.string.smart_accessibility_service),
        summary = context.getString(R.string.whitelist_app_auto_authorization),
        category = "settings"),
    item(title = context.getString(R.string.accessibility_whitelist),
        category = "settings"),
    item(title = context.getString(R.string.remove_installation_frequency_popup),
        category = "appdetail"),
    item(title = context.getString(R.string.remove_attempt_installation_popup),
        category = "appdetail"),
    item(title = context.getString(R.string.remove_version_check),
        category = "appdetail"),
    item(title = context.getString(R.string.remove_security_check),
        category = "appdetail")
)

var notInstallList = mutableStateOf(emptyList<String>())

@SuppressLint("UnrememberedMutableState")
@Composable
fun Main_Function(
    topAppBarScrollBehavior: ScrollBehavior,
    navController: NavController,
    padding: PaddingValues
) {
    val context = LocalContext.current
    var miuixSearchValue by remember { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var isKeyboardVisible by remember { mutableStateOf(false) }
    DisposableEffect(context) {
        val rootView = (context as MainActivity).window.decorView
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val insets = ViewCompat.getRootWindowInsets(rootView)
            isKeyboardVisible = insets?.isVisible(WindowInsetsCompat.Type.ime()) == true
        }

        rootView.viewTreeObserver.addOnGlobalLayoutListener(listener)

        onDispose {
            rootView.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }

    // 过滤符合搜索条件的功能
    val collator = Collator.getInstance(Locale.CHINA)
    val filteredFeatures by remember(miuixSearchValue) {
        derivedStateOf {
            features(context)
                .filter {
                    it.title.contains(miuixSearchValue, ignoreCase = true) ||
                            it.summary?.contains(miuixSearchValue, ignoreCase = true) ?: false
                }
                .sortedWith(compareBy(collator) { it.title })
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SearchBar(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .background(Color.Transparent)
                .padding(top = padding.calculateTopPadding()),
            inputField = {
                InputField(
                    query = miuixSearchValue,
                    onQueryChange = { miuixSearchValue = it },
                    onSearch = { expanded = false },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    label = stringResource(R.string.Search),
                    leadingIcon = {
                        Image(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            imageVector = MiuixIcons.Useful.Search,
                            colorFilter = BlendModeColorFilter(
                                MiuixTheme.colorScheme.onSurfaceContainer,
                                BlendMode.SrcIn
                            ),
                            contentDescription = stringResource(R.string.Search)
                        )
                    }
                )
            },
            outsideRightAction = {
                Text(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .clickable {
                            expanded = false
                            miuixSearchValue = ""
                        },
                    text = stringResource(R.string.cancel),
                    color = MiuixTheme.colorScheme.primary
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 6.dp,
                        bottom = if (isKeyboardVisible) 0.dp else padding.calculateBottomPadding()
                    )
            ) {
                LazyColumn(modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)) {
                    if (filteredFeatures.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "空空如也~")
                            }
                        }
                    }

                    filteredFeatures.forEachIndexed { index, feature ->
                        item {
                            val route = rememberSaveable { mutableStateOf("") }
                            if (route.value == "") {
                                LaunchedEffect(Unit) {
                                    route.value = GetFuncRoute(feature.category,context)
                                }
                            }
                            SearchList(
                                title = highlightMatches(feature.title, miuixSearchValue),
                                summary = highlightMatches(if (feature.summary != null) feature.summary + "\n" + route.value else route.value, miuixSearchValue),
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    //miuixSearchValue = feature.title
                                    expanded = false
                                    navController.navigate(feature.category)
                                }
                            )
                            if (index < filteredFeatures.size - 1) {
                                addline()
                            }
                        }
                    }
                }
            }
        }

        if (expanded) {
            // 如果 expanded 为 true，则显示搜索结果
        } else {
            // 如果 expanded 为 false，则显示 Card
            LazyColumn(Modifier.fillMaxSize().nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)) {
                item {
                    Spacer(modifier = Modifier.size(68.dp+padding.calculateTopPadding()))
                    val appList = listOf(
                        AppInfo("android", "android"),
                        AppInfo("com.android.systemui", "systemui"),
                        AppInfo("com.android.settings", "settings"),
                        AppInfo("com.android.launcher", "launcher"),
                        AppInfo("com.oplus.battery", "battery"),
                        AppInfo("com.heytap.speechassist", "speechassist"),
                        AppInfo("com.coloros.ocrscanner", "ocrscanner"),
                        AppInfo("com.oplus.games", "games"),
                        AppInfo("com.finshell.wallet", "wallet"),
                        AppInfo("com.coloros.phonemanager", "phonemanager"),
                        AppInfo("com.oplus.phonemanager", "oplusphonemanager"),
                        AppInfo("com.android.mms", "mms"),
                        AppInfo("com.coloros.securepay", "securepay"),
                        AppInfo("com.heytap.health", "health"),
                        AppInfo("com.oplus.appdetail", "appdetail"),
                        AppInfo("com.heytap.quicksearchbox", "quicksearchbox")
                        )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 6.dp)
                    ) {
                        Column {
                            appList.forEachIndexed { index, appInfo ->
                                val notInstall = rememberSaveable { mutableStateOf(false) }
                                FunctionApp(
                                    packageName = appInfo.packageName,
                                    activityName = appInfo.activityName,
                                    navController = navController
                                ) {
                                    if (it == "noapp") {
                                        if (!notInstallList.value.contains(appInfo.packageName)) notInstallList.value += appInfo.packageName
                                        notInstall.value = true
                                    }
                                }
                                if (index < appList.size - 1 && !notInstall.value) {
                                    addline()
                                }
                            }
                        }
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 6.dp, top = 6.dp)
                    ) {
                        SuperArrow(
                            title = stringResource(R.string.app_not_found_in_list),
                            titleColor = BasicComponentDefaults.titleColor(color = MiuixTheme.colorScheme.primaryVariant),
                            onClick = {
                                navController.navigate("hide_apps_notice")
                            }
                        )
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 6.dp, top = 6.dp)
                    ) {
                        Column {
                            SuperArrow(title = stringResource(id = R.string.cpu_freq_main),
                                onClick = {
                                    navController.navigate("testfunc\\cpu_freq")
                                })
                        }
                    }
                    Spacer(modifier = Modifier.padding(bottom = padding.calculateBottomPadding()))
                }
            }
        }
    }
}

data class AppInfo(val packageName: String, val activityName: String)

// 高亮匹配内容的函数
fun highlightMatches(text: String, query: String): AnnotatedString {
    if (query.isBlank()) return AnnotatedString(text) // 如果查询为空，则返回原始文本

    val regex = Regex("($query)", RegexOption.IGNORE_CASE) // 匹配查询字符串的正则表达式
    val annotatedStringBuilder = AnnotatedString.Builder()

    var lastIndex = 0
    for (match in regex.findAll(text)) {
        // 添加匹配前的文本
        annotatedStringBuilder.append(text.substring(lastIndex, match.range.first))
        // 添加高亮部分
        annotatedStringBuilder.pushStyle(SpanStyle(color = Color.Red))
        annotatedStringBuilder.append(match.value)
        annotatedStringBuilder.pop()
        lastIndex = match.range.last + 1
    }
    // 添加剩余的文本
    annotatedStringBuilder.append(text.substring(lastIndex))

    return annotatedStringBuilder.toAnnotatedString()
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun FunctionApp(packageName: String, activityName: String, navController: NavController, onResult: (String) -> Unit) {
    GetAppIconAndName(packageName = packageName) { appName, icon ->
        if (appName != "noapp") {
            val defaultColor = MiuixTheme.colorScheme.primary

            // 使用 remember 缓存 dominantColor 的状态
            val dominantColor = remember { mutableStateOf(colorCache[packageName] ?: defaultColor) }
            val isLoading = remember { mutableStateOf(dominantColor.value == defaultColor) }

            // 使用 LaunchedEffect 在 icon 或 dominantColor 变化时启动协程
            LaunchedEffect(icon, dominantColor.value) {
                if (isLoading.value) {
                    val newColor = withContext(Dispatchers.Default) {
                        if (!YukiHookAPI.Status.isModuleActive) defaultColor else getAutoColor(icon)
                    }
                    dominantColor.value = newColor
                    colorCache[packageName] = newColor
                    isLoading.value = false
                }
            }

            Row(
                modifier = Modifier
                    .clickable { navController.navigate(activityName) }
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isLoading.value) {
                    Card(
                        color = if (YukiHookAPI.Status.isModuleActive) dominantColor.value else MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier
                            .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                            .drawColoredShadow(
                                if (YukiHookAPI.Status.isModuleActive) dominantColor.value else MaterialTheme.colorScheme.errorContainer,
                                1f,
                                borderRadius = 13.dp,
                                shadowRadius = 7.dp,
                                offsetX = 0.dp,
                                offsetY = 0.dp,
                                roundedRect = false
                            )
                    ) {
                        Image(bitmap = icon, contentDescription = "App Icon", modifier = Modifier.size(45.dp))
                    }
                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        Text(text = appName)
                        Text(
                            text = packageName,
                            fontSize = MiuixTheme.textStyles.subtitle.fontSize,
                            fontWeight = FontWeight.Medium,
                            color = MiuixTheme.colorScheme.onBackgroundVariant
                        )
                    }
                }
            }
        } else {
            onResult("noapp")
        }
    }
}

// 全局颜色缓存
internal val colorCache = mutableMapOf<String, Color>()

// 获取主色调的函数
suspend fun getAutoColor(icon: ImageBitmap): Color {
    return withContext(Dispatchers.IO) {
        val bitmap = icon.asAndroidBitmap()
        Palette.from(bitmap).generate().dominantSwatch?.rgb?.let { Color(it) } ?: Color.White
    }
}

data class item(
    val title: String,
    val summary: String? = null,
    val category: String
)

@Composable
fun SearchBar(
    inputField: @Composable () -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    outsideRightAction: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.zIndex(1f),color = Color.Transparent,
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    inputField()
                }
                AnimatedVisibility(
                    visible = expanded
                ) {
                    outsideRightAction?.invoke()
                }
            }

            AnimatedVisibility(
                visible = expanded
            ) {
                content()
            }
        }
    }

    BackHandler(enabled = expanded) {
        onExpandedChange(false)
    }
}

@Composable
fun InputField(
    query: String,
    onQueryChange: (String) -> Unit,
    label: String = "",
    onSearch: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    insideMargin: DpSize = DpSize(12.dp, 12.dp),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }

    val paddingModifier = remember(insideMargin, leadingIcon, trailingIcon) {
        if (leadingIcon == null && trailingIcon == null) Modifier.padding(horizontal = insideMargin.width, vertical = insideMargin.height)
        else if (leadingIcon == null) Modifier
            .padding(start = insideMargin.width)
            .padding(vertical = insideMargin.height)
        else if (trailingIcon == null) Modifier
            .padding(end = insideMargin.width)
            .padding(vertical = insideMargin.height)
        else Modifier.padding(vertical = insideMargin.height)
    }

    val focused = interactionSource.collectIsFocusedAsState().value
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged { if (it.isFocused) onExpandedChange(true) }
            .semantics {
                onClick {
                    focusRequester.requestFocus()
                    true
                }
            },
        enabled = enabled,
        singleLine = true,
        textStyle = MiuixTheme.textStyles.main,
        cursorBrush = SolidColor(MiuixTheme.colorScheme.primary),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch(query) }),
        interactionSource = interactionSource,
        decorationBox =
        @Composable { innerTextField ->
            val shape = remember { derivedStateOf { SmoothRoundedCornerShape(50.dp) } }
            Box(
                modifier = Modifier
                    .background(
                        color = MiuixTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.75f),
                        shape = shape.value
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (leadingIcon != null) {
                        leadingIcon()
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .then(paddingModifier),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = if (!(query.isNotEmpty() || expanded)) label else "",
                            color = MiuixTheme.colorScheme.onSurfaceContainerHigh
                        )

                        innerTextField()
                    }
                    if (trailingIcon != null) {
                        trailingIcon()
                    }
                }
            }
        }
    )

    val shouldClearFocus = !expanded && focused
    LaunchedEffect(expanded) {
        if (shouldClearFocus) {
            delay(100)
            focusManager.clearFocus()
        }
    }
}
