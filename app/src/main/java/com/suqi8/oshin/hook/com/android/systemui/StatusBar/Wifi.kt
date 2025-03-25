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

class Wifi: YukiBaseHooker() {
    val StyleSelectedOption = prefs("systemui\\status_bar_wifi").getInt("StyleSelectedOption",0)
    val slow_speed_threshold = prefs("systemui\\status_bar_wifi").getInt("slow_speed_threshold",20)
    val hide_on_slow = prefs("systemui\\status_bar_wifi").getBoolean("hide_on_slow", false)
    val hide_when_both_slow = prefs("systemui\\status_bar_wifi").getBoolean("hide_when_both_slow", false)
    val icon_indicator = prefs("systemui\\status_bar_wifi").getInt("icon_indicator",0)

    @SuppressLint("SetTextI18n")
    override fun onHook() {
        loadApp("com.android.systemui"){
            "com.oplus.systemui.statusbar.phone.netspeed.widget.NetworkSpeedView".toClass().apply {
                method {
                    name = "updateNetworkSpeed"
                }.hook {
                    before {
                        instance<FrameLayout>().layoutParams.takeIf { it != null }?.width = LayoutParams.WRAP_CONTENT
                        if (StyleSelectedOption == 0) {
                            field {
                                name = "mSpeedNumber"
                                type = "android.widget.TextView"
                            }.get(instance).cast<TextView>()!!.apply {
                                if (prefs("systemui\\status_bar_wifi").getInt("speed_font_size",-1) != -1) setTextSize(TypedValue.COMPLEX_UNIT_DIP, prefs("systemui\\status_bar_wifi").getInt("speed_font_size",-1).toFloat())
                                //layoutParams.height = LayoutParams.WRAP_CONTENT
                                layoutParams.width = LayoutParams.WRAP_CONTENT
                            }
                            field {
                                name = "mSpeedUnit"
                                type = "android.widget.TextView"
                            }.get(instance).cast<TextView>()!!.apply {
                                if (prefs("systemui\\status_bar_wifi").getInt("unit_font_size",-1) != -1)setTextSize(TypedValue.COMPLEX_UNIT_DIP, prefs("systemui\\status_bar_wifi").getInt("unit_font_size",-1).toFloat())
                                //layoutParams.height = LayoutParams.WRAP_CONTENT
                                layoutParams.width = LayoutParams.WRAP_CONTENT
                            }
                        }
                        if (StyleSelectedOption == 1) {
                            val TotalByte = getCurrentSpeed()
                            field {
                                name = "mSpeedNumber"
                                type = "android.widget.TextView"
                            }.get(instance).cast<TextView>()!!.apply {
                                setTextSize(TypedValue.COMPLEX_UNIT_DIP, if (prefs("systemui\\status_bar_wifi").getInt("upload_font_size",-1) != -1) prefs("systemui\\status_bar_wifi").getInt("upload_font_size",-1).toFloat() else 8F)
                                //layoutParams.height = LayoutParams.WRAP_CONTENT
                                layoutParams.width = LayoutParams.WRAP_CONTENT
                                if (hide_on_slow && !hide_when_both_slow) visibility = if (TotalByte.second / 1024 <= slow_speed_threshold) View.INVISIBLE else View.VISIBLE
                                if (hide_when_both_slow) visibility = if ((TotalByte.second / 1024 <= slow_speed_threshold) && (TotalByte.first / 1024 <= slow_speed_threshold)) View.INVISIBLE else View.VISIBLE
                                var uptext = formatBytes(TotalByte.second)
                                uptext = uptext + txArrow(icon_indicator, TotalByte.second/1024, slow_speed_threshold)
                                text = uptext
                            }
                            val mSpeedUnit = field {
                                name = "mSpeedUnit"
                                type = "android.widget.TextView"
                            }.get(instance).cast<TextView>()
                            mSpeedUnit!!.apply {
                                setTextSize(TypedValue.COMPLEX_UNIT_DIP, if (prefs("systemui\\status_bar_wifi").getInt("download_font_size",-1) != -1) prefs("systemui\\status_bar_wifi").getInt("download_font_size",-1).toFloat() else 8F)
                                if (hide_on_slow && !hide_when_both_slow) visibility = if (TotalByte.first / 1024 <= slow_speed_threshold) View.INVISIBLE else View.VISIBLE
                                if (hide_when_both_slow) visibility = if ((TotalByte.second / 1024 <= slow_speed_threshold) && (TotalByte.first / 1024 <= slow_speed_threshold)) View.INVISIBLE else View.VISIBLE
                                //layoutParams.height = LayoutParams.WRAP_CONTENT
                                layoutParams.width = LayoutParams.WRAP_CONTENT
                                var uptext = formatBytes(TotalByte.first)
                                uptext = uptext + rxArrow(icon_indicator, TotalByte.first/1024, slow_speed_threshold)
                                text = uptext
                            }
                        }
                        resultNull()
                    }
                }
            }
        }
    }
    
    fun txArrow(icons: Int, Speed: Long, lowLevel: Int): String {
        return when (icons) {
            1 -> if (Speed < lowLevel) "△" else "▲"
            2 -> if (Speed < lowLevel) " ▵" else " ▴"
            3 -> if (Speed < lowLevel) " ☖" else " ☗"
            4 -> if (Speed < lowLevel) "↑" else "↑"
            5 -> if (Speed < lowLevel) "⇧" else "⇧"
            else -> ""
        }
    }

    fun rxArrow(icons: Int, Speed: Long, lowLevel: Int): String {
        return when (icons) {
            1 -> if (Speed < lowLevel) "▽" else "▼"
            2 -> if (Speed < lowLevel) " ▿" else " ▾"
            3 -> if (Speed < lowLevel) " ⛉" else " ⛊"
            4 -> if (Speed < lowLevel) "↓" else "↓"
            5 -> if (Speed < lowLevel) "⇩" else "⇩"
            else -> ""
        }
    }

    data class NetStat(val rxBytes: Long, val txBytes: Long, val timestamp: Long)

    fun getNetStat(): NetStat {
        val totalRx = TrafficStats.getTotalRxBytes() - TrafficStats.getRxBytes("lo")
        val totalTx = TrafficStats.getTotalTxBytes() - TrafficStats.getTxBytes("lo")
        return NetStat(totalRx, totalTx, SystemClock.elapsedRealtimeNanos())
    }

    private var lastStat: NetStat? = null
    private var lastTime = 0L
    private var lastRxSpeed = 0L
    private var lastTxSpeed = 0L

    // 返回 Pair<rxSpeed(B/s), txSpeed(B/s)>
    fun getCurrentSpeed(): Pair<Long, Long> {
        val currentStat = getNetStat()
        if (lastTime != Instant.now().epochSecond) {
            lastTime = Instant.now().epochSecond
            lastStat?.let { prev ->
                val timeDelta = (currentStat.timestamp - prev.timestamp) / 1e9 // 转换为秒
                if (timeDelta > 0) {
                    val rxSpeed = ((currentStat.rxBytes - prev.rxBytes) / timeDelta).toLong()
                    val txSpeed = ((currentStat.txBytes - prev.txBytes) / timeDelta).toLong()
                    lastStat = currentStat
                    lastRxSpeed = rxSpeed.coerceAtLeast(0)
                    lastTxSpeed = txSpeed.coerceAtLeast(0)
                    return Pair(rxSpeed.coerceAtLeast(0),txSpeed.coerceAtLeast(0))
                }
            }
        } else {
            return Pair(lastRxSpeed,lastTxSpeed)
        }
        lastStat = currentStat
        return Pair(0, 0)
    }

    fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0 B"

        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        var size = bytes.toDouble()
        var unitIndex = 0

        while (size >= 1024 && unitIndex < units.size - 1) {
            size /= 1024.0
            unitIndex++
        }

        return when {
            unitIndex == 0 -> "${size.toLong()} ${units[unitIndex]}"        // 字节不保留小数
            size < 10 -> "%.2f ${units[unitIndex]}".format(size)            // 小于10保留两位小数
            size < 100 -> "%.1f ${units[unitIndex]}".format(size)           // 小于100保留一位小数
            else -> "%.0f ${units[unitIndex]}".format(size)                  // 整数显示
        }
    }
}
