package com.suqi8.oshin.hook.com.android.settings

import android.hardware.Sensor
import android.os.PersistableBundle
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType

class feature: YukiBaseHooker() {
    override fun onHook() {
        loadApp(name = "com.android.settings") {
            "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                method {
                    name = "hasAllClientFeature"
                    emptyParam()
                    returnType = BooleanType
                }.hook {
                    before {
                        if (prefs("settings\\feature").getBoolean("demo_only_device", false)) result = false
                        if (prefs("settings\\feature").getBoolean("retail_locked_terminal", false)) result = true
                    }
                }
            }
            if (prefs("settings\\feature").getBoolean("force_enable_karaoke", false)) {
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
            if (prefs("settings\\feature").getBoolean("force_enable_all_features", false)) {
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
            if (prefs("settings\\feature").getBoolean("force_enable_3d_camera_color", false)) {
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
            if (prefs("settings\\feature").getBoolean("force_aon_explorer", false)) {
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
                "com.oplus.settings.feature.display.controller.KeepOnLookingController".toClass().apply {
                    method {
                        name = "isAonEnable"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings\\feature").getBoolean("force_enable_app_freeze", false)) {
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
            if (prefs("settings\\feature").getBoolean("check_ble_audio_whitelist", false)) {
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
            if (prefs("settings\\feature").getBoolean("force_breathing_light_sync", false)) {
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
            if (prefs("settings\\feature").getBoolean("force_breathing_light_color", false)) {
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
            if (prefs("settings\\feature").getBoolean("force_support_wide_gamut", false)) {
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
            if (prefs("settings\\feature").getBoolean("force_support_color_mode", false)) {
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
            if (prefs("settings\\feature").getBoolean("force_support_hidden_app_feature", false)) {
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
            if (prefs("settings\\feature").getBoolean("force_support_smart_case", false)) {
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
            if (prefs("settings\\feature").getBoolean("force_dirac_audio", false)) {
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
            if (prefs("settings\\feature").getBoolean("force_dolby_audio", false)) {
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
            if (prefs("settings\\feature").getBoolean("force_dual_earbuds", false)) {
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
            if (prefs("settings\\feature").getBoolean("force_foldable_screen", false)) {
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
            if (prefs("settings\\feature").getBoolean("force_fold_or_flip_screen", false)) {
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
            if (prefs("settings\\feature").getBoolean("disable_display_remapping", false)) {
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
            if (prefs("settings\\feature").getBoolean("disable_gesture_navigation", false)) {
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
            if (prefs("settings\\feature").getBoolean("disable_google_mobile_services", false)) {
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
            if (prefs("settings\\feature").getBoolean("hide_storage_info", false)) {
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
            if (prefs("settings\\feature").getBoolean("enable_holo_audio", false)) {
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
            if (prefs("settings\\feature").getBoolean("force_hd_video", false)) {
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
            if (prefs("settings\\feature").getBoolean("auto_grant_install", false)) {
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
            if (prefs("settings\\feature").getBoolean("disable_lock_wallpaper", false)) {
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
            if (prefs("settings\\feature").getBoolean("light_os", false)) {
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
            if (prefs("settings\\feature").getBoolean("force_multi_volume", false)) {
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
            if (prefs("settings\\feature").getBoolean("force_app_clone", false)) {
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
            if (prefs("settings\\feature").getBoolean("force_adaptive_brightness", false)) {
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
            if (prefs("settings\\feature").getBoolean("disable_ota", false)) {
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
            if (prefs("settings\\feature").getBoolean("enable_audio_boost", false)) {
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
            if (prefs("settings\\feature").getBoolean("enable_ai_image", false)) {
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
            if (prefs("settings\\feature").getBoolean("enable_osie_tech", false)) {
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
            if (prefs("settings\\feature").getBoolean("force_shutdown_key", false)) {
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
            if (prefs("settings\\feature").getBoolean("single_pulse_pwm", false)) {
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
            if (prefs("settings\\feature").getBoolean("disable_res_switch", false)) {
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
            if (prefs("settings\\feature").getBoolean("manual_refresh_rate", false)) {
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
            if (prefs("settings\\feature").getBoolean("default_smart_refresh", false)) {
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
            if (prefs("settings\\feature").getBoolean("refresh_rate_notify", false)) {
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
            if (prefs("settings\\feature").getBoolean("enable_sell_mode", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSellModeVersion" // 原方法名保持
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } } // 强制返回已启用状态
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_dual_sim", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSimGeminiSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } } // 欺骗系统支持双卡
                }
            }
            if (prefs("settings\\feature").getBoolean("disable_single_sim_check", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSingleCardPhone"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = false } } // 强制返回非单卡状态
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_anti_voyeur", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSmartAntiVoyeurEnabled"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } } // 强制启用防偷窥
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_snc_content", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSncSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } } // 伪装支持 SNC
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_sound_combo", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSoundEffectCombinedSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } } // 激活组合音效
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_sound_settings", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSoundEffectSettingsSupported"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } } // 解锁音效设置
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_audio_input", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSoundInputDeviceSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } } // 强制支持输入设备
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_1.5k_resolution", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupport15Resolution"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_adfr", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportADFR"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_aod", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportAod"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_aon_face", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportAonFace"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_autolayout", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportAutoLayout"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_blade_colormode", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportBladeColorMode"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_breeno_suggest", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportBreenoSuggest"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_brightness_anim", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportBrightnessNewAnimation"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_cinema_mode", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportColorModeCinema"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_oled_colorful", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportColorModeColorful"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_custom_color", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportColorModeCustomize"
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_colorful_mode", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportColorModeOplusColorful"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_powersaving_color", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportColorModePowerSaving"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_compact_window", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportCompactWindow"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_dc_backlight", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportDCBacklight"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_dynamic_brightness", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportDevelopmentAdjustBrightnessBarRange"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_dirac_a2dp", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportDirac"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_dynamic_fps", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportDynamicFpsMode"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_edge_anti_touch", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportEdgePreventMistouch"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_5g_support", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportFiveG"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("disable_fold_remap", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportFoldRemapDisable"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } } // 开启此开关会激活禁用逻辑
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_gt_mode", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportGTMode"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_hdr_alwayson", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportHdrAlwayson"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_hdr_highlight", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportHdrVideoHighLightMode"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_smart_color_temp2", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportIntelligentColorTemperature2"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_linear_vibration", false)) {
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
            if (prefs("settings\\feature").getBoolean("enable_luxun_vibration", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportLuXunVibration"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_multi_led_breathing", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportMultiLedBreathingLight"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_phone_limit", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportPhoneLimit"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_pixelworks_x7", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportPixelworksX7Enable"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_resolution_switch", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportResolution"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_ringtone_vibration", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportRingtoneVibration"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_satellite_network", false)) {
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
            if (prefs("settings\\feature").getBoolean("enable_spatializer_speaker", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportSpatializerSpeaker"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            arrayOf("enable_super_volume2x", "enable_super_volume3x").forEach { key ->
                if (prefs("settings\\feature").getBoolean(key, false)) {
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
            if (prefs("settings\\feature").getBoolean("enable_temp_adjust", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportTemperatureAdjustment"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_touchpad_split", false)) {
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
            if (prefs("settings\\feature").getBoolean("enable_ultrasonic_fp", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSupportUltrasonicFP"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }

            if (prefs("settings\\feature").getBoolean("enable_volume_boost", false)) {
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
            if (prefs("settings\\feature").getBoolean("enable_color_ball", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSuppprtColorTemperateBall" 
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_surround_effect", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isSurroundEffectSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_tablet_mode", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isTabletDevice"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_typec_menu", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isTypecSupported"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_ultrasonic_security", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isUltrasonicFPConfidential"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_vibrator_style", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isVibratorStyleSwitchSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_smart_screenoff", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "sIsSmartScreenOffSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_richtap_vibrate", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "sRichtapSupport"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_dirac_v2", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "supportDiracVersion2"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_iris5_display", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "supportIris5"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_ring_haptic", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "supportRingingWithHaptic"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_video_osie", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "supportVideoOsie"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_video_sr", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "supportVideoSuperResolution"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("disable_deactivate_app", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().apply {
                    method {
                        name = "disableDeactivateApp"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before { result = true }
                    }
                }
            }
            if (prefs("settings\\feature").getBoolean("disable_haptic_preview", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().apply {
                    method {
                        name = "disableHapticPreview"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before { result = true }
                    }
                }
            }
            if (prefs("settings\\feature").getBoolean("disable_modify_devname", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().apply {
                    method {
                        name = "disableModifyDeviceName"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before { result = true }
                    }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_super_sleep", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().apply {
                    method {
                        name = "hasSuperSleepFeature"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before { result = true } // 强制开启超级睡眠
                    }
                }
            }
            if (prefs("settings\\feature").getBoolean("disable_5g_reminder", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "is5gGuidanceReminder"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = false } } // 禁用提醒
            }
            if (prefs("settings\\feature").getBoolean("disable_account_dialog", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isAccountDialogDisabled"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } } // 强制禁用弹窗
            }
            if (prefs("settings\\feature").getBoolean("enable_app_disable", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isAppDisallowDisable"
                    returnType = BooleanType
                }.hook { before { result = false } } // 允许禁用任意应用
            }
            if (prefs("settings\\feature").getBoolean("hide_cmiit_auth", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isAuthenticationCmiitInvisible"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("enable_hyper_vision", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isCWSupport"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("disable_carrier", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isCarrierDisabled"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("locale_uk_to_en", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isCastLocaleNameFromUkEnToEn"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("disable_clear_cache", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isClearCacheDisabled"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("enable_colorful_real", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isColorfulShowReal"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("disable_confidential", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isConfidentialVersion"
                    emptyParam()
                    returnType = BooleanType
                }.hook {
                    before {
                        result = false
                    }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_cyberpunk", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isCyberpunkCustomizeVersion"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("auto_resolution", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isDefaultResolutionAuto"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }

            if (prefs("settings\\feature").getBoolean("enable_oem_unlock", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isDisableOemUnlock"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = false } } // 返回false表示不禁用OEM解锁
            }

            if (prefs("settings\\feature").getBoolean("disable_auto_rotate", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isDisableShowAutoRotate"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } } // 返回true表示隐藏选项
            }

            if (prefs("settings\\feature").getBoolean("disable_app_switch", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isDisallowSwitchToPreviousApp"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }

            if (prefs("settings\\feature").getBoolean("enable_euex", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isEuexVersion"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }

            if (prefs("settings\\feature").getBoolean("force_exp_version", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isExpVersion"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } } // 覆盖CN版本检测
            }

            if (prefs("settings\\feature").getBoolean("enable_film_finger", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isFilmEffectFingerFeature"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("enable_finger_anim", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isFingerprintAnimStyleDisable"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = false } } // 返回false表示不禁用
            }
            if (prefs("settings\\feature").getBoolean("enable_fintech_nfc", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isFintechLifeNfcSupport"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("force_flip_device", false)) {
                // 同时Hook两个相关类
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isFlipDevice"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }

                "com.oplus.settings.utils.SysFeatureUtils".toClass().method {
                    name = "isFoldRemapDisableDevice"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("disable_gesture", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isGestureDisabled"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("keep_gesture_up", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isGestureUpForceKeeped"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("more_gesture_up", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isGestureUpMore"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("enable_gota_update", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isGotaUpdateSupport"
                    emptyParam()
                    returnType = BooleanType
                }.hook {
                    before {
                        result = true
                    }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_business_state", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isHasBusinessStatementFeature"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("enable_ultimate_clean", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isHasUltimateCleanupFeature"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("hide_hw_version", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isHiddenHardWareVersion"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("hide_device_id", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isHideDevicesIdentify"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("hide_ktv_loopback", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isHideKtvLoopback"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("hide_mms_ringtone", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isHideMmsRingtone"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("move_dc_to_dev", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isHideMoveDcToDevelop"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("hide_network_speed", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isHideNetworkSpeed"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("hide_power_wake3", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isHidePowerWakeUpItem3"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("hide_sim_signal", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isHideSimSignalStrength"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("enable_humming", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isHummingEnabled"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("show_kernel_id", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isIdKernelVersion"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("ignore_repeat_click", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isIgnoreRepeatClickSupport"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("imei_sv_from_ota", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isImeiSvFromOtaVersion"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("enable_light_func", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isLightFunc"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("enable_marvel", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isMarvelVersion"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("hide_portrait_center", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isNeedHidePortraitCenter"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("hide_video_beauty", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isNeedHideVideoBeauty"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("show_2g3g", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isNeedShow2g3g"
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("disable_ocloud", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isOCloudDisabled"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("force_oh_device", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isOHDeviceExp"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } } // 覆盖硬编码的false
            }
            if (prefs("settings\\feature").getBoolean("only_hw_version", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isOnlyShowHardwareVersion"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("enable_kddi_au", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isOperatorKDDIShowAU"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("show_operator", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isOperatorSupport"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("hide_privacy_email", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isOplusPrivacyEmailNotSupport"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("keep_swipe_up", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isOplusSwipeUpForceKeeped"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("disable_ota", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isOtaDisabled"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("disable_otg_alarm", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isOtgAutoCloseAlarmDisable"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("disable_otg_entry", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isOtgEntryDisabled"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("enable_pac_custom", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isPacCustomizeVersion"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("disable_privacy", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isPrivacyDisabled"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("hide_fake_base", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isPseudoBaseStationInvisible"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("enable_rl_delete", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isRLDeleteLanguages"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("force_rlm_device", false)) {
                // 同时Hook两个相关方法
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().apply {
                    method { name = "isRLMDevice"; emptyParam() }.hook { before { result = true } }
                    method { name = "isRLMDeviceExp"; emptyParam() }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_raise_wake", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isRaiseToWakeSupported"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("disable_recent_task", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isRecentTaskManagementUnavailable"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("remove_cota_home", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isRemoveCotaHomeTag"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("disable_resize_screen", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isResizeableScreenDisabled"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("enable_rlm_feedback", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isRlmSupportFeedbackV2"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("disable_screen_pin", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isScreenPinningDisable"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("disable_search_index", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isSearchIndexDisabled"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("enable_seedling_exp", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isSeedlingExpRegion"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("enable_custom_devname", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isSetCustomizeDeviceName"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("enable_cota_devname", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isSetDeviceNameByCOTA"
                    returnType = BooleanType
                }.hook {
                    before {
                        result = true
                    }
                }
            }
            if (prefs("settings\\feature").getBoolean("disable_set_password", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isSetPasswordDisabled"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("hide_all_anr", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isShowAllAnrDisabled"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("show_brand_name", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isShowBrandName"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("show_carrier_config", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isShowCarrierConfigVersion"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("show_carrier_update", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isShowCarrierSystemUpdate"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("show_custom_details", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isShowCustomDetails"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("hide_data_usage", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isShowDataUsageInfoInvisible"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("show_diagnostic", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isShowDiagnostic"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("show_os_firstname", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isShowFirstNameOfOsVersion"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("show_hw_version", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isShowHardWareVersionInAboutDevice"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("show_ims_status", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isShowImsRegistrationStatus"
                    returnType = BooleanType
                }.hook {
                    before {
                        val bundle = args[0] as? PersistableBundle
                        bundle?.putBoolean("show_ims_registration_status_bool", true)
                        result = true
                    }
                }
            }
            if (prefs("settings\\feature").getBoolean("show_kernel_time", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isShowKernelVersionTime"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("show_net_unlock", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isShowNetworkUnlock"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("show_never_timeout", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isShowNeverTimeout"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("hide_npu_detail", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isShowNpuDetailDisable"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("show_processor", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().apply {
                    method { name = "isShowProcessorDetails" }.hook {
                        before {
                            // 覆盖保密版本检测
                            method { name = "isConfidentialVersion" }.hook { before { result = false } }
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings\\feature").getBoolean("show_processor_gen2", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isShowProcessorDetailsGen2"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("screen_size_cm", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isShowScreenPhysicsSizeUnitCM"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("show_sw_version", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isShowSoftWareVersionInAboutDevice"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("sw_instead_build", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isShowSwVersionInsteadOfBuildNumber"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("show_uicc_unlock", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isShowUICCUnlock"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("enable_sim_lock", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().apply {
                    method { name = "isSimLockStateSupport" }.hook {
                        before {
                            "com.oplus.settings.utils.SettingsPackageUtils".toClass()
                                .method { name = "isActionSupported" }
                                .hook { before { result = true } }
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings\\feature").getBoolean("hide_sim_toolkit", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isSimToolkitInvisible"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("force_software_conf", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().apply {
                    method {
                        name = "isSoftwareConfidention"
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("special_side_finger", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isSpecialSideFinger"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("enable_circle_search", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isSupportCircleToSearch"
                    emptyParam()
                    returnType = BooleanType
                }.hook {
                    before {
                        result = true
                    }
                }
            }
            if (prefs("settings\\feature").getBoolean("show_custom_ver", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isSupportCustomVersion"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("enable_electronic_label", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().apply {
                    method { name = "isSupportElectronicLabel" }.hook {
                        before {
                            result = true
                        }
                    }
                    "com.oplus.settings.utils.RegionUtils".toClass()
                        .method { name = "getRegion"; emptyParam() }
                        .hook { before { result = "IN_SYSTEM_REGION_ID" } }
                }
            }
            if (prefs("settings\\feature").getBoolean("fullscreen_apps", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isSupportFullScreenDisplay"
                    returnType = BooleanType
                }.hook {
                    before {
                        // 强制所有应用返回true
                        result = true
                    }
                }
            }
            if (prefs("settings\\feature").getBoolean("smart_gesture", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isSupportGestureIntelligentPerception"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            arrayOf("show_imsi", "show_meid").forEach { key ->
                if (prefs("settings\\feature").getBoolean(key, false)) {
                    "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                        name = when(key) {
                            "show_imsi" -> "isSupportImsi"
                            else -> "isSupportMeid"
                        }
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }
            if (prefs("settings\\feature").getBoolean("member_rcc_show", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isSupportMemberRccShow"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("mini_capsule", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isSupportMiniCapsule"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("number_recognition", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isSupportNumberRecognition"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("enable_oguard", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isSupportOGuardInfo"
                    emptyParam()
                    returnType = BooleanType
                }.hook {
                    before {
                        "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().apply {
                            method {
                                name = "isExpVersion"
                                emptyParam()
                                returnType = BooleanType
                            }.hook {
                                before { result = true }
                            }
                        }
                        result = true
                    }
                }
            }
            if (prefs("settings\\feature").getBoolean("oh_india_version", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isSupportOHIndiaVersion"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("usb_tether_boot", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isSupportOpenUsbTetheringBoot"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("quick_app_support", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().apply {
                    method { name = "isSupportQuickApp" }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings\\feature").getBoolean("region_picker", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isSupportRegion"
                    emptyParam()
                    returnType = BooleanType
                }.hook {
                    before {
                        result = true
                    }
                }
            }
            if (prefs("settings\\feature").getBoolean("enable_roulette", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().apply {
                    method { name = "isSupportRouletteSupport" }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }

            if (prefs("settings\\feature").getBoolean("show_wfc_dialog", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isSupportShowWfcTipDialog"
                    returnType = BooleanType
                }.hook { before { result = true } }
            }

            arrayOf("smart_touch", "smart_touch_v2").forEach { key ->
                if (prefs("settings\\feature").getBoolean(key, false)) {
                    "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                        name = when(key) {
                            "smart_touch" -> "isSupportSmartTouch"
                            else -> "isSupportSmartTouchV2"
                        }
                        emptyParam()
                        returnType = BooleanType
                    }.hook { before { result = true } }
                }
            }

            if (prefs("settings\\feature").getBoolean("show_sms_number", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isSupportSmsNumber"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }

            if (prefs("settings\\feature").getBoolean("ai_eye_protect", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isSuppprtAiIntelligentEyeProtect"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }

            if (prefs("settings\\feature").getBoolean("disable_edge_panel", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isSystemEdgePanel"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("disable_stable_plan", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isSystemStablePlanDisable"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("disable_time_change", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isTimeChangeDisabled"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }

            if (prefs("settings\\feature").getBoolean("disable_gaze_ringtone", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isUnSupportGazeFadeRingtone"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }

            if (prefs("settings\\feature").getBoolean("disable_user_exp", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isUserExperienceDisabled"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }

            if (prefs("settings\\feature").getBoolean("disable_verify_dialog", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isVerificationDialogDisabled"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }

            if (prefs("settings\\feature").getBoolean("virtual_comm_device", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().apply {
                    method { name = "initVirtualComm" }.hook { before { } } // 跳过初始化
                    method { name = "isVirtualCommConsumerDevice" }.hook { before { result = true } }
                }
            }

            if (prefs("settings\\feature").getBoolean("virtual_comm_service", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isVirtualCommInService"
                    returnType = BooleanType
                }.hook {
                    before {
                        // 强制返回服务可用状态
                        result = true
                    }
                }
            }

            if (prefs("settings\\feature").getBoolean("disable_vowifi_setting", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isVoWifiSettingUnavailable"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("disable_volte_setting", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isVolteSettingUnavailable"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("volte_icon_off", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isVolteStatusIconDefaultOff"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("disable_wifi_setting", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "isWifiSettingsUnavailable"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("hide_install_sources", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "needHideInstallationSources"
                    emptyParam()
                    returnType = List::class.java
                }.hook {
                    before {
                        // 返回空列表隐藏所有安装来源
                        result = emptyList<String>()
                    }
                }
            }
            if (prefs("settings\\feature").getBoolean("biometric_privacy", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "needPrivacyStatementWithBiometricUnlock"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("upload_error_log", false)) {
                "com.oplus.settings.utils.CustomizeFeatureUtils".toClass().method {
                    name = "supportUploadErrorLog"
                    emptyParam()
                    returnType = BooleanType
                }.hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("dirac_sound", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().method {
                    name = "isDiracSupported"
                    emptyParam()
                    returnType = BooleanType
                }.hook {
                    before {
                        result = true
                    }
                }
            }
            if (prefs("settings\\feature").getBoolean("fluid_cloud", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().method {
                    name = "isSupportFluidCloud"
                    emptyParam()
                    returnType = BooleanType
                }.hook {
                    before {
                        result = true
                    }
                }
            }
            if (prefs("settings\\feature").getBoolean("hyper_mode", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().method {
                    name = "isSupportHyperMode"
                    emptyParam()
                    returnType = BooleanType
                }.hook {
                    before {
                        Runtime.getRuntime().exec("echo performance > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor")
                        result = true
                    }
                }
            }
            if (prefs("settings\\feature").getBoolean("edge_panel", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method { name = "isSystemEdgePanel" }.hook { before { result = true } }
                    method { name = "getEdgePanelLayout" }.hook {
                        before {
                            result = "custom_edge_panel_layout"
                        }
                    }
                }
            }
            arrayOf("linear_vibration", "op7_vibration").forEach { key ->
                if (prefs("settings\\feature").getBoolean(key, false)) {
                    "com.oplus.settings.utils.SysFeatureUtils".toClass().method {
                        name = when(key) {
                            "linear_vibration" -> "isSupportLinearVibration"
                            else -> "isSupportOp7Vibration"
                        }
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings\\feature").getBoolean("stealth_security", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method { name = "isStealthSecurityModeSupport" }.hook { before { result = true } }
                    method { name = "isAlertSliderWithStealthSecurityModeSupport" }.hook { before { result = true } }
                }
                "com.android.keyguard.KeyguardSecurityContainer".toClass()
                    .method { name = "showStealthSecurityView" }
                    .hook { before { result = true } }
            }
            if (prefs("settings\\feature").getBoolean("anti_voyeur", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass()
                    .method { name = "isSmartAntiVoyeurEnabled" }
                    .hook { before { result = true } }

                "android.hardware.SystemSensorManager".toClass()
                    .method { name = "getDefaultSensor" }
                    .hook {
                        before {
                            if (args[0] == Sensor.TYPE_PROXIMITY) {
                                result = null // 禁用距离传感器
                            }
                        }
                    }
            }
            if (prefs("settings\\feature").getBoolean("enable_redpacket_helper", false)) {
                "com.oplus.settings.feature.convenient.controller.RedEnvelopeController".toClass().apply {
                    method {
                        name = "isSupportEnvelope"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("settings\\feature").getBoolean("palm_unlock", false)) {
                "com.oplus.settings.feature.palm.PalmUtils".toClass().apply {
                    method {
                        name = "supportPalm"
                        param("android.content.Context")
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
        }
    }
}
