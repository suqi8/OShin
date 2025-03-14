package com.suqi8.oshin.hook.appilcations

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.java.UnitType
import java.io.File
import java.io.FileInputStream
import java.util.Locale
import java.util.Properties
import kotlin.math.abs

class StatusBarConsumption_indicator: YukiBaseHooker() {
    @SuppressLint("SetTextI18n")
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
        "com.android.systemui.statusbar.phone.fragment.CollapsedStatusBarFragment".toClass().apply {
            method {
                name = "onViewCreated"
                param("android.view.View", "android.os.Bundle")
                returnType = UnitType
            }.hook {
                after {
                    val statusBar = instance
                    val view = args[0] as View

                    // 获取时钟视图作为锚点
                    val clockView = statusBar.javaClass.field { name = "mClockView" }.get(statusBar)
                    //YLog.info("mClockView实际类型: ${clockView.javaClass.name ?: "null"}")
                    /*val parent = clockView.parent as ViewGroup
                    val newTextView = TextView(parent.context).apply {
                        text = "111"
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
                    parent.removeView(newTextView)*/
                }
            }
        }
        "com.android.systemui.statusbar.phone.PhoneStatusBarView".toClass().apply {
            method {
                name = "onFinishInflate"
                emptyParam()
                returnType = UnitType
            }.hook {
                after {
                    val statusBar = instance as View
                }
            }
        }
        "com.android.systemui.statusbar.policy.Clock".toClass().apply {
            method {
                name = "onAttachedToWindow"
                emptyParam()
                returnType = UnitType
            }.hook {
                after {
                    // 获取 Clock（时间显示的 TextView 控件）
                    val clockTextView = instance as TextView
                    if (clockTextView.javaClass.name != "com.oplus.systemui.statusbar.widget.StatClock") return@after
                    // 获取状态栏父布局
                    val parentViewGroup = clockTextView.parent as ViewGroup
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
                        syncWithClockStyle(clockTextView)
                        //setTypeface(typeface, if (bold_text) Typeface.BOLD else Typeface.NORMAL)
                    }

                    // 在 Clock 后面插入新控件
                    parentViewGroup.addView(newTextView,calculateInsertPosition(clockTextView))
                    clockTextView.post {
                        newTextView.x = clockTextView.x + clockTextView.width + 8 // 添加8个像素的间距
                    }
                    startColorSync(clockTextView, newTextView)
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
                    Temperature(clockTextView,parentViewGroup)
                }
            }
        }
    }
    fun Temperature(clockTextView: TextView, parentViewGroup: ViewGroup) {
        if (prefs("systemui\\hardware_indicator").getBoolean("temperature_indicator", false)) {
            val isdual_raw = prefs("systemui\\hardware_indicator").getBoolean(
                "temperature_indicator_dual_row",
                false
            )
            val show1 =
                prefs("systemui\\hardware_indicator").getInt("temperature_indicator_display_select1", 0)
            val show2 =
                prefs("systemui\\hardware_indicator").getInt("temperature_indicator_display_select2", 0)
            val font_size =
                prefs("systemui\\hardware_indicator").getFloat("temperature_indicator_font_size", 0f)
            val bold_text = prefs("systemui\\hardware_indicator").getBoolean(
                "temperature_indicator_bold_text",
                false
            )
            val update_time =
                prefs("systemui\\hardware_indicator").getInt("temperature_indicator_update_time", 0)
            val alignment =
                prefs("systemui\\hardware_indicator").getInt("temperature_indicator_alignment", 0)
            val cpu_temp_source =
                prefs("systemui\\hardware_indicator").getInt("temperature_indicator_cpu_temp_source", 0)
            val hideBatteryUnit = prefs("systemui\\hardware_indicator").getBoolean(
                "temperature_indicator_hideBatteryUnit",
                false
            )
            val hidecpuUnit = prefs("systemui\\hardware_indicator").getBoolean(
                "temperature_indicator_hideCpuUnit",
                false
            )
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
                syncWithClockStyle(clockTextView)
                //setTypeface(typeface, if (bold_text) Typeface.BOLD else Typeface.NORMAL)
            }

            // 在 Clock 后面插入新控件
            parentViewGroup.addView(newTextView,calculateInsertPosition(clockTextView))
            clockTextView.post {
                newTextView.x = clockTextView.x + clockTextView.width + 8 // 添加8个像素的间距
            }
            startColorSync(clockTextView, newTextView)
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
    // 计算插入位置的函数
    fun calculateInsertPosition(clock: View): Int {
        // 获取父容器中所有子View
        val children = (clock.parent as ViewGroup).children.toList()

        // 寻找通知图标区域的起始位置
        val notificationAreaIndex = children.indexOfFirst {
            it.javaClass.simpleName.contains("NotificationIconContainer")
        }
        YLog.info(notificationAreaIndex.toString()+(children.indexOf(clock) + 1).toString())

        // 如果找到通知区域，插入到时钟和通知区域之间
        return if (notificationAreaIndex > 0) {
            notificationAreaIndex
        } else {
            // 默认插入到时钟之后
            children.indexOf(clock) + 1
        }
    }
    // 启动颜色同步
    private fun startColorSync(clock: TextView, target: TextView) {
        val mainHandler = Handler(Looper.getMainLooper())
        var lastColor = clock.currentTextColor

        // 颜色同步检查任务
        val colorSyncTask = object : Runnable {
            override fun run() {
                runCatching {
                    val currentColor = clock.currentTextColor
                    if (currentColor != lastColor) {
                        lastColor = currentColor
                        mainHandler.post {
                            target.setTextColor(currentColor)
                            target.invalidate()
                        }
                    }
                }
                mainHandler.postDelayed(this, 500) // 每500ms检查一次
            }
        }

        // 启动同步
        mainHandler.post(colorSyncTask)

        // 监听系统主题变化
        registerConfigChangeReceiver(clock.context) {
            mainHandler.post {
                target.setTextColor(clock.currentTextColor)
            }
        }
    }
    // 同步文本样式
    private fun TextView.syncWithClockStyle(clock: TextView) {
        // 复制字体样式
        typeface = clock.typeface
        //textSize = clock.textSize
        gravity = clock.gravity

        // 初始颜色同步
        setTextColor(clock.currentTextColor)
    }

    // 注册配置变化接收器
    private fun registerConfigChangeReceiver(context: Context, callback: () -> Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                callback()
            }
        }
        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED))
    }
}
