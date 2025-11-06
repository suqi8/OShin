package com.suqi8.oshin.hook.settings

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import org.luckypray.dexkit.DexKitBridge

class SettingsFeature : YukiBaseHooker() {

    companion object {
        const val OPLUS_SETTINGS_PREFS_NAME = "settings\\feature"
        const val KEY_GET_METHODS = "get_oplus_feature_methods"
        const val KEY_RETURN_METHODS = "return_oplus_feature_methods"
        const val KEY_CACHED_METHODS = "cached_oplus_feature_methods"

        const val TARGET_CLASS_DEVICE_INFO = "com.oplus.settings.feature.deviceinfo.DeviceInfoUtils"
        const val TARGET_CLASS_CUSTOMIZE = "com.oplus.settings.utils.CustomizeFeatureUtils"
        const val TARGET_CLASS_SYS = "com.oplus.settings.utils.SysFeatureUtils"
    }

    override fun onHook() {
        loadApp(name = "com.android.settings") {

            // 监听 UI 请求
            dataChannel.wait(KEY_GET_METHODS) {
                YLog.info("接收到特性方法扫描请求")
                Thread {
                    try {
                        DexKitBridge.create(appInfo.sourceDir).use { bridge ->
                            YLog.info("开始扫描 Oplus Settings 功能方法...")

                            fun findBooleanMethods(className: String, returnType: String): List<String> {
                                val methods = bridge.findMethod {
                                    matcher {
                                        declaredClass { this.className = className }
                                        this.returnType = returnType
                                    }
                                }
                                YLog.info("在 $className 中找到 ${methods.size} 个返回 $returnType 的方法。")
                                return methods.map { "$className.${it.name}" }
                            }

                            val allKeys = buildList {
                                addAll(findBooleanMethods(TARGET_CLASS_CUSTOMIZE, "boolean"))
                                addAll(findBooleanMethods(TARGET_CLASS_SYS, "boolean"))
                                addAll(findBooleanMethods(TARGET_CLASS_DEVICE_INFO, "boolean"))
                                addAll(findBooleanMethods(TARGET_CLASS_DEVICE_INFO, "java.lang.Boolean"))
                            }.distinct().sorted()

                            YLog.info("扫描完成，共找到 ${allKeys.size} 个唯一功能 Key，发送回模块UI。")
                            dataChannel.put(KEY_RETURN_METHODS, ArrayList(allKeys))
                        }
                    } catch (e: Throwable) {
                        YLog.error("扫描 Oplus 功能方法时出错!", e)
                        dataChannel.put(KEY_RETURN_METHODS, ArrayList<String>())
                    }
                }.start()
            }

            // Hook 已配置的功能项
            val prefs = prefs(OPLUS_SETTINGS_PREFS_NAME)
            val modifiedKeys = prefs.getStringSet(KEY_CACHED_METHODS, emptySet())
                .filter { prefs.getInt(it, 0) != 0 }

            if (modifiedKeys.isNotEmpty()) {
                YLog.info("找到 ${modifiedKeys.size} 个已配置功能 Key，开始 Hook")
                modifiedKeys.forEach { uniqueKey ->
                    val className = uniqueKey.substringBeforeLast('.')
                    val methodName = uniqueKey.substringAfterLast('.')
                    if (className.isNotEmpty() && methodName.isNotEmpty())
                        hookFeatureMethod(uniqueKey, methodName, className)
                }
            }
        }
    }

    private fun PackageParam.hookFeatureMethod(uniqueKey: String, methodName: String, className: String) {
        val prefs = prefs(OPLUS_SETTINGS_PREFS_NAME)
        val mode = prefs.getInt(uniqueKey, 0)
        className.toClass().resolve().firstMethod { name = methodName }.hook {
            before {
                YLog.info("Hooked $className.$methodName -> 模式: $mode")
                result = when (mode) {
                    1 -> true
                    2 -> false
                    else -> result
                }
            }
        }
    }
}
