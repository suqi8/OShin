package com.suqi8.oshin.hook.com.android.systemui.StatusBar

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.java.UnitType
import java.io.File
import java.io.FileInputStream
import java.util.Properties
import kotlin.math.abs

class Hardware_indicator: YukiBaseHooker() {
    @SuppressLint("SetTextI18n")
    override fun onHook() {
        loadApp("com.android.systemui"){
            "com.android.systemui.statusbar.policy.Clock".toClass().apply {
                method {
                    name = "onAttachedToWindow"
                    emptyParam()
                    returnType = UnitType
                }.hook {
                    after {
                        val clockTextView = instance as? TextView ?: return@after
                        if (clockTextView.javaClass.name != "com.oplus.systemui.statusbar.widget.StatClock") return@after
                        val parentViewGroup = clockTextView.parent as? LinearLayout ?: return@after

                        createConsumptionIndicator(clockTextView, parentViewGroup)
                        createTemperatureIndicator(clockTextView, parentViewGroup)
                    }
                }
            }
        }
    }

    // 公共函数：创建指标 TextView（初始样式仅用于创建，后续均实时更新）
    private fun createIndicatorTextView(clockTextView: TextView): TextView =
        TextView(clockTextView.context).apply {
            text = ""
            isSingleLine = false
            syncWithClockStyle(clockTextView)
        }

    // 公共函数：添加控件，设置垂直居中，并启动颜色同步
    private fun addIndicatorView(clockTextView: TextView, parent: ViewGroup, indicator: TextView) {
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER_VERTICAL
        }
        parent.addView(indicator, calculateInsertPosition(clockTextView), layoutParams)
        clockTextView.post { indicator.x = clockTextView.x + clockTextView.width + 8 }
        startColorSync(clockTextView, indicator)
    }

    // 公共函数：启动周期更新，每次更新返回下次延迟（单位毫秒）
    private fun startPeriodicUpdate(updateAction: () -> Long) {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val delay = updateAction()
                handler.postDelayed(this, delay)
            }
        }
        handler.post(runnable)
    }

    // 公共函数：读取电池属性文件
    private fun readBatteryProperties(): Properties =
        Properties().apply { FileInputStream("/sys/class/power_supply/battery/uevent").use { load(it) } }

    // 创建电量消耗指标，返回创建的 indicator
    private fun createConsumptionIndicator(clockTextView: TextView, parent: ViewGroup): TextView {
        val localPrefs = prefs("systemui\\hardware_indicator")
        if (!localPrefs.getBoolean("power_consumption_indicator", false))
            return TextView(clockTextView.context)
        val indicator = createIndicatorTextView(clockTextView)
        addIndicatorView(clockTextView, parent, indicator)

        startPeriodicUpdate {
            val fontSize = localPrefs.getFloat("power_consumption_indicator_font_size", 8f)
            val boldText = localPrefs.getBoolean("power_consumption_indicator_bold_text", false)
            val alignment = localPrefs.getInt("power_consumption_indicator_alignment", Gravity.CENTER)
            indicator.gravity = alignment
            indicator.textSize = fontSize
            indicator.setTypeface(indicator.typeface, if (boldText) Typeface.BOLD else Typeface.NORMAL)

            val isDualRow = localPrefs.getBoolean("power_consumption_indicator_dual_row", false)
            val show1 = localPrefs.getInt("powerDisplaySelect1", 0)
            val show2 = localPrefs.getInt("powerDisplaySelect2", 0)
            val isDualCell = localPrefs.getBoolean("power_consumption_indicator_dual_cell", false)
            val hidePowerUnit = localPrefs.getBoolean("power_consumption_hidePowerUnit", false)
            val hideCurrentUnit = localPrefs.getBoolean("power_consumption_hideCurrentUnit", false)
            val hideVoltageUnit = localPrefs.getBoolean("power_consumption_hideVoltageUnit", false)
            val absolute = localPrefs.getBoolean("power_consumption_indicator_absolute", false)
            val updateTime = localPrefs.getInt("power_consumption_indicator_update_time", 1000).toLong()

            val props = readBatteryProperties()
            var batteryCurrentMa = props.getProperty("POWER_SUPPLY_CURRENT_NOW").toFloat() * -1
            var batteryCurrent = String.format("%.2f", batteryCurrentMa / 1000f).toFloat()
            if (absolute) {
                batteryCurrentMa = abs(batteryCurrentMa)
                batteryCurrent = abs(batteryCurrent)
            }
            val batteryVoltage = String.format("%.2f", props.getProperty("POWER_SUPPLY_VOLTAGE_NOW").toFloat() / 1_000_000f).toFloat()
            val batteryWatt = if (isDualCell)
                String.format("%.2f", batteryCurrent * batteryVoltage * 2)
            else
                String.format("%.2f", batteryCurrent * batteryVoltage)
            val powerUnit = if (hidePowerUnit) "" else "W"
            val currentUnit = if (hideCurrentUnit) "" else "mA"
            val voltageUnit = if (hideVoltageUnit) "" else "V"
            val line1 = when (show1) {
                0 -> "$batteryWatt$powerUnit"
                1 -> "$batteryCurrentMa$currentUnit"
                else -> "$batteryVoltage$voltageUnit"
            }
            val line2 = when (show2) {
                0 -> "$batteryWatt$powerUnit"
                1 -> "$batteryCurrentMa$currentUnit"
                else -> "$batteryVoltage$voltageUnit"
            }
            indicator.setPadding(0, 0, 4.dpToPx(clockTextView.context), 0)
            indicator.text = if (isDualRow) "$line1\n$line2" else line1

            updateTime
        }
        return indicator
    }

    // 创建温度指标，返回创建的 indicator
    private fun createTemperatureIndicator(clockTextView: TextView, parent: ViewGroup): TextView {
        val localPrefs = prefs("systemui\\hardware_indicator")
        if (!localPrefs.getBoolean("temperature_indicator", false))
            return TextView(clockTextView.context)

        val indicator = createIndicatorTextView(clockTextView)
        addIndicatorView(clockTextView, parent, indicator)

        startPeriodicUpdate {
            val prefs = prefs("systemui\\hardware_indicator")
            val fontSize = prefs.getFloat("temperature_indicator_font_size", 8f)
            val boldText = prefs.getBoolean("temperature_indicator_bold_text", false)
            val alignment = prefs.getInt("temperature_indicator_alignment", Gravity.CENTER)
            indicator.gravity = alignment
            indicator.textSize = fontSize
            indicator.setTypeface(indicator.typeface, if (boldText) Typeface.BOLD else Typeface.NORMAL)

            val isDualRow = prefs.getBoolean("temperature_indicator_dual_row", false)
            val show1 = prefs.getInt("temperature_indicator_display_select1", 0)
            val show2 = prefs.getInt("temperature_indicator_display_select2", 0)
            val cpuTempSource = prefs.getInt("temperature_indicator_cpu_temp_source", 0)
            val hideBatteryUnit = prefs.getBoolean("temperature_indicator_hideBatteryUnit", false)
            val hideCpuUnit = prefs.getBoolean("temperature_indicator_hideCpuUnit", false)
            val updateTime = prefs.getInt("temperature_indicator_update_time", 1000).toLong()

            val cpuTemp = runCatching {
                File("/sys/class/thermal/thermal_zone$cpuTempSource/temp")
                    .readText().trim().toInt() / 1000f
            }.getOrElse { 0f }.let { String.format("%.2f", it).toFloat() }
            val batteryTemp = runCatching {
                File("/sys/class/power_supply/battery/temp")
                    .readText().trim().toInt() / 10f
            }.getOrElse { 0f }
            val batteryUnit = if (hideBatteryUnit) "" else "°C"
            val cpuUnit = if (hideCpuUnit) "" else "°C"
            val line1 = if (show1 == 0) "$batteryTemp$batteryUnit" else "$cpuTemp$cpuUnit"
            val line2 = if (show2 == 0) "$batteryTemp$batteryUnit" else "$cpuTemp$cpuUnit"
            indicator.setPadding(0, 0, 4.dpToPx(clockTextView.context), 0)
            indicator.text = if (isDualRow) "$line1\n$line2" else line1

            updateTime
        }
        return indicator
    }

    fun calculateInsertPosition(clock: View): Int {
        val children = (clock.parent as ViewGroup).children.toList()
        val notificationIndex = children.indexOfFirst { it.javaClass.simpleName.contains("NotificationIconContainer") }
        YLog.info("$notificationIndex${children.indexOf(clock) + 1}")
        return if (notificationIndex > 0) notificationIndex else children.indexOf(clock) + 1
    }

    // 使用 OnPreDrawListener 实时跟随颜色变化
    private fun startColorSync(clock: TextView, target: TextView) {
        target.setTextColor(clock.currentTextColor)
        clock.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                val currentColor = clock.currentTextColor
                if (target.currentTextColor != currentColor) {
                    target.setTextColor(currentColor)
                }
                return true
            }
        })
        registerConfigChangeReceiver(clock.context) { target.setTextColor(clock.currentTextColor) }
    }

    private fun TextView.syncWithClockStyle(clock: TextView) {
        setTextColor(clock.currentTextColor)
    }

    private fun registerConfigChangeReceiver(context: Context, callback: () -> Unit) {
        context.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) = callback()
        }, IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED))
    }

    fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}
