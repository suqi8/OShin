package com.suqi8.oshin.hook.com.oplus.ota

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import org.luckypray.dexkit.DexKitBridge

class ota: YukiBaseHooker() {
    override fun onHook() {
        loadApp(name = "com.oplus.ota") {
            DexKitBridge.create(this.appInfo.sourceDir).use {
                it.findClass {
                    matcher {
                        addMethod {
                            usingStrings("upgrade_show_download_dialog_time_interval","entry ui is not exist!!")
                        }
                        addMethod {
                            usingStrings("upgrade_show_install_dialog_time_interval", "global_dialog_install_delay")
                        }
                    }
                }.singleOrNull()?.also {
                    if (prefs("ota").getBoolean("remove_system_update_dialog", false)) {
                        it.findMethod {
                            matcher {
                                usingStrings("There are no overlays right to showNotifyDownloadDialog, so return")
                            }
                        }.singleOrNull()?.also {
                            it.className.toClass().method { name = it.methodName }.hook { replaceUnit {  } }
                        }
                    }
                    if (prefs("ota").getBoolean("remove_wlan_auto_download_dialog", false)) {
                        it.findMethod {
                            matcher {
                                usingStrings("upgrade_show_download_dialog_time_interval","OTA_NoticeAlertDialog")
                            }
                        }.singleOrNull()?.also {
                            it.className.toClass().method { name = it.methodName }.hook { replaceUnit {  } }
                        }
                    }
                }
                if (prefs("ota").getBoolean("remove_system_update_notification", false)) {
                    it.findClass {
                        matcher {
                            addMethod {
                                usingStrings("ota_notify_new_channel_default_id","ota_notify_new_channel_id")
                            }
                            addMethod {
                                usingStrings("NotificationHelper notifyABFinalizingProgress", "NotificationHelper initABFinalizingNotificationBuilder")
                            }
                        }
                    }.singleOrNull()?.also {
                        it.findMethod {
                            matcher {
                                usingStrings("notifyNewVersionUpdate false, big version upgrade and not has enough space","notifyNewVersionUpdate false, has disable download and install remind")
                            }
                        }.singleOrNull()?.also {
                            it.className.toClass().method { name = it.methodName }.hook { replaceUnit {  } }
                        }
                    }
                }
                if (prefs("ota").getBoolean("remove_wlan_auto_download_dialog", false)) {
                    it.findClass {
                        searchPackages("com.oplus.common")
                        matcher {
                            addMethod {
                                usingStrings("initStmapAndVersionType","can not get oplus_custom_ota_version_info ")
                            }
                            addMethod {
                                usingStrings("OTA_AUTO_DOWNLOAD_STATUS should check the alarm now", "User change switch to Wlan, so set alarm")
                            }
                        }
                    }.singleOrNull()?.also {
                        it.findMethod {
                            matcher {
                                usingStrings("ro.boot.veritymode","ro.boot.vbmeta.device_state")
                            }
                        }.singleOrNull()?.also {
                            it.className.toClass().method { name = it.methodName }.hook { before { result = false } }
                        }
                    }
                }
            }
        }
    }
}
