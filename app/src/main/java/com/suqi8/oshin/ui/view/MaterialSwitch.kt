@file:Suppress("SameParameterValue")

package com.suqi8.oshin.ui.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.SwitchCompat
import com.suqi8.oshin.utils.factory.dp
import com.suqi8.oshin.utils.factory.isSystemInDarkMode
import top.defaults.drawabletoolbox.DrawableBuilder

class MaterialSwitch(context: Context, attrs: AttributeSet?) : SwitchCompat(context, attrs) {

    private fun trackColors(selected: Int, pressed: Int, normal: Int): ColorStateList {
        val colors = intArrayOf(selected, pressed, normal)
        val states = arrayOfNulls<IntArray>(3)
        states[0] = intArrayOf(android.R.attr.state_checked)
        states[1] = intArrayOf(android.R.attr.state_pressed)
        states[2] = intArrayOf()
        return ColorStateList(states, colors)
    }

    private val thumbColor get() = if (context.isSystemInDarkMode) 0xFF7C7C7C else 0xFFCCCCCC

    init {
        trackDrawable = DrawableBuilder()
            .rectangle()
            .rounded()
            .solidColor(0xFF656565.toInt())
            .height(20.dp(context))
            .cornerRadius(15.dp(context))
            .build()
        thumbDrawable = DrawableBuilder()
            .rectangle()
            .rounded()
            .solidColor(Color.WHITE)
            .size(20.dp(context), 20.dp(context))
            .cornerRadius(20.dp(context))
            .strokeWidth(8.dp(context))
            .strokeColor(Color.TRANSPARENT)
            .build()
        trackTintList = trackColors(
            0xFF656565.toInt(),
            thumbColor.toInt(),
            thumbColor.toInt()
        )
        isSingleLine = true
        ellipsize = TextUtils.TruncateAt.END
    }
}
