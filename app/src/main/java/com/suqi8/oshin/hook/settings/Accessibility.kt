package com.suqi8.oshin.hook.settings

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BundleClass

class Accessibility : YukiBaseHooker() {
    @SuppressLint("PrivateApi")
    override fun onHook() {
        loadApp(name = "com.android.settings") {
            val auth = prefs("settings").getBoolean("auth", false)
            val jump = prefs("settings").getBoolean("jump", false)
            val autoauth = prefs("settings").getBoolean("autoauth", false)
            if (!auth && !jump && !autoauth) return
            val autoauthwhite = prefs("settings").getString("autoauthwhite", "")
            "com.android.settings.SettingsActivity".toClass().apply {
                method {
                    name = "onCreate"
                    param(BundleClass)
                }.hook {
                    after {
                        val activity = instance as Activity // 获取当前 Activity 实例
                        val intent = activity.intent // 获取启动该 Activity 的 Intent
                        val action = intent.action // 获取 Intent 的 Action
                        //YLog.info("Activity: $activity")

                        // 检查是否是无障碍服务设置的请求
                        if (action == "android.settings.ACCESSIBILITY_SETTINGS") {
                            val packageManager =
                                activity.packageManager // 获取 PackageManager 以查询应用信息
                            val referrer: Uri? = activity.referrer // 获取打开此 Activity 的应用包名
                            val packageName: String? = referrer?.host // 提取包名
                            if (packageName.isNullOrEmpty()) return@after

                            var accessibilityService: String? = null // 无障碍服务类名
                            var summary: String? = null // 无障碍服务描述信息

                            // 获取 AccessibilityManager 以查询已安装的无障碍服务
                            val accessibilityManager =
                                activity.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
                            val appName = packageManager.getApplicationLabel(
                                packageManager.getApplicationInfo(
                                    packageName,
                                    0
                                )
                            ).toString()

                            // 遍历已安装的无障碍服务，查找对应包名的服务
                            for (serviceInfo in accessibilityManager.installedAccessibilityServiceList) {
                                val service = serviceInfo.resolveInfo.serviceInfo
                                if (packageName == service.packageName) {
                                    accessibilityService = service.name
                                    summary =
                                        serviceInfo.loadDescription(packageManager) // 获取无障碍服务的描述信息
                                    break
                                }
                            }

                            // 如果未找到对应的无障碍服务，记录日志并退出
                            if (accessibilityService == null) {
                                //YLog.error("错误：$packageName 没有对应的无障碍服务！")
                                return@after
                            }

                            val serviceName = "$packageName/$accessibilityService" // 生成完整的无障碍服务名称

                            if (auth) {
                                // 读取当前启用的无障碍服务列表
                                val enabledServices = Settings.Secure.getString(
                                    activity.contentResolver,
                                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                                )?.split(":")?.toMutableList() ?: mutableListOf()

                                // 先移除可能已存在的相同服务，避免重复添加
                                enabledServices.remove(serviceName)
                                enabledServices.add(serviceName) // 添加目标无障碍服务

                                // 更新系统无障碍服务设置
                                Settings.Secure.putString(
                                    activity.contentResolver,
                                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                                    enabledServices.joinToString(":")
                                )
                                activity.finish() // 关闭当前 Activity
                                return@after
                            }

                            if (jump) {
                                val intentOpenSub = Intent(
                                    activity,
                                    classLoader?.loadClass("com.android.settings.SubSettings")
                                )
                                intentOpenSub.action = "android.intent.action.MAIN"
                                intentOpenSub.putExtra(":settings:show_fragment_title", appName)
                                intentOpenSub.putExtra(":settings:source_metrics", 0)
                                intentOpenSub.putExtra(":settings:show_fragment_title_resid", -1)
                                intentOpenSub.putExtra(
                                    ":settings:show_fragment",
                                    "com.android.settings.accessibility.VolumeShortcutToggleAccessibilityServicePreferenceFragment"
                                )

                                val bundle = Bundle().apply {
                                    putParcelable(
                                        "component_name",
                                        ComponentName(packageName, accessibilityService)
                                    )
                                    putString("package", packageName)
                                    putString(
                                        "preference_key",
                                        "$packageName/$accessibilityService"
                                    )
                                    putString("summary", summary)
                                }
                                intentOpenSub.putExtra(":settings:show_fragment_args", bundle)
                                activity.startActivity(intentOpenSub)
                            }

                            if (autoauth) {
                                if (packageName in autoauthwhite.split(",")) {
                                    // 读取当前启用的无障碍服务列表
                                    val enabledServices = Settings.Secure.getString(
                                        activity.contentResolver,
                                        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                                    )?.split(":")?.toMutableList() ?: mutableListOf()

                                    // 先移除可能已存在的相同服务，避免重复添加
                                    enabledServices.remove(serviceName)
                                    enabledServices.add(serviceName) // 添加目标无障碍服务

                                    // 更新系统无障碍服务设置
                                    Settings.Secure.putString(
                                        activity.contentResolver,
                                        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                                        enabledServices.joinToString(":")
                                    )
                                    activity.finish() // 关闭当前 Activity
                                    return@after
                                } else {
                                    val intentOpenSub = Intent(
                                        activity,
                                        classLoader?.loadClass("com.android.settings.SubSettings")
                                    )
                                    intentOpenSub.action = "android.intent.action.MAIN"
                                    intentOpenSub.putExtra(":settings:show_fragment_title", appName)
                                    intentOpenSub.putExtra(":settings:source_metrics", 0)
                                    intentOpenSub.putExtra(":settings:show_fragment_title_resid", -1)
                                    intentOpenSub.putExtra(
                                        ":settings:show_fragment",
                                        "com.android.settings.accessibility.VolumeShortcutToggleAccessibilityServicePreferenceFragment"
                                    )

                                    val bundle = Bundle().apply {
                                        putParcelable(
                                            "component_name",
                                            ComponentName(packageName, accessibilityService)
                                        )
                                        putString("package", packageName)
                                        putString(
                                            "preference_key",
                                            "$packageName/$accessibilityService"
                                        )
                                        putString("summary", summary)
                                    }
                                    intentOpenSub.putExtra(":settings:show_fragment_args", bundle)
                                    activity.startActivity(intentOpenSub)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
