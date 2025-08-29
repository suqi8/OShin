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
        var configChangeReceiver: BroadcastReceiver? = null
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
                prefix = "power_indicator"
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
                prefix = "temp_indicator"
            )
            localPrefs.getInt("temp_indicator_update_interval", 1000).toLong()
        }.also { uiHandler.post(it) }
    }

    // --- 周期性更新逻辑 (已统一) ---

    /**
     * 更新单个指标的样式和文本
     */
    private fun updateIndicator(indicator: TextView, prefix: String) {
        // 1. 更新样式
        val fontSize = localPrefs.getFloat("${prefix}_font_size", 8f)
        val boldText = localPrefs.getBoolean("${prefix}_bold", false)
        val alignment = localPrefs.getInt("${prefix}_alignment", Gravity.CENTER)
        indicator.gravity = alignment
        indicator.textSize = fontSize
        indicator.setTypeface(null, if (boldText) Typeface.BOLD else Typeface.NORMAL)

        // 2. 在后台获取所有数据并更新文本
        updateIndicatorText(indicator, prefix)
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun updateIndicatorText(indicator: TextView, prefix: String) {
        // 在后台线程读取所有文件数据
        backgroundExecutor.execute {
            // --- 获取所有硬件数据 ---
            val props = runCatching { readBatteryProperties() }.getOrNull()
            val currentNow = props?.getProperty("POWER_SUPPLY_CURRENT_NOW")?.toFloatOrNull() ?: 0f
            val voltageNow = props?.getProperty("POWER_SUPPLY_VOLTAGE_NOW")?.toFloatOrNull() ?: 0f
            val cpuTempSource = localPrefs.getInt("data_temp_cpu_source", 0)
            val cpuTemp = runCatching {
                File("/sys/class/thermal/thermal_zone$cpuTempSource/temp").readText().trim().toFloat() / 1000f
            }.getOrElse { 0f }
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
            val batteryCurrent = batteryCurrentMa / 1000f
            val batteryVoltage = voltageNow / 1_000_000f * (if (isDualCell) 2 else 1)
            val batteryWatt = batteryCurrent * batteryVoltage

            // --- 将结果 post 到 UI 线程进行更新 ---
            uiHandler.post {
                // --- 读取显示相关的配置 ---
                val show1 = localPrefs.getInt("${prefix}_line1_content", 0)
                val show2 = localPrefs.getInt("${prefix}_line2_content", 0)
                val isDualRow = localPrefs.getBoolean("${prefix}_dual_row", false)

                // --- 格式化所有数据为字符串 ---
                val dataStrings = formatAllDataToStrings(batteryWatt, batteryCurrentMa, batteryCurrent, batteryVoltage, batteryTemp, cpuTemp)

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
     * 顺序: 0:功率, 1:电流, 2:电压, 3:CPU温度, 4:电池温度
     */
    private fun formatAllDataToStrings(
        batteryWatt: Float, batteryCurrentMa: Float, batteryCurrent: Float, batteryVoltage: Float,
        batteryTemp: Float, cpuTemp: Float
    ): List<String> {
        val hidePowerUnit = localPrefs.getBoolean("unit_hide_power", false)
        val hideCurrentUnit = localPrefs.getBoolean("unit_hide_current", false)
        val hideVoltageUnit = localPrefs.getBoolean("unit_hide_voltage", false)
        val hideBatteryUnit = localPrefs.getBoolean("unit_hide_temp_battery", false)
        val hideCpuUnit = localPrefs.getBoolean("unit_hide_temp_cpu", false)

        val powerStr = String.format("%.2f", batteryWatt) + if (hidePowerUnit) "" else "W"
        val currentStr = if (abs(batteryCurrentMa) >= 800) {
            String.format("%.2f", batteryCurrent) + if (hideCurrentUnit) "" else "A"
        } else {
            String.format("%.0f", batteryCurrentMa) + if (hideCurrentUnit) "" else "mA"
        }
        val voltageStr = String.format("%.2f", batteryVoltage) + if (hideVoltageUnit) "" else "V"
        val batteryTempStr = String.format("%.1f", batteryTemp) + if (hideBatteryUnit) "" else "°C"
        val cpuTempStr = String.format("%.1f", cpuTemp) + if (hideCpuUnit) "" else "°C"

        return listOf(powerStr, currentStr, voltageStr, cpuTempStr, batteryTempStr)
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
