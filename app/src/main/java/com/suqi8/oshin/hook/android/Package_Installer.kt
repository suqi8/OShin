package com.suqi8.oshin.hook.android

import android.content.Intent
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.java.IntType

class Package_Installer : YukiBaseHooker() {
    override fun onHook() {
        loadSystem {
            if (prefs("android").getBoolean("", false)) {
            }
            loadSystem {
                // Hook系统级Activity启动服务
                //hookSystemServices()
            }
            loadApp("com.android.packageinstaller") {
                //hookInstallerActivities()  // Hook安装器Activity
            }
        }
    }

    /**
     * Hook系统服务核心方法
     * 目标：拦截ActivityStarter的intent处理
     */
    private fun hookSystemServices()  {
        "com.android.server.wm.ActivityStarter".toClass().apply {
            method {
                name = "execute"
                emptyParam()
                returnType = IntType
            }.hook {
                before {
                    // 获取 com.android.server.wm.ActivityStarter\$Request 类的 Class 对象
                    val requestClass = "com.android.server.wm.ActivityStarter\$Request".toClass()

                    // 确保 requestClass 不为 null
                    requestClass.let { clazz ->
                        // 获取当前实例的 mRequest 字段值
                        val mRequest = instance.javaClass.field {
                            name = "mRequest"
                            type = clazz
                        }.get()

                        // 确保 mRequest 不为 null
                        mRequest.let { requestInstance ->
                            // 获取 intent 字段的值
                            val intent = requestInstance.javaClass.field {
                                name = "intent"
                                type = Intent::class.java
                            }.get()

                            // 确保 intent 不为 null
                            intent.let { intentInstance ->
                                YLog.info("$intentInstance")
                                // 调用 handleIntentRedirect 方法处理 intent
                                //handleIntentRedirect(intentInstance)
                            }
                        }
                    }
                    //YLog.info("befintent: $intent mRequest: ${mRequest}")
                    // 从ActivityStarter$Request对象获取原始intent
                    /*handleIntentRedirect(
                        intent
                    )*/
                }
                after {
                    "com.android.server.wm.ActivityStarter\$Request".toClass().apply {
                        field {
                            name = "intent"
                            type = "android.content.Intent"
                        }.get(this).cast<Intent>()?.apply {
                            YLog.info("$this")
                        }
                    }
                    //YLog.info("befintent: $intent mRequest: ${mRequest}")
                    // 从ActivityStarter$Request对象获取原始intent
                    /*handleIntentRedirect(
                        intent
                    )*/
                }
            }
        }
    }
    /**
     * 核心Intent处理逻辑
     * @param intent 系统原始intent
     */
    private fun handleIntentRedirect(intent: Intent?) {
        intent?.apply {
            when {
                // 处理卸载请求
                //isUninstallIntent() -> handleUninstall()
                // 处理安装请求
                //isInstallIntent() -> handleInstall()
            }
        }
    }
}
