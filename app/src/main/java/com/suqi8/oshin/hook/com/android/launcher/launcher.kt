package com.suqi8.oshin.hook.com.android.launcher

import android.annotation.SuppressLint
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.UnitType

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
                "com.android.common.util.ScreenUtils".toClass().apply {
                    method {
                        name = "isSupportDockerExpandScreen"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            if (prefs("launcher").getBoolean("force_enable_fold_dock", false)) {
                "com.android.launcher3.OplusHotseat".toClass().apply {
                    method {
                        name = "init"
                        param("android.content.Context")
                        returnType = UnitType
                    }.hook {
                        before {
                            method { name = "setDockerBackground" }.get(instance).call()
                        }
                    }
                }
            }
        }
    }
}
