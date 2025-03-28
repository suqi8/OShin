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
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import java.time.Instant

/**
 * Wifi 类用于hook系统状态栏中的网络速度显示。
 * 本类实现了网络流量的实时监控，并根据用户设置动态改变显示样式。
 * 所有设置均实时生效，设置修改后无需重启即可生效。
 */
class Wifi : YukiBaseHooker() {

    /**
     * Settings 数据类封装所有用户配置项，
     * 每次调用更新方法时都会实时读取配置，保证配置变更立即生效。
     */
    data class Settings(
        val styleOption: Int,             // 显示样式选项（0 或 1）
        val slowThreshold: Int,           // 慢速阈值（单位：KB/s）
        val hideOnSlow: Boolean,          // 当速度低于阈值时是否隐藏显示
        val hideWhenBothSlow: Boolean,    // 当上下行均低于阈值时是否隐藏显示
        val iconIndicator: Int,           // 箭头图标选择
        val hideBs: Boolean,              // 是否隐藏单位 "b/s"
        val hideSpace: Boolean,           // 是否隐藏单位与数值之间的空格
        val swapUploadDownload: Boolean,  // 是否交换上传与下载的字体大小
        val uploadFontSize: Int,          // 上传速度字体大小
        val downloadFontSize: Int,        // 下载速度字体大小
        val positionIndicatorFront: Boolean, // 指示图标是否在前面显示
        val speedFontSize: Int,           // 数值字体大小
        val unitFontSize: Int             // 单位字体大小
    )

    /**
     * 每次调用更新方法时读取最新配置
     */
    private fun getSettings() = Settings(
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

    /**
     * hook入口方法
     * 当系统状态栏更新网络速度时调用此方法，根据配置动态更新UI显示效果
     */
    @SuppressLint("SetTextI18n")
    override fun onHook() {
        // 如果设置中关闭状态栏WiFi显示，则不进行hook
        if (!prefs("systemui\\status_bar_wifi").getBoolean("status_bar_wifi", false)) return

        loadApp("com.android.systemui") {
            "com.oplus.systemui.statusbar.phone.netspeed.widget.NetworkSpeedView".toClass().apply {
                // hook updateNetworkSpeed 方法，实时更新显示
                method { name = "updateNetworkSpeed" }.hook {
                    before {
                        // 设置容器宽度为自适应宽度
                        instance<FrameLayout>().layoutParams?.width = LayoutParams.WRAP_CONTENT

                        // 每次更新时实时读取最新设置
                        val settings = getSettings()

                        // 获取当前网络速度，单位为B/s
                        val (rxBytes, txBytes) = getCurrentSpeed()

                        // 根据不同显示样式进行处理
                        when (settings.styleOption) {
                            // 样式0：只控制控件的可见性
                            0 -> {
                                val rxKB = rxBytes / 1024
                                val txKB = txBytes / 1024
                                // 判断是否需要隐藏：当设置要求隐藏且速度低于阈值时隐藏
                                val hide = (settings.hideWhenBothSlow && (rxKB <= settings.slowThreshold && txKB <= settings.slowThreshold))
                                        || (settings.hideOnSlow && (rxKB <= settings.slowThreshold))
                                // 更新显示数字控件
                                field { name = "mSpeedNumber"; type = "android.widget.TextView" }
                                    .get(instance).cast<TextView>()?.apply {
                                        if (settings.speedFontSize != -1)
                                            setTextSize(TypedValue.COMPLEX_UNIT_DIP, settings.speedFontSize.toFloat())
                                        layoutParams.width = LayoutParams.WRAP_CONTENT
                                        visibility = if (hide) View.GONE else View.VISIBLE
                                    }
                                // 更新单位控件
                                field { name = "mSpeedUnit"; type = "android.widget.TextView" }
                                    .get(instance).cast<TextView>()?.apply {
                                        if (settings.unitFontSize != -1)
                                            setTextSize(TypedValue.COMPLEX_UNIT_DIP, settings.unitFontSize.toFloat())
                                        layoutParams.width = LayoutParams.WRAP_CONTENT
                                        visibility = if (hide) View.GONE else View.VISIBLE
                                    }
                            }
                            // 样式1：显示网络速度数值和箭头图标
                            1 -> {
                                // 获取显示数值的控件
                                val numView = field { name = "mSpeedNumber"; type = "android.widget.TextView" }
                                    .get(instance).cast<TextView>()
                                numView?.apply {
                                    // 根据上传下载交换设置选择合适的字体大小
                                    val font = if (!settings.swapUploadDownload)
                                        if (settings.uploadFontSize != -1) settings.uploadFontSize.toFloat() else 8F
                                    else
                                        if (settings.downloadFontSize != -1) settings.downloadFontSize.toFloat() else 8F
                                    setTextSize(TypedValue.COMPLEX_UNIT_DIP, font)
                                    layoutParams.width = LayoutParams.WRAP_CONTENT
                                    // 根据配置决定显示上传或下载速度
                                    text = if (!settings.swapUploadDownload)
                                        txSpeed(settings, txBytes, rxBytes)
                                    else
                                        rxSpeed(settings, rxBytes, txBytes)
                                }
                                // 获取显示单位或箭头的控件
                                val unitView = field { name = "mSpeedUnit"; type = "android.widget.TextView" }
                                    .get(instance).cast<TextView>()
                                unitView?.apply {
                                    val font = if (!settings.swapUploadDownload)
                                        if (settings.downloadFontSize != -1) settings.downloadFontSize.toFloat() else 8F
                                    else
                                        if (settings.uploadFontSize != -1) settings.uploadFontSize.toFloat() else 8F
                                    setTextSize(TypedValue.COMPLEX_UNIT_DIP, font)
                                    layoutParams.width = LayoutParams.WRAP_CONTENT
                                    // 显示另一方向的速度
                                    text = if (!settings.swapUploadDownload)
                                        rxSpeed(settings, rxBytes, txBytes)
                                    else
                                        txSpeed(settings, txBytes, rxBytes)
                                }
                                resultNull()
                            }
                            // 如果样式不符合预期，则返回空
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    /**
     * 生成上传速度显示文本
     * @param settings 当前用户设置
     * @param txBytes 当前上传字节数（B/s）
     * @param rxBytes 当前下载字节数（B/s）
     * @return 返回带箭头图标的上传速度文本
     */
    private fun txSpeed(settings: Settings, txBytes: Long, rxBytes: Long): String {
        val txKB = txBytes / 1024
        val rxKB = rxBytes / 1024
        // 如果设置要求隐藏，并且速度低于阈值，则返回空字符串
        if (settings.hideWhenBothSlow && (txKB <= settings.slowThreshold && rxKB <= settings.slowThreshold)) return ""
        if (settings.hideOnSlow && (rxKB <= settings.slowThreshold)) return ""
        // 根据上传速度生成对应箭头图标
        val arrow = txArrow(settings, txKB)
        // 根据配置决定箭头在前还是在后
        return if (settings.positionIndicatorFront)
            arrow + formatBytes(settings, txBytes)
        else
            formatBytes(settings, txBytes) + arrow
    }

    /**
     * 生成下载速度显示文本
     * @param settings 当前用户设置
     * @param rxBytes 当前下载字节数（B/s）
     * @param txBytes 当前上传字节数（B/s）
     * @return 返回带箭头图标的下载速度文本
     */
    private fun rxSpeed(settings: Settings, rxBytes: Long, txBytes: Long): String {
        val rxKB = rxBytes / 1024
        val txKB = txBytes / 1024
        // 判断是否需要隐藏显示
        if (settings.hideWhenBothSlow && (rxKB <= settings.slowThreshold && txKB <= settings.slowThreshold)) return ""
        if (settings.hideOnSlow && (txKB <= settings.slowThreshold)) return ""
        // 根据下载速度生成箭头图标
        val arrow = rxArrow(settings, rxKB)
        return if (settings.positionIndicatorFront)
            arrow + formatBytes(settings, rxBytes)
        else
            formatBytes(settings, rxBytes) + arrow
    }

    /**
     * 根据上传速度生成箭头图标
     * @param settings 当前用户设置
     * @param speedKB 上传速度（单位：KB/s）
     * @return 返回对应的箭头字符串
     */
    private fun txArrow(settings: Settings, speedKB: Long): String = when (settings.iconIndicator) {
        1 -> if (speedKB < settings.slowThreshold) "△" else "▲"
        2 -> if (settings.positionIndicatorFront)
            if (speedKB < settings.slowThreshold) "▵ " else "▴ "
        else
            if (speedKB < settings.slowThreshold) " ▵" else " ▴"
        3 -> if (settings.positionIndicatorFront)
            if (speedKB < settings.slowThreshold) "☖ " else "☗ "
        else
            if (speedKB < settings.slowThreshold) " ☖" else " ☗"
        4 -> "↑"
        5 -> "⇧"
        else -> ""
    }

    /**
     * 根据下载速度生成箭头图标
     * @param settings 当前用户设置
     * @param speedKB 下载速度（单位：KB/s）
     * @return 返回对应的箭头字符串
     */
    private fun rxArrow(settings: Settings, speedKB: Long): String = when (settings.iconIndicator) {
        1 -> if (speedKB < settings.slowThreshold) "▽" else "▼"
        2 -> if (settings.positionIndicatorFront)
            if (speedKB < settings.slowThreshold) "▿ " else "▾ "
        else
            if (speedKB < settings.slowThreshold) " ▿" else " ▾"
        3 -> if (settings.positionIndicatorFront)
            if (speedKB < settings.slowThreshold) "⛉ " else "⛊ "
        else
            if (speedKB < settings.slowThreshold) " ⛉" else " ⛊"
        4 -> "↓"
        5 -> "⇩"
        else -> ""
    }

    /**
     * NetStat 数据类封装当前网络状态数据
     */
    data class NetStat(val rxBytes: Long, val txBytes: Long, val timestamp: Long)

    /**
     * 获取当前网络状态数据，包括下载、上传字节数和当前时间戳
     */
    private fun getNetStat() = NetStat(
        TrafficStats.getTotalRxBytes() - TrafficStats.getRxBytes("lo"),
        TrafficStats.getTotalTxBytes() - TrafficStats.getTxBytes("lo"),
        SystemClock.elapsedRealtimeNanos()
    )

    // 用于记录上一次的网络状态数据及时间，计算速率时使用
    private var lastStat: NetStat? = null
    private var lastTime = 0L
    private var lastRxSpeed = 0L
    private var lastTxSpeed = 0L

    /**
     * 计算当前网络速率（B/s），更新频率为1秒
     * 利用上一次与当前数据的差值，除以时间差得到速率
     * @return 返回 Pair(rxSpeed, txSpeed)
     */
    private fun getCurrentSpeed(): Pair<Long, Long> {
        val currentStat = getNetStat()
        val currentSec = Instant.now().epochSecond
        // 如果当前秒数与上次记录不一致，则重新计算速度
        if (lastTime != currentSec && lastStat != null) {
            val dt = (currentStat.timestamp - lastStat!!.timestamp) / 1e9
            if (dt > 0) {
                lastRxSpeed = ((currentStat.rxBytes - lastStat!!.rxBytes) / dt).toLong().coerceAtLeast(0)
                lastTxSpeed = ((currentStat.txBytes - lastStat!!.txBytes) / dt).toLong().coerceAtLeast(0)
            }
            lastTime = currentSec
        }
        // 更新上一次状态数据
        lastStat = currentStat
        return Pair(lastRxSpeed, lastTxSpeed)
    }

    /**
     * 格式化字节数为带单位的字符串，例如 "1.23 Kb/s"
     * 根据字节数大小自动选择合适的单位显示
     * @param settings 当前用户设置，用于判断是否显示空格或单位
     * @param bytes 字节数（B/s）
     * @return 格式化后的字符串
     */
    private fun formatBytes(settings: Settings, bytes: Long): String {
        if (bytes <= 0) return "0${if (settings.hideSpace) "" else " "}b/s"
        val units = arrayOf("", "K", "M", "G", "T")
        var size = bytes.toDouble()
        var index = 0
        // 循环除以1024，直到size小于1024或达到单位上限
        while (size >= 1024 && index < units.lastIndex) {
            size /= 1024
            index++
        }
        val space = if (settings.hideSpace) "" else " "
        // 根据数值大小选择保留小数点的位数
        val formatted = when {
            index == 0 -> "${size.toLong()}$space${units[index]}"
            size < 10 -> "%.2f$space${units[index]}".format(size)
            size < 100 -> "%.1f$space${units[index]}".format(size)
            else -> "%.0f$space${units[index]}".format(size)
        }
        return formatted + if (settings.hideBs) "" else "b/s"
    }
}
