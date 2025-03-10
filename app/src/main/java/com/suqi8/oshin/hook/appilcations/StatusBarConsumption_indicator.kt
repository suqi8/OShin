package com.suqi8.oshin.hook.appilcations

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.UnitType
import java.io.FileInputStream
import java.util.Locale
import java.util.Properties
import kotlin.math.abs

class StatusBarConsumption_indicator: YukiBaseHooker() {
    override fun onHook() {
        val isdual_raw = prefs("systemui\\hardware_indicator").getBoolean("power_consumption_indicator_dual_row", false)
        val show1 = prefs("systemui\\hardware_indicator").getInt("powerDisplaySelect1", 0)
        val show2 = prefs("systemui\\hardware_indicator").getInt("powerDisplaySelect2", 0)
        val isdual_cell = prefs("systemui\\hardware_indicator").getBoolean("power_consumption_indicator_dual_cell", false)
        val hidePowerUnit = prefs("systemui\\hardware_indicator").getBoolean("power_consumption_hidePowerUnit", false)
        val hideCurrentUnit = prefs("systemui\\hardware_indicator").getBoolean("power_consumption_hideCurrentUnit", false)
        val hideVoltageUnit = prefs("systemui\\hardware_indicator").getBoolean("power_consumption_hideVoltageUnit", false)
        val font_size = prefs("systemui\\hardware_indicator").getFloat("power_consumption_indicator_font_size", 0f)
        val absolute = prefs("systemui\\hardware_indicator").getBoolean("power_consumption_indicator_absolute", false)
        val bold_text = prefs("systemui\\hardware_indicator").getBoolean("power_consumption_indicator_bold_text", false)
        val power_consumption_indicator_update_time = prefs("systemui\\hardware_indicator").getInt("power_consumption_indicator_update_time", 0)
        val power_consumption_indicator_alignment = prefs("systemui\\hardware_indicator").getInt("power_consumption_indicator_alignment", 0)
        "com.android.systemui.statusbar.policy.Clock".toClass().apply {
            method {
                name = "onAttachedToWindow"
                emptyParam()
                returnType = UnitType
            }.hook {
                before {
                    // 获取 Clock（时间显示的 TextView 控件）
                    val clockTextView = instance as TextView

                    // 获取状态栏父布局
                    val parentViewGroup = clockTextView.parent as LinearLayout
                    // 创建一个新的 TextView 控件
                    val newTextView = TextView(clockTextView.context).apply {
                        text = ""
                        gravity = when (power_consumption_indicator_alignment) {
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
                        textSize = if (font_size == 0f) 8f else font_size
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
                            val BatteryWatt = if (isdual_cell) String.format(Locale.getDefault(), "%.2f", (BatteryCurrent * BatteryVoltage) * 2) else String.format(
                                Locale.getDefault(), "%.2f", BatteryCurrent * BatteryVoltage)
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
