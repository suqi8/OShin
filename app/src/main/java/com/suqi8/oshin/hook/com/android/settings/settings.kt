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
        loadApp(hooker = feature())
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
            /*"com.android.settings.SettingsActivity".toClass().apply {
                method {
                    name = "onCreate"
                    param(BundleClass)
                }.hook {
                    after {
                        // 获取当前 Activity 实例
                        val activity = instance<Activity>()
                        // 获取启动该 Activity 的 Intent
                        val intent = activity.intent
                        // 获取 Intent 的 Action 字段
                        val action = intent.action
                        YLog.info("Activity: $activity")

                        // 如果 Action 为 ACCESSIBILITY_SETTINGS，则执行无障碍服务授权逻辑
                        if (action != null && action == "android.settings.ACCESSIBILITY_SETTINGS") {
                            // 获取 PackageManager，用于后续查询应用信息
                            val packageManager = activity.packageManager

                            // 获取打开该 Activity 的应用包名
                            var packageName: String? = null
                            try {
                                // 通过反射获取 Intent 内部隐藏字段 mIntentExt
                                val mIntentExt = field { name = "mIntentExt" }.get(intent)
                                if (mIntentExt == null) return@after
                                // 从 mIntentExt 中获取调用者 UID（字段 mCallingUid）
                                val uid = field { name = "mCallingUid" }.get(mIntentExt) as? Int
                                // 通过 UID 获取对应的包名数组
                                val packages = packageManager.getPackagesForUid(uid ?: -1)
                                if (packages.isNullOrEmpty()) return@after
                                // 选择第一个包名作为目标包名
                                packageName = packages[0]
                                if (packages.size > 1) {
                                    YLog.info("UID $uid 下有多个包名：${packages.contentToString()}，选择第一个：$packageName")
                                }
                            } catch (e: NoSuchFieldError) {
                                // 若未找到 mIntentExt 字段，则尝试直接从 Intent 中获取 mSenderPackageName 字段
                                packageName = field { name = "mSenderPackageName" }.get(intent) as? String
                            }

                            // 若无法确定包名，则退出后续处理
                            if (packageName == null) return@after

                            // 定义变量存储无障碍服务类名及其简介（描述信息）
                            var accessibilityService: String? = null
                            var summary: String? = null

                            // 显式声明参数类型为 Class<*>
                            val parameterTypes = arrayOf<Class<*>>(Context::class.java)
                            val args = arrayOf<Any>(activity)
                            // 调用 AccessibilityManager.getInstance(Context)
                            val accessibilityManager = callStaticMethod(
                                AccessibilityManager::class.java,
                                "getInstance",
                                parameterTypes,
                                args
                            ) as AccessibilityManager


                            // 遍历已安装的无障碍服务列表，查找包名匹配的服务
                            for (serviceInfo in accessibilityManager.installedAccessibilityServiceList) {
                                val service = serviceInfo.resolveInfo.serviceInfo
                                if (packageName == service.packageName) {
                                    accessibilityService = service.name
                                    // 获取该服务的描述信息
                                    summary = serviceInfo.loadDescription(packageManager)
                                    break
                                }
                            }

                            // 获取目标应用名称，用于提示或日志记录
                            val appName = packageManager
                                .getApplicationLabel(packageManager.getApplicationInfo(packageName, 0))
                                .toString()

                            // 如果未找到对应的无障碍服务，则记录日志并退出
                            if (accessibilityService == null) {
                                YLog.info("错误：$packageName 没有对应的无障碍服务！")
                                return@after
                            }

                            // 拼接完整服务名称，格式为 "包名/服务类名"
                            val serviceName = "$packageName/$accessibilityService"
                            // 从系统设置中读取当前已启用的无障碍服务列表（以冒号分隔）
                            val eas = android.provider.Settings.Secure.getString(
                                activity.contentResolver,
                                android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                            )

                            // 如果当前没有启用任何服务，则直接写入目标服务
                            if (eas.isNullOrEmpty()) {
                                android.provider.Settings.Secure.putString(
                                    activity.contentResolver,
                                    android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                                    serviceName
                                )
                            } else {
                                // 否则，将现有服务列表拆分成可变列表
                                val serviceList = eas.split(":").toMutableList()
                                // 如果目标服务已存在，则先移除
                                if (serviceList.contains(serviceName)) {
                                    serviceList.remove(serviceName)
                                    android.provider.Settings.Secure.putString(
                                        activity.contentResolver,
                                        android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                                        serviceList.joinToString(":")
                                    )
                                }
                                // 添加目标服务，并更新系统设置
                                serviceList.add(serviceName)
                                android.provider.Settings.Secure.putString(
                                    activity.contentResolver,
                                    android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                                    serviceList.joinToString(":")
                                )
                            }

                            // 弹出 Toast 提示用户目标无障碍服务已被授权
                            android.widget.Toast.makeText(
                                activity,
                                "已直接授权: $serviceName",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()

                            // 结束当前 Activity
                            activity.finish()
                            return@after

                            // 以下为无障碍服务直达功能的代码示例（已注释掉）
                            *//*
                            loggerD("无障碍服务直达：$packageName/$accessibilityService")
                            val intentOpenSub = android.content.Intent(activity, classLoader.loadClass("com.android.settings.SubSettings"))
                            intentOpenSub.action = "android.intent.action.MAIN"
                            intentOpenSub.putExtra(":settings:show_fragment_title", appName)
                            intentOpenSub.putExtra(":settings:source_metrics", 0)
                            intentOpenSub.putExtra(":settings:show_fragment_title_resid", -1)
                            intentOpenSub.putExtra(":settings:show_fragment", "com.android.settings.accessibility.VolumeShortcutToggleAccessibilityServicePreferenceFragment")
                            val bundle1 = android.os.Bundle()
                            val componentName = android.content.ComponentName(packageName, accessibilityService)
                            bundle1.putParcelable("component_name", componentName)
                            bundle1.putString("package", packageName)
                            bundle1.putString("preference_key", "$packageName/$accessibilityService")
                            bundle1.putString("summary", summary)
                            intentOpenSub.putExtra(":settings:show_fragment_args", bundle1)
                            activity.startActivity(intentOpenSub)
                            *//*
                        }
                    }
                }
            }*/
        }
    }
}

