package io.github.suqi8.opatch.hook.launcher

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.Shader
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.compose.ui.platform.LocalContext
import com.highcapable.yukihookapi.hook.core.annotation.LegacyHookApi
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ResourcesClass
import com.highcapable.yukihookapi.hook.type.java.FloatType
import io.github.suqi8.opatch.R
import io.github.suqi8.opatch.hook.appilcations.StatusBarClock
import io.github.suqi8.opatch.getSetting
import io.github.suqi8.opatch.hook.appilcations.StatusLayout

class LauncherIcon: YukiBaseHooker() {
    @OptIn(LegacyHookApi::class)
    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onHook() {
        loadApp("com.android.launcher"){
            //状态栏时钟
            if (prefs("settings").getFloat("com_android_launcher_icon_text", 1.00f) != 1.00f) {
                "com.android.launcher3.DeviceProfile".toClass().apply {
                    method {
                        name = "updateIconSize"
                        paramCount = 2
                        param(FloatType, ResourcesClass)

                    }.hook {
                        /*before {
                            val f = args[0] as Float
                            val scale = Math.min(2.0f,2.0f)
                            val field = "com.android.launcher3.DeviceProfile".toClass().getField("iconScale")
                            val iconScaleField = instance.javaClass.getDeclaredField("iconScale").apply {
                            isAccessible = true
                        }
                        iconScaleField.set(instance,scale)
                            field.get(instance).set(2.0f)*/
                        before {
                            args[0] = prefs("settings").getFloat("com_android_launcher_icon_text", 1.00f)
                        }
                    }
                }
            }
            "com.android.launcher.Launcher".toClass().apply {
                method {
                    name = "onCreate"
                }.hook {
                    after {
                        // 获取当前 Hotseat 的 View
                        val hotseatView = instance<ViewGroup>()

                        // 创建新的 TextView
                        val textView = TextView(hotseatView.context).apply {
                            text = "Hello Hotseat!" // 你希望显示的文字
                            textSize = 16f // 字体大小
                            setTextColor(Color.WHITE) // 文字颜色
                            gravity = Gravity.CENTER // 文字居中显示
                        }

                        // 创建 LayoutParams 设置 TextView 大小和位置
                        val layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            gravity = Gravity.CENTER // 你可以调整文字的显示位置
                        }

                        // 添加 TextView 到 Hotseat
                        hotseatView.addView(textView, layoutParams)
                    }
                }
            }
        }
    }
}
