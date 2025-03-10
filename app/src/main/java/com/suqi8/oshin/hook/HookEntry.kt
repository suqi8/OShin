package com.suqi8.oshin.hook

import android.annotation.SuppressLint
import android.os.Build
import android.os.Build.VERSION_CODES.VANILLA_ICE_CREAM
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.xposed.bridge.event.YukiXposedEvent
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import com.suqi8.oshin.hook.android.OplusRootCheck
import com.suqi8.oshin.hook.android.split_screen_multi_window
import com.suqi8.oshin.hook.com.android.settings.settings
import com.suqi8.oshin.hook.com.coloros.ocrscanner.ocrscanner
import com.suqi8.oshin.hook.com.finshell.wallet.wallet
import com.suqi8.oshin.hook.com.heytap.speechassist.ai_call
import com.suqi8.oshin.hook.com.oplus.battery.battery
import com.suqi8.oshin.hook.com.oplus.games.games
import com.suqi8.oshin.hook.launcher.LauncherIcon
import com.suqi8.oshin.hook.launcher.recent_task
import com.suqi8.oshin.hook.systemui.StatusBar.StatusBar
import com.suqi8.oshin.hook.systemui.StatusBar.StatusBarClock
import com.suqi8.oshin.hook.systemui.StatusBar.StatusBarIcon
import com.suqi8.oshin.hook.systemui.StatusBar.StatusBarhardware_indicator
import com.suqi8.oshin.hook.systemui.StatusBar.notification
import com.suqi8.oshin.hook.systemui.aod.allday_screenoff
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage


@InjectYukiHookWithXposed(entryClassName = "oshin", isUsingResourcesHook = true)
class HookEntry : IYukiHookXposedInit {

    override fun onInit() = configs {
        debugLog {
            tag = "OShin"
        }
        isDebug = false
    }

    @SuppressLint("RestrictedApi")
    override fun onHook() = encase {
        loadApp(hooker = OplusRootCheck())
        loadApp(hooker = StatusBarClock())
        loadApp(hooker = StatusBarhardware_indicator())
        loadApp(hooker = LauncherIcon())
        loadApp(hooker = StatusBarIcon())
        loadApp(hooker = recent_task())
        loadApp(hooker = notification())
        loadApp(hooker = battery())
        loadApp(name = "com.android.systemui") {
            /*"com.android.systemui.statusbar.phone.StatusBarIconController".toClass().apply {
                method {
                    name = "setIconVisibility"
                    param(StringClass, BooleanClass)
                }.hook {
                    before {
                        args[1] = true
                    }
                }
            }*/
        }
        loadApp(hooker = ai_call())
        /*loadApp(name = "com.android.settings") {
            resources().hook {
                injectResource {
                    conditions {
                        name = "device_ota_card_bg"
                        drawable()
                    }
                    replaceToModuleResource(R.drawable.aboutbackground)
                }
            }
        }*/
        loadApp(hooker = games())
        loadApp(hooker = ocrscanner())
        loadApp(hooker = StatusBar())
        loadApp(hooker = allday_screenoff())
        loadApp(hooker = split_screen_multi_window())
        loadApp(hooker = settings())
        loadApp(hooker = wallet())
    }

    override fun onXposedEvent() {
        YukiXposedEvent.onHandleLoadPackage { lpparam: XC_LoadPackage.LoadPackageParam ->
            run {
                if (lpparam.packageName == "android" && lpparam.processName == "android") {
                    if (Build.VERSION.SDK_INT == VANILLA_ICE_CREAM) {
                        com.suqi8.oshin.hook.android.corepatch.CorePatchForV()
                            .handleLoadPackage(lpparam)
                    }
                }
            }
        }
        YukiXposedEvent.onInitZygote { startupParam: IXposedHookZygoteInit.StartupParam ->
            run {
            }
        }
    }
}
