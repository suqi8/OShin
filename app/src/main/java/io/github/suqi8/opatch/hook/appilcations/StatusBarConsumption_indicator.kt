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
        val isdual_cell = prefs("settings").getBoolean("com_android_systemui_dual_cell", false)
        val hidePowerUnit = prefs("settings").getBoolean("com_android_systemui_hidePowerUnit", false)
        val hideCurrentUnit = prefs("settings").getBoolean("com_android_systemui_hideCurrentUnit", false)
        val hideVoltageUnit = prefs("settings").getBoolean("com_android_systemui_hideVoltageUnit", false)
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
                            @SuppressLint("SetTextI18n", "DefaultLocale")
                            override fun run() {
                                var props: Properties? = null
                                val fis: FileInputStream = FileInputStream("/sys/class/power_supply/battery/uevent")
                                props = Properties()
                                props.load(fis)
                                val BatteryCurrentmA = props.getProperty("POWER_SUPPLY_CURRENT_NOW").toFloat() * -1
                                val BatteryCurrent = String.format(Locale.getDefault(),"%.2f",props.getProperty("POWER_SUPPLY_CURRENT_NOW").toFloat() * -1 / 1000f).toFloat()
                                val BatteryVoltage = String.format(Locale.getDefault(),"%.2f",props.getProperty("POWER_SUPPLY_VOLTAGE_NOW").toFloat() / 1000000f).toFloat()
                                val BatteryWatt = if (isdual_cell) String.format(Locale.getDefault(), "%.2f", (BatteryCurrent * BatteryVoltage) * 2) else String.format(Locale.getDefault(), "%.2f", BatteryCurrent * BatteryVoltage)
                                var batteryInfo = ""
                                var PowerUnit = if (hidePowerUnit) "" else "W"
                                var CurrentUnit = if (hideCurrentUnit) "" else "mA"
                                var VoltageUnit = if (hideVoltageUnit) "" else "V"
                                var line1 = if (show1 == 0) {
                                    BatteryWatt + PowerUnit
                                } else if (show1 == 1) {
                                    BatteryCurrentmA.toString() + CurrentUnit
                                } else {
                                    BatteryVoltage.toString() + VoltageUnit
                                }
                                var line2 = if (show2 == 0) {
                                    BatteryWatt + PowerUnit
                                } else if (show2 == 1) {
                                    BatteryCurrentmA.toString() + CurrentUnit
                                } else {
                                    BatteryVoltage.toString() + VoltageUnit
                                }
                                if (isdual_raw) {
                                    batteryInfo = line1 + "\n" + line2
                                    //BatteryCurrentmA.toString() + "mA" + "\n" + BatteryWatt + "W" + BatteryVoltage + "V"
                                } else {
                                    batteryInfo = line1
                                }
                                newTextView.text = batteryInfo
                                handler.postDelayed(this, 1000) // 1秒后再次执行
                            }
                        }

                        // 启动定时更新
                        handler.post(runnable)
                    }
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
