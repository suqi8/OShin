package io.github.suqi8.opatch.hook.appilcations

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.view.ViewGroup
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import de.robv.android.xposed.XposedHelpers.getObjectField

class StatusLayout: YukiBaseHooker() {
    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        /*val collapsedStatusBarFragmentClass =
            loadClass("com.android.systemui.statusbar.phone.fragment.CollapsedStatusBarFragment")
        var statusBarLeft = 0
        var statusBarTop = 0
        var statusBarRight = 0
        var statusBarBottom = 0

        var statusBar: ViewGroup? = null

        fun updateLayout(context: Context, mode: Int) {
            val mConfiguration: Configuration = context.resources.configuration
            if (mConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT) { // 横屏
                statusBar?.setPadding(
                    statusBarLeft,
                    statusBarTop,
                    statusBarRight,
                    statusBarBottom
                )
            }
        }
        collapsedStatusBarFragmentClass.methodFinder()
            .filterByName("onViewCreated")
            .filterByParamCount(2)
            .single().createHook {
                after {
                    val PhoneStatusBarView = it.thisObject.getObjectFieldAs<ViewGroup>("mStatusBar")
                    val context: Context = PhoneStatusBarView.context
                    val res: Resources = PhoneStatusBarView.resources
                    val statusBarId: Int =
                        res.getIdentifier("status_bar", "id", "com.android.systemui")
                    statusBar = PhoneStatusBarView.findViewById(statusBarId)
                    if (statusBar == null) return@after

                }
            }*/
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> Any.getObjectFieldAs(field: String?) = getObjectField(this, field) as T
