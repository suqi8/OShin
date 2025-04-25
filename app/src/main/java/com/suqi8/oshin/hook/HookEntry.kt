package com.suqi8.oshin.hook

import android.os.Build
import android.os.Build.VERSION_CODES.VANILLA_ICE_CREAM
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.xposed.bridge.event.YukiXposedEvent
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import com.suqi8.oshin.hook.android.android
import com.suqi8.oshin.hook.android.corepatch.CorePatchForV
import com.suqi8.oshin.hook.com.android.launcher.LauncherIcon
import com.suqi8.oshin.hook.com.android.launcher.launcher
import com.suqi8.oshin.hook.com.android.launcher.recent_task
import com.suqi8.oshin.hook.com.android.mms.mms
import com.suqi8.oshin.hook.com.android.settings.settings
import com.suqi8.oshin.hook.com.android.systemui.systemui
import com.suqi8.oshin.hook.com.coloros.ocrscanner.ocrscanner
import com.suqi8.oshin.hook.com.coloros.oshare.oshare
import com.suqi8.oshin.hook.com.coloros.phonemanager.phonemanager
import com.suqi8.oshin.hook.com.coloros.securepay.securepay
import com.suqi8.oshin.hook.com.finshell.wallet.wallet
import com.suqi8.oshin.hook.com.heytap.health.health
import com.suqi8.oshin.hook.com.heytap.quicksearchbox.quicksearchbox
import com.suqi8.oshin.hook.com.heytap.speechassist.speechassist
import com.suqi8.oshin.hook.com.mi.health.mihealth
import com.suqi8.oshin.hook.com.oplus.appdetail.appdetail
import com.suqi8.oshin.hook.com.oplus.battery.battery
import com.suqi8.oshin.hook.com.oplus.games.games
import com.suqi8.oshin.hook.com.oplus.ota.ota
import com.suqi8.oshin.hook.com.oplus.phonemanager.oplusphonemanager
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

    override fun onHook() = encase {
        System.loadLibrary("dexkit")
        loadApp(hooker = android())
        loadApp(hooker = systemui())
        loadApp(hooker = LauncherIcon())
        loadApp(hooker = recent_task())
        loadApp(hooker = battery())
        loadApp(hooker = speechassist())
        loadApp(hooker = games())
        loadApp(hooker = ocrscanner())
        loadApp(hooker = settings())
        loadApp(hooker = wallet())
        loadApp(hooker = launcher())
        loadApp(hooker = phonemanager())
        loadApp(hooker = oplusphonemanager())
        loadApp(hooker = mms())
        loadApp(hooker = securepay())
        loadApp(hooker = health())
        loadApp(hooker = appdetail())
        loadApp(hooker = quicksearchbox())
        loadApp(hooker = mihealth())
        loadApp(hooker = ota())
        loadApp(hooker = oshare())
    }

    override fun onXposedEvent() {
        YukiXposedEvent.onHandleLoadPackage { lpparam: XC_LoadPackage.LoadPackageParam ->
            run {
                if (lpparam.packageName == "android" && lpparam.processName == "android") {
                    if (Build.VERSION.SDK_INT == VANILLA_ICE_CREAM) {
                        CorePatchForV()
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
