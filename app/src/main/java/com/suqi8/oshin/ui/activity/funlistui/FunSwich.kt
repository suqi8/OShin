package com.suqi8.oshin.ui.activity.funlistui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.LocalColorMode
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

// FunSwich 作为顶层调用者，用于处理数据持久化
@Composable
fun FunSwich(title: String, summary: String? = null, category: String, key: String, defValue: Boolean = false, onCheckedChange: ((Boolean) -> Unit)? = null) {
    val context = LocalContext.current
    val isChecked = remember { mutableStateOf(context.prefs(category).getBoolean(key, defValue)) }
    SuperSwitch(
        title = title,
        checked = isChecked.value,
        onCheckedChange = {
            context.prefs(category).edit { putBoolean(key, it) }
            isChecked.value = it
            onCheckedChange?.invoke(it)
        },
        summary = summary
    )
}

/**
 * 一个带有标题和摘要的开关组件。
 * 整个组件作为单个可点击区域，用于切换开关状态。
 */
@Composable
fun SuperSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    title: String,
    modifier: Modifier = Modifier,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    leftAction: @Composable (() -> Unit)? = null,
    rightActions: @Composable RowScope.() -> Unit = {},
    switchColors: SwitchColors = SwitchDefaults.switchColors(),
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true
) {
    BasicComponent(
        modifier = modifier,
        insideMargin = insideMargin,
        title = title,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        leftAction = leftAction,
        rightActions = {
            SuperSwitchRightActions(
                rightActions = rightActions,
                checked = checked,
                enabled = enabled,
                switchColors = switchColors
            )
        },
        // 点击事件由 BasicComponent (即整个行) 处理，实现一体化交互
        onClick = {
            if (enabled) {
                onClick?.invoke()
                onCheckedChange?.invoke(!checked)
            }
        },
        enabled = enabled
    )
}

/**
 * 仅用于布局的私有辅助函数。
 */
@Composable
private fun RowScope.SuperSwitchRightActions(
    rightActions: @Composable RowScope.() -> Unit,
    checked: Boolean,
    enabled: Boolean,
    switchColors: SwitchColors
) {
    rightActions()
    // 调用纯视觉的 Switch 组件，不传递任何交互回调
    Switch(
        checked = checked,
        enabled = enabled,
        colors = switchColors
    )
}

/**
 * 一个纯视觉的 Switch 组件，没有独立的点击或拖动交互。
 * 它的状态完全由外部的 `checked` 参数决定。
 */
@Composable
fun Switch(
    checked: Boolean,
    modifier: Modifier = Modifier,
    colors: SwitchColors = SwitchDefaults.switchColors(),
    enabled: Boolean = true
) {
    // 定义开关尺寸
    val trackWidth = 38.dp
    val trackHeight = 24.dp
    val thumbDiameter = 18.dp
    val thumbPadding = (trackHeight - thumbDiameter) / 2

    val uncheckedThumbOffset = thumbPadding
    val checkedThumbOffset = trackWidth - thumbDiameter - thumbPadding

    // 动画逻辑，由外部传入的 checked 状态驱动
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) checkedThumbOffset else uncheckedThumbOffset,
        animationSpec = tween(durationMillis = 200),
        label = "ThumbOffset"
    )

    val thumbColor by animateColorAsState(
        targetValue = colors.thumbColor(enabled),
        animationSpec = tween(durationMillis = 200),
        label = "ThumbColor"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (checked) colors.checkedTrackColor(enabled) else colors.uncheckedTrackColor(enabled),
        animationSpec = tween(durationMillis = 200),
        label = "BackgroundColor"
    )

    val trackClipShape = remember { SmoothRoundedCornerShape(50.dp) }

    // Box 上所有的交互修饰符都已被移除，使其成为纯视觉组件
    Box(
        modifier = modifier
            .wrapContentSize(Alignment.Center)
            .size(trackWidth, trackHeight)
            .clip(trackClipShape)
            .background(backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .padding(start = thumbOffset)
                .align(Alignment.CenterStart)
                .size(thumbDiameter)
                .background(thumbColor, shape = SmoothRoundedCornerShape(50.dp))
        )
    }
}

object SwitchDefaults {

    /**
     * 根据系统主题（白天/夜间模式）提供不同的颜色。
     */
    @Composable
    fun switchColors(
        // 白天模式颜色
        lightCheckedTrackColor: Color = Color(0xFF0166FF),
        lightUncheckedTrackColor: Color = Color(0xFFD6D6D6),
        // 夜间模式颜色
        darkCheckedTrackColor: Color = Color(0xFF247DFF),
        darkUncheckedTrackColor: Color = Color(0xFF535353),
        // 通用颜色
        thumbColor: Color = Color.White,
        disabledThumbColor: Color = Color.White.copy(alpha = 0.8f),
        disabledTrackAlpha: Float = 0.5f

    ): SwitchColors {
        val colorModeState = LocalColorMode.current.value
        val isDark = if (colorModeState == 2) true else isSystemInDarkTheme()

        val checkedTrack = if (isDark) darkCheckedTrackColor else lightCheckedTrackColor
        val uncheckedTrack = if (isDark) darkUncheckedTrackColor else lightUncheckedTrackColor

        return SwitchColors(
            thumbColor = thumbColor,
            disabledThumbColor = disabledThumbColor,
            checkedTrackColor = checkedTrack,
            uncheckedTrackColor = uncheckedTrack,
            disabledCheckedTrackColor = checkedTrack.copy(alpha = disabledTrackAlpha),
            disabledUncheckedTrackColor = uncheckedTrack.copy(alpha = disabledTrackAlpha)
        )
    }
}

/**
 * 用于存储开关在不同状态下的颜色。
 */
@Immutable
class SwitchColors(
    private val thumbColor: Color,
    private val disabledThumbColor: Color,
    private val checkedTrackColor: Color,
    private val uncheckedTrackColor: Color,
    private val disabledCheckedTrackColor: Color,
    private val disabledUncheckedTrackColor: Color
) {
    @Stable
    internal fun thumbColor(enabled: Boolean): Color =
        if (enabled) thumbColor else disabledThumbColor

    @Stable
    internal fun checkedTrackColor(enabled: Boolean): Color =
        if (enabled) checkedTrackColor else disabledCheckedTrackColor

    @Stable
    internal fun uncheckedTrackColor(enabled: Boolean): Color =
        if (enabled) uncheckedTrackColor else disabledUncheckedTrackColor
}
