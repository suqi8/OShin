package com.suqi8.oshin.hook.com.android.systemui

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

class ToastForceShowAppIcon: YukiBaseHooker() {
    override fun onHook() {
        loadApp(name = "com.android.systemui") {
            if (prefs("systemui").getBoolean("toast_force_show_app_icon", false)) {
                "com.oplus.systemui.toast.OplusSystemUIToast".toClass().resolve().apply {
                    firstConstructor {
                        modifiers(Modifiers.PUBLIC)
                        parameters("android.content.Context", "java.lang.CharSequence", "com.android.systemui.plugins.ToastPlugin\$Toast", String::class, Int::class, Int::class, "com.oplus.systemui.common.helper.ResponsiveUIModelHelper")
                    }.hook {
                        after {
                            val mIconView =firstField {
                                modifiers(Modifiers.PUBLIC)
                                name = "mIconView"
                                type = "android.widget.ImageView"
                            }.of(instance).get() as ImageView
                            val context = args[0] as Context
                            val pkgName = args[3] as String
                            val appIcon = context.packageManager.getApplicationIcon(pkgName)
                            mIconView.setImageDrawable(appIcon)
                            mIconView.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }
}
