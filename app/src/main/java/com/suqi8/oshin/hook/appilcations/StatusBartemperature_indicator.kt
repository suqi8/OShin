package com.suqi8.oshin.hook.appilcations

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.highcapable.yukihookapi.hook.core.annotation.LegacyHookApi
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import java.io.File
import java.util.Locale

class StatusBartemperature_indicator: YukiBaseHooker() {
    @SuppressLint("DiscouragedApi")
    @OptIn(LegacyHookApi::class)
    override fun onHook() {
        val isdual_raw = prefs("systemui\\hardware_indicator").getBoolean("temperature_indicator_dual_row", false)
        val show1 = prefs("systemui\\hardware_indicator").getInt("temperature_indicator_display_select1", 0)
        val show2 = prefs("systemui\\hardware_indicator").getInt("temperature_indicator_display_select2", 0)
        val font_size = prefs("systemui\\hardware_indicator").getFloat("temperature_indicator_font_size", 0f)
        val bold_text = prefs("systemui\\hardware_indicator").getBoolean("temperature_indicator_bold_text", false)
        val update_time = prefs("systemui\\hardware_indicator").getInt("temperature_indicator_update_time", 0)
        val alignment = prefs("systemui\\hardware_indicator").getInt("temperature_indicator_alignment", 0)
        val cpu_temp_source = prefs("systemui\\hardware_indicator").getInt("temperature_indicator_cpu_temp_source", 0)
        val hideBatteryUnit = prefs("systemui\\hardware_indicator").getBoolean("hideBatteryUnit", false)
        val hidecpuUnit = prefs("systemui\\hardware_indicator").getBoolean("hideCpuUnit", false)
        "com.android.systemui.statusbar.policy.Clock".toClass().apply {
            hook {
                injectMember {
                    method {
                        name = "onAttachedToWindow"
                    }

                    afterHook {
                        // 获取 Clock（时间显示的 TextView 控件）
                        val clockTextView = instance as TextView

                        // 获取状态栏父布局
                        val parentViewGroup = clockTextView.parent as ConstraintLayout
                        // 获取 Clock 的索引
                        val clockIndex = parentViewGroup.indexOfChild(clockTextView)
                        val clockTextColor = clockTextView.context.resources.getIdentifier("status_bar_clock_color", "color", "com.android.systemui") // 适用于 Android 6.0 及以上版本

                        // 创建一个新的 TextView 控件
                        val newTextView = TextView(clockTextView.context).apply {
                            text = ""
                            gravity = when (alignment) {
                                0 -> Gravity.CENTER        // 居中对齐
                                1 -> Gravity.TOP           // 顶部对齐
                                2 -> Gravity.BOTTOM        // 底部对齐
                                3 -> Gravity.START         // 起始位置对齐
                                4 -> Gravity.END           // 结束位置对齐
                                5 -> Gravity.CENTER_HORIZONTAL  // 水平居中
                                6 -> Gravity.CENTER_VERTICAL    // 垂直居中
                                7 -> Gravity.FILL               // 填满整个空间
                                8 -> Gravity.FILL_HORIZONTAL   // 水平填满
                                9 -> Gravity.FILL_VERTICAL     // 垂直填满
                                else -> Gravity.CENTER         // 默认居中对齐
                            }
                            textSize = if (font_size == 0f) 8f else font_size.toFloat()
                            isSingleLine = false
                            setTypeface(typeface, if (bold_text) Typeface.BOLD else Typeface.NORMAL)
                        }

                        // 在 Clock 后面插入新控件
                        parentViewGroup.addView(newTextView)
                        clockTextView.post {
                            newTextView.x = clockTextView.x + clockTextView.width + 8 // 添加8个像素的间距
                        }
                        val handler = Handler(Looper.getMainLooper())
                        val runnable = object : Runnable {
                            @SuppressLint("SetTextI18n", "DefaultLocale")
                            override fun run() {
                                var temperatureInfo = ""
                                var cpu = File("/sys/class/thermal/thermal_zone"+cpu_temp_source+"/temp").readText().trim().toInt() / 1000f
                                cpu = String.format(Locale.getDefault(),"%.2f",cpu).toFloat()
                                val battery = File("/sys/class/power_supply/battery/temp").readText().trim().toInt() / 10f
                                val BattrtyUnit = if (hideBatteryUnit) "" else "°C"
                                val cpuUnit = if (hidecpuUnit) "" else "°C"
                                val line1 = if (show1 == 0) {
                                    battery.toString() + BattrtyUnit
                                } else {
                                    cpu.toString() + cpuUnit
                                }
                                val line2 = if (show2 == 0) {
                                    battery.toString() + BattrtyUnit
                                } else {
                                    cpu.toString() + cpuUnit
                                }
                                if (isdual_raw) {
                                    temperatureInfo = line1 + "\n" + line2
                                } else {
                                    temperatureInfo = line1
                                }
                                newTextView.text = temperatureInfo
                                handler.postDelayed(this, if (update_time == 0) 1000 else update_time.toLong())
                            }
                        }

                        // 启动定时更新
                        handler.post(runnable)
                    }
                }
            }
        }
    }
}
