package com.suqi8.oshin.hook.appilcations


import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.provider.Settings
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.IntType
import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Timer
import java.util.TimerTask


class StatusBarClock : YukiBaseHooker() {
    val ClockStyleSelectedOption = prefs("systemui\\status_bar_clock").getInt("ClockStyleSelectedOption", 0)
    val ShowYears = prefs("systemui\\status_bar_clock").getBoolean("ShowYears", false)
    val ShowMonth = prefs("systemui\\status_bar_clock").getBoolean("ShowMonth", false)
    val ShowDay = prefs("systemui\\status_bar_clock").getBoolean("ShowDay", false)
    val ShowWeek = prefs("systemui\\status_bar_clock").getBoolean("ShowWeek", false)
    val ShowCNHour = prefs("systemui\\status_bar_clock").getBoolean("ShowCNHour", false)
    val Showtime_period = prefs("systemui\\status_bar_clock").getBoolean("Showtime_period", false)
    val ShowSeconds = prefs("systemui\\status_bar_clock").getBoolean("ShowSeconds", false)
    val ShowMillisecond = prefs("systemui\\status_bar_clock").getBoolean("ShowMillisecond", false)
    val HideSpace = prefs("systemui\\status_bar_clock").getBoolean("HideSpace", false)
    val DualRow = prefs("systemui\\status_bar_clock").getBoolean("DualRow", false)
    var nowTime: Date? = null
    var FontSize = prefs("systemui\\status_bar_clock").getFloat("ClockSize", 0f)
    //var updateSpeed = prefs("settings").getInt("Status_Bar_Time_ClockUpdateSpeed", 0)
    var updateSpeed = prefs("systemui\\status_bar_clock").getInt("ClockUpdateSpeed", 0)
    var newline = ""
    var customClockStyle = prefs("systemui\\status_bar_clock").getString("CustomClockStyle", "HH:mm")
    var customAlignment = prefs("systemui\\status_bar_clock").getInt("alignment", 0)
    var ClockLeftPadding = prefs("systemui\\status_bar_clock").getInt("LeftPadding", 0)
    var ClockRightPadding = prefs("systemui\\status_bar_clock").getInt("RightPadding", 0)
    var ClockTopPadding = prefs("systemui\\status_bar_clock").getInt("TopPadding", 0)
    var ClockBottomPadding = prefs("systemui\\status_bar_clock").getInt("BottomPadding", 0)
    var ClockColor = prefs("systemui\\status_bar_clock").getString("Color", "null")

    @SuppressLint("SetTextI18n", "ResourceType")
    override fun onHook() {
        var context: Context? = null
        "com.android.systemui.statusbar.policy.Clock".toClass().apply {
            if (ClockStyleSelectedOption == 0) {
                constructor {
                    param("android.content.Context", "android.util.AttributeSet", IntType)
                }.hook {
                    after {
                        context = args(0).cast()
                        val clockView = instance<TextView>()
                        clockView.apply {
                            if (this.resources.getResourceEntryName(id) != "clock") return@after
                            isSingleLine = false
                            gravity = Gravity.CENTER
                            setPadding(
                                if (ClockLeftPadding != 0) ClockLeftPadding else paddingLeft,
                                if (ClockTopPadding != 0) ClockTopPadding else paddingTop,
                                if (ClockRightPadding != 0) ClockRightPadding else paddingRight,
                                if (ClockBottomPadding != 0) ClockBottomPadding else paddingBottom
                            )

                            if (DualRow) {
                                newline = "\n"
                                var defaultSize = 8F
                                if (FontSize != 0f) defaultSize = FontSize
                                setTextSize(TypedValue.COMPLEX_UNIT_DIP, defaultSize)
                                setLineSpacing(0F, 0.8F)
                            } else {
                                if (FontSize != 0f) {
                                    setTextSize(
                                        TypedValue.COMPLEX_UNIT_DIP,
                                        FontSize.toFloat()
                                    )
                                }
                            }

                        }

                        if (updateSpeed != 0) {
                            // 获取 updateClock 方法并设置定时器
                            val updateClockMethod: Method =
                                clockView.javaClass.superclass.getDeclaredMethod("updateClock")
                            val runnable = Runnable {
                                updateClockMethod.isAccessible = true
                                updateClockMethod.invoke(clockView)
                            }

                            // 自定义定时器任务
                            class CustomTimerTask : TimerTask() {
                                override fun run() {
                                    Handler(clockView.context.mainLooper).post(runnable)
                                }
                            }

                            // 使用 Timer 控制刷新频率，改为 500 毫秒
                            Timer().schedule(
                                CustomTimerTask(),
                                1000 - System.currentTimeMillis() % 1000,
                                updateSpeed.toLong()
                            )
                        }
                    }
                }

                "com.android.systemui.statusbar.policy.Clock".toClass().apply {
                    method {
                        name = "getSmallTime"
                        emptyParam()
                        returnType = "java.lang.CharSequence"
                    }.hook {
                        after {
                            instance<TextView>().apply {
                                if (this.resources.getResourceEntryName(id) != "clock") return@after
                            }
                            nowTime = Calendar.getInstance().time
                            result = getDate(context!!) + newline + getTime(context!!)
                        }
                    }
                }
            } else {
                constructor {
                    param("android.content.Context", "android.util.AttributeSet", IntType)
                }.hook {
                    after {
                        context = args(0).cast<Context>()
                        val clockView = instance<TextView>()
                        clockView.apply {
                            setPadding(
                                if (ClockLeftPadding != 0) ClockLeftPadding else paddingLeft,
                                if (ClockTopPadding != 0) ClockTopPadding else paddingTop,
                                if (ClockRightPadding != 0) ClockRightPadding else paddingRight,
                                if (ClockBottomPadding != 0) ClockBottomPadding else paddingBottom
                            )

                            isSingleLine = false
                            if (this.resources.getResourceEntryName(id) != "clock") return@after
                            gravity = when (customAlignment) {
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

                            if (FontSize != 0f) {
                                setTextSize(
                                    TypedValue.COMPLEX_UNIT_DIP,
                                    FontSize.toFloat()
                                )
                            }
                        }

                        if (updateSpeed != 0) {
                            // 获取 updateClock 方法并设置定时器
                            val updateClockMethod: Method =
                                clockView.javaClass.superclass.getDeclaredMethod("updateClock")
                            val runnable = Runnable {
                                updateClockMethod.isAccessible = true
                                updateClockMethod.invoke(clockView)
                            }

                            // 自定义定时器任务
                            class CustomTimerTask : TimerTask() {
                                override fun run() {
                                    Handler(clockView.context.mainLooper).post(runnable)
                                }
                            }

                            // 使用 Timer 控制刷新频率，改为 500 毫秒
                            Timer().schedule(
                                CustomTimerTask(),
                                1000 - System.currentTimeMillis() % 1000,
                                updateSpeed.toLong()
                            )
                        }
                    }
                }
                "com.android.systemui.statusbar.policy.Clock".toClass().apply {
                    method {
                        name = "getSmallTime"
                        emptyParam()
                        returnType = "java.lang.CharSequence"
                    }.hook {
                        after {
                            instance<TextView>().apply {
                                if (this.resources.getResourceEntryName(id) != "clock") return@after
                            }
                            nowTime = Calendar.getInstance().time
                            result = getCustomDate(customClockStyle)//.replace("\\n", "\n")
                        }
                    }
                }
            }
        }
        /*"com.android.systemui.statusbar.phone.PhoneStatusBarView".toClass().apply {
            hook {
                injectMember {
                    method {
                        name = "onCreate" // 钩住创建方法
                    }
                    afterHook {
                        // 获取状态栏视图的 ID
                        val statusBarView = instance<ViewGroup>().findViewById<View>(Resources.getSystem().getIdentifier("status_bar", "id", "android"))
                        // 你可以在这里对状态栏进行其他操作
                        if (statusBarView != null) {
                            statusBarView.setPadding(100,0,100,0)
                        }
                    }
                }
            }
        }*/
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCustomDate(format: String): String {
        return SimpleDateFormat(format).format(nowTime)
    }


    @SuppressLint("SimpleDateFormat")
    private fun getDate(context: Context): String {
        var dateFormat = ""
        if (isZh(context)) {
            if (ShowYears) dateFormat += "YY年"
            if (ShowMonth) dateFormat += "M月"
            if (ShowDay) dateFormat += "d日"
            if (ShowWeek) dateFormat += "E"
            if (!HideSpace && !DualRow) dateFormat += " "
        } else {
            if (ShowYears) {
                dateFormat += "YY"
                if (ShowMonth || ShowDay) dateFormat += "/"
            }
            if (ShowMonth) {
                dateFormat += "M"
                if (ShowDay) dateFormat += "/"
            }
            if (ShowDay) dateFormat += "d"
            if (!HideSpace && !DualRow) dateFormat += " "
            if (ShowWeek) dateFormat += "E"
            if (!HideSpace && !DualRow) dateFormat += " "
        }
        return SimpleDateFormat(dateFormat).format(nowTime!!)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getTime(context: Context): String {
        var timeFormat = ""
        timeFormat += if (is24(context)) "HH:mm" else "hh:mm"
        if (ShowSeconds) timeFormat += ":ss"
        if (ShowMillisecond) timeFormat += ":SSS"
        timeFormat = SimpleDateFormat(timeFormat).format(nowTime!!)
        if (isZh(context)) timeFormat =
            getPeriod(context) + timeFormat else timeFormat += getPeriod(context)
        timeFormat = getDoubleHour() + timeFormat
        return timeFormat
    }

    @SuppressLint("SimpleDateFormat")
    fun getDoubleHour(): String {
        var doubleHour = ""
        if (ShowCNHour) {
            when (SimpleDateFormat("HH").format(nowTime!!)) {
                "23", "00" -> {
                    doubleHour = "子时"
                }

                "01", "02" -> {
                    doubleHour = "丑时"
                }

                "03", "04" -> {
                    doubleHour = "寅时"
                }

                "05", "06" -> {
                    doubleHour = "卯时"
                }

                "07", "08" -> {
                    doubleHour = "辰时"
                }

                "09", "10" -> {
                    doubleHour = "巳时"
                }

                "11", "12" -> {
                    doubleHour = "午时"
                }

                "13", "14" -> {
                    doubleHour = "未时"
                }

                "15", "16" -> {
                    doubleHour = "申时"
                }

                "17", "18" -> {
                    doubleHour = "酉时"
                }

                "19", "20" -> {
                    doubleHour = "戌时"
                }

                "21", "22" -> {
                    doubleHour = "亥时"
                }
            }
            if (!HideSpace) doubleHour = "$doubleHour "
        }
        return doubleHour
    }

    @SuppressLint("SimpleDateFormat")
    private fun getPeriod(context: Context): String {
        var period = ""
        if (Showtime_period) {
            if (isZh(context)) {
                when (SimpleDateFormat("HH").format(nowTime!!)) {
                    "00", "01", "02", "03", "04", "05" -> {
                        period = "凌晨"
                    }

                    "06", "07", "08", "09", "10", "11" -> {
                        period = "上午"
                    }

                    "12" -> {
                        period = "中午"
                    }

                    "13", "14", "15", "16", "17" -> {
                        period = "下午"
                    }

                    "18" -> {
                        period = "傍晚"
                    }

                    "19", "20", "21", "22", "23" -> {
                        period = "晚上"
                    }
                }
                if (!HideSpace) period += " "
            } else {
                period = " " + SimpleDateFormat("a").format(nowTime!!)
            }
        }
        return period
    }

    private fun isZh(context: Context): Boolean {
        val locale = context.resources.configuration.locales[0]
        val language = locale.language
        return language.endsWith("zh")
    }

    private fun is24(context: Context): Boolean {
        val t = Settings.System.getString(context.contentResolver, Settings.System.TIME_12_24)
        return t == "24"
    }
}
