package io.github.suqi8.opatch.hook.launcher

import android.annotation.SuppressLint
import android.content.res.Resources
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
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ResourcesClass
import com.highcapable.yukihookapi.hook.type.java.FloatType
import com.highcapable.yukihookapi.hook.type.java.IntClass
import io.github.suqi8.opatch.R
import io.github.suqi8.opatch.hook.appilcations.StatusBarClock
import io.github.suqi8.opatch.getSetting
import io.github.suqi8.opatch.hook.appilcations.StatusLayout

class LauncherIcon: YukiBaseHooker() {
    @OptIn(LegacyHookApi::class)
    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onHook() {
        loadApp("com.android.launcher"){
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
            "com.android.launcher3.DeviceProfile".toClass().apply {
                method {
                    name = "updateIconSize"
                    paramCount = 2
                    param(FloatType, ResourcesClass)
                }.hook {
                    before {
                        // 访问目标类的字段并进行修改
                        val iconSizePxField = "com.android.launcher3.DeviceProfile".toClass().getDeclaredField("iconSizePx")
                        iconSizePxField.isAccessible = true // 确保可以访问私有字段

                        // 这里假设您要将新的图标大小设置为 px 值
                        val iconSizePxValue = 20
                        iconSizePxField.set("com.android.launcher3.DeviceProfile".toClass(), iconSizePxValue) // 更新 iconSizePx

                    }
                }
            }
        }
    }
}
