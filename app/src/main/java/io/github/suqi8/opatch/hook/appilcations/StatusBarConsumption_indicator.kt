package io.github.suqi8.opatch.hook.appilcations

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.widget.LinearLayout
import android.widget.TextView
import com.highcapable.yukihookapi.hook.core.annotation.LegacyHookApi
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.prefs
import java.io.File
import java.io.FileInputStream
import java.util.Locale
import java.util.Properties
import kotlin.math.abs

class StatusBarConsumption_indicator: YukiBaseHooker() {
    @OptIn(LegacyHookApi::class)
    override fun onHook() {
        val isdual_raw = prefs("settings").getBoolean("com_android_systemui_power_consumption_indicator_dual_row", false)
        val show1 = prefs("settings").getInt("com_android_systemui_powerDisplaySelect1", 0)
        val show2 = prefs("settings").getInt("com_android_systemui_powerDisplaySelect2", 0)
        val isdual_cell = prefs("settings").getBoolean("com_android_systemui_power_consumption_indicator_dual_cell", false)
        val hidePowerUnit = prefs("settings").getBoolean("com_android_systemui_hidePowerUnit", false)
        val hideCurrentUnit = prefs("settings").getBoolean("com_android_systemui_hideCurrentUnit", false)
        val hideVoltageUnit = prefs("settings").getBoolean("com_android_systemui_hideVoltageUnit", false)
        val power_consumption_indicator_font_size = prefs("settings").getInt("com_android_systemui_power_consumption_indicator_font_size", 0)
        val absolute = prefs("settings").getBoolean("com_android_systemui_power_consumption_indicator_absolute", false)
        val bold_text = prefs("settings").getBoolean("com_android_systemui_power_consumption_indicator_bold_text", false)
        val power_consumption_indicator_update_time = prefs("settings").getInt("com_android_systemui_power_consumption_indicator_update_time", 0)
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
                            text = ""
                            textSize = if (power_consumption_indicator_font_size == 0) 8f else power_consumption_indicator_font_size.toFloat()
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
                                var props: Properties? = null
                                val fis: FileInputStream = FileInputStream("/sys/class/power_supply/battery/uevent")
                                props = Properties()
                                props.load(fis)
                                var BatteryCurrentmA = props.getProperty("POWER_SUPPLY_CURRENT_NOW").toFloat() * -1
                                var BatteryCurrent = String.format(Locale.getDefault(),"%.2f",props.getProperty("POWER_SUPPLY_CURRENT_NOW").toFloat() * -1 / 1000f).toFloat()
                                if (absolute) {
                                    BatteryCurrentmA = abs(BatteryCurrentmA)
                                    BatteryCurrent = abs(BatteryCurrent)
                                }
                                val BatteryVoltage = String.format(Locale.getDefault(),"%.2f",props.getProperty("POWER_SUPPLY_VOLTAGE_NOW").toFloat() / 1000000f).toFloat()
                                val BatteryWatt = if (isdual_cell) String.format(Locale.getDefault(), "%.2f", (BatteryCurrent * BatteryVoltage) * 2) else String.format(Locale.getDefault(), "%.2f", BatteryCurrent * BatteryVoltage)
                                var batteryInfo = ""
                                val PowerUnit = if (hidePowerUnit) "" else "W"
                                val CurrentUnit = if (hideCurrentUnit) "" else "mA"
                                val VoltageUnit = if (hideVoltageUnit) "" else "V"
                                val line1 = if (show1 == 0) {
                                    BatteryWatt + PowerUnit
                                } else if (show1 == 1) {
                                    BatteryCurrentmA.toString() + CurrentUnit
                                } else {
                                    BatteryVoltage.toString() + VoltageUnit
                                }
                                val line2 = if (show2 == 0) {
                                    BatteryWatt + PowerUnit
                                } else if (show2 == 1) {
                                    BatteryCurrentmA.toString() + CurrentUnit
                                } else {
                                    BatteryVoltage.toString() + VoltageUnit
                                }
                                if (isdual_raw) {
                                    batteryInfo = line1 + "\n" + line2
                                } else {
                                    batteryInfo = line1
                                }
                                newTextView.text = batteryInfo
                                handler.postDelayed(this, if (power_consumption_indicator_update_time == 0) 1000 else power_consumption_indicator_update_time.toLong())
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
