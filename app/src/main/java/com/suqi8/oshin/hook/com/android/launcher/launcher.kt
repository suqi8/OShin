package com.suqi8.oshin.hook.com.android.launcher

import android.annotation.SuppressLint
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.FloatType
import com.highcapable.yukihookapi.hook.type.java.UnitType

class launcher: YukiBaseHooker() {
    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onHook() {
        loadApp("com.android.launcher"){
            /*"com.coui.appcompat.uiutil.UIUtil".toClass().apply {
                method {
                    name = "b"
                    param("com.coui.appcompat.uiutil.AnimLevel")
                    returnType = BooleanType
                }.hook {
                    before {
                        result = false
                    }
                }
            }*/
            val set_anim_level = prefs("launcher").getInt("set_anim_level", -1)
            if (set_anim_level != -1) {
                "com.android.common.util.PlatformLevelUtils\$animationLevelOS14\$2".toClass().apply {
                    method {
                        name = "invoke"
                        emptyParam()
                        returnType = "java.lang.Integer"
                    }.hook {
                        before {
                            result = set_anim_level
                        }
                    }
                }
                "com.android.common.util.PlatformLevelUtils\$animationLevelOS14\$2".toClass().apply {
                    method {
                        name = "invoke"
                        emptyParam()
                        returnType = "java.lang.Integer"
                    }.hook {
                        before {
                            result = set_anim_level
                        }
                    }
                }
            }
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
            if (prefs("launcher").getFloat("dock_transparency", 1f) != 1f) {
                "com.android.launcher3.OplusHotseat".toClass().apply {
                    method {
                        name = "setBackgroundAlpha"
                        param(FloatType)
                        returnType = UnitType
                    }.hook {
                        before {
                            args[0] = prefs("launcher").getFloat("dock_transparency", 1f)
                        }
                    }
                }
            }
            if (prefs("launcher").getBoolean("force_enable_dock_blur", false)) {
                "com.android.launcher3.uioverrides.states.blurdrawable.OplusBlurProperties".toClass().apply {
                    method {
                        name = "isSupportNewBlur"
                        param("android.content.Context", BooleanType)
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
                "com.android.common.util.ScreenUtils".toClass().apply {
                    method {
                        name = "hasLargeDisplayFeatures"
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
