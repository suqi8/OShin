package io.github.suqi8.opatch.hook.appilcations

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.widget.LinearLayout
import android.widget.TextView
import com.highcapable.yukihookapi.hook.core.annotation.LegacyHookApi
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.prefs
import java.io.File

class StatusBarConsumption_indicator: YukiBaseHooker() {
    @OptIn(LegacyHookApi::class)
    override fun onHook() {
        val show = prefs("settings").getInt("com_android_systemui_powerDisplaySelect", 0)
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
                        val parentViewGroup = clockTextView.parent as LinearLayout
                        // 获取 Clock 的索引
                        val clockIndex = parentViewGroup.indexOfChild(clockTextView)
                        val clockTextColor = clockTextView.context.resources.getIdentifier("status_bar_clock_color", "color", "com.android.systemui") // 适用于 Android 6.0 及以上版本

                        // 创建一个新的 TextView 控件
                        val newTextView = TextView(clockTextView.context).apply {
                            text = "a"
                            textSize = 8f
                            isSingleLine = false
                            setTextAppearance(clockTextColor)
                        }

                        // 在 Clock 后面插入新控件
                        parentViewGroup.addView(newTextView)
                        clockTextView.post {
                            newTextView.x = clockTextView.x + clockTextView.width + 8 // 添加8个像素的间距
                        }
                        val handler = Handler(Looper.getMainLooper())
                        val runnable = object : Runnable {
                            @SuppressLint("SetTextI18n")
                            override fun run() {
                                if (show == 0 ) {
                                    newTextView.text = """${calculatePower()}W
                                        |${getBatteryCurrent()}mA
                                    """.trimMargin()
                                } else if (show == 1) {
                                    newTextView.text = "${calculatePower()}W"
                                } else {
                                    newTextView.text = "${getBatteryCurrent()}mA"
                                }
                                handler.postDelayed(this, 1000) // 1秒后再次执行
                            }
                        }

                        // 启动定时更新
                        handler.post(runnable)
                    }
                    /*beforeHook {
                        val clockView = instance<View>() // 获取时间View
                        val parentLayout = clockView.parent as LinearLayout // 获取父布局

                        // 检查是否已经添加了自定义 TextView，避免重复添加
                        val customTextView = parentLayout.findViewWithTag<TextView>("customText")

                        if (customTextView == null) {
                            // 创建新的 TextView

                            // 获取通知图标的索引，假设通知图标在时间的后面
                            val clockIndex = parentLayout.indexOfChild(clockView)
                            var notificationIconIndex = -1

                            // 遍历子布局，找到通知图标的位置
                            for (i in clockIndex + 1 until parentLayout.childCount) {
                                val child = parentLayout.getChildAt(i)
                                if (child is ImageView) { // 假设通知图标是ImageView
                                    notificationIconIndex = i
                                    break
                                }
                            }
                            val newTextView = TextView(clockView.context).apply {
                                text = "新的文本"+notificationIconIndex // 自定义文本
                                textSize = 13f
                                tag = "customText" // 用标签标识
                                layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                ).apply {
                                    marginStart = 8 // 与时间的距离
                                }
                            }
                            // 如果找到通知图标，将TextView插入到时间后面、通知图标前面
                            parentLayout.addView(newTextView, notificationIconIndex)
                        }
                    }*/
                }
            }
        }
    }
    // 读取电流值（单位：微安，mA）
    fun getBatteryCurrent(): Long {
        val currentFile = File("/sys/class/power_supply/battery/current_now")
        return try {
            if (isCharging() == "Charging") {
                currentFile.readText().trim().toLong()
            } else {
                currentFile.readText().trim().toLong() * -1
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0L // 如果读取失败，返回 0
        }
    }

    //是否充电
    fun isCharging(): String {
        val chargingFile = File("/sys/class/power_supply/battery/status")
        return try {
            chargingFile.readText().trim()
        } catch (e: Exception) {
            e.printStackTrace()
            "Not charhing" // 如果读取失败，返回 false
        }
    }

    // 读取电压值（单位：微伏，mV）
    fun getBatteryVoltage(): Long {
        val voltageFile = File("/sys/class/power_supply/battery/voltage_now")
        return try {
            voltageFile.readText().trim().toLong()
        } catch (e: Exception) {
            e.printStackTrace()
            0L // 如果读取失败，返回 0
        }
    }

    // 计算功率
    @SuppressLint("DefaultLocale")
    fun calculatePower(): Double {
        val currentInAmps = getBatteryCurrent() / 1000.0
        val voltageInVolts = getBatteryVoltage() / 1000000.0 // 转换为伏特

        return String.format("%.2f", (currentInAmps * voltageInVolts)).toDouble() // 功率，单位：瓦特 (W)
    }
}
