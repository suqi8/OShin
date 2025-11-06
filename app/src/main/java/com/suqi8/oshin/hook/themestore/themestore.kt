package com.suqi8.oshin.hook.themestore

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.luckypray.dexkit.DexKitBridge

class themestore : YukiBaseHooker() {
    override fun onHook() {
        val prefs = prefs("themestore")
        loadApp(name = "com.heytap.themestore") {
            val bridge = DexKitBridge.create(this.appInfo.sourceDir)
            "com.nearme.themespace.activities.ThemeMainActivity".toClass().resolve().apply {
                firstMethod {
                    modifiers(Modifiers.PROTECTED)
                    name = "onCreate"
                    parameters("android.os.Bundle")
                    returnType = Void.TYPE
                }.hook {
                    after {
                        println("mytest: hooked ThemeMainActivity onCreate")
                    }
                }
            }
        }
    }
}
