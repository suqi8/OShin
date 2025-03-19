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
            if (prefs("settings").getBoolean("force_breathing_light_color", false)) {
                "com.oplus.settings.utils.SysFeatureUtils".toClass().apply {
                    method {
                        name = "isBreathingLightSupport"
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
        }
    }
}
