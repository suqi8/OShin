package com.suqi8.oshin.ui.activity.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.interfaces.HoldDownInteraction
import top.yukonga.miuix.kmp.theme.MiuixTheme


/**
 * 列表项在卡片组中的位置。
 */
enum class CouiListItemPosition {
    Top, Middle, Bottom, Single
}

private enum class SlotsEnum { Start, Center, End }

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
    titleModifier: Modifier = Modifier,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    leftAction: @Composable (() -> Unit?)? = null,
    rightActions: @Composable RowScope.() -> Unit = {},
    modifier: Modifier = Modifier,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    onClick: (() -> Unit)? = null,
    position: CouiListItemPosition = CouiListItemPosition.Middle,
    holdDownState: Boolean = false,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val indication = LocalIndication.current
    val extraTopDp = if (position == CouiListItemPosition.Top || position == CouiListItemPosition.Single) 2.dp else 0.dp
    val extraBottomDp = if (position == CouiListItemPosition.Bottom || position == CouiListItemPosition.Single) 2.dp else 0.dp
    val minHeight = 48.dp
    val density = LocalDensity.current
    val horizontalGapPx = with(density) { 16.dp.roundToPx() }

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

    val layoutDirection = androidx.compose.ui.platform.LocalLayoutDirection.current

    SubcomposeLayout(
        modifier = modifier
            .heightIn(min = minHeight + extraTopDp + extraBottomDp)
            .fillMaxWidth()
            .then(clickableModifier)
            .padding(
                start = insideMargin.calculateStartPadding(layoutDirection),
                end = insideMargin.calculateEndPadding(layoutDirection),
                top = insideMargin.calculateTopPadding() + extraTopDp,
                bottom = insideMargin.calculateBottomPadding() + extraBottomDp
            )
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
        val leftGap = if (leftWidth > 0) horizontalGapPx else 0
        val rightGap = if (rightWidth > 0) horizontalGapPx else 0

        val contentMaxWidth = maxOf(0, constraints.maxWidth - leftWidth - leftGap - rightWidth - rightGap)
        val titlePlaceable = title?.let {
            subcompose("title") {
                Box(
                    modifier = Modifier.heightIn(min = 21.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = it,
                        fontSize = 16.sp,
                        lineHeight = 1.2.em,
                        fontWeight = FontWeight.Medium,
                        color = titleColor.color(enabled),
                        modifier = titleModifier
                    )
                }
            }.first().measure(looseConstraints.copy(maxWidth = contentMaxWidth))
        }
        val summaryPlaceable = summary?.let {
            subcompose("summary") {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    lineHeight = 1.3.em,
                    fontWeight = FontWeight.Normal,
                    color = summaryColor.color(enabled)
                )
            }.first().measure(looseConstraints.copy(maxWidth = contentMaxWidth))
        }
        val gap = 2.dp.roundToPx()
        val contentHeight = (titlePlaceable?.height ?: 0) +
                (if (titlePlaceable != null && summaryPlaceable != null) gap else 0) +
                (summaryPlaceable?.height ?: 0)
        val layoutHeight = maxOf(leftHeight, rightHeight, contentHeight).coerceAtLeast(constraints.minHeight)
        layout(constraints.maxWidth, layoutHeight) {
            var x = 0
            if (leftWidth > 0) {
                leftPlaceables.forEach {
                    it.placeRelative(x, (layoutHeight - it.height) / 2)
                }
                x += leftWidth + leftGap
            }

            var contentY = (layoutHeight - contentHeight) / 2
            titlePlaceable?.let {
                it.placeRelative(x, contentY)
                contentY += it.height
                if (summaryPlaceable != null) contentY += gap
            }
            summaryPlaceable?.placeRelative(x, contentY)

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
        enabledColor: Color = MiuixTheme.colorScheme.onSurface,
        disabledColor: Color = MiuixTheme.colorScheme.disabledOnSecondaryVariant
    ): BasicComponentColors {
        return BasicComponentColors(
            enabledColor = enabledColor,
            disabledColor = disabledColor
        )
    }

    /**
     * The default color of the summary.
     */
    @Composable
    fun summaryColor(
        enabledColor: Color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        disabledColor: Color = MiuixTheme.colorScheme.disabledOnSecondaryVariant
    ): BasicComponentColors = BasicComponentColors(
        enabledColor = enabledColor,
        disabledColor = disabledColor
    )
}

@Immutable
class BasicComponentColors(
    private val enabledColor: Color,
    private val disabledColor: Color
) {
    @Stable
    internal fun color(enabled: Boolean): Color = if (enabled) enabledColor else disabledColor
}
