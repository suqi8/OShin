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
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import java.io.File
import java.io.FileInputStream
import java.util.Properties
import java.util.concurrent.Executors
import kotlin.math.abs

class HardwareIndicator : YukiBaseHooker() {

    // 使用 lazy 委托，确保 SharedPreferences 只被初始化一次
    private val localPrefs by lazy { prefs("systemui\\hardware_indicator") }
    // 创建单线程的后台执行器，用于处理文件 I/O
    private val backgroundExecutor = Executors.newSingleThreadExecutor()
    // UI 线程的 Handler
    private val uiHandler = Handler(Looper.getMainLooper())

    // 用于存储与 Clock 实例关联的资源，防止泄漏
    private val clockResources = mutableMapOf<TextView, ClockHookResources>()

    // 封装与单个 Clock 实例相关的、需要被管理的资源
    private data class ClockHookResources(
        var consumptionIndicator: TextView? = null,
        var temperatureIndicator: TextView? = null,
        var consumptionRunnable: Runnable? = null,
        var temperatureRunnable: Runnable? = null,
        var preDrawListener: ViewTreeObserver.OnPreDrawListener? = null,
        var configChangeReceiver: BroadcastReceiver? = null,
        // 用于计算CPU使用率
        var lastCpuTotalTime: Long = 0L,
        var lastCpuIdleTime: Long = 0L
    )

    override fun onHook() {
        loadApp("com.android.systemui") {
            val clockClass = "com.android.systemui.statusbar.policy.Clock".toClass()

            // Hook onAttachedToWindow 方法来创建和初始化所有资源
            clockClass.resolve().firstMethod {
                name = "onAttachedToWindow"
            }.hook {
                after {
                    val clockTextView = instance as? TextView ?: return@after
                    if (clockTextView.javaClass.name != "com.oplus.systemui.statusbar.widget.StatClock") return@after

                    // 清理旧资源，以防万一
                    cleanupResourcesFor(clockTextView)

                    val resources = ClockHookResources()
                    clockResources[clockTextView] = resources

                    // 创建并设置两个指标
                    createConsumptionIndicator(clockTextView, resources)
                    createTemperatureIndicator(clockTextView, resources)

                    // 启动颜色同步和配置变化监听
                    startSyncListeners(clockTextView, resources)
                }
            }

            // Hook onDetachedFromWindow 方法来释放所有资源，防止内存泄漏
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

    /**
     * 为指定的 Clock TextView 清理所有关联的资源
     */
    private fun cleanupResourcesFor(clock: TextView) {
        clockResources.remove(clock)?.apply {
            // 停止所有周期性任务
            consumptionRunnable?.let { uiHandler.removeCallbacks(it) }
            temperatureRunnable?.let { uiHandler.removeCallbacks(it) }
            // 移除监听器
            preDrawListener?.let { clock.viewTreeObserver.removeOnPreDrawListener(it) }
            // 反注册广播接收器
            configChangeReceiver?.let {
                runCatching { clock.context.unregisterReceiver(it) }
                    .onFailure { YLog.error("Failed to unregister BroadcastReceiver", it) }
            }
        }
    }

    // --- 指标创建与管理 ---

    private fun createConsumptionIndicator(clock: TextView, res: ClockHookResources) {
        if (!localPrefs.getBoolean("power_indicator_enabled", false)) return
        val parent = clock.parent as? ViewGroup ?: return
        val indicator = createIndicatorTextView(clock)
        res.consumptionIndicator = indicator

        addIndicatorView(clock, parent, indicator)

        // 创建并启动周期性更新任务
        res.consumptionRunnable = createPeriodicUpdater {
            // 每次更新都重新读取样式和文本
            updateIndicator(
                indicator = indicator,
                prefix = "power_indicator",
                resources = res
            )
            localPrefs.getInt("power_indicator_update_interval", 1000).toLong()
        }.also { uiHandler.post(it) }
    }

    private fun createTemperatureIndicator(clock: TextView, res: ClockHookResources) {
        if (!localPrefs.getBoolean("temp_indicator_enabled", false)) return
        val parent = clock.parent as? ViewGroup ?: return
        val indicator = createIndicatorTextView(clock)
        res.temperatureIndicator = indicator

        addIndicatorView(clock, parent, indicator)

        // 创建并启动周期性更新任务
        res.temperatureRunnable = createPeriodicUpdater {
            // 每次更新都重新读取样式和文本
            updateIndicator(
                indicator = indicator,
                prefix = "temp_indicator",
                resources = res
            )
            localPrefs.getInt("temp_indicator_update_interval", 1000).toLong()
        }.also { uiHandler.post(it) }
    }

    // --- 周期性更新逻辑 (已统一) ---

    /**
     * 更新单个指标的样式和文本
     */
    private fun updateIndicator(indicator: TextView, prefix: String, resources: ClockHookResources) {
        // 1. 更新样式
        val fontSize = localPrefs.getFloat("${prefix}_font_size", 8f)
        val boldText = localPrefs.getBoolean("${prefix}_bold", false)
        val alignment = localPrefs.getInt("${prefix}_alignment", Gravity.CENTER)
        indicator.gravity = alignment
        indicator.textSize = fontSize
        indicator.setTypeface(null, if (boldText) Typeface.BOLD else Typeface.NORMAL)

        // 2. 在后台获取所有数据并更新文本
        updateIndicatorText(indicator, prefix, resources)
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun updateIndicatorText(indicator: TextView, prefix: String, resources: ClockHookResources) {
        // 在后台线程读取所有文件数据
        backgroundExecutor.execute {
            // --- 获取所有硬件数据 ---
            val props = runCatching { readBatteryProperties() }.getOrNull()
            val currentNow = props?.getProperty("POWER_SUPPLY_CURRENT_NOW")?.toFloatOrNull() ?: 0f
            val voltageNow = props?.getProperty("POWER_SUPPLY_VOLTAGE_NOW")?.toFloatOrNull() ?: 0f

            val cpuTempSource = localPrefs.getInt("data_temp_cpu_source", 0)
            val cpuTemp = readCpuTemp(cpuTempSource)

            val cpuFreqSource = localPrefs.getInt("data_freq_cpu_source", 0)
            val cpuFreq = readCpuFreq(cpuFreqSource)

            val cpuUsage = calculateCpuUsage(resources)
            val ramUsage = readRamUsage()

            val batteryTemp = runCatching {
                File("/sys/class/power_supply/battery/temp").readText().trim().toFloat() / 10f
            }.getOrElse { 0f }

            // --- 在后台进行计算 ---
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

            // --- 将结果 post 到 UI 线程进行更新 ---
            uiHandler.post {
                // --- 读取显示相关的配置 ---
                val show1 = localPrefs.getInt("${prefix}_line1_content", 0)
                val show2 = localPrefs.getInt("${prefix}_line2_content", 0)
                val isDualRow = localPrefs.getBoolean("${prefix}_dual_row", false)

                // --- 格式化所有数据为字符串 ---
                val dataStrings = formatAllDataToStrings(
                    batteryWatt, batteryCurrentMa, batteryCurrent, batteryVoltage,
                    batteryTemp, cpuTemp, cpuFreq, cpuUsage, ramUsage
                )

                // --- 根据配置选择要显示的字符串 ---
                val line1 = dataStrings.getOrElse(show1) { "" }
                val line2 = dataStrings.getOrElse(show2) { "" }

                // --- 设置最终文本 ---
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

    // --- 辅助函数 ---

    /**
     * 将所有硬件数据格式化为字符串列表
     * 顺序: 0:功率, 1:电流, 2:电压, 3:CPU温度, 4:电池温度, 5:CPU频率, 6:CPU使用率, 7:内存占用
     */
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

    private fun readCpuTemp(source: Int) = runCatching {
        File("/sys/class/thermal/thermal_zone$source/temp").readText().trim().toFloat() / 1000f
    }.getOrElse { 0f }

    private fun readCpuFreq(source: Int) = runCatching {
        File("/sys/devices/system/cpu/cpu$source/cpufreq/scaling_cur_freq").readText().trim().toInt() / 1000
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
            setTextColor(clock.currentTextColor) // 设置初始颜色
            setPadding(0, 0, 4.dpToPx(context), 0)
        }

    private fun addIndicatorView(clock: TextView, parent: ViewGroup, indicator: TextView) {
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER_VERTICAL
            marginStart = 4.dpToPx(clock.context)
        }
        parent.addView(indicator, calculateInsertPosition(clock), layoutParams)
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

    private fun calculateInsertPosition(clock: View): Int {
        val children = (clock.parent as ViewGroup).children.toList()
        val notificationIndex = children.indexOfFirst { it.javaClass.simpleName.contains("NotificationIconContainer") }
        return if (notificationIndex > 0) notificationIndex else children.indexOf(clock) + 1
    }

    private fun startSyncListeners(clock: TextView, res: ClockHookResources) {
        val indicators = listOfNotNull(res.consumptionIndicator, res.temperatureIndicator)
        if (indicators.isEmpty()) return

        // 1. OnPreDrawListener 用于实时同步颜色和可见性
        res.preDrawListener = object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                if (!clock.isAttachedToWindow) return true
                val currentColor = clock.currentTextColor
                val currentVisibility = clock.visibility
                indicators.forEach { target ->
                    if (target.currentTextColor != currentColor) target.setTextColor(currentColor)
                    if (target.visibility != currentVisibility) target.visibility = currentVisibility
                }
                return true
            }
        }.also { clock.viewTreeObserver.addOnPreDrawListener(it) }

        // 2. BroadcastReceiver 用于监听系统配置变化（如深色模式切换）
        res.configChangeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                // 延迟一小段时间以确保 Clock 的颜色已经更新
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
