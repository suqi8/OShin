package com.suqi8.oshin.hook.ota

import android.content.ContentResolver
import android.provider.Settings
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import org.luckypray.dexkit.DexKitBridge

class ota : YukiBaseHooker() {
    override fun onHook() {
        val prefs = prefs("ota")
        loadApp(name = "com.oplus.ota") {
            val bridge = DexKitBridge.create(this.appInfo.sourceDir)
            bridge.also {
                it.findClass {
                    matcher {
                        addMethod {
                            usingStrings(
                                "upgrade_show_download_dialog_time_interval",
                                "entry ui is not exist!!"
                            )
                        }
                        addMethod {
                            usingStrings(
                                "upgrade_show_install_dialog_time_interval",
                                "global_dialog_install_delay"
                            )
                        }
                    }
                }.singleOrNull()?.also {
                    if (prefs.getBoolean("remove_system_update_dialog", false)) {
                        it.findMethod {
                            matcher {
                                usingStrings("There are no overlays right to showNotifyDownloadDialog, so return")
                            }
                        }.singleOrNull()?.also {
                            it.className.toClass().method { name = it.methodName }
                                .hook { replaceUnit { } }
                        }
                    }
                    if (prefs.getBoolean("remove_wlan_auto_download_dialog", false)) {
                        it.findMethod {
                            matcher {
                                usingStrings(
                                    "upgrade_show_download_dialog_time_interval",
                                    "OTA_NoticeAlertDialog"
                                )
                            }
                        }.singleOrNull()?.also {
                            it.className.toClass().method { name = it.methodName }
                                .hook { replaceUnit { } }
                        }
                    }
                }
                if (prefs.getBoolean("remove_system_update_notification", false)) {
                    it.findClass {
                        matcher {
                            addMethod {
                                usingStrings(
                                    "ota_notify_new_channel_default_id",
                                    "ota_notify_new_channel_id"
                                )
                            }
                            addMethod {
                                usingStrings(
                                    "NotificationHelper notifyABFinalizingProgress",
                                    "NotificationHelper initABFinalizingNotificationBuilder"
                                )
                            }
                        }
                    }.singleOrNull()?.also {
                        it.findMethod {
                            matcher {
                                usingStrings(
                                    "notifyNewVersionUpdate false, big version upgrade and not has enough space",
                                    "notifyNewVersionUpdate false, has disable download and install remind"
                                )
                            }
                        }.singleOrNull()?.also {
                            it.className.toClass().method { name = it.methodName }
                                .hook { replaceUnit { } }
                        }
                    }
                }
                if (prefs.getBoolean("remove_wlan_auto_download_dialog", false)) {
                    it.findClass {
                        searchPackages("com.oplus.common")
                        matcher {
                            addMethod {
                                usingStrings(
                                    "initStmapAndVersionType",
                                    "can not get oplus_custom_ota_version_info "
                                )
                            }
                            addMethod {
                                usingStrings(
                                    "OTA_AUTO_DOWNLOAD_STATUS should check the alarm now",
                                    "User change switch to Wlan, so set alarm"
                                )
                            }
                        }
                    }.singleOrNull()?.also {
                        it.findMethod {
                            matcher {
                                usingStrings("ro.boot.veritymode", "ro.boot.vbmeta.device_state")
                            }
                        }.singleOrNull()?.also {
                            it.className.toClass().method { name = it.methodName }
                                .hook { before { result = false } }
                        }
                    }
                }
            }
            if (prefs.getBoolean("bypass_preinstall_checks", false)) {
                val className = bridge.findClass {
                    matcher {
                        usingStrings("forbid_ota_local_update")
                    }
                }
                className.findMethod {
                    matcher {
                        usingStrings("forbid_ota_local_update")
                    }
                }.forEach {
                    it.className.toClass().resolve().apply {
                        firstMethod {
                            modifiers(Modifiers.PUBLIC, Modifiers.FINAL)
                            name = it.methodName
                            parameters("android.content.Context", String::class)
                            returnType = Int::class
                        }.hook {
                            after {
                                className.findField {
                                    matcher {
                                        type = "boolean"
                                    }
                                }.forEach {
                                    firstField {
                                        modifiers(Modifiers.PUBLIC)
                                        name = it.name
                                        type = Boolean::class
                                    }.of(instance).set(false)
                                }
                            }
                        }
                    }
                }
            }
            if (prefs.getBoolean("force_show_local_install", false)) {
                Settings.Global::class.java.resolve().firstMethod {
                    name = "getInt"
                    parameters(ContentResolver::class, String::class, Int::class)
                }.hook {
                    before {
                        if ("development_settings_enabled" == args[1]) result = 1
                    }
                }
            }

            if (prefs.getBoolean("force_download_last_update_package", false)) {
                val className = bridge.findClass {
                    matcher {
                        usingStrings("ro.build.display.id.show")
                    }
                }
                className.findMethod {
                    matcher {
                        usingStrings("ro.build.display.id.show")
                    }
                }.singleOrNull()?.also {
                    it.className.toClass().resolve().apply {
                        firstMethod {
                            modifiers(Modifiers.PUBLIC, Modifiers.STATIC)
                            name = it.methodName
                            emptyParameters()
                            returnType = String::class
                        }.hook {
                            after {
                                // 匹配 PLK110_16.0.0.001(CN01) 末尾三位数字并替换为 001
                                val origin = result as? String ?: ""
                                val replaced = origin.replace(Regex("(\\.\\d{3}\\()"), ".001(")
                                result = replaced
                            }
                        }
                    }
                }

                className.findMethod {
                    matcher {
                        usingStrings("ro.build.version.ota")
                    }
                }.singleOrNull()?.also {
                    it.className.toClass().resolve().apply {
                        firstMethod {
                            modifiers(Modifiers.PUBLIC, Modifiers.STATIC)
                            name = it.methodName
                            emptyParameters()
                            returnType = String::class
                        }.hook {
                            after {
                                // 匹配 _xxxx_yyyyyyyyyyyy 为 _0001_197001010001
                                val origin = result as? String ?: ""
                                val replaced =
                                    origin.replace(Regex("_(\\d{4})_(\\d{12})")) { "_0001_197001010001" }
                                result = replaced
                            }
                        }
                    }
                }
            }
        }
    }
}
