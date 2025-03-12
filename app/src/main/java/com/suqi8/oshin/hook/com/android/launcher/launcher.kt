package com.suqi8.oshin.hook.com.android.launcher

import android.annotation.SuppressLint
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType

class launcher: YukiBaseHooker() {
    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onHook() {
        loadApp("com.android.launcher"){
            if (prefs("launcher").getBoolean("force_enable_fold_mode", false)) {
                if (prefs("launcher").getInt("fold_mode",0) == 0) {
                    "com.android.common.util.ScreenUtils".toClass().apply {
                        method {
                            name = "isFoldScreenExpanded"
                            emptyParam()
                            returnType = BooleanType
                        }.hook {
                            before {
                                result = true
                            }
                        }
                    }
                } else {
                    "com.android.common.util.ScreenUtils".toClass().apply {
                        method {
                            name = "isFoldScreenFolded"
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
}
