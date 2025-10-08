package com.suqi8.oshin.ui.activity.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.suqi8.oshin.R
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun funArrow(
    title: String,
    summary: String? = null,
    rightText: String? = null,
    onClick: () -> Unit
) {
    SuperArrow(
        title = title,
        summary = summary,
        rightText = rightText,
        onClick = onClick
    )
}

/**
 * A arrow with a title and a summary.
 *
 * @param title The title of the [SuperArrow].
 * @param titleColor The color of the title.
 * @param summary The summary of the [SuperArrow].
 * @param summaryColor The color of the summary.
 * @param leftAction The [Composable] content that on the left side of the [SuperArrow].
 * @param rightText The text on the right side of the [SuperArrow].
 * @param rightActionColor The color of the right action.
 * @param modifier The modifier to be applied to the [SuperArrow].
 * @param insideMargin The margin inside the [SuperArrow].
 * @param onClick The callback when the [SuperArrow] is clicked.
 * @param holdDownState Used to determine whether it is in the pressed state.
 * @param enabled Whether the [SuperArrow] is clickable.
 */
@Composable
fun SuperArrow(
    title: String,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    leftAction: @Composable (() -> Unit)? = null,
    rightText: String? = null,
    rightActionColor: RightActionColors = SuperArrowDefaults.rightActionColors(),
    modifier: Modifier = Modifier,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    onClick: (() -> Unit)? = null,
    holdDownState: Boolean = false,
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
            SuperArrowRightActions(
                rightText = rightText,
                rightActionColor = rightActionColor,
                enabled = enabled
            )
        },
        onClick = onClick?.takeIf { enabled },
        holdDownState = holdDownState,
        enabled = enabled
    )
}

@Composable
private fun SuperArrowRightActions(
    rightText: String?,
    rightActionColor: RightActionColors,
    enabled: Boolean
) {
    val currentRightActionColor = rightActionColor.color(enabled)

    if (rightText != null) {
        Text(
            modifier = Modifier.widthIn(max = 130.dp),
            text = rightText,
            fontSize = MiuixTheme.textStyles.body2.fontSize,
            color = currentRightActionColor,
            textAlign = TextAlign.End,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2
        )
    }
    Image(
        modifier = Modifier
            .padding(start = 8.dp)
            .size(width = 12.dp, height = 24.dp),
        painter = painterResource(R.drawable.coui_btn_next_normal),
        contentDescription = null,
        colorFilter = ColorFilter.tint(currentRightActionColor),
    )
}

object SuperArrowDefaults {
    /**
     * The default color of the arrow.
     */
    @Composable
    fun rightActionColors() = RightActionColors(
        color = MiuixTheme.colorScheme.onSurfaceVariantActions,
        disabledColor = MiuixTheme.colorScheme.disabledOnSecondaryVariant
    )
}


@Immutable
class RightActionColors(
    private val color: Color,
    private val disabledColor: Color
) {
    @Stable
    internal fun color(enabled: Boolean): Color = if (enabled) color else disabledColor
}
