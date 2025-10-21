package com.suqi8.oshin.hook

import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.xposed.bridge.event.YukiXposedEvent
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import com.suqi8.oshin.hook.android.android
import com.suqi8.oshin.hook.appdetail.appdetail
import com.suqi8.oshin.hook.battery.battery
import com.suqi8.oshin.hook.browser.browser
import com.suqi8.oshin.hook.exsystemservice.exsystemservice
import com.suqi8.oshin.hook.games.games
import com.suqi8.oshin.hook.health.health
import com.suqi8.oshin.hook.incallui.incallui
import com.suqi8.oshin.hook.launcher.LauncherIcon
import com.suqi8.oshin.hook.launcher.launcher
import com.suqi8.oshin.hook.launcher.recent_task
import com.suqi8.oshin.hook.mihealth.mihealth
import com.suqi8.oshin.hook.mms.mms
import com.suqi8.oshin.hook.notificationmanager.NotificationManager
import com.suqi8.oshin.hook.ocrscanner.ocrscanner
import com.suqi8.oshin.hook.oshare.oshare
import com.suqi8.oshin.hook.ota.ota
import com.suqi8.oshin.hook.padconnect.padconnect
import com.suqi8.oshin.hook.phone.phone
import com.suqi8.oshin.hook.phonemanager.oplusphonemanager
import com.suqi8.oshin.hook.phonemanager.phonemanager
import com.suqi8.oshin.hook.quicksearchbox.quicksearchbox
import com.suqi8.oshin.hook.securepay.securepay
import com.suqi8.oshin.hook.settings.settings
import com.suqi8.oshin.hook.speechassist.speechassist
import com.suqi8.oshin.hook.systemui.systemui
import com.suqi8.oshin.hook.wallet.wallet
import com.suqi8.oshin.hook.weather.weather
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

    companion object {
        private const val RECENT_APP_VIEW_HOLDER = "com.oplus.smartsidebar.panelview.edgepanel.mainpanel.holder.RecentAppViewHolder"
        private const val COMBINED_IMAGE_VIEW = "com.oplus.smartsidebar.panelview.edgepanel.base.CombinedImageView"
        private const val IMAGE_DATA_HANDLER = "com.oplus.smartsidebar.panelview.edgepanel.data.viewdatahandlers.ImageDataHandleImpl"

        private const val TARGET_APP_COUNT = 4
        private const val EXTRA_VIEWS_KEY = "custom_recent_views"
    }

    override fun onHook() = encase {
        try { System.loadLibrary("dexkit") } catch (t: Throwable) {}
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
        loadApp(hooker = incallui())
        loadApp(hooker = NotificationManager())
        loadHooker(exsystemservice())
        loadHooker(phone())
        loadHooker(padconnect())
        loadHooker(weather())
        loadHooker(browser())
    }

    override fun onXposedEvent() {
        YukiXposedEvent.onHandleLoadPackage { lpparam: XC_LoadPackage.LoadPackageParam ->
            run {
            }
        }
        YukiXposedEvent.onInitZygote { startupParam: IXposedHookZygoteInit.StartupParam ->
            run {
            }
        }
    }
}
