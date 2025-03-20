package com.suqi8.oshin.hook.com.android.settings

import android.graphics.ImageDecoder
import android.os.Environment
import android.widget.RelativeLayout
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import java.io.File

class settings: YukiBaseHooker() {
    override fun onHook() {
        loadApp(name = "com.android.settings") {
            if (prefs("settings").getString("custom_display_model", "") != "") {
                "com.oplus.settings.feature.deviceinfo.controller.OplusDeviceModelPreferenceController".toClass().apply {
                    method{
                        name = "getStatusText"
                        emptyParam()
                        returnType = StringClass
                    }.hook {
                        replaceTo(prefs("settings").getString("custom_display_model", ""))
                    }
                }
            }
            if (prefs("settings").getBoolean("enable_ota_card_bg", false)) {
                "com.oplus.settings.widget.preference.AboutDeviceOtaUpdatePreference".toClass().apply {
                    method {
                        name = "onBindViewHolder"
                        param("androidx.preference.PreferenceViewHolder")
                        returnType = UnitType
                    }.hook {
                        after {
                            val holder = args[0] as Any
                            val itemView = holder.javaClass.getField("itemView").get(holder) as RelativeLayout
                            File("${Environment.getExternalStorageDirectory()}/.OShin/settings/ota_card.png").takeIf { it.exists() }?.let { file ->
                                val bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(file))
                                RoundedBitmapDrawableFactory.create(appContext!!.resources, bitmap).apply {
                                    cornerRadius = prefs("settings").getFloat("ota_corner_radius", 0f)
                                }.also { itemView.post { itemView.background = it } }
                            }
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("force_show_nfc_security_chip", false)) {
                "com.oplus.settings.feature.deviceinfo.DeviceInfoUtils".toClass().apply {
                    method {
                        name = "isSupportNfcEse"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                method {
                    name = "hasAllClientFeature"
                    emptyParam()
                    returnType = BooleanType
                }.hook {
                    before {
                        if (prefs("settings").getBoolean("demo_only_device", false)) result = false
                        if (prefs("settings").getBoolean("retail_locked_terminal", false)) result = true
                    }
                }
            }
            if (prefs("settings").getBoolean("force_enable_karaoke", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "hasSupportKaraokeFeature"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("force_enable_all_features", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "hasOplusFeature"
                        param("java.lang.String")
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "hasSystemFeature"
                        param("java.lang.String")
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("force_enable_3d_camera_color", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "is3DCameraColorSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("force_aon_explorer", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isAonExplorerEnable"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("force_enable_app_freeze", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isAppFrozenSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("check_ble_audio_whitelist", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isBleAudioWhiteListEnable"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isBtLeAudioWhiteList"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isBtLeAudioWhiteListEnable"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("force_breathing_light_sync", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isBreathLightMusicSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("force_breathing_light_color", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isBreathingLightColorSupport"
                        param("android.content.Context")
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("force_support_wide_gamut", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isColorManagementSupprot"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("force_support_color_mode", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isColorModeSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("force_support_hidden_app_feature", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isCustomHideAppSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("force_support_smart_case", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isDeviceCaseSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("force_dirac_audio", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isDiracSupported"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("force_dolby_audio", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isDolbySupported"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("force_dual_earbuds", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isDualheadphone"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("force_foldable_screen", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isFoldDevice"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("force_fold_or_flip_screen", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isFoldOrFlipDevice"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("disable_display_remapping", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isFoldRemapDisableDevice"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("disable_gesture_navigation", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isGestureNavigationDisable"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("disable_google_mobile_services", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isGmsControlSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("hide_storage_info", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isHideStorage"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("enable_holo_audio", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isHoloAudioSupported"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isHoloAudioSupportedSpeaker"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isHoloAudioVoipExceptBle"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isHoloAudioVoipSupported"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("force_hd_video", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isHqvPlan"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("auto_grant_install", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isInstallPermissionAutoAllowed"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("disable_lock_wallpaper", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isKeyguardPictorialDisabled"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("light_os", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isLightOS"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("force_multi_volume", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isMultiAppVolumeAdjustmentSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("force_app_clone", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isMultiappSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("force_adaptive_brightness", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isMultibitsSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("disable_ota", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isOTANotSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("enable_audio_boost", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isOrealitySupported"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("enable_ai_image", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isOsieAipqSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("enable_osie_tech", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isOsieSupported"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("force_shutdown_key", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isPressPowerButtonThreeSecondsToShutDownSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("single_pulse_pwm", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isPwmSinglePulseSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isPwmSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("disable_res_switch", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isResolutionAutoDisableSupport"
                        param("android.content.Context")
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("manual_refresh_rate", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isScreenHighRefreshChoiceSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("default_smart_refresh", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isScreenRateRefreshAsAuto"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("refresh_rate_notify", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isScreenRateRefreshAsAutoNotification"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("enable_sell_mode", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSellModeVersion" // 原方法名保持
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } } // 强制返回已启用状态
                }
            }
            if (prefs("settings").getBoolean("enable_dual_sim", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSimGeminiSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } } // 欺骗系统支持双卡
                }
            }
            if (prefs("settings").getBoolean("disable_single_sim_check", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSingleCardPhone"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = false } } // 强制返回非单卡状态
                }
            }
            if (prefs("settings").getBoolean("enable_anti_voyeur", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSmartAntiVoyeurEnabled"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } } // 强制启用防偷窥
                }
            }
            if (prefs("settings").getBoolean("enable_snc_content", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSncSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } } // 伪装支持 SNC
                }
            }
            if (prefs("settings").getBoolean("enable_sound_combo", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSoundEffectCombinedSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } } // 激活组合音效
                }
            }
            if (prefs("settings").getBoolean("enable_sound_settings", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSoundEffectSettingsSupported"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } } // 解锁音效设置
                }
            }
            if (prefs("settings").getBoolean("enable_audio_input", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSoundInputDeviceSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } } // 强制支持输入设备
                }
            }
            if (prefs("settings").getBoolean("enable_1.5k_resolution", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupport15Resolution"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_adfr", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportADFR"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_aod", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportAod"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_aon_face", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportAonFace"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_autolayout", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportAutoLayout"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_blade_colormode", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportBladeColorMode"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_breeno_suggest", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportBreenoSuggest"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_brightness_anim", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportBrightnessNewAnimation"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_cinema_mode", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportColorModeCinema"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_oled_colorful", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportColorModeColorful"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_custom_color", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportColorModeCustomize"
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_colorful_mode", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportColorModeOplusColorful"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_powersaving_color", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportColorModePowerSaving"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_compact_window", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportCompactWindow"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_dc_backlight", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportDCBacklight"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_dynamic_brightness", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportDevelopmentAdjustBrightnessBarRange"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_dirac_a2dp", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportDirac"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_dynamic_fps", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportDynamicFpsMode"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_edge_anti_touch", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportEdgePreventMistouch"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_5g_support", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportFiveG"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("disable_fold_remap", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportFoldRemapDisable"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } } // 开启此开关会激活禁用逻辑
                }
            }
            if (prefs("settings").getBoolean("enable_gt_mode", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportGTMode"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_hdr_alwayson", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportHdrAlwayson"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_hdr_highlight", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportHdrVideoHighLightMode"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_smart_color_temp2", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportIntelligentColorTemperature2"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_linear_vibration", false)) {
                // 需要同时Hook两个类的方法
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportLinearVibration"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
                "com.oplus.settings.utils.FeatureUtils".toClass().apply {
                    method {
                        name = "isSupportLinearVibration"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_luxun_vibration", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportLuXunVibration"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_multi_led_breathing", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportMultiLedBreathingLight"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_phone_limit", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportPhoneLimit"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_pixelworks_x7", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportPixelworksX7Enable"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_resolution_switch", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportResolution"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_ringtone_vibration", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportRingtoneVibration"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_satellite_network", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportSatelliteNetwork"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("enable_spatializer_speaker", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportSpatializerSpeaker"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            arrayOf("enable_super_volume2x", "enable_super_volume3x").forEach { key ->
                if (prefs("settings").getBoolean(key, false)) {
                    "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                        method {
                            name = when (key) {
                                "enable_super_volume2x" -> "isSupportSuperVolume2X"
                                else -> "isSupportSuperVolume3X"
                            }
                            emptyParam()
                            returnType = BooleanType
                        }.hook { before { result = true } }
                    }
                }
            }
            if (prefs("settings").getBoolean("enable_temp_adjust", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportTemperatureAdjustment"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_touchpad_split", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportTouchpadSplitView"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("enable_ultrasonic_fp", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportUltrasonicFP"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }

            if (prefs("settings").getBoolean("enable_volume_boost", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportVolumeBoost"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true // 强制返回true，覆盖RLM设备检测和属性检查
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("enable_color_ball", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSuppprtColorTemperateBall" 
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_surround_effect", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSurroundEffectSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_tablet_mode", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isTabletDevice"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_typec_menu", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isTypecSupported"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_ultrasonic_security", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isUltrasonicFPConfidential"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_vibrator_style", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isVibratorStyleSwitchSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_smart_screenoff", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "sIsSmartScreenOffSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_richtap_vibrate", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "sRichtapSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_dirac_v2", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "supportDiracVersion2"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_iris5_display", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "supportIris5"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_ring_haptic", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "supportRingingWithHaptic"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_video_osie", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "supportVideoOsie"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("enable_video_sr", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "supportVideoSuperResolution"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings").getBoolean("disable_display_remapping", false)) {}
            if (prefs("settings").getBoolean("disable_display_remapping", false)) {}
            if (prefs("settings").getBoolean("disable_display_remapping", false)) {}
            if (prefs("settings").getBoolean("disable_display_remapping", false)) {}
            if (prefs("settings").getBoolean("disable_display_remapping", false)) {}
            if (prefs("settings").getBoolean("disable_display_remapping", false)) {}
            if (prefs("settings").getBoolean("disable_display_remapping", false)) {}
            if (prefs("settings").getBoolean("disable_display_remapping", false)) {}
            if (prefs("settings").getBoolean("disable_display_remapping", false)) {}
            if (prefs("settings").getBoolean("disable_display_remapping", false)) {}
            if (prefs("settings").getBoolean("disable_display_remapping", false)) {}
            if (prefs("settings").getBoolean("disable_display_remapping", false)) {}
        }
    }
}
