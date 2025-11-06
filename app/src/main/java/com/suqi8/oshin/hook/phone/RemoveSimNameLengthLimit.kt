package com.suqi8.oshin.hook.phone

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.luckypray.dexkit.DexKitBridge


class RemoveSimNameLengthLimit: YukiBaseHooker() {
    override fun onHook() {
        loadApp(name = "com.android.phone") {
            val prefs = prefs("phone")
            val bridge = DexKitBridge.create(this.appInfo.sourceDir)
            if (prefs.getBoolean("remove_sim_name_length_limit")) {
                bridge.findClass {
                    searchPackages("com.android.simsettings.utils")
                    matcher {
                        usingStrings("save sim name keep = ","SIMS_SimNameHelper")
                    }
                }.singleOrNull()?.also {
                    it.name.toClass().resolve().apply {
                        firstMethod {
                            modifiers(Modifiers.PUBLIC, Modifiers.FINAL)
                            name = "filter"
                            parameters("java.lang.CharSequence", Int::class, Int::class, "android.text.Spanned", Int::class, Int::class)
                            returnType = "java.lang.CharSequence"
                        }.hook {
                            before {
                                result = null
                            }
                        }
                    }
                }

            }
        }
    }
}
