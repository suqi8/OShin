package com.suqi8.oshin.ui.activity.funlistui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.LocalColorMode
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.interfaces.HoldDownInteraction
import top.yukonga.miuix.kmp.theme.MiuixTheme
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
 * A basic component with Miuix style. Widely used in other extension components.
 *
 * @param title The title of the [BasicComponent].
 * @param titleColor The color of the title.
 * @param summary The summary of the [BasicComponent].
 * @param summaryColor The color of the summary.
 * @param leftAction The [Composable] content that on the left side of the [BasicComponent].
 * @param rightActions The [Composable] content on the right side of the [BasicComponent].
 * @param modifier The modifier to be applied to the [BasicComponent].
 * @param insideMargin The margin inside the [BasicComponent].
 * @param onClick The callback when the [BasicComponent] is clicked.
 * @param holdDownState Used to determine whether it is in the pressed state.
 * @param enabled Whether the [BasicComponent] is enabled.
 * @param interactionSource The [MutableInteractionSource] for the [BasicComponent].
 */
@Composable
fun BasicComponent(
    title: String? = null,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    leftAction: @Composable (() -> Unit?)? = null,
    rightActions: @Composable RowScope.() -> Unit = {},
    modifier: Modifier = Modifier,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    onClick: (() -> Unit)? = null,
    holdDownState: Boolean = false,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val indication = LocalIndication.current

    val holdDown = remember { mutableStateOf<HoldDownInteraction.HoldDown?>(null) }
    LaunchedEffect(holdDownState) {
        if (holdDownState) {
            val interaction = HoldDownInteraction.HoldDown()
            holdDown.value = interaction
            interactionSource.emit(interaction)
        } else {
            holdDown.value?.let { oldValue ->
                interactionSource.emit(HoldDownInteraction.Release(oldValue))
                holdDown.value = null
            }
        }
    }

    val clickableModifier = remember(onClick, enabled, interactionSource) {
        if (onClick != null && enabled) {
            Modifier.clickable(
                indication = indication,
                interactionSource = interactionSource,
                onClick = onClick
            )
        } else Modifier
    }

    SubcomposeLayout(
        modifier = modifier
            .heightIn(min = 48.dp)
            .fillMaxWidth()
            .then(clickableModifier)
            .padding(insideMargin)
    ) { constraints ->
        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        // 1. leftAction
        val leftPlaceables = leftAction?.let {
            subcompose("leftAction") { it() }.map { it -> it.measure(looseConstraints) }
        } ?: emptyList()
        val leftWidth = leftPlaceables.maxOfOrNull { it.width } ?: 0
        val leftHeight = leftPlaceables.maxOfOrNull { it.height } ?: 0
        // 2. rightActions
        val rightPlaceables = subcompose("rightActions") {
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                content = rightActions
            )
        }.map { it.measure(looseConstraints) }
        val rightWidth = rightPlaceables.maxOfOrNull { it.width } ?: 0
        val rightHeight = rightPlaceables.maxOfOrNull { it.height } ?: 0
        // 3. content
        val contentMaxWidth = maxOf(0, constraints.maxWidth - leftWidth - rightWidth - 16.dp.roundToPx())
        val titlePlaceable = title?.let {
            subcompose("title") {
                Text(
                    text = it,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = titleColor.color(enabled)
                )
            }.first().measure(looseConstraints.copy(maxWidth = contentMaxWidth))
        }
        val summaryPlaceable = summary?.let {
            subcompose("summary") {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = summaryColor.color(enabled)
                )
            }.first().measure(looseConstraints.copy(maxWidth = contentMaxWidth))
        }
        val gap = 4.dp.roundToPx()
        val contentHeight = (titlePlaceable?.height ?: 0) +
                (if (titlePlaceable != null && summaryPlaceable != null) gap else 0) +
                (summaryPlaceable?.height ?: 0)
        val layoutHeight = maxOf(leftHeight, rightHeight, contentHeight)
        layout(constraints.maxWidth, layoutHeight) {
            var x = 0
            // leftAction
            leftPlaceables.forEach {
                it.placeRelative(x, (layoutHeight - it.height) / 2)
                x += it.width
            }
            // content
            var contentY = (layoutHeight - contentHeight) / 2
            titlePlaceable?.let {
                it.placeRelative(x, contentY)
                contentY += it.height
                if (summaryPlaceable != null) contentY += gap
            }
            summaryPlaceable?.placeRelative(x, contentY)
            // rightActions
            val rightX = constraints.maxWidth - rightWidth
            rightPlaceables.forEach {
                it.placeRelative(rightX, (layoutHeight - it.height) / 2)
            }
        }
    }
}

object BasicComponentDefaults {

    /**
     * The default margin inside the [BasicComponent].
     */
    val InsideMargin = PaddingValues(vertical = 8.dp, horizontal = 16.dp)

    /**
     * The default color of the title.
     */
    @Composable
    fun titleColor(
        color: Color = MiuixTheme.colorScheme.onSurface,
        disabledColor: Color = MiuixTheme.colorScheme.disabledOnSecondaryVariant
    ): BasicComponentColors {
        return BasicComponentColors(
            color = color,
            disabledColor = disabledColor
        )
    }

    /**
     * The default color of the summary.
     */
    @Composable
    fun summaryColor(
        color: Color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        disabledColor: Color = MiuixTheme.colorScheme.disabledOnSecondaryVariant
    ): BasicComponentColors = BasicComponentColors(
        color = color,
        disabledColor = disabledColor
    )
}

@Immutable
class BasicComponentColors(
    private val color: Color,
    private val disabledColor: Color
) {
    @Stable
    internal fun color(enabled: Boolean): Color = if (enabled) color else disabledColor
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
