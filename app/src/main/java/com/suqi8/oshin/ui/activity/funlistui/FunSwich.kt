package com.suqi8.oshin.ui.activity.funlistui

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.LocalColorMode
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

/**
 * 一个带有数据持久化功能的 Switch 开关组件。
 *
 * 此组件作为顶层调用者，封装了 [SuperSwitch]，并使用 YukiHookAPI 的 `prefs` 来自动处理状态的读取和保存。
 *
 * @param title 开关行的主标题。
 * @param summary 可选的副标题或摘要信息。
 * @param category 用于 `prefs` 的分类名。
 * @param key 用于 `prefs` 的键名。
 * @param defValue 开关的默认状态。
 * @param onCheckedChange 当开关状态改变时触发的回调，返回新的布尔值状态。
 */
@Composable
fun FunSwich(
    title: String,
    summary: String? = null,
    category: String,
    key: String,
    defValue: Boolean = false,
    onCheckedChange: ((Boolean) -> Unit)? = null
) {
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
 * 一个高度集成的开关组件行，包含标题、摘要和开关。
 *
 * 整个组件行作为一个统一的可点击区域来切换开关状态，提供了更一体化的用户体验。
 * 它将视觉表现（[Switch]）和业务逻辑（状态切换）分离开。
 *
 * @param checked 开关的当前状态。
 * @param onCheckedChange 当开关状态改变时触发的回调。
 * @param title 主标题。
 * @param modifier [Modifier] 应用于整个组件行。
 * @param titleColor 标题的颜色。
 * @param summary 可选的副标题。
 * @param summaryColor 副标题的颜色。
 * @param leftAction 在标题左侧的可组合内容区域。
 * @param rightActions 在开关左侧、标题右侧的可组合内容区域。
 * @param switchColors 开关在不同状态下的颜色配置。
 * @param insideMargin 组件行内部的边距。
 * @param onClick 整个组件行的点击事件回调。
 * @param enabled 组件是否可用。
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
 * 仅用于 [SuperSwitch] 内部布局的私有辅助函数。
 */
@Composable
private fun RowScope.SuperSwitchRightActions(
    rightActions: @Composable RowScope.() -> Unit,
    checked: Boolean,
    enabled: Boolean,
    switchColors: SwitchColors
) {
    rightActions()
    Switch(
        checked = checked,
        enabled = enabled,
        colors = switchColors
    )
}

/**
 * 一个纯视觉的 Switch 组件，不处理任何交互事件。
 *
 * 它的状态完全由外部的 `checked` 参数驱动，并实现了细腻的“粘滞拉伸”动画和阴影效果。
 *
 * @param checked 开关的当前状态 (on/off)。
 * @param modifier [Modifier] 应用于开关的根布局。
 * @param colors 开关在不同状态下的颜色配置。
 * @param enabled 开关是否可用，会影响颜色和视觉表现。
 */
@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun Switch(
    checked: Boolean,
    modifier: Modifier = Modifier,
    colors: SwitchColors = SwitchDefaults.switchColors(),
    enabled: Boolean = true
) {
    // 尺寸定义
    val trackWidth = 38.dp
    val trackHeight = 24.dp
    val thumbDiameter = 18.dp
    val thumbPadding = (trackHeight - thumbDiameter) / 2

    // 动画参数
    val thumbStretchedWidth = thumbDiameter + 5.dp
    val animationDuration = 250

    // 计算滑块在关/开状态下的左侧偏移量
    val uncheckedThumbOffset = thumbPadding
    val checkedThumbOffset = trackWidth - thumbDiameter - thumbPadding

    // 使用 updateTransition 管理基于 checked 状态的多个动画
    val transition = updateTransition(targetState = checked, label = "SwitchTransition")

    // 动画化滑块宽度，实现拉伸效果
    val thumbWidth by transition.animateDp(
        transitionSpec = {
            keyframes {
                durationMillis = animationDuration
                thumbStretchedWidth at animationDuration / 2
            }
        },
        label = "ThumbWidth"
    ) { @SuppressLint("UnusedTransitionTargetStateParameter")
        thumbDiameter
    }

    // 动画化滑块偏移量，实现“粘滞”效果
    val thumbOffset by transition.animateDp(
        transitionSpec = {
            keyframes {
                durationMillis = animationDuration
                if (targetState) {
                    uncheckedThumbOffset at animationDuration / 2
                } else {
                    (checkedThumbOffset + thumbDiameter - thumbStretchedWidth) at animationDuration / 2
                }
            }
        },
        label = "ThumbOffset"
    ) { isChecked ->
        if (isChecked) checkedThumbOffset else uncheckedThumbOffset
    }

    // 动画化背景和滑块的颜色
    val backgroundColor by animateColorAsState(
        targetValue = if (checked) colors.checkedTrackColor(enabled) else colors.uncheckedTrackColor(enabled),
        animationSpec = tween(durationMillis = animationDuration),
        label = "BackgroundColor"
    )
    val thumbColor by animateColorAsState(
        targetValue = colors.thumbColor(enabled),
        animationSpec = tween(durationMillis = animationDuration),
        label = "ThumbColor"
    )

    val trackClipShape = remember { SmoothRoundedCornerShape(50.dp) }

    // 轨道 (Track)
    Box(
        modifier = modifier
            .wrapContentSize(Alignment.Center)
            .size(trackWidth, trackHeight)
            .clip(trackClipShape)
            .background(backgroundColor)
    ) {
        // 滑块 (Thumb)
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = thumbOffset)
                .height(thumbDiameter)
                .width(thumbWidth)
                // 使用 graphicsLayer 提升动画质感和抗锯齿效果
                .graphicsLayer {
                    this.shadowElevation = 2.dp.toPx()
                    this.shape = trackClipShape
                    this.clip = true
                }
                .background(color = thumbColor)
        )
    }
}

/**
 * 提供 [Switch] 组件默认颜色配置的对象。
 */
object SwitchDefaults {

    /**
     * 创建一个 [SwitchColors] 实例，根据系统是否处于深色模式提供不同的颜色。
     *
     * @param lightCheckedTrackColor 亮色模式下，开启状态的轨道颜色。
     * @param lightUncheckedTrackColor 亮色模式下，关闭状态的轨道颜色。
     * @param darkCheckedTrackColor 暗色模式下，开启状态的轨道颜色。
     * @param darkUncheckedTrackColor 暗色模式下，关闭状态的轨道颜色。
     * @param thumbColor 滑块的颜色。
     * @param disabledThumbColor 禁用状态下滑块的颜色。
     * @param disabledTrackAlpha 禁用状态下轨道的透明度。
     * @return 一个配置好的 [SwitchColors] 实例。
     */
    @Composable
    fun switchColors(
        lightCheckedTrackColor: Color = Color(0xFF0166FF),
        lightUncheckedTrackColor: Color = Color(0xFFD6D6D6),
        darkCheckedTrackColor: Color = Color(0xFF247DFF),
        darkUncheckedTrackColor: Color = Color(0xFF535353),
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
 * 持有 Switch 在不同状态下的颜色值的不可变类。
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
