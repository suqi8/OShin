package com.suqi8.oshin.hook.systemui.StatusBar

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.TextView
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import java.io.File
import java.io.FileInputStream
import java.util.Properties
import java.util.concurrent.Executors
import kotlin.math.abs

class HardwareIndicator : YukiBaseHooker() {

    private val localPrefs by lazy { prefs("systemui\\hardware_indicator") }
    private val backgroundExecutor = Executors.newSingleThreadExecutor()
    private val uiHandler = Handler(Looper.getMainLooper())
    private val clockResources = mutableMapOf<TextView, ClockHookResources>()

    private data class ClockHookResources(
        var indicatorContainer: LinearLayout? = null,
        var consumptionIndicator: TextView? = null,
        var temperatureIndicator: TextView? = null,
        var consumptionRunnable: Runnable? = null,
        var temperatureRunnable: Runnable? = null,
        var preDrawListener: ViewTreeObserver.OnPreDrawListener? = null,
        var configChangeReceiver: BroadcastReceiver? = null,
        var lastCpuTotalTime: Long = 0L,
        var lastCpuIdleTime: Long = 0L
    )

    override fun onHook() {
        loadApp("com.android.systemui") {
            val clockClass = "com.android.systemui.statusbar.policy.Clock".toClass()

            clockClass.resolve().firstMethod {
                name = "onAttachedToWindow"
            }.hook {
                after {
                    val clockTextView = instance as? TextView ?: return@after
                    if (clockTextView.javaClass.name != "com.oplus.systemui.statusbar.widget.StatClock") return@after

                    // [核心修改] 将所有视图操作 post 到 UI 线程消息队列的末尾执行
                    // 以确保系统自身的布局流程已经完成，避免竞争条件导致通知图标消失
                    uiHandler.post {
                        // 在 post 的代码块内部，需要重新检查 clock 是否还附着在窗口上
                        if (!clockTextView.isAttachedToWindow) return@post
                        val parent = clockTextView.parent as? ViewGroup ?: return@post

                        cleanupResourcesFor(clockTextView)

                        val resources = ClockHookResources()
                        clockResources[clockTextView] = resources

                        val powerEnabled = localPrefs.getBoolean("power_indicator_enabled", false)
                        val tempEnabled = localPrefs.getBoolean("temp_indicator_enabled", false)

                        if (!powerEnabled && !tempEnabled) return@post

                        // 1. 创建总容器
                        val container = LinearLayout(clockTextView.context).apply {
                            orientation = LinearLayout.HORIZONTAL
                            gravity = Gravity.CENTER_VERTICAL
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.MATCH_PARENT
                            ).apply {
                                //gravity = Gravity.CENTER_VERTICAL
                                marginStart = 2.dpToPx(context)
                            }
                        }
                        resources.indicatorContainer = container

                        // 2. 添加指标到容器
                        if (powerEnabled) createConsumptionIndicator(clockTextView, container, resources)
                        if (tempEnabled) createTemperatureIndicator(clockTextView, container, resources)

                        // 3. 将总容器一次性添加到时钟后面
                        val clockIndex = parent.indexOfChild(clockTextView)
                        if (clockIndex != -1) {
                            parent.addView(container, clockIndex + 1)
                        } else {
                            parent.addView(container)
                        }

                        // 4. 启动监听
                        startSyncListeners(clockTextView, resources)
                    }
                }
            }

            clockClass.resolve().firstMethod {
                name = "onDetachedFromWindow"
            }.hook {
                after {
                    val clockTextView = instance as? TextView ?: return@after
                    cleanupResourcesFor(clockTextView)
                }
            }
        }
    }

    private fun cleanupResourcesFor(clock: TextView) {
        clockResources.remove(clock)?.apply {
            consumptionRunnable?.let { uiHandler.removeCallbacks(it) }
            temperatureRunnable?.let { uiHandler.removeCallbacks(it) }
            preDrawListener?.let { clock.viewTreeObserver.removeOnPreDrawListener(it) }
            configChangeReceiver?.let {
                runCatching { clock.context.unregisterReceiver(it) }
                    .onFailure { YLog.error("Failed to unregister BroadcastReceiver", it) }
            }
            indicatorContainer?.let {
                (it.parent as? ViewGroup)?.removeView(it)
            }
        }
    }

    private fun createConsumptionIndicator(clock: TextView, container: LinearLayout, res: ClockHookResources) {
        val indicator = createIndicatorTextView(clock)
        res.consumptionIndicator = indicator
        container.addView(indicator)

        res.consumptionRunnable = createPeriodicUpdater {
            updateIndicator(
                indicator = indicator,
                prefix = "power_indicator",
                resources = res
            )
            localPrefs.getFloat("power_indicator_update_interval", 1000f).toLong()
        }.also { uiHandler.post(it) }
    }

    private fun createTemperatureIndicator(clock: TextView, container: LinearLayout, res: ClockHookResources) {
        val indicator = createIndicatorTextView(clock)
        res.temperatureIndicator = indicator
        container.addView(indicator)

        res.temperatureRunnable = createPeriodicUpdater {
            updateIndicator(
                indicator = indicator,
                prefix = "temp_indicator",
                resources = res
            )
            localPrefs.getFloat("temp_indicator_update_interval", 1000f).toLong()
        }.also { uiHandler.post(it) }
    }

    private fun updateIndicator(indicator: TextView, prefix: String, resources: ClockHookResources) {
        val fontSize = localPrefs.getFloat("${prefix}_font_size", 8f)
        val boldText = localPrefs.getBoolean("${prefix}_bold", false)
        val alignment = localPrefs.getInt("${prefix}_alignment", Gravity.CENTER)
        indicator.gravity = alignment
        indicator.textSize = fontSize
        indicator.setTypeface(null, if (boldText) Typeface.BOLD else Typeface.NORMAL)
        updateIndicatorText(indicator, prefix, resources)
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun updateIndicatorText(indicator: TextView, prefix: String, resources: ClockHookResources) {
        backgroundExecutor.execute {
            val props = runCatching { readBatteryProperties() }.getOrNull()
            val currentNow = props?.getProperty("POWER_SUPPLY_CURRENT_NOW")?.toFloatOrNull() ?: 0f
            val voltageNow = props?.getProperty("POWER_SUPPLY_VOLTAGE_NOW")?.toFloatOrNull() ?: 0f
            val cpuTempSource = localPrefs.getFloat("data_temp_cpu_source", 0f)
            val cpuTemp = readCpuTemp(cpuTempSource)
            val cpuFreqSource = localPrefs.getFloat("data_freq_cpu_source", 0f)
            val cpuFreq = readCpuFreq(cpuFreqSource)
            val cpuUsage = calculateCpuUsage(resources)
            val ramUsage = readRamUsage()
            val batteryTemp = runCatching {
                File("/sys/class/power_supply/battery/temp").readText().trim().toFloat() / 10f
            }.getOrElse { 0f }

            val absolute = localPrefs.getBoolean("data_power_absolute_current", false)
            var batteryCurrentMa = currentNow * -1
            if (absolute) {
                batteryCurrentMa = abs(batteryCurrentMa)
            }
            val isDualCell = localPrefs.getBoolean("data_power_dual_cell", false)
            if (isDualCell) {
                batteryCurrentMa *= 2
            }
            val batteryCurrent = batteryCurrentMa / 1000f
            val batteryVoltage = voltageNow / 1_000_000f
            val batteryWatt = batteryCurrent * batteryVoltage

            uiHandler.post {
                val show1 = localPrefs.getInt("${prefix}_line1_content", 0)
                val show2 = localPrefs.getInt("${prefix}_line2_content", 0)
                val isDualRow = localPrefs.getBoolean("${prefix}_dual_row", false)
                val dataStrings = formatAllDataToStrings(
                    batteryWatt, batteryCurrentMa, batteryCurrent, batteryVoltage,
                    batteryTemp, cpuTemp, cpuFreq, cpuUsage, ramUsage
                )
                val line1 = dataStrings.getOrElse(show1) { "" }
                val line2 = dataStrings.getOrElse(show2) { "" }
                indicator.text = if (isDualRow) {
                    if (line1 == line2 || line2.isBlank()) line1
                    else if (line1.isBlank()) line2
                    else "$line1\n$line2"
                } else {
                    line1
                }
            }
        }
    }

    private fun formatAllDataToStrings(
        batteryWatt: Float, batteryCurrentMa: Float, batteryCurrent: Float, batteryVoltage: Float,
        batteryTemp: Float, cpuTemp: Float, cpuFreq: Int, cpuUsage: Int, ramUsage: Int
    ): List<String> {
        val hidePowerUnit = localPrefs.getBoolean("unit_hide_power", false)
        val hideCurrentUnit = localPrefs.getBoolean("unit_hide_current", false)
        val hideVoltageUnit = localPrefs.getBoolean("unit_hide_voltage", false)
        val hideBatteryUnit = localPrefs.getBoolean("unit_hide_temp_battery", false)
        val hideCpuUnit = localPrefs.getBoolean("unit_hide_temp_cpu", false)
        val hideCpuFreqUnit = localPrefs.getBoolean("unit_hide_cpu_frequency", false)
        val hideCpuUsageUnit = localPrefs.getBoolean("unit_hide_cpu_usage", false)
        val hideRamUsageUnit = localPrefs.getBoolean("unit_hide_ram_usage", false)

        val powerStr = String.format("%.2f", batteryWatt) + if (hidePowerUnit) "" else "W"
        val currentStr = if (abs(batteryCurrentMa) >= 800) {
            String.format("%.2f", batteryCurrent) + if (hideCurrentUnit) "" else "A"
        } else {
            String.format("%.0f", batteryCurrentMa) + if (hideCurrentUnit) "" else "mA"
        }
        val voltageStr = String.format("%.2f", batteryVoltage) + if (hideVoltageUnit) "" else "V"
        val batteryTempStr = String.format("%.1f", batteryTemp) + if (hideBatteryUnit) "" else "°C"
        val cpuTempStr = String.format("%.1f", cpuTemp) + if (hideCpuUnit) "" else "°C"
        val cpuFreqStr = "$cpuFreq" + if (hideCpuFreqUnit) "" else "MHz"
        val cpuUsageStr = "$cpuUsage" + if (hideCpuUsageUnit) "" else "%"
        val ramUsageStr = "$ramUsage" + if (hideRamUsageUnit) "" else "%"

        return listOf(powerStr, currentStr, voltageStr, cpuTempStr, batteryTempStr, cpuFreqStr, cpuUsageStr, ramUsageStr)
    }

    private fun readCpuTemp(source: Float) = runCatching {
        File("/sys/class/thermal/thermal_zone${source.toInt()}/temp").readText().trim().toFloat() / 1000f
    }.getOrElse { 0f }

    private fun readCpuFreq(source: Float) = runCatching {
        File("/sys/devices/system/cpu/cpu${source.toInt()}/cpufreq/scaling_cur_freq").readText().trim().toInt() / 1000
    }.getOrElse { 0 }

    private fun readRamUsage(): Int {
        return try {
            val file = File("/proc/meminfo")
            var memTotal: Long = -1
            var memAvailable: Long = -1
            file.forEachLine { line ->
                when {
                    line.startsWith("MemTotal:") -> memTotal = line.split("\\s+".toRegex())[1].toLong()
                    line.startsWith("MemAvailable:") -> memAvailable = line.split("\\s+".toRegex())[1].toLong()
                }
                if (memTotal != -1L && memAvailable != -1L) return@forEachLine
            }
            if (memTotal > 0 && memAvailable > 0) {
                (((memTotal - memAvailable) * 100) / memTotal).toInt()
            } else 0
        } catch (e: Exception) {
            0
        }
    }

    private fun calculateCpuUsage(res: ClockHookResources): Int {
        return try {
            val stats = File("/proc/stat").readText().lines().first()
            val fields = stats.split("\\s+".toRegex()).drop(1)
            val user = fields[0].toLong()
            val nice = fields[1].toLong()
            val system = fields[2].toLong()
            val idle = fields[3].toLong()
            val iowait = fields[4].toLong()
            val irq = fields[5].toLong()
            val softirq = fields[6].toLong()

            val totalTime = user + nice + system + idle + iowait + irq + softirq
            val idleTime = idle
            val totalDiff = totalTime - res.lastCpuTotalTime
            val idleDiff = idleTime - res.lastCpuIdleTime
            res.lastCpuTotalTime = totalTime
            res.lastCpuIdleTime = idleTime

            if (totalDiff > 0) {
                (100 * (totalDiff - idleDiff) / totalDiff).toInt()
            } else 0
        } catch (e: Exception) {
            0
        }
    }

    private fun createIndicatorTextView(clock: TextView): TextView =
        TextView(clock.context).apply {
            isSingleLine = false
            setTextColor(clock.currentTextColor)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.marginStart = 2.dpToPx(context)
            params.marginEnd = 2.dpToPx(context)
            layoutParams = params
        }

    private fun createPeriodicUpdater(updateAction: () -> Long): Runnable {
        return object : Runnable {
            override fun run() {
                val delay = updateAction()
                uiHandler.postDelayed(this, delay)
            }
        }
    }

    private fun readBatteryProperties(): Properties =
        Properties().apply {
            runCatching {
                FileInputStream("/sys/class/power_supply/battery/uevent").use { load(it) }
            }.onFailure { YLog.error("Failed to read battery properties", it) }
        }

    private fun startSyncListeners(clock: TextView, res: ClockHookResources) {
        val container = res.indicatorContainer ?: return
        val indicators = listOfNotNull(res.consumptionIndicator, res.temperatureIndicator)
        if (indicators.isEmpty()) return

        res.preDrawListener = object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                if (!clock.isAttachedToWindow) return true
                val currentColor = clock.currentTextColor
                val currentVisibility = clock.visibility

                if (container.visibility != currentVisibility) {
                    container.visibility = currentVisibility
                }

                indicators.forEach { target ->
                    if (target.currentTextColor != currentColor) target.setTextColor(currentColor)
                }
                return true
            }
        }.also { clock.viewTreeObserver.addOnPreDrawListener(it) }

        res.configChangeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                uiHandler.postDelayed({
                    if (clock.isAttachedToWindow) {
                        indicators.forEach { it.setTextColor(clock.currentTextColor) }
                    }
                }, 100)
            }
        }.also { clock.context.registerReceiver(it, IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED)) }
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}
