package io.github.suqi8.opatch.hook.appilcations

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.provider.Settings
import android.text.TextUtils.replace
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.highcapable.yukihookapi.hook.core.annotation.LegacyHookApi
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.prefs
import com.highcapable.yukihookapi.hook.type.java.CharSequenceType
import com.highcapable.yukihookapi.hook.type.java.IntClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Timer
import java.util.TimerTask

class StatusBarClock : YukiBaseHooker() {
    val ClockStyleSelectedOption = prefs("settings").getInt("ClockStyleSelectedOption", 0)
    val ShowYears = prefs("settings").getBoolean("Status_Bar_Time_ShowYears", false)
    val ShowMonth = prefs("settings").getBoolean("Status_Bar_Time_ShowMonth", false)
    val ShowDay = prefs("settings").getBoolean("Status_Bar_Time_ShowDay", false)
    val ShowWeek = prefs("settings").getBoolean("Status_Bar_Time_ShowWeek", false)
    val ShowCNHour = prefs("settings").getBoolean("Status_Bar_Time_ShowCNHour", false)
    val Showtime_period = prefs("settings").getBoolean("Status_Bar_Time_Showtime_period", false)
    val ShowSeconds = prefs("settings").getBoolean("Status_Bar_Time_ShowSeconds", false)
    val ShowMillisecond = prefs("settings").getBoolean("Status_Bar_Time_ShowMillisecond", false)
    val HideSpace = prefs("settings").getBoolean("Status_Bar_Time_HideSpace", false)
    val DualRow = prefs("settings").getBoolean("Status_Bar_Time_DualRow", false)
    var nowTime: Date? = null
    var FontSize = prefs("settings").getInt("Status_Bar_Time_ClockSize", 0)
    var updateSpeed = prefs("settings").getInt("Status_Bar_Time_ClockUpdateSpeed", 0)
    var newline = ""
    var customClockStyle = prefs("settings").getString("Status_Bar_Time_CustomClockStyle", "HH:mm")
    var customAlignment = prefs("settings").getInt("Status_Bar_Time_alignment", 0)
    var ClockLeftPadding = prefs("settings").getInt("Status_Bar_Time_LeftPadding", 0)
    var ClockRightPadding = prefs("settings").getInt("Status_Bar_Time_RightPadding", 0)
    var ClockTopPadding = prefs("settings").getInt("Status_Bar_Time_TopPadding", 0)
    var ClockBottomPadding = prefs("settings").getInt("Status_Bar_Time_BottomPadding", 0)

    @SuppressLint("SetTextI18n")
    @OptIn(LegacyHookApi::class)
    override fun onHook() {
        var context: Context? = null
        "com.android.systemui.statusbar.policy.Clock".toClass().apply {
            hook {
                if (ClockStyleSelectedOption == 0) {
                    injectMember {
                        constructor {
                            paramCount = 3
                        }
                        afterHook {
                            context = args(0).cast<Context>()
                            val clockView = instance<TextView>()
                            clockView.apply {
                                if (this.resources.getResourceEntryName(id) != "clock") return@afterHook
                                isSingleLine = false
                                gravity = Gravity.CENTER
                                setPadding(if (ClockLeftPadding!=0) ClockLeftPadding else paddingLeft,
                                    if (ClockTopPadding!=0) ClockTopPadding else paddingTop,
                                    if (ClockRightPadding!=0) ClockRightPadding else paddingRight,
                                    if (ClockBottomPadding!=0) ClockBottomPadding else paddingBottom)

                                if (DualRow) {
                                    newline = "\n"
                                    var defaultSize = 8F
                                    if (FontSize != 0) defaultSize = FontSize.toFloat()
                                    setTextSize(TypedValue.COMPLEX_UNIT_DIP, defaultSize)
                                    setLineSpacing(0F, 0.8F)
                                } else {
                                    if (FontSize != 0) {
                                        setTextSize(
                                            TypedValue.COMPLEX_UNIT_DIP,
                                            FontSize.toFloat()
                                        )
                                    }
                                }
                            }

                            if (updateSpeed != 0) {
                                // 获取 updateClock 方法并设置定时器
                                val updateClockMethod: Method = clockView.javaClass.superclass.getDeclaredMethod("updateClock")
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
                                Timer().schedule(CustomTimerTask(), 1000 - System.currentTimeMillis() % 1000, updateSpeed.toLong())
                            }
                        }
                    }

                    // Hook getSmallTime 方法自定义显示格式
                    injectMember {
                        method {
                            name = "getSmallTime"
                            returnType = CharSequenceType
                        }
                        afterHook {
                            instance<TextView>().apply {
                                if (this.resources.getResourceEntryName(id) != "clock") return@afterHook
                            }
                            nowTime = Calendar.getInstance().time
                            result = getDate(context!!) + newline + getTime(context!!)
                        }
                    }

                } else {
                    // 其他样式选择逻辑
                    injectMember {
                        constructor {
                            paramCount = 3
                        }
                        afterHook {
                            context = args(0).cast<Context>()
                            val clockView = instance<TextView>()
                            clockView.apply {
                                setPadding(if (ClockLeftPadding!=0) ClockLeftPadding else paddingLeft,
                                    if (ClockTopPadding!=0) ClockTopPadding else paddingTop,
                                    if (ClockRightPadding!=0) ClockRightPadding else paddingRight,
                                    if (ClockBottomPadding!=0) ClockBottomPadding else paddingBottom)

                                isSingleLine = false
                                if (this.resources.getResourceEntryName(id) != "clock") return@afterHook
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

                                if (FontSize != 0) {
                                    setTextSize(
                                        TypedValue.COMPLEX_UNIT_DIP,
                                        FontSize.toFloat()
                                    )
                                }
                            }

                            if (updateSpeed != 0) {
                                // 获取 updateClock 方法并设置定时器
                                val updateClockMethod: Method = clockView.javaClass.superclass.getDeclaredMethod("updateClock")
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
                                Timer().schedule(CustomTimerTask(), 1000 - System.currentTimeMillis() % 1000, updateSpeed.toLong())
                            }
                        }
                    }
                    injectMember {
                        method {
                            name = "getSmallTime"
                            returnType = CharSequenceType
                        }
                        afterHook {
                            instance<TextView>().apply {
                                if (this.resources.getResourceEntryName(id) != "clock") return@afterHook
                            }
                            nowTime = Calendar.getInstance().time
                            result = getCustomDate(context!!, customClockStyle).replace("\\n", "\n")
                        }
                    }
                    /*injectMember {
                        method {
                            name = "onAttachedToWindow"
                        }

                        beforeHook {
                            // 获取 Clock（时间显示的 TextView 控件）
                            val clockTextView = instance as TextView

                            // 获取状态栏父布局
                            val parentViewGroup = clockTextView.parent as LinearLayout
                            var parentLayout = parentViewGroup.parent as ViewGroup

                            // 获取 Clock 的索引
                            val clockIndex = parentViewGroup.indexOfChild(clockTextView)

                            // 获取状态栏的背景颜色
                            val statusBarColor = (clockTextView.context as Activity).window.decorView.rootView.background
                            val color = (statusBarColor as ColorDrawable).color

                            // 创建一个新的 TextView 控件
                            val newTextView = TextView(clockTextView.context).apply {
                                text = "文本" + clockIndex
                                textSize = 12f
                                // 设置反色
                                setTextColor(if (Color.red(color) + Color.green(color) + Color.blue(color) < 382) Color.WHITE else Color.BLACK)
                            }

                            // 在 Clock 后面插入新控件
                            parentViewGroup.addView(newTextView)
                            clockTextView.post {
                                newTextView.x = clockTextView.x + clockTextView.width + 8 // 添加8个像素的间距
                            }
                        }
                        *//*beforeHook {
                            val clockView = instance<View>() // 获取时间View
                            val parentLayout = clockView.parent as LinearLayout // 获取父布局

                            // 检查是否已经添加了自定义 TextView，避免重复添加
                            val customTextView = parentLayout.findViewWithTag<TextView>("customText")

                            if (customTextView == null) {
                                // 创建新的 TextView

                                // 获取通知图标的索引，假设通知图标在时间的后面
                                val clockIndex = parentLayout.indexOfChild(clockView)
                                var notificationIconIndex = -1

                                // 遍历子布局，找到通知图标的位置
                                for (i in clockIndex + 1 until parentLayout.childCount) {
                                    val child = parentLayout.getChildAt(i)
                                    if (child is ImageView) { // 假设通知图标是ImageView
                                        notificationIconIndex = i
                                        break
                                    }
                                }
                                val newTextView = TextView(clockView.context).apply {
                                    text = "新的文本"+notificationIconIndex // 自定义文本
                                    textSize = 13f
                                    tag = "customText" // 用标签标识
                                    layoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    ).apply {
                                        marginStart = 8 // 与时间的距离
                                    }
                                }
                                // 如果找到通知图标，将TextView插入到时间后面、通知图标前面
                                parentLayout.addView(newTextView, notificationIconIndex)
                            }
                        }*//*
                    }*/
                }
            }
        }
        "com.android.systemui.statusbar.phone.PhoneStatusBarView".toClass().apply {
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
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCustomDate(context: Context,format: String): String {
        return SimpleDateFormat(format).format(nowTime!!)
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
