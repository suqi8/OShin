package com.suqi8.oshin.hook.com.android.systemui.StatusBar

import android.annotation.SuppressLint
import android.net.TrafficStats
import android.os.SystemClock
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.GridLayout.LayoutParams
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import java.util.Locale

/**
 * Hook 系统状态栏网络速度指示器。
 */
class Wifi : YukiBaseHooker() {

    private companion object {
        /** 样式1中，当字体大小未设置时的默认值 */
        private const val DEFAULT_FONT_SIZE = 8F
        /** 速度单位的前缀 (B, K, M, G, T) */
        private val SI_UNITS = arrayOf("B", "K", "M", "G", "T")
        /**
         * 速度计算的节流阀间隔（毫秒）。
         * 只有当距离上次有效计算超过这个时间，才进行下一次计算。
         */
        private const val CALCULATION_INTERVAL_MS = 20L
    }

    // --- 速度计算所需的状态变量 ---

    /** 上一次有效计算时的时间戳 (纳秒) */
    private var lastTimestampNanos: Long = 0L
    /** 上一次有效计算时的接收字节数 */
    private var lastRxBytes: Long = 0L
    /** 上一次有效计算时的发送字节数 */
    private var lastTxBytes: Long = 0L
    /** 上一次计算出的稳定速度值，在节流间隔内将持续返回此值 */
    private var lastCalculatedSpeed: Pair<Long, Long> = Pair(0L, 0L)

    // --- 设置模块 ---

    /**
     * 用户设置的数据类模型。
     */
    data class Settings(
        val styleOption: Int, val slowThreshold: Int, val hideOnSlow: Boolean,
        val hideWhenBothSlow: Boolean, val iconIndicator: Int, val hideBs: Boolean,
        val hideSpace: Boolean, val swapUploadDownload: Boolean, val uploadFontSize: Int,
        val downloadFontSize: Int, val positionIndicatorFront: Boolean, val speedFontSize: Int,
        val unitFontSize: Int
    )

    /**
     * 获取用户设置。
     *
     * 该实现为每次都直接从文件中读取最新配置，以保证设置可以实时生效。
     * @return [Settings] 对象
     */
    private fun getSettings(): Settings {
        return Settings(
            styleOption = prefs("systemui\\status_bar_wifi").getInt("StyleSelectedOption", 0),
            slowThreshold = prefs("systemui\\status_bar_wifi").getInt("slow_speed_threshold", 20),
            hideOnSlow = prefs("systemui\\status_bar_wifi").getBoolean("hide_on_slow", false),
            hideWhenBothSlow = prefs("systemui\\status_bar_wifi").getBoolean("hide_when_both_slow", false),
            iconIndicator = prefs("systemui\\status_bar_wifi").getInt("icon_indicator", 0),
            hideBs = prefs("systemui\\status_bar_wifi").getBoolean("hide_bs", false),
            hideSpace = prefs("systemui\\status_bar_wifi").getBoolean("hide_space", false),
            swapUploadDownload = prefs("systemui\\status_bar_wifi").getBoolean("swap_upload_download", false),
            uploadFontSize = prefs("systemui\\status_bar_wifi").getInt("upload_font_size", -1),
            downloadFontSize = prefs("systemui\\status_bar_wifi").getInt("download_font_size", -1),
            positionIndicatorFront = prefs("systemui\\status_bar_wifi").getBoolean("position_speed_indicator_front", false),
            speedFontSize = prefs("systemui\\status_bar_wifi").getInt("speed_font_size", -1),
            unitFontSize = prefs("systemui\\status_bar_wifi").getInt("unit_font_size", -1)
        )
    }

    /**
     * Hook 入口方法，定义所有 Hook 行为。
     */
    @SuppressLint("SetTextI18n")
    override fun onHook() {
        if (!prefs("systemui\\status_bar_wifi").getBoolean("status_bar_wifi", false)) return
        loadApp("com.android.systemui") {
            "com.oplus.systemui.statusbar.phone.netspeed.widget.NetworkSpeedView".toClass().apply {
                method { name = "applyNetworkState" }.hook {
                    before {
                        val settings = getSettings()
                        val (rxSpeed, txSpeed) = getCurrentSpeed()

                        if (settings.styleOption == 1) {
                            // --- 样式1：完全接管系统显示 ---
                            // 我们自己设置所有内容，然后阻止原始方法执行
                            val iconState = args[0] ?: return@before
                            field { name = "mState" }.get(instance).set(iconState)
                            val shouldBeVisible = try {
                                iconState.current().method { name = "getVisible" }.call() as? Boolean ?: true
                            } catch (_: Throwable) { true }
                            if (!shouldBeVisible) {
                                instance<View>().visibility = View.GONE
                                resultNull(); return@before
                            }
                            instance<View>().visibility = View.VISIBLE
                            instance<FrameLayout>().layoutParams?.width = LayoutParams.WRAP_CONTENT
                            val numView = field { name = "mSpeedNumber" }.get(instance).cast<TextView>()
                            numView?.apply {
                                visibility = View.VISIBLE
                                val font = if (!settings.swapUploadDownload) settings.uploadFontSize.takeIf { it != -1 }?.toFloat() ?: DEFAULT_FONT_SIZE else settings.downloadFontSize.takeIf { it != -1 }?.toFloat() ?: DEFAULT_FONT_SIZE
                                setTextSize(TypedValue.COMPLEX_UNIT_DIP, font)
                                layoutParams.width = LayoutParams.WRAP_CONTENT
                                text = if (!settings.swapUploadDownload) txSpeed(settings, txSpeed, rxSpeed) else rxSpeed(settings, rxSpeed, txSpeed)
                            }
                            val unitView = field { name = "mSpeedUnit" }.get(instance).cast<TextView>()
                            unitView?.apply {
                                visibility = View.VISIBLE
                                val font = if (!settings.swapUploadDownload) settings.downloadFontSize.takeIf { it != -1 }?.toFloat() ?: DEFAULT_FONT_SIZE else settings.uploadFontSize.takeIf { it != -1 }?.toFloat() ?: DEFAULT_FONT_SIZE
                                setTextSize(TypedValue.COMPLEX_UNIT_DIP, font)
                                layoutParams.width = LayoutParams.WRAP_CONTENT
                                text = if (!settings.swapUploadDownload) rxSpeed(settings, rxSpeed, txSpeed) else txSpeed(settings, txSpeed, rxSpeed)
                            }
                            resultNull() // 阻止原始方法运行
                        } else {
                            // --- 样式0：增强系统原生显示 ---
                            // 我们只修改字体、可见性等，然后让原始方法继续执行，由它来设置速度文本
                            instance<FrameLayout>().layoutParams?.width = LayoutParams.WRAP_CONTENT
                            val rxKB = rxSpeed / 1024
                            val txKB = txSpeed / 1024
                            val hide = (settings.hideWhenBothSlow && (rxKB <= settings.slowThreshold && txKB <= settings.slowThreshold)) || (settings.hideOnSlow && (rxKB <= settings.slowThreshold))
                            field { name = "mSpeedNumber" }.get(instance).cast<TextView>()?.apply {
                                settings.speedFontSize.takeIf { it != -1 }?.let { setTextSize(TypedValue.COMPLEX_UNIT_DIP, it.toFloat()) }
                                layoutParams.width = LayoutParams.WRAP_CONTENT
                                visibility = if (hide) View.GONE else View.VISIBLE
                            }
                            field { name = "mSpeedUnit" }.get(instance).cast<TextView>()?.apply {
                                settings.unitFontSize.takeIf { it != -1 }?.let { setTextSize(TypedValue.COMPLEX_UNIT_DIP, it.toFloat()) }
                                layoutParams.width = LayoutParams.WRAP_CONTENT
                                visibility = if (hide) View.GONE else View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 核心速度计算方法。
     * 内置状态和节流阀，以防止数值抖动和初始为0的问题。
     * @return 一个包含下行和上行稳定速度的 [Pair]。
     */
    private fun getCurrentSpeed(): Pair<Long, Long> {
        val currentTimestamp = SystemClock.elapsedRealtimeNanos()
        val currentRxBytes = TrafficStats.getTotalRxBytes() - TrafficStats.getRxBytes("lo")
        val currentTxBytes = TrafficStats.getTotalTxBytes() - TrafficStats.getTxBytes("lo")

        // 如果是模块加载后的第一次调用，我们无法计算速度。
        // 此时仅初始化基准数据，并返回0。UI将在约2秒后更新。
        if (lastTimestampNanos == 0L) {
            lastTimestampNanos = currentTimestamp
            lastRxBytes = currentRxBytes
            lastTxBytes = currentTxBytes
            return Pair(0L, 0L)
        }

        // 计算距离上次有效计算的时间差
        val dtNanos = currentTimestamp - lastTimestampNanos

        // 节流阀：只有当时间间隔足够长时，才进行一次新计算
        if (dtNanos >= CALCULATION_INTERVAL_MS * 1_000_000) {
            val dtSeconds = dtNanos / 1e9
            // 避免 dt 过小导致除零异常或数值溢出
            if (dtSeconds > 0.1) {
                val rxSpeed = ((currentRxBytes - lastRxBytes) / dtSeconds).toLong().coerceAtLeast(0)
                val txSpeed = ((currentTxBytes - lastTxBytes) / dtSeconds).toLong().coerceAtLeast(0)

                // 将本次计算结果存为“最新的稳定速度”
                lastCalculatedSpeed = Pair(rxSpeed, txSpeed)
            }

            // 更新下一次计算用的基准数据
            lastTimestampNanos = currentTimestamp
            lastRxBytes = currentRxBytes
            lastTxBytes = currentTxBytes
        }

        // 在节流间隔内，所有调用都将返回上一次算出的稳定结果
        return lastCalculatedSpeed
    }

    /**
     * 生成上传速度的显示文本。
     */
    private fun txSpeed(settings: Settings, txBytes: Long, rxBytes: Long): String {
        val txKB = txBytes / 1024; val rxKB = rxBytes / 1024
        if (settings.hideWhenBothSlow && (txKB <= settings.slowThreshold && rxKB <= settings.slowThreshold)) return ""
        if (settings.hideOnSlow && (txKB <= settings.slowThreshold)) return ""
        val arrow = txArrow(settings, txKB)
        return if (settings.positionIndicatorFront) arrow + formatBytes(settings, txBytes) else formatBytes(settings, txBytes) + arrow
    }

    /**
     * 生成下载速度的显示文本。
     */
    private fun rxSpeed(settings: Settings, rxBytes: Long, txBytes: Long): String {
        val rxKB = rxBytes / 1024; val txKB = txBytes / 1024
        if (settings.hideWhenBothSlow && (rxKB <= settings.slowThreshold && txKB <= settings.slowThreshold)) return ""
        if (settings.hideOnSlow && (rxKB <= settings.slowThreshold)) return ""
        val arrow = rxArrow(settings, rxKB)
        return if (settings.positionIndicatorFront) arrow + formatBytes(settings, rxBytes) else formatBytes(settings, rxBytes) + arrow
    }

    /**
     * 根据上传速度和配置生成箭头图标。
     */
    private fun txArrow(settings: Settings, speedKB: Long): String = when (settings.iconIndicator) {
        1 -> if (speedKB < settings.slowThreshold) "△" else "▲"
        2 -> if (speedKB < settings.slowThreshold) "▵ " else "▴ "
        3 -> if (speedKB < settings.slowThreshold) "☖ " else "◗ "
        4 -> "↑"
        5 -> "⇧"
        else -> ""
    }

    /**
     * 根据下载速度和配置生成箭头图标。
     */
    private fun rxArrow(settings: Settings, speedKB: Long): String = when (settings.iconIndicator) {
        1 -> if (speedKB < settings.slowThreshold) "▽" else "▼"
        2 -> if (speedKB < settings.slowThreshold) "▿ " else "▾ "
        3 -> if (speedKB < settings.slowThreshold) "⛉ " else "⛊ "
        4 -> "↓"
        5 -> "⇩"
        else -> ""
    }

    /**
     * 将字节速率格式化为易于阅读的字符串 (例如 "1.23 Mb/s")。
     * @param settings 当前用户设置，用于控制格式。
     * @param bytes 待格式化的原始字节速率 (B/s)。
     * @return 格式化后的速度字符串。
     */
    private fun formatBytes(settings: Settings, bytes: Long): String {
        if (bytes < 0) return ""
        val space = if (settings.hideSpace) "" else " "
        val suffix = if (settings.hideBs) "" else "b/s"
        if (bytes < 1024) return bytes.toString() + space + suffix
        var size = bytes.toDouble()
        var index = 0
        while (size >= 1024 && index < SI_UNITS.size - 1) {
            size /= 1024; index++
        }
        val prefix = SI_UNITS[index]
        val formattedNumber = when {
            size < 10 -> String.format(Locale.US, "%.2f", size)
            size < 100 -> String.format(Locale.US, "%.1f", size)
            else -> String.format(Locale.US, "%.0f", size)
        }
        return formattedNumber + space + prefix + suffix
    }
}
