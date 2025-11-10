package com.suqi8.oshin.ui.activity.components

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.ui.main.LocalColorMode

/**
 * 一个带有数据持久化和触感反馈功能的 Switch 开关组件。
 *
 * 此组件作为顶层调用者，封装了 [SuperSwitch]，并使用 YukiHookAPI 的 `prefs` 来自动处理状态的读取和保存。
 * 当开关切换动画完成时，会触发一次手机震动。
 *
 * **重要提示**: 请确保已在 AndroidManifest.xml 文件中添加震动权限：
 * `<uses-permission android:name="android.permission.VIBRATE" />`
 */
@Composable
fun FunSwitch(
    title: String,
    summary: String? = null,
    category: String,
    key: String,
    defValue: Boolean = false,
    // [新增] position 参数
    position: CouiListItemPosition = CouiListItemPosition.Middle,
    onCheckedChange: ((Boolean) -> Unit)? = null
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val isChecked = remember { mutableStateOf(context.prefs(category).getBoolean(key, defValue)) }

    SuperSwitch(
        title = title,
        summary = summary,
        checked = isChecked.value,
        // [修改] 传递 position
        position = position,
        onCheckedChange = { checked ->
            context.prefs(category).edit { putBoolean(key, checked) }
            isChecked.value = checked
            onCheckedChange?.invoke(checked)
        },
        onAnimationFinished = {
            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
        }
    )
}

@Composable
fun FunSwitch(
    title: String,
    summary: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    // [修改] 替换 externalPadding 为 position
    position: CouiListItemPosition = CouiListItemPosition.Middle,
    enabled: Boolean = true
) {
    val haptic = LocalHapticFeedback.current
    SuperSwitch(
        title = title,
        summary = summary,
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        // [修改] 传递 position
        position = position,
        onAnimationFinished = {
            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
        },
    )
}

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
    switchColors: SwitchColors = SwitchDefaults.colors(),
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    onClick: (() -> Unit)? = null,
    // [修改] 替换 externalPadding 为 position
    position: CouiListItemPosition = CouiListItemPosition.Middle,
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
        // [修改] 传递 position 给 BasicComponent
        position = position,
        rightActions = {
            rightActions()
            Switch(
                checked = checked,
                onCheckedChange = null, // 点击由外层接管
                enabled = enabled,
                colors = switchColors,
                onAnimationFinished = onAnimationFinished
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
 * ColorOS 风格 Switch 最终完美复刻版 (Standard Compact Edition)。
 *
 * 数据来源 (全部来自反编译原厂文件):
 * - [尺寸]: bar_width=38dp, bar_height=24dp, thumb_size=20dp (derived padding=2dp)
 * - [颜色]: couiBlueTintControlNormal=#ff006aff, coui_color_controls=#29000000
 * - [动画]: 383ms/133ms, 贝塞尔(0.3, 0, 0.1, 1)
 * - [物理]: Spring 阻尼按压反馈
 */
@SuppressLint("UnusedTransitionTargetStateParameter")
/**
 * ColorOS 风格 Switch 最终纯净复刻版 (Zero Guess Edition)。
 *
 * 数据来源 (全部来自反编译原厂文件):
 * - [尺寸]: 38x24dp 轨道, 18dp 滑块 (derived padding=3dp)
 * - [颜色]: 基于 colors.xml 的精确值
 * - [动画]: 383ms/133ms, 贝塞尔(0.3, 0, 0.1, 1)
 * - [物理]: Spring 阻尼按压反馈
 */
@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: SwitchColors = SwitchDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onAnimationFinished: () -> Unit = {}
) {
    val trackWidth = 38.dp
    val trackHeight = 24.dp
    val thumbSize = 18.dp
    val thumbMargin = 3.dp

    // 仅在有回调时才监听按压状态，否则它应该是一个纯静止的视觉元素
    val isPressed by if (onCheckedChange != null) {
        interactionSource.collectIsPressedAsState()
    } else {
        remember { mutableStateOf(false) }
    }

    val toggleDuration = 383
    val scaleUpDuration = 133
    val toggleEasing = CubicBezierEasing(0.3f, 0.0f, 0.1f, 1.0f)

    val transition = updateTransition(checked, label = "Switch")

    val trackColor by animateColorAsState(
        targetValue = colors.trackColor(enabled, checked),
        animationSpec = tween(durationMillis = 450),
        label = "TrackColor"
    )

    val thumbMoveRange = trackWidth - thumbSize - (thumbMargin * 2)
    val thumbOffset by transition.animateDp(
        transitionSpec = {
            tween(durationMillis = toggleDuration, easing = toggleEasing)
        },
        label = "ThumbOffset"
    ) { isChecked ->
        if (isChecked) thumbMoveRange else 0.dp
    }

    LaunchedEffect(thumbOffset) {
        if (thumbOffset == (if (checked) thumbMoveRange else 0.dp)) {
            onAnimationFinished()
        }
    }

    val toggleScaleX by transition.animateFloat(
        transitionSpec = {
            keyframes {
                durationMillis = toggleDuration
                1.0f at 0 using toggleEasing
                1.3f at scaleUpDuration using toggleEasing
                1.0f at toggleDuration
            }
        },
        label = "ThumbToggleScaleX"
    ) { 1.0f }

    val pressScale = remember { Animatable(1f) }
    LaunchedEffect(isPressed) {
        pressScale.animateTo(
            targetValue = if (isPressed) 0.9f else 1.0f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow)
        )
    }

    val finalScaleX = toggleScaleX * pressScale.value
    val finalScaleY = pressScale.value

    // --- 关键修正开始 ---
    // 只有当提供了 onCheckedChange 回调时，才应用 toggleable。
    // 否则，它只是一个单纯展示状态的 Box，点击事件会自然穿透给父级 (SuperSwitch)。
    val toggleableModifier = if (onCheckedChange != null) {
        Modifier.toggleable(
            value = checked,
            onValueChange = onCheckedChange,
            enabled = enabled,
            role = Role.Switch,
            interactionSource = interactionSource,
            indication = null
        )
    } else {
        Modifier
    }
    // --- 关键修正结束 ---

    Box(
        modifier = modifier
            .then(toggleableModifier) // 应用条件修饰符
            .wrapContentSize(Alignment.Center)
            .requiredSize(trackWidth, trackHeight)
            .clip(RoundedCornerShape(percent = 50))
            .background(trackColor)
            .padding(thumbMargin)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = thumbOffset)
                .requiredSize(thumbSize)
                .graphicsLayer {
                    scaleX = finalScaleX
                    scaleY = finalScaleY
                    shadowElevation = if (isPressed) 1.dp.toPx() else 2.dp.toPx()
                    shape = CircleShape
                    clip = true
                }
                .background(colors.thumbColor(enabled))
        )
    }
}

object SwitchDefaults {
    private val ColorPrimary = Color(0xFFFF006AFF)
    private val TrackUncheckedLight = Color(0xFFE5E5E5)
    private val TrackUncheckedDark = Color(0xFF757575)
    private val TrackDisabledLight = Color(0xFFF2F2F2)
    private val TrackDisabledDark = Color(0x80757575)
    private val TrackCheckedDisabledAlpha = 0.3f
    private val ThumbNormal = Color(0xFFFFFFFF)
    private val ThumbDisabled = Color(0x8AFFFFFF)

    @Composable
    fun colors(
        checkedTrackColor: Color = ColorPrimary,
        uncheckedTrackColor: Color = Color.Unspecified,
        thumbColor: Color = ThumbNormal,
        disabledThumbColor: Color = ThumbDisabled
    ): SwitchColors {
        val colorModeState = LocalColorMode.current.value
        val isDark = if (colorModeState == 2) true else isSystemInDarkTheme()

        val actualUncheckedTrackColor = if (uncheckedTrackColor != Color.Unspecified) {
            uncheckedTrackColor
        } else {
            if (isDark) TrackUncheckedDark else TrackUncheckedLight
        }

        val disabledTrackBase = if (isDark) TrackDisabledDark else TrackDisabledLight

        return SwitchColors(
            checkedTrackColor = checkedTrackColor,
            uncheckedTrackColor = actualUncheckedTrackColor,
            thumbColor = thumbColor,
            disabledThumbColor = disabledThumbColor,
            disabledCheckedTrackColor = checkedTrackColor.copy(alpha = TrackCheckedDisabledAlpha),
            disabledUncheckedTrackColor = disabledTrackBase
        )
    }
}

@Immutable
class SwitchColors(
    private val checkedTrackColor: Color,
    private val uncheckedTrackColor: Color,
    private val disabledCheckedTrackColor: Color,
    private val disabledUncheckedTrackColor: Color,
    private val thumbColor: Color,
    private val disabledThumbColor: Color
) {
    @Stable
    fun trackColor(enabled: Boolean, checked: Boolean): Color {
        return if (!enabled) {
            if (checked) disabledCheckedTrackColor else disabledUncheckedTrackColor
        } else {
            if (checked) checkedTrackColor else uncheckedTrackColor
        }
    }

    @Stable
    fun thumbColor(enabled: Boolean): Color {
        return if (enabled) thumbColor else disabledThumbColor
    }
}
