package com.suqi8.oshin.ui.activity.funlistui

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.interfaces.HoldDownInteraction
import top.yukonga.miuix.kmp.theme.MiuixTheme

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
                Box(
                    modifier = Modifier.heightIn(min = 21.14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = it,
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = titleColor.color(enabled)
                    )
                }
            }.first().measure(looseConstraints.copy(maxWidth = contentMaxWidth))
        }
        val summaryPlaceable = summary?.let {
            subcompose("summary") {
                Box(
                    modifier = Modifier.heightIn(min = 18.57.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = summaryColor.color(enabled)
                    )
                }
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
