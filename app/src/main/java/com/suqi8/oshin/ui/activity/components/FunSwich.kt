package com.suqi8.oshin.ui.activity.components

import android.R.attr.category
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.ui.main.LocalColorMode
import top.yukonga.miuix.kmp.utils.G2RoundedCornerShape

/**
 * 一个带有数据持久化和触感反馈功能的 Switch 开关组件。
 *
 * 此组件作为顶层调用者，封装了 [SuperSwitch]，并使用 YukiHookAPI 的 `prefs` 来自动处理状态的读取和保存。
 * 当开关切换动画完成时，会触发一次手机震动。
 *
 * **重要提示**: 请确保已在 AndroidManifest.xml 文件中添加震动权限：
 * `<uses-permission android:name="android.permission.VIBRATE" />`
 *
 * @param title 开关行的主标题。
 * @param summary 可选的副标题或摘要信息。
 * @param category 用于 `prefs` 的分类名。
 * @param key 用于 `prefs` 的键名。
 * @param defValue 开关的默认状态。
 * @param onCheckedChange 当开关状态改变时触发的回调，返回新的布尔值状态。
 */
@Composable
fun FunSwitch(
    title: String,
    summary: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    externalPadding: PaddingValues = PaddingValues(0.dp),
    enabled: Boolean = true
) {
    val haptic = LocalHapticFeedback.current
    SuperSwitch(
        title = title,
        summary = summary,
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        externalPadding = externalPadding,
        onAnimationFinished = {
            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
        },
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
 * @param onAnimationFinished 动画完成时的回调。
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
    externalPadding: PaddingValues = PaddingValues(0.dp),
    enabled: Boolean = true,
    onAnimationFinished: () -> Unit = {}
) {
    BasicComponent(
        modifier = modifier,
        insideMargin = insideMargin,
        title = title,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        leftAction = leftAction,
        externalPadding = externalPadding,
        rightActions = {
            SuperSwitchRightActions(
                rightActions = rightActions,
                checked = checked,
                enabled = enabled,
                switchColors = switchColors,
                onAnimationFinished = onAnimationFinished // 向下传递回调
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

@Composable
fun FunSwitch(
    title: String,
    summary: String? = null,
    category: String,
    key: String,
    defValue: Boolean = false,
    onCheckedChange: ((Boolean) -> Unit)? = null
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val isChecked = remember { mutableStateOf(context.prefs(category).getBoolean(key, defValue)) }

    SuperSwitch(
        title = title,
        checked = isChecked.value,
        onCheckedChange = {
            // 点击时仅更新状态，不再立即触发震动
            context.prefs(category).edit { putBoolean(key, it) }
            isChecked.value = it
            onCheckedChange?.invoke(it)
        },
        // 将震动逻辑移至动画完成的回调中
        onAnimationFinished = {
            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
        },
        summary = summary
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
    switchColors: SwitchColors,
    onAnimationFinished: () -> Unit
) {
    rightActions()
    Switch(
        checked = checked,
        enabled = enabled,
        colors = switchColors,
        onAnimationFinished = onAnimationFinished // 向下传递回调
    )
}

/**
 * 一个纯视觉的 Switch 组件，不处理任何交互事件。
 *
 * 它的状态完全由外部的 `checked` 参数驱动，并实现了细腻的“粘滞拉伸”及“回弹”动画和阴影效果。
 *
 * @param checked 开关的当前状态 (on/off)。
 * @param modifier [Modifier] 应用于开关的根布局。
 * @param colors 开关在不同状态下的颜色配置。
 * @param enabled 开关是否可用，会影响颜色和视觉表现。
 * @param onAnimationFinished 动画完成时的回调。
 */
@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun Switch(
    checked: Boolean,
    modifier: Modifier = Modifier,
    colors: SwitchColors = SwitchDefaults.switchColors(),
    enabled: Boolean = true,
    onAnimationFinished: () -> Unit = {}
) {
    // 尺寸定义
    val trackWidth = 38.dp
    val trackHeight = 24.dp
    val thumbDiameter = 18.dp
    val thumbPadding = (trackHeight - thumbDiameter) / 2

    // 动画参数
    val thumbStretchedWidth = thumbDiameter + 5.dp
    val animationDuration = 300 // 稍微增加总时长以容纳回弹动画
    val overshoot = 1.5.dp // 定义回弹动画中“过头”的距离

    // 计算滑块在关/开状态下的左侧偏移量
    val uncheckedThumbOffset = thumbPadding
    val checkedThumbOffset = trackWidth - thumbDiameter - thumbPadding

    // 使用 updateTransition 管理基于 checked 状态的多个动画
    val transition = updateTransition(targetState = checked, label = "SwitchTransition")

    // 新增：使用 LaunchedEffect 检测动画何时结束
    val isRunning = transition.isRunning
    val previousIsRunning = remember { mutableStateOf(isRunning) }
    LaunchedEffect(isRunning) {
        // 当动画从“运行中”变为“已停止”时，触发回调
        if (previousIsRunning.value && !isRunning) {
            onAnimationFinished()
        }
        previousIsRunning.value = isRunning
    }

    // 动画化滑块宽度，实现拉伸效果
    val thumbWidth by transition.animateDp(
        transitionSpec = {
            keyframes {
                durationMillis = animationDuration
                thumbStretchedWidth at animationDuration * 2 / 5
            }
        },
        label = "ThumbWidth"
    ) { _ ->
        thumbDiameter
    }

    // 动画化滑块偏移量，实现“粘滞”及“回弹”效果
    val thumbOffset by transition.animateDp(
        transitionSpec = {
            keyframes {
                durationMillis = animationDuration
                if (targetState) {
                    uncheckedThumbOffset at animationDuration * 2 / 5
                    (checkedThumbOffset + overshoot) at animationDuration * 4 / 5
                } else {
                    (checkedThumbOffset + thumbDiameter - thumbStretchedWidth) at animationDuration * 2 / 5
                    (uncheckedThumbOffset - overshoot) at animationDuration * 4 / 5
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

    val trackClipShape = remember { G2RoundedCornerShape(50.dp) }

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
