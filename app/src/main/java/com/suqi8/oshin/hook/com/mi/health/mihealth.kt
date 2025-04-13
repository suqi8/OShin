package com.suqi8.oshin.hook.com.mi.health

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.UnitType

class mihealth: YukiBaseHooker() {
    override fun onHook() {
        if (prefs("mihealth").getBoolean("enable_alarm_reminder", false)) {
            loadApp(name = "com.mi.health") {
                "com.xiaomi.fitness.devicesettings.base.clock.AlarmClockViewModel".toClass().apply {
                    method {
                        name = "hasClockInstalled"
                        param("android.content.pm.PackageManager")
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
                "com.xiaomi.fitness.common.utils.RomUtils".toClass().apply {
                    method {
                        name = "isXiaomi"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
                "com.xiaomi.fitness.common.utils.AppUtil".toClass().apply {
                    method {
                        name = "isPlayChannel"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
                "com.xiaomi.fitness.common.utils.RomUtils".toClass().apply {
                    method {
                        name = "isOppo"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = false
                        }
                    }
                }
                "com.xiaomi.fitness.common.utils.RomUtils".toClass().apply {
                    method {
                        name = "isMiui"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
                "com.xiaomi.fitness.common.utils.RomUtils".toClass().apply {
                    method {
                        name = "isMIUIRom"
                        param("android.content.Context")
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
                "com.xiaomi.fitness.common.utils.RomUtils".toClass().apply {
                    method {
                        name = "checkIsMiui"
                        param(BooleanType)
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
                "com.xiaomi.fitness.common.utils.RomUtils\$RomInfo".toClass().apply {
                    method {
                        name = "component5"
                        emptyParam()
                        returnType = "java.lang.String"
                    }.hook {
                        before {
                            result = "25010PN30C"
                        }
                    }
                }
                "com.xiaomi.fitness.common.utils.RomUtils\$RomInfo".toClass().apply {
                    method {
                        name = "getName"
                        emptyParam()
                        returnType = "java.lang.String"
                    }.hook {
                        before {
                            result = "xiaomi"
                        }
                    }
                }
                "com.xiaomi.fitness.common.utils.RomUtils\$RomInfo".toClass().apply {
                    method {
                        name = "setModel"
                        param("java.lang.String")
                        returnType = UnitType
                    }.hook {
                        before {
                            args[0] = "25010PN30C"
                        }
                    }
                }
            }
        }
    }
}
