package com.suqi8.oshin.hook.com.android.mms

import android.annotation.SuppressLint
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.luckypray.dexkit.DexKitBridge

class mms: YukiBaseHooker() {
    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onHook() {
        loadApp("com.android.mms"){
            if (prefs("mms").getBoolean("remove_message_ads", false)) {
                DexKitBridge.create(this.appInfo.sourceDir).use {
                    it.findClass {
                        matcher {
                            usingStrings("e = ","TedCardUtil")
                            usingStrings("isParsedSmsEntity: isSyncingMessage = ")
                            usingStrings("isVerificationCode = ")
                            usingStrings("getSmsEntity: JSONException")
                        }
                    } .findMethod {
                        matcher {
                            paramTypes(null, "java.util.HashMap", "java.util.HashMap")
                            returnType = "java.util.List"
                            usingStrings("\\s")
                        }
                    }.singleOrNull()?.also {
                        it.className.toClass().resolve().firstMethod { name = it.methodName }.hook { before { result = emptyList<Any>() } }
                    }
                }
            }
        }
    }
}
