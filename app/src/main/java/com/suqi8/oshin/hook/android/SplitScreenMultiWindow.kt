package com.suqi8.oshin.hook.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType

class SplitScreenMultiWindow : YukiBaseHooker() {
    override fun onHook() {
        loadSystem {
            if (prefs("android\\split_screen_multi_window").getBoolean("remove_all_small_window_restrictions", false)) {
                "com.android.server.wm.FlexibleWindowUtils".toClass().apply {
                    method {
                        name = "isUnSupportCallerFlexibleWindow"
                        param("java.lang.String")
                        returnType = BooleanType
                    }.hook {
                        after {
                            result = true
                        }
                    }
                }
                "com.android.server.wm.FlexibleWindowUtils".toClass().apply {
                    method {
                        name = "isSupportFlexibleWindow"
                        param("java.lang.String", "java.lang.String")
                        returnType = BooleanType
                    }.hook {
                        after {
                            result = true
                        }
                    }
                }
                "com.android.server.wm.FlexibleWindowUtils".toClass().apply {
                    method {
                        name = "isInFlexibleWindowBlackList"
                        param("java.lang.String", "java.lang.String")
                        returnType = BooleanType
                    }.hook {
                        after {
                            result = false
                        }
                    }
                }
                "com.android.server.wm.FlexibleWindowUtils".toClass().apply {
                    method {
                        name = "isInMultiWindowFlexibleBlackList"
                        param("java.lang.String")
                        returnType = BooleanType
                    }.hook {
                        after {
                            result = false
                        }
                    }
                }
                "com.android.server.wm.FlexibleWindowUtils".toClass().apply {
                    method {
                        name = "isSupportFlexibleWindow"
                        param("android.content.Intent", "android.content.pm.ActivityInfo")
                        returnType = BooleanType
                    }.hook {
                        after {
                            result = true
                        }
                    }
                }
                "com.android.server.wm.FlexibleWindowUtils".toClass().apply {
                    method {
                        name = "isSupportFlexibleWindow"
                        param("com.android.server.wm.Task")
                        returnType = BooleanType
                    }.hook {
                        after {
                            result = true
                        }
                    }
                }
                "com.android.server.wm.FlexibleWindowUtils".toClass().apply {
                    method {
                        name = "isFlexibleTaskInPSBlackList"
                        param("android.content.Intent", "android.content.pm.ActivityInfo")
                        returnType = BooleanType
                    }.hook {
                        after {
                            result = false
                        }
                    }
                }
                "com.android.server.wm.FlexibleWindowUtils".toClass().apply {
                    method {
                        name = "getUnSupportRatiosInFlexibleTask"
                        param("java.lang.String")
                        returnType = "java.lang.String"
                    }.hook {
                        after {
                            result = false
                        }
                    }
                }
            }
            if (prefs("android\\split_screen_multi_window").getBoolean("force_multi_window_mode", false)) {
                "com.android.server.wm.FlexibleWindowUtils".toClass().apply {
                    method {
                        name = "isSupportMultiMode"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        after {
                            result = true
                        }
                    }
                }
            }
            if (prefs("android\\split_screen_multi_window").getInt("max_simultaneous_small_windows", -1) != -1) {
                "com.android.server.wm.FlexibleWindowManagerService".toClass().apply {
                    method {
                        name = "getMaxWinNum"
                        param(IntType)
                        returnType = IntType
                    }.hook {
                        after {
                            result = prefs("android\\split_screen_multi_window").getInt("max_simultaneous_small_windows", -1)
                        }
                    }
                }
            }
            if (prefs("android\\split_screen_multi_window").getInt("small_window_corner_radius", -1) != -1) {
                "com.android.server.wm.FlexibleWindowManagerService".toClass().apply {
                    method {
                        name = "getCornerRadius"
                        param(IntType)
                        returnType = IntType
                    }.hook {
                        after {
                            result = prefs("android\\split_screen_multi_window").getInt("small_window_corner_radius", -1)
                        }
                    }
                }
            }
            if (prefs("android\\split_screen_multi_window").getInt("small_window_focused_shadow", -1) != -1) {
                "com.android.server.wm.FlexibleWindowManagerService".toClass().apply {
                    method {
                        name = "getShadowRadiusFocused"
                        param(IntType)
                        returnType = IntType
                    }.hook {
                        after {
                            result = prefs("android\\split_screen_multi_window").getInt("small_window_focused_shadow", -1)
                        }
                    }
                }
            }
            if (prefs("android\\split_screen_multi_window").getInt("small_window_unfocused_shadow", -1) != -1) {
                "com.android.server.wm.FlexibleWindowManagerService".toClass().apply {
                    method {
                        name = "getShadowRadiusUnfocused"
                        param(IntType)
                        returnType = IntType
                    }.hook {
                        after {
                            result = prefs("android\\split_screen_multi_window").getInt("small_window_unfocused_shadow", -1)
                        }
                    }
                }
            }
        }
    }
}
