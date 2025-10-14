package com.suqi8.oshin.hook.settings

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import org.luckypray.dexkit.DexKitBridge

class SettingsFeature : YukiBaseHooker() {
    companion object {
        const val OPLUS_SETTINGS_PREFS_NAME = "settings\\feature"
        // 用于 DataChannel 通信的 Key
        const val KEY_GET_METHODS = "get_oplus_feature_methods"
        const val KEY_RETURN_METHODS = "return_oplus_feature_methods"
        // 用于在模块UI侧缓存方法列表的Key
        const val KEY_CACHED_METHODS = "cached_oplus_feature_methods"
        // 目标类
        const val TARGET_CLASS_CUSTOMIZE = "com.oplus.settings.utils.CustomizeFeatureUtils"
        const val TARGET_CLASS_SYS = "com.oplus.settings.utils.SysFeatureUtils"
    }

    override fun onHook() {
        loadApp(name = "com.android.settings") {
            // 监听来自模块 UI 的请求
            dataChannel.apply {
                wait(KEY_GET_METHODS) {
                    YLog.info("接收到特性方法的请求")
                    Thread {
                        try {
                            DexKitBridge.create(appInfo.sourceDir).use { bridge ->
                                YLog.info("开始扫描 Oplus Settings 功能方法...")

                                val customizeMethods = bridge.findMethod {
                                    matcher {
                                        declaredClass { className = TARGET_CLASS_CUSTOMIZE }
                                        returnType = "boolean"
                                    }
                                }
                                val customizeKeys = customizeMethods.map { method ->
                                    "${TARGET_CLASS_CUSTOMIZE}.${method.name}"
                                }
                                YLog.info("在 $TARGET_CLASS_CUSTOMIZE 中找到 ${customizeKeys.size} 个方法。")

                                // 2. 单独扫描第二个类
                                val sysMethods = bridge.findMethod {
                                    matcher {
                                        declaredClass { className = TARGET_CLASS_SYS }
                                        returnType = "boolean"
                                    }
                                }
                                val sysKeys = sysMethods.map { method ->
                                    "${TARGET_CLASS_SYS}.${method.name}"
                                }
                                YLog.info("在 $TARGET_CLASS_SYS 中找到 ${sysKeys.size} 个方法。")

                                val uniqueKeys = ArrayList((customizeKeys + sysKeys).distinct().sorted())

                                YLog.info("扫描完成, 共找到 ${uniqueKeys.size} 个唯一功能 Key，正在发送回模块UI。")
                                put(KEY_RETURN_METHODS, value = uniqueKeys)
                            }
                        } catch (e: Throwable) {
                            YLog.error("扫描 Oplus 功能方法时发生严重错误!", e)
                            put(KEY_RETURN_METHODS, value = ArrayList<String>())
                        }
                    }.start()
                }
            }




            // --- 优化点 1: 先过滤，再执行 ---
            val prefs = prefs(OPLUS_SETTINGS_PREFS_NAME)
            val allCachedKeys = prefs.getStringSet(KEY_CACHED_METHODS, emptySet())

            if (allCachedKeys.isNotEmpty()) {
                // 先从所有缓存的 Keys 中，筛选出 mode 不为 0 (非默认) 的项
                val modifiedKeys = allCachedKeys.filter { prefs.getInt(it, 0) != 0 }

                if (modifiedKeys.isNotEmpty()) {
                    YLog.info("找到 ${modifiedKeys.size} 个已配置的功能 Key 进行 Hook")
                    modifiedKeys.forEach { uniqueKey ->
                        val className = uniqueKey.substringBeforeLast('.')
                        val methodName = uniqueKey.substringAfterLast('.')

                        if (className.isNotEmpty() && methodName.isNotEmpty()) {
                            YLog.info("Hooking uniqueKey: $uniqueKey")
                            hookFeatureMethod(uniqueKey, methodName, className)
                        }
                    }
                }
            }
        }
    }

    private fun PackageParam.hookFeatureMethod(uniqueKey: String, methodName: String, className: String) {
        val prefs = prefs(OPLUS_SETTINGS_PREFS_NAME)
        val mode = prefs.getInt(uniqueKey, 0)
        className.toClass().resolve().firstMethod {
            name = methodName
        }.hook {
            before {
                YLog.info("Hooked $className.$methodName -> 模式: $mode")
                result = when (mode) {
                    1 -> true   // 强制开启
                    2 -> false  // 强制关闭
                    else -> result
                }
            }
        }
    }
}
