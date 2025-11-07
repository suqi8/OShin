package com.suqi8.oshin.hook.themestore

import android.content.Intent
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.kavaref.extension.JDouble
import com.highcapable.kavaref.extension.JInteger
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.luckypray.dexkit.DexKitBridge
import java.lang.reflect.Modifier

class themestore : YukiBaseHooker() {
    override fun onHook() {
        val prefs = prefs("themestore")
        loadApp(name = "com.heytap.themestore") {
            val bridge = DexKitBridge.create(this.appInfo.sourceDir)

            //VIP
            if (prefs.getBoolean("unlock_themestore_vip_features", false)) {

                "com.oppo.cdo.card.theme.dto.page.WeatherPageResponseDto".toClass().resolve()
                    .apply {
                        firstMethod {
                            modifiers(Modifiers.PUBLIC)
                            name = "getVipStatus"
                            emptyParameters()
                            returnType = Int::class
                        }.hook {
                            before {
                                firstField { name = "vipStatus" }.of(instance).set(1)
                            }
                        }
                    }

                "com.oppo.cdo.card.theme.dto.vip.VipUserDto".toClass().resolve().apply {
                    firstMethod {
                        modifiers(Modifiers.PUBLIC)
                        name = "getVipStatus"
                        emptyParameters()
                        returnType = Int::class
                    }.hook {
                        before {
                            firstField { name = "vipStatus" }.of(instance).set(1)
                            firstField { name = "vipDays" }.of(instance).set(99999)
                            firstField { name = "endTime" }.of(instance).set(999999999L)
                        }
                    }
                }

                "com.oppo.cdo.card.theme.dto.vip.VipUserDto".toClass().resolve().apply {
                    firstMethod {
                        modifiers(Modifiers.PUBLIC)
                        name = "getVipDays"
                        emptyParameters()
                        returnType = Int::class
                    }.hook {
                        before {
                            firstField { name = "vipStatus" }.of(instance).set(1)
                            firstField { name = "vipDays" }.of(instance).set(99999)
                            firstField { name = "endTime" }.of(instance).set(999999999L)
                        }
                    }
                }

                "com.oppo.cdo.theme.domain.dto.response.ResourceItemDto".toClass().resolve().apply {
                    firstMethod {
                        modifiers(Modifiers.PUBLIC)
                        name = "getIsVip"
                        emptyParameters()
                        returnType = JInteger::class
                    }.hook {
                        before {
                            firstField { name = "isVip" }.of(instance).set(1)
                            firstField { name = "isVipAvailable" }.of(instance).set(1)
                        }
                    }
                }

                "com.oppo.cdo.theme.domain.dto.response.ResourceItemDto".toClass().resolve().apply {
                    firstMethod {
                        modifiers(Modifiers.PUBLIC)
                        name = "getIsVipAvailable"
                        emptyParameters()
                        returnType = JInteger::class
                    }.hook {
                        before {
                            firstField { name = "isVip" }.of(instance).set(1)
                            firstField { name = "isVipAvailable" }.of(instance).set(1)
                        }
                    }
                }

                "com.nearme.themespace.UserInfoManager".toClass().resolve().apply {
                    firstMethod {
                        modifiers(Modifiers.PUBLIC)
                        name = "w"
                        emptyParameters()
                        returnType = Int::class
                    }.hook {
                        after {
                            result = 1
                        }
                    }
                }

                "com.nearme.themespace.UserInfoManager".toClass().resolve().apply {
                    firstMethod {
                        modifiers(Modifiers.PRIVATE)
                        name = "D"
                        emptyParameters()
                        returnType = "com.nearme.themespace.account.VipUserStatus"
                    }.hook {
                        after {
                            val status =
                                "com.nearme.themespace.account.VipUserStatus".toClass().resolve()
                                    .firstField { name = "VALID" }.of(result).get()
                            result = status
                        }
                    }
                }

                "com.nearme.themespace.download.mvvm.DownloadRepository".toClass().resolve().apply {
                    firstMethod {
                        modifiers(Modifiers.PUBLIC)
                        name = "c"
                        parameters("com.nearme.themespace.model.LocalProductInfo")
                        returnType = "com.oplus.aiunit.vision.jv2"
                    }.hook {
                        before {
                            args[0]?.apply {
                                firstField { name = "mPurchaseStatus" }.set(1)
                                firstField { name = "mResourceVipType" }.set(0)
                                firstField { name = "forceVip" }.set(0)
                            }
                        }
                    }
                }

                "com.nearme.themespace.trial.ThemeTrialExpireReceiver".toClass().resolve().apply {
                    firstMethod {
                        modifiers(Modifiers.PRIVATE)
                        name = "a"
                        parameters("android.content.Context", String::class)
                        returnType = Boolean::class
                    }.hook {
                        before {
                            args[0] = null
                        }
                    }
                }

                "com.nearme.themespace.trial.ThemeTrialExpireReceiver".toClass().resolve().apply {
                    firstMethod {
                        modifiers(Modifiers.PUBLIC)
                        name = "onReceive"
                        parameters("android.content.Context", "android.content.Intent")
                        returnType = Void.TYPE
                    }.hook {
                        before {
                            val intent = args[1] as Intent
                            intent.action = ""
                        }
                    }
                }

                bridge.findMethod {
                    matcher {
                        modifiers = Modifier.PUBLIC
                        returnType = JDouble.TYPE.name
                        name = "getPrice"
                        paramTypes = emptyList()
                    }
                }.forEach { methodData ->
                    methodData.declaredClass?.name?.toClass()?.let {
                        it.resolve().firstMethod {
                            modifiers(Modifiers.PUBLIC)
                            name = methodData.name
                            emptyParameters()
                            returnType = JDouble.TYPE
                        }.hook {
                            after {
                                result = 0.0
                            }
                        }
                    }
                }
            }

            //去广告
            if (prefs.getBoolean("remove_themestore_splash_ads", false)) {
                println("testMe themestore splash ad hook")
                "com.oppo.cdo.card.theme.dto.SplashDto".toClass().resolve().apply {
                    firstMethod {
                        modifiers(Modifiers.PUBLIC)
                        name = "getAdData"
                        emptyParameters()
                        returnType = "com.oppo.cdo.card.theme.dto.AdDataDto"
                    }.hook {
                        after {
                            result = null
                        }
                    }
                }

                "com.oppo.cdo.card.theme.dto.SplashDto".toClass().resolve().apply {
                    firstMethod {
                        modifiers(Modifiers.PUBLIC)
                        name = "getImage"
                        emptyParameters()
                        returnType = String::class
                    }.hook {
                        after {
                            result = null
                        }
                    }
                }

                "com.oppo.cdo.card.theme.dto.SplashDto".toClass().resolve().apply {
                    firstMethod {
                        modifiers(Modifiers.PUBLIC)
                        name = "getStartTime"
                        emptyParameters()
                        returnType = Long::class
                    }.hook {
                        after {
                            result = System.currentTimeMillis() + 86400000
                        }
                    }
                }

                "com.oppo.cdo.card.theme.dto.SplashDto".toClass().resolve().apply {
                    firstMethod {
                        modifiers(Modifiers.PUBLIC)
                        name = "getEndTime"
                        emptyParameters()
                        returnType = Long::class
                    }.hook {
                        after {
                            result = System.currentTimeMillis() - 86400000
                        }
                    }
                }
            }

            //去更新
            if (prefs.getBoolean("remove_themestore_upgrade", false)) {
                println("testMe remove_themestore_upgrade")
                "com.heytap.upgrade.UpgradeSDK".toClass().resolve().apply {
                    firstMethod {
                        modifiers(Modifiers.PUBLIC)
                        name = "checkUpgrade"
                        parameters("com.oplus.aiunit.vision.qv0")
                        returnType = Void.TYPE
                    }.hook{
                        replaceUnit {  }
                    }
                }
            }


        }
    }
}
