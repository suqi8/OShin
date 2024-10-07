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
                            @SuppressLint("SetTextI18n", "DefaultLocale")
                            override fun run() {
                                var props: Properties? = null
                                var fis: FileInputStream? = null
                                try {
                                    fis =
                                        FileInputStream("/sys/class/power_supply/battery/uevent")
                                    props = Properties()
                                    props.load(fis)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                } finally {
                                    fis?.close()
                                }
                                val currVal: String
                                var rawCurr = 0
                                try {
                                    if (props != null) {
                                        rawCurr = -1 * Math.round(
                                            props.getProperty("POWER_SUPPLY_CURRENT_NOW")
                                                .toFloat()
                                        )
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                val currNow = rawCurr
                                if (abs(rawCurr.toDouble()) > 999) {
                                    currVal = String.format("%.2f", rawCurr)
                                } else {
                                    currVal = "" + rawCurr
                                }

                                if (show == 0 ) {
                                    var voltVal = 0f
                                    val powerNow: String?
                                    try {
                                        powerNow =
                                            props!!.getProperty("POWER_SUPPLY_VOLTAGE_NOW")
                                    } catch (e: Exception) {
                                        throw RuntimeException(e)
                                    }
                                    if (powerNow != null) voltVal =
                                        powerNow.toInt() / 1000000f

                                    var wattVal = voltVal * currNow / 1000.00f
                                    val simpleWatt =
                                        String.format(Locale.getDefault(), "%.2f", wattVal)
                                    val batteryInfo =
                                        currVal + "mA" + "\n" + simpleWatt + "W" + voltVal + "V"
                                    newTextView.text = batteryInfo
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
