package com.suqi8.oshin.hook.com.oplus.battery

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import org.luckypray.dexkit.DexKitBridge
import java.lang.reflect.Modifier

class battery: YukiBaseHooker() {
    override fun onHook() {
        loadApp(name = "com.oplus.battery") {
            DexKitBridge.create(this.appInfo.sourceDir).use {
                if (prefs("battery").getBoolean("low_battery_fluid_cloud", false)) {
                    it.findClass {
                        matcher {
                            addMethod {
                                usingStrings("serviceInstanceId","isSupportMultiInstance")
                            }
                            addMethod {
                                usingStrings("createCallBack: resultCode = ")
                            }
                            addMethod {
                                usingStrings("createCallBack,action = ")
                            }
                        }
                    }.singleOrNull()?.also {
                        it.name.toClass().method { name = "sendSeedling" }.hook { before { result = 0 } }
                    }
                }
                if (prefs("battery").getInt("auto_start_max_limit", 5) != 5) {
                    it.findClass {
                        matcher {
                            addMethod {
                                usingStrings(" ready for first page: isAutoStart:"," stop async work when loadIcon: ")
                            }
                        }
                    }.findMethod {
                        matcher {
                            modifiers = Modifier.PUBLIC
                            returnType = "int"
                            paramTypes()
                            invokeMethods {
                                add {
                                    modifiers = Modifier.PUBLIC
                                    returnType = "int"
                                    paramTypes()
                                    usingNumbers(5,20)
                                }
                            }
                        }
                    }.singleOrNull()?.also {
                        it.className.toClass().method { name = it.methodName }.hook { replaceTo(prefs("battery").getInt("auto_start_max_limit", 5)) }
                    }
                }
            }
        }
    }
}
