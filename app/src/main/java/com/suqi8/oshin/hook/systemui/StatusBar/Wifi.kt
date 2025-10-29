package com.suqi8.oshin.hook.systemui.StatusBar

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.net.TrafficStats
import android.os.SystemClock
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import java.util.Locale
import android.view.ViewGroup.LayoutParams as ViewGroupParams
import android.widget.FrameLayout.LayoutParams as FrameLayoutParams

/**
 * Hook 系统状态栏网络速度指示器
 *
 * 功能说明：
 * - 自定义状态栏网络速度显示样式
 * - 支持上传/下载速度分别显示或合并显示
 * - 支持多种箭头指示器样式
 * - 支持慢速隐藏、字体自定义等功能
 */
class Wifi : YukiBaseHooker() {

    // ========== 常量定义 ==========
    private companion object {
        /** 默认字体大小 (单位: dp) */
        private const val DEFAULT_FONT_SIZE = 8F

        /** SI 单位前缀数组: 字节 -> KB -> MB -> GB -> TB */
        private val SI_PREFIXES = arrayOf("", "K", "M", "G", "T")

        /** 网速计算时间间隔 (毫秒) */
        private const val CALCULATION_INTERVAL_MS = 20L

        /** Oplus 系统默认宽度 (单位: dp) */
        private const val OPLUS_DEFAULT_WIDTH_DP = 22
    }

    // ========== 状态变量 ==========
    /** 上次计算时的时间戳 (纳秒) */
    private var lastTimestampNanos: Long = 0L

    /** 上次记录的接收字节数 */
    private var lastRxBytes: Long = 0L

    /** 上次记录的发送字节数 */
    private var lastTxBytes: Long = 0L

    /** 上次计算出的网速 (接收速度, 发送速度) */
    private var lastCalculatedSpeed: Pair<Long, Long> = Pair(0L, 0L)

    /** 原始数字字体 (用于恢复) */
    private var originalNumTypeface: Typeface? = null

    /** 原始单位字体 (用于恢复) */
    private var originalUnitTypeface: Typeface? = null

    // ========== 配置数据类 ==========
    /**
     * 网速显示设置
     *
     * @property styleOption 样式选项: 0=增强原生, 1=完全自定义
     * @property slowThreshold 慢速阈值 (KB/s)
     * @property hideOnSlow 慢速时隐藏
     * @property hideWhenBothSlow 上传下载都慢时隐藏
     * @property iconIndicator 箭头指示器样式 (0-5)
     * @property hideBs 隐藏 "B/s" 后缀
     * @property hideSpace 隐藏数字和单位之间的空格
     * @property swapUploadDownload 交换上传下载位置
     * @property uploadFontSize 上传字体大小
     * @property downloadFontSize 下载字体大小
     * @property positionIndicatorFront 箭头指示器放在前面
     * @property speedFontSize 速度数字字体大小
     * @property unitFontSize 单位字体大小
     * @property useSystemFont 使用系统字体
     * @property useUppercaseB 使用大写 B (Byte)
     * @property hidePerSecond 隐藏 "/s" 后缀
     * @property alignment 对齐方式: 0=居中, 1=左对齐, 3=右对齐
     */
    data class Settings(
        val styleOption: Int,
        val slowThreshold: Int,
        val hideOnSlow: Boolean,
        val hideWhenBothSlow: Boolean,
        val iconIndicator: Int,
        val hideBs: Boolean,
        val hideSpace: Boolean,
        val swapUploadDownload: Boolean,
        val uploadFontSize: Int,
        val downloadFontSize: Int,
        val positionIndicatorFront: Boolean,
        val speedFontSize: Int,
        val unitFontSize: Int,
        val useSystemFont: Boolean,
        val useUppercaseB: Boolean,
        val hidePerSecond: Boolean,
        val alignment: Int
    )

    /**
     * 从配置文件读取设置
     *
     * @return Settings 配置对象
     */
    private fun getSettings(): Settings {
        val prefs = prefs("systemui\\status_bar\\status_bar_wifi")
        return Settings(
            styleOption = prefs.getInt("StyleSelectedOption", 0),
            slowThreshold = prefs.getFloat("slow_speed_threshold", 20f).toInt(),
            hideOnSlow = prefs.getBoolean("hide_on_slow", false),
            hideWhenBothSlow = prefs.getBoolean("hide_when_both_slow", false),
            iconIndicator = prefs.getInt("icon_indicator", 0),
            hideBs = prefs.getBoolean("hide_bs", false),
            hideSpace = prefs.getBoolean("hide_space", false),
            swapUploadDownload = prefs.getBoolean("swap_upload_download", false),
            uploadFontSize = prefs.getFloat("upload_font_size", -1f).toInt(),
            downloadFontSize = prefs.getFloat("download_font_size", -1f).toInt(),
            positionIndicatorFront = prefs.getBoolean("position_speed_indicator_front", false),
            speedFontSize = prefs.getFloat("speed_font_size", -1f).toInt(),
            unitFontSize = prefs.getFloat("unit_font_size", -1f).toInt(),
            useSystemFont = prefs.getBoolean("use_system_font", false),
            useUppercaseB = prefs.getBoolean("use_uppercase_b", false),
            hidePerSecond = prefs.getBoolean("hide_per_second", false),
            alignment = prefs.getInt("alignment", 0)
        )
    }

    /**
     * 应用布局宽度和对齐方式
     *
     * 这个函数负责设置网速视图的布局参数，包括:
     * - 父视图的宽度 (根据对齐方式决定是固定宽度还是匹配父布局)
     * - 数字和单位文本的对齐方式 (左/中/右对齐)
     * - 重置边距以确保紧凑布局
     *
     * @param view 父视图 (NetworkSpeedView)
     * @param numView 数字文本视图
     * @param unitView 单位文本视图
     * @param settings 用户设置
     */
    private fun applyLayoutAlignment(
        view: View,
        numView: TextView,
        unitView: TextView,
        settings: Settings
    ) {
        // 获取布局参数，如果为空则直接返回
        val viewLp = view.layoutParams ?: return
        val numLp = numView.layoutParams as? FrameLayoutParams ?: return
        val unitLp = unitView.layoutParams as? FrameLayoutParams ?: return

        // 获取屏幕密度，用于 dp 到 px 的转换
        val density = view.resources.displayMetrics.density
        val defaultWidthPx = (OPLUS_DEFAULT_WIDTH_DP * density).toInt()
        val align = settings.alignment

        // 1. 设置父视图宽度
        // - 如果是居中对齐 (align == 0)，使用固定宽度
        // - 否则使用 MATCH_PARENT 以便文本可以左右对齐
        viewLp.width = if (align != 0) ViewGroupParams.MATCH_PARENT else defaultWidthPx
        viewLp.height = ViewGroupParams.MATCH_PARENT
        view.layoutParams = viewLp

        // 2. 根据用户设置确定水平对齐方式
        val horizontalGravity = when (align) {
            1 -> Gravity.START      // 左对齐
            3 -> Gravity.END        // 右对齐
            else -> Gravity.CENTER_HORIZONTAL  // 居中
        }

        // 3. 设置数字和单位文本的布局参数
        // 关键修改: 重置 margins 并设置紧凑间距，避免文本重叠
        numLp.width = ViewGroupParams.WRAP_CONTENT
        numLp.gravity = horizontalGravity

        unitLp.width = ViewGroupParams.WRAP_CONTENT
        unitLp.gravity = horizontalGravity

        // 应用布局参数
        numView.layoutParams = numLp
        unitView.layoutParams = unitLp
    }

    /**
     * Hook 入口方法
     *
     * 在这里进行三个关键的 Hook:
     * 1. onFinishInflate - 初始化时保存原始字体
     * 2. updateSpeedNumberParams - 更新字体和布局参数
     * 3. applyNetworkState - 应用网速状态和显示
     */
    @SuppressLint("SetTextI18n")
    override fun onHook() {
        // 检查功能是否启用
        if (!prefs("systemui\\status_bar\\status_bar_wifi")
                .getBoolean("status_bar_wifi", false)) return

        // Oplus 系统网速视图的完整类名
        val viewClassName = "com.oplus.systemui.statusbar.phone.netspeed.widget.NetworkSpeedView"

        loadApp("com.android.systemui") {
            viewClassName.toClass().resolve().apply {

                // ========== HOOK 1: onFinishInflate ==========
                // 在视图加载完成时执行，保存原始字体以便后续恢复
                firstMethod { name = "onFinishInflate" }.hook {
                    after {
                        // 获取数字和单位文本视图
                        val numView = firstField { name = "mSpeedNumber" }.of(instance).get() as TextView
                        val unitView = firstField { name = "mSpeedUnit" }.of(instance).get() as TextView

                        // 保存原始字体
                        originalNumTypeface = numView.typeface
                        originalUnitTypeface = unitView.typeface

                        // 如果用户选择使用系统字体，则立即应用
                        val settings = getSettings()
                        if (settings.useSystemFont) {
                            numView.setTypeface(Typeface.DEFAULT)
                            unitView.setTypeface(Typeface.DEFAULT)
                        }
                    }
                }

                // ========== HOOK 2: updateSpeedNumberParams ==========
                // [关键] 在原方法执行后修改参数
                // 允许原始方法先设置边距，然后我们覆盖字体和布局
                firstMethod { name = "updateSpeedNumberParams" }.hook {
                    after {
                        val settings = getSettings()

                        // 获取视图实例
                        val view: View = instance()
                        val numView = firstField { name = "mSpeedNumber" }
                            .of(instance).get() as TextView? ?: return@after
                        val unitView = firstField { name = "mSpeedUnit" }
                            .of(instance).get() as TextView? ?: return@after

                        // 1. 覆盖字体设置 (以防配置在运行时更改)
                        if (settings.useSystemFont) {
                            numView.setTypeface(Typeface.DEFAULT)
                            unitView.setTypeface(Typeface.DEFAULT)
                        } else {
                            // 恢复原始字体
                            originalNumTypeface?.let { numView.setTypeface(it) }
                            originalUnitTypeface?.let { unitView.setTypeface(it) }
                        }

                        // 2. 覆盖字体大小
                        if (settings.styleOption == 1) {
                            // 样式1: 上传和下载使用不同字体大小
                            val upFont = settings.uploadFontSize
                                .takeIf { it != -1 }?.toFloat() ?: DEFAULT_FONT_SIZE
                            val downFont = settings.downloadFontSize
                                .takeIf { it != -1 }?.toFloat() ?: DEFAULT_FONT_SIZE

                            // 根据是否交换上传下载，应用相应字体大小
                            numView.setTextSize(
                                TypedValue.COMPLEX_UNIT_DIP,
                                if (!settings.swapUploadDownload) upFont else downFont
                            )
                            unitView.setTextSize(
                                TypedValue.COMPLEX_UNIT_DIP,
                                if (!settings.swapUploadDownload) downFont else upFont
                            )
                        } else {
                            // 样式0: 统一字体大小
                            settings.speedFontSize.takeIf { it != -1 }?.let {
                                numView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, it.toFloat())
                            }
                            settings.unitFontSize.takeIf { it != -1 }?.let {
                                unitView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, it.toFloat())
                            }
                        }

                        // 3. 覆盖布局和对齐方式
                        applyLayoutAlignment(view, numView, unitView, settings)
                    }
                }

                // ========== HOOK 3: applyNetworkState ==========
                // 在应用网络状态前执行，更新网速显示
                firstMethod { name = "applyNetworkState" }.hook {
                    before {
                        val settings = getSettings()

                        // 获取当前网速 (接收速度, 发送速度)
                        val (rxSpeed, txSpeed) = getCurrentSpeed()

                        // 获取视图实例
                        val view: View = instance()
                        val numView = firstField { name = "mSpeedNumber" }
                            .of(instance).get() as TextView? ?: return@before
                        val unitView = firstField { name = "mSpeedUnit" }
                            .of(instance).get() as TextView? ?: return@before

                        // 1. 每次更新时都确保布局正确
                        // 这样可以保证对齐方式实时生效
                        applyLayoutAlignment(view, numView, unitView, settings)

                        if (settings.styleOption == 1) {
                            // ========== 样式1: 完全自定义模式 ==========
                            // 完全接管网速显示逻辑

                            // 获取网络图标状态
                            val iconState = args[0] ?: return@before
                            firstField { name = "mState" }.of(instance).set(iconState)

                            // 检查是否应该显示网速
                            val shouldBeVisible = iconState.asResolver()
                                .firstMethod { name = "getVisible" }
                                .invoke() as Boolean

                            if (!shouldBeVisible) {
                                // 网络不可用时隐藏
                                view.visibility = View.GONE
                                resultNull()
                                return@before
                            }

                            // 显示网速
                            view.visibility = View.VISIBLE

                            // 设置数字文本 (上传或下载，根据交换设置)
                            numView.visibility = View.VISIBLE
                            numView.text = if (!settings.swapUploadDownload)
                                txSpeed(settings, txSpeed, rxSpeed)
                            else
                                rxSpeed(settings, rxSpeed, txSpeed)

                            // 设置单位文本 (下载或上传，根据交换设置)
                            unitView.visibility = View.VISIBLE
                            unitView.text = if (!settings.swapUploadDownload)
                                rxSpeed(settings, rxSpeed, txSpeed)
                            else
                                txSpeed(settings, txSpeed, rxSpeed)

                            // 阻止原方法执行
                            resultNull()

                        } else {
                            // ========== 样式0: 增强系统原生模式 ==========
                            // 基于系统原生显示，添加慢速隐藏等增强功能

                            // 转换为 KB/s
                            val rxKB = rxSpeed / 1024
                            val txKB = txSpeed / 1024

                            // 判断是否应该隐藏
                            val hide = (settings.hideWhenBothSlow &&
                                    (rxKB <= settings.slowThreshold &&
                                            txKB <= settings.slowThreshold)) ||
                                    (settings.hideOnSlow &&
                                            (rxKB <= settings.slowThreshold))

                            // 根据隐藏标志显示/隐藏
                            numView.visibility = if (hide) View.GONE else View.VISIBLE
                            unitView.visibility = if (hide) View.GONE else View.VISIBLE

                            // 允许原始方法继续运行 (它会设置文本内容)
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取当前网速
     *
     * 计算逻辑:
     * 1. 获取当前时间戳和流量数据
     * 2. 计算与上次记录的时间差和流量差
     * 3. 通过 速度 = 流量差 / 时间差 计算网速
     * 4. 每隔 CALCULATION_INTERVAL_MS 更新一次
     *
     * @return Pair<Long, Long> (接收速度, 发送速度) 单位: 字节/秒
     */
    private fun getCurrentSpeed(): Pair<Long, Long> {
        // 获取当前时间戳 (纳秒)
        val currentTimestamp = SystemClock.elapsedRealtimeNanos()

        // 获取总流量，排除本地回环接口 (lo)
        val currentRxBytes = TrafficStats.getTotalRxBytes() - TrafficStats.getRxBytes("lo")
        val currentTxBytes = TrafficStats.getTotalTxBytes() - TrafficStats.getTxBytes("lo")

        // 首次调用时初始化
        if (lastTimestampNanos == 0L) {
            lastTimestampNanos = currentTimestamp
            lastRxBytes = currentRxBytes
            lastTxBytes = currentTxBytes
            return Pair(0L, 0L)
        }

        // 计算时间差 (纳秒)
        val dtNanos = currentTimestamp - lastTimestampNanos

        // 如果时间间隔足够长，则重新计算网速
        if (dtNanos >= CALCULATION_INTERVAL_MS * 1_000_000) {
            // 转换为秒
            val dtSeconds = dtNanos / 1e9

            if (dtSeconds > 0.1) {
                // 计算速度: (当前流量 - 上次流量) / 时间差
                val rxSpeed = ((currentRxBytes - lastRxBytes) / dtSeconds)
                    .toLong().coerceAtLeast(0)
                val txSpeed = ((currentTxBytes - lastTxBytes) / dtSeconds)
                    .toLong().coerceAtLeast(0)

                // 保存计算结果
                lastCalculatedSpeed = Pair(rxSpeed, txSpeed)
            }

            // 更新上次记录
            lastTimestampNanos = currentTimestamp
            lastRxBytes = currentRxBytes
            lastTxBytes = currentTxBytes
        }

        // 返回最新的网速
        return lastCalculatedSpeed
    }

    /**
     * 格式化上传速度文本
     *
     * @param settings 用户设置
     * @param txBytes 发送字节数/秒
     * @param rxBytes 接收字节数/秒 (用于慢速检测)
     * @return 格式化的上传速度字符串
     */
    private fun txSpeed(settings: Settings, txBytes: Long, rxBytes: Long): String {
        val txKB = txBytes / 1024
        val rxKB = rxBytes / 1024

        // 根据慢速设置判断是否隐藏
        if (settings.hideWhenBothSlow &&
            (txKB <= settings.slowThreshold && rxKB <= settings.slowThreshold)) return ""
        if (settings.hideOnSlow && (txKB <= settings.slowThreshold)) return ""

        // 获取上传箭头
        val arrow = txArrow(settings, txKB)

        // 根据设置决定箭头位置 (前/后)
        return if (settings.positionIndicatorFront)
            arrow + formatBytes(settings, txBytes)
        else
            formatBytes(settings, txBytes) + arrow
    }

    /**
     * 格式化下载速度文本
     *
     * @param settings 用户设置
     * @param rxBytes 接收字节数/秒
     * @param txBytes 发送字节数/秒 (用于慢速检测)
     * @return 格式化的下载速度字符串
     */
    private fun rxSpeed(settings: Settings, rxBytes: Long, txBytes: Long): String {
        val rxKB = rxBytes / 1024
        val txKB = txBytes / 1024

        // 根据慢速设置判断是否隐藏
        if (settings.hideWhenBothSlow &&
            (rxKB <= settings.slowThreshold && txKB <= settings.slowThreshold)) return ""
        if (settings.hideOnSlow && (rxKB <= settings.slowThreshold)) return ""

        // 获取下载箭头
        val arrow = rxArrow(settings, rxKB)

        // 根据设置决定箭头位置 (前/后)
        return if (settings.positionIndicatorFront)
            arrow + formatBytes(settings, rxBytes)
        else
            formatBytes(settings, rxBytes) + arrow
    }

    /**
     * 生成上传箭头图标
     *
     * 根据上传速度和用户设置的指示器样式，生成对应的箭头符号
     * 部分样式支持根据速度快慢显示不同的箭头
     *
     * @param settings 用户设置
     * @param speedKB 上传速度 (KB/s)
     * @return 箭头字符串
     */
    private fun txArrow(settings: Settings, speedKB: Long): String = when (settings.iconIndicator) {
        1 -> if (speedKB < settings.slowThreshold) "△" else "▲"  // 空心/实心三角形
        2 -> if (speedKB < settings.slowThreshold) "▵ " else "▴ " // 白色/黑色三角形
        3 -> if (speedKB < settings.slowThreshold) "☖ " else "☗ " // 将棋符号
        4 -> "↑"  // 简单向上箭头
        5 -> "⇧"  // 双线向上箭头
        else -> ""  // 无箭头
    }

    /**
     * 生成下载箭头图标
     *
     * 根据下载速度和用户设置的指示器样式，生成对应的箭头符号
     * 部分样式支持根据速度快慢显示不同的箭头
     *
     * @param settings 用户设置
     * @param speedKB 下载速度 (KB/s)
     * @return 箭头字符串
     */
    private fun rxArrow(settings: Settings, speedKB: Long): String = when (settings.iconIndicator) {
        1 -> if (speedKB < settings.slowThreshold) "▽" else "▼"  // 空心/实心倒三角
        2 -> if (speedKB < settings.slowThreshold) "▿ " else "▾ " // 白色/黑色倒三角
        3 -> if (speedKB < settings.slowThreshold) "⛉ " else "⛊ " // 十字符号
        4 -> "↓"  // 简单向下箭头
        5 -> "⇩"  // 双线向下箭头
        else -> ""  // 无箭头
    }

    /**
     * 格式化字节数为人类可读的字符串
     *
     * 转换规则:
     * - 小于 1024 字节: 显示字节
     * - 1024 - 1024² 字节: 显示 KB
     * - 1024² - 1024³ 字节: 显示 MB
     * - 以此类推...
     *
     * 小数位数规则:
     * - 0: 显示为 "0"
     * - < 10: 保留 2 位小数 (如 9.87 MB)
     * - < 100: 保留 1 位小数 (如 98.7 MB)
     * - >= 100: 不保留小数 (如 987 MB)
     *
     * @param settings 用户设置
     * @param bytes 字节数
     * @return 格式化的字符串 (如 "1.23 MB/s")
     */
    private fun formatBytes(settings: Settings, bytes: Long): String {
        // 负数返回空字符串
        if (bytes < 0) return ""

        // 根据用户设置确定格式
        val space = if (settings.hideSpace) "" else " "  // 空格
        val baseUnit = if (settings.useUppercaseB) "B" else "b"  // B(字节) 或 b(比特)
        val perSecond = if (settings.hidePerSecond) "" else "/s"  // "/s" 后缀

        var size = bytes.toDouble()
        var index = 0  // SI 前缀索引

        // 转换为合适的单位
        if (bytes > 0) {
            while (size >= 1024 && index < SI_PREFIXES.size - 1) {
                size /= 1024
                index++
            }
        }

        // 获取 SI 前缀 (如 K, M, G)
        val prefix = SI_PREFIXES[index]

        // 根据大小确定小数位数
        val formattedNumber = when {
            size == 0.0 -> "0"
            index == 0 -> String.format(Locale.US, "%.0f", size)  // 字节不显示小数
            size < 10 -> String.format(Locale.US, "%.2f", size)    // < 10: 2位小数
            size < 100 -> String.format(Locale.US, "%.1f", size)   // < 100: 1位小数
            else -> String.format(Locale.US, "%.0f", size)         // >= 100: 无小数
        }

        // 组装单位字符串
        val unit = if (settings.hideBs) {
            prefix  // 只显示前缀 (如 "K", "M")
        } else {
            prefix + baseUnit + perSecond  // 完整单位 (如 "KB/s", "Mb/s")
        }

        // 返回最终格式化的字符串
        return formattedNumber + space + unit
    }
}
