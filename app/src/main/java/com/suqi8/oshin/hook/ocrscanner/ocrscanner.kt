package com.suqi8.oshin.hook.ocrscanner

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType

class ocrscanner: YukiBaseHooker() {
    override fun onHook() {
        if (prefs("ocrscanner").getBoolean("full_screen_translation", false)) {
            loadApp(name = "com.coloros.ocrscanner") {
                "com.oplus.scanner.screentrans.ui.ScreenTranslationRootView".toClass().apply {
                    method {
                        name = "s0"
                        param("com.oplus.scanner.screentrans.business.k")
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = false
                        }
                    }
                }
                "com.oplus.scanner.screentrans.ui.ScreenTranslationRootView\$onNotSupportApp$1".toClass().apply {
                    method {
                        name = "invokeSuspend"
                        param("java.lang.Object")
                        returnType = "java.lang.Object"
                    }.hook {
                        before {
                            result = false
                        }
                    }
                }
                "com.oplus.scanner.screentrans.ui.ScreenTranslationToolCapsule".toClass().apply {
                    method {
                        name = "O0"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
        }
    }
}
