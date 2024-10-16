package io.github.suqi8.opatch.hook

import android.annotation.SuppressLint
import android.content.res.Resources
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import com.github.kyuubiran.ezxhelper.finders.FieldFinder.`-Static`.fieldFinder
import com.github.kyuubiran.ezxhelper.params
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.core.annotation.LegacyHookApi
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.android.ResourcesClass
import com.highcapable.yukihookapi.hook.type.android.ViewClass
import com.highcapable.yukihookapi.hook.type.java.BooleanClass
import com.highcapable.yukihookapi.hook.type.java.FloatClass
import com.highcapable.yukihookapi.hook.type.java.FloatType
import com.highcapable.yukihookapi.hook.type.java.IntClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedHelpers
import io.github.suqi8.opatch.hook.StatusBar.StatusBarClock
import io.github.suqi8.opatch.hook.StatusBar.StatusBarIcon
import io.github.suqi8.opatch.hook.StatusBar.StatusBarhardware_indicator
import io.github.suqi8.opatch.hook.appilcations.getObjectFieldAs
import io.github.suqi8.opatch.hook.launcher.LauncherIcon

@InjectYukiHookWithXposed(entryClassName = "opatch", isUsingResourcesHook = true)
class HookEntry : IYukiHookXposedInit {

    override fun onInit() = configs {
        // Your code here.
    }

    @SuppressLint("RestrictedApi")
    override fun onHook() = encase {
        // Your code here.
        /*loadApp(name = "com.android.settings") {
            "com.oplus.settings.feature.deviceinfo.controller.OplusDeviceModelPreferenceController".toClass().apply {
                method{
                    name = "getStatusText"
                    emptyParam()
                    returnType = StringClass
                }.hook {
                    replaceTo("原神手机酸奶独家定制版")
                }
            }
        }*/
        loadApp(hooker = StatusBarClock())
        loadApp(hooker = StatusBarhardware_indicator())
        loadApp(hooker = LauncherIcon())
        loadApp(hooker = StatusBarIcon())
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
            "com.android.systemui.statusbar.phone.fragment.CollapsedStatusBarFragment".toClass().apply {
                method {
                    name = "onViewCreated"
                    param(ViewClass, BundleClass)
                }.hook {
                    after {
                        val view = args[0] as View
                        view.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }
}
