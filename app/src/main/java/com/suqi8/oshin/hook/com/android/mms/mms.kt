package com.suqi8.oshin.hook.com.android.mms

import android.annotation.SuppressLint
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import org.luckypray.dexkit.DexKitBridge

class mms: YukiBaseHooker() {
    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onHook() {
        loadApp("com.android.mms"){
            if (prefs("mms").getBoolean("remove_message_ads", false)) {
                DexKitBridge.create(this.appInfo.sourceDir).use {
                    it.findMethod {
                        matcher {
                            paramTypes(null, "java.util.HashMap", "java.util.HashMap")
                            returnType = "java.util.List"
                            usingStrings("\\s")
                        }
                    }.singleOrNull()?.also {
                        YLog.info("methodName:"+it.methodName + " className:" + it.className)
                        it.className.toClass().method { name = it.methodName }.hook { before { result = emptyList<message_ads>() } }
                    }
                }
            }
        }
    }
    data class message_ads(
        val any: Any
    )
}
