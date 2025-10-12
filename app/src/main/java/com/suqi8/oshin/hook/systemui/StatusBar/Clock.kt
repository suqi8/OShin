package com.suqi8.oshin.hook.systemui.StatusBar

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.provider.Settings
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import com.highcapable.kavaref.KavaRef
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Timer
import java.util.TimerTask

class Clock: YukiBaseHooker() {

    // --- 配置读取区 (使用 lazy 延迟初始化, 仅在首次使用时读取一次) ---
    private val statusBarPrefs by lazy { prefs("systemui\\status_bar_clock") }
    private val clockEnabled by lazy { statusBarPrefs.getBoolean("status_bar_clock", false) }
    private val clockStyleSelectedOption by lazy { statusBarPrefs.getInt("ClockStyleSelectedOption", 0) }
    private val showYears by lazy { statusBarPrefs.getBoolean("ShowYears", false) }
    private val showMonth by lazy { statusBarPrefs.getBoolean("ShowMonth", false) }
    private val showDay by lazy { statusBarPrefs.getBoolean("ShowDay", false) }
    private val showWeek by lazy { statusBarPrefs.getBoolean("ShowWeek", false) }
    private val showCNHour by lazy { statusBarPrefs.getBoolean("ShowCNHour", false) }
    private val showtimePeriod by lazy { statusBarPrefs.getBoolean("Showtime_period", false) }
    private val showSeconds by lazy { statusBarPrefs.getBoolean("ShowSeconds", true) }
    private val showMillisecond by lazy { statusBarPrefs.getBoolean("ShowMillisecond", false) }
    private val hideSpace by lazy { statusBarPrefs.getBoolean("HideSpace", false) }
    private val dualRow by lazy { statusBarPrefs.getBoolean("DualRow", false) }
    private val fontSize by lazy { statusBarPrefs.getFloat("ClockSize", 0f) }
    private val updateSpeed by lazy { statusBarPrefs.getFloat("ClockUpdateSpeed", 0f) }
    private val customClockStyle by lazy { statusBarPrefs.getString("CustomClockStyle", "HH:mm") }
    private val customAlignment by lazy { statusBarPrefs.getInt("alignment", 0) }
    private val clockLeftPadding by lazy { statusBarPrefs.getFloat("LeftPadding", 0f) }
    private val clockRightPadding by lazy { statusBarPrefs.getFloat("RightPadding", 0f) }
    private val clockTopPadding by lazy { statusBarPrefs.getFloat("TopPadding", 0f) }
    private val clockBottomPadding by lazy { statusBarPrefs.getFloat("BottomPadding", 0f) }
    //private val clockColor by lazy { statusBarPrefs.getString("Color", "null") }

    // --- 工具变量区 ---
    private lateinit var hookContext: Context
    private val sdfCache = mutableMapOf<String, SimpleDateFormat>()
    private fun getFormatter(pattern: String): SimpleDateFormat =
        sdfCache.getOrPut(pattern) { SimpleDateFormat(pattern) }

    @SuppressLint("SetTextI18n")
    override fun onHook() {
        if (!clockEnabled) return

        loadApp("com.android.systemui") {
            val kavaRef = "com.android.systemui.statusbar.policy.Clock".toClass().resolve()
            hookConstructor(kavaRef)
            hookGetSmallTime(kavaRef)
        }
    }

    /** Hook 构造函数，用于初始化和设置视图 */
    private fun hookConstructor(kavaRef: KavaRef.MemberScope<Any>) = kavaRef.apply {
        firstConstructor {
            modifiers(Modifiers.PUBLIC)
            parameters("android.content.Context", "android.util.AttributeSet", Int::class)
        }.hook {
            after {
                hookContext = args(0).cast<Context>()!!
                setupClockView(instance())
            }
        }
    }

    /** Hook 时间文本的获取方法 */
    private fun hookGetSmallTime(kavaRef: KavaRef.MemberScope<Any>) = kavaRef.apply {
        firstMethod {
            modifiers(Modifiers.PRIVATE, Modifiers.FINAL)
            name = "getSmallTime"
            emptyParameters()
            returnType = "java.lang.CharSequence"
        }.hook {
            after {
                val now = Calendar.getInstance().time
                instance<TextView>().apply {
                    if (this.resources.getResourceEntryName(id) != "clock") return@after
                }
                result = if (clockStyleSelectedOption == 0) {
                    val dateStr = getDate(now)
                    val timeStr = getTime(now)
                    val newline = if (dualRow) "\n" else ""
                    dateStr + newline + timeStr
                } else {
                    getCustomDate(now, customClockStyle)
                }
            }
        }
    }

    /** 统一设置 Clock View 的外观和行为 */
    @SuppressLint("SetTextI18n")
    private fun setupClockView(clockView: TextView) {
        clockView.apply {
            if (this.resources.getResourceEntryName(id) != "clock") return@apply
            isSingleLine = false
            gravity = when (customAlignment) {
                0 -> Gravity.CENTER
                1 -> Gravity.TOP
                2 -> Gravity.BOTTOM
                3 -> Gravity.START
                4 -> Gravity.END
                5 -> Gravity.CENTER_HORIZONTAL
                6 -> Gravity.CENTER_VERTICAL
                7 -> Gravity.FILL
                8 -> Gravity.FILL_HORIZONTAL
                9 -> Gravity.FILL_VERTICAL
                else -> Gravity.CENTER
            }
            fun dp(value: Float): Int =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, resources.displayMetrics).toInt()

            setPadding(
                if (clockLeftPadding != 0f) dp(clockLeftPadding) else paddingLeft,
                if (clockTopPadding != 0f) dp(clockTopPadding) else paddingTop,
                if (clockRightPadding != 0f) dp(clockRightPadding) else paddingRight,
                if (clockBottomPadding != 0f) dp(clockBottomPadding) else paddingBottom
            )
            if (dualRow) {
                val defaultSize = if (fontSize != 0f) fontSize else 8F
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, defaultSize)
                setLineSpacing(0F, 0.8F)
            } else {
                if (fontSize != 0f) setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize)
            }
        }

        if (updateSpeed > 0f) {
            setupHighFrequencyUpdate(clockView)
        }
    }

    private fun setupHighFrequencyUpdate(clockView: TextView) {
        val updateClockMethod: Method = clockView.javaClass.superclass.getDeclaredMethod("updateClock")
        updateClockMethod.isAccessible = true
        val runnable = Runnable { updateClockMethod.invoke(clockView) }

        class CustomTimerTask : TimerTask() {
            private val handler = Handler(clockView.context.mainLooper)
            override fun run() { handler.post(runnable) }
        }

        Timer().schedule(
            CustomTimerTask(),
            1000 - System.currentTimeMillis() % 1000,
            updateSpeed.toLong()
        )
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCustomDate(now: Date, format: String): String = getFormatter(format).format(now)

    @SuppressLint("SimpleDateFormat")
    private fun getDate(now: Date): String {
        var dateFormat = ""
        if (isZh(hookContext)) {
            if (showYears) dateFormat += "yy年"
            if (showMonth) dateFormat += "M月"
            if (showDay) dateFormat += "d日"
            if (showWeek) dateFormat += "E"
            if (!hideSpace && !dualRow) dateFormat += " "
        } else {
            if (showYears) {
                dateFormat += "yy"
                if (showMonth || showDay) dateFormat += "/"
            }
            if (showMonth) {
                dateFormat += "M"
                if (showDay) dateFormat += "/"
            }
            if (showDay) dateFormat += "d"
            if (showWeek) dateFormat += " E"
            if (!hideSpace && !dualRow) dateFormat += " "
        }
        return if (dateFormat.trim().isNotEmpty()) getFormatter(dateFormat).format(now) else ""
    }

    @SuppressLint("SimpleDateFormat")
    private fun getTime(now: Date): String {
        var timeFormatPattern = if (is24(hookContext)) "HH:mm" else "hh:mm"
        if (showSeconds) timeFormatPattern += ":ss"
        if (showMillisecond) timeFormatPattern += ".SSS"

        var timeFormat = getFormatter(timeFormatPattern).format(now)
        timeFormat = if (isZh(hookContext)) getPeriod(now) + timeFormat else timeFormat + getPeriod(now)
        timeFormat = getDoubleHour(now) + timeFormat
        return timeFormat
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDoubleHour(now: Date): String {
        var doubleHour = ""
        if (showCNHour) {
            when (getFormatter("HH").format(now)) {
                "23", "00" -> doubleHour = "子时"
                "01", "02" -> doubleHour = "丑时"
                "03", "04" -> doubleHour = "寅时"
                "05", "06" -> doubleHour = "卯时"
                "07", "08" -> doubleHour = "辰时"
                "09", "10" -> doubleHour = "巳时"
                "11", "12" -> doubleHour = "午时"
                "13", "14" -> doubleHour = "未时"
                "15", "16" -> doubleHour = "申时"
                "17", "18" -> doubleHour = "酉时"
                "19", "20" -> doubleHour = "戌时"
                "21", "22" -> doubleHour = "亥时"
            }
            if (!hideSpace) doubleHour += " "
        }
        return doubleHour
    }

    @SuppressLint("SimpleDateFormat")
    private fun getPeriod(now: Date): String {
        var period = ""
        if (showtimePeriod) {
            if (isZh(hookContext)) {
                when (getFormatter("HH").format(now)) {
                    in "00".."05" -> period = "凌晨"
                    in "06".."11" -> period = "上午"
                    "12" -> period = "中午"
                    in "13".."17" -> period = "下午"
                    "18" -> period = "傍晚"
                    in "19".."23" -> period = "晚上"
                }
                if (!hideSpace) period += " "
            } else {
                period = " " + getFormatter("a").format(now)
            }
        }
        return period
    }

    private fun isZh(context: Context): Boolean {
        val locale = context.resources.configuration.locales[0]
        return locale.language.endsWith("zh")
    }

    private fun is24(context: Context): Boolean = Settings.System.getString(context.contentResolver, Settings.System.TIME_12_24) == "24"
}
