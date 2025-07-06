package com.suqi8.oshin.hook.com.coloros.securepay

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.luckypray.dexkit.DexKitBridge

class securepay: YukiBaseHooker() {
    override fun onHook() {
        loadApp(name = "com.coloros.securepay") {
            if (prefs("securepay").getBoolean("security_payment_remove_risky_fluid_cloud", false)) {
                DexKitBridge.create(this.appInfo.sourceDir).also {
                    it.findClass {
                        matcher {
                            usingStrings("[updateFixingCard] ","pages/fix")
                            usingStrings("showProgress")
                            usingStrings("entrancePackageNameKey")
                            usingStrings("pantanal.intent.business.app.system.PAY_SCAN")
                        }
                    }.findMethod {
                        matcher {
                            usingStrings("pantanal.intent.business.app.system.PAY_SCAN")
                            returnType = "void"
                            paramTypes("java.util.List")
                        }
                    }.singleOrNull()?.also {
                        it.className.toClass().resolve().firstMethod {
                            name = it.methodName
                        }.hook {
                            replaceUnit {  }
                        }
                    }
                }
            }
        }
    }
}
