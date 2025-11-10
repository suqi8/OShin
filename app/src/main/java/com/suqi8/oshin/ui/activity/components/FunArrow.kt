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

/**
 * 一个带有标题、摘要（可选）、右侧文本（可选）和右向箭头的列表项组件。
 * 通常用于导航到下一个页面。
 *
 * @param title 列表项的主标题。
 * @param modifier 应用于组件的修饰符。
 * @param summary 可选的副标题或摘要信息。
 * @param rightText 显示在箭头左侧的可选文本。
 * @param leftAction 在标题左侧显示的可选组件（如图标）。
 * @param position 列表项在卡片组中的位置，用于自动调整高度和圆角边距。
 * @param onClick 点击时的回调。
 */
@Composable
fun FunArrow(
    title: String,
    modifier: Modifier = Modifier,
    titleModifier: Modifier = Modifier,
    summary: String? = null,
    rightText: String? = null,
    leftAction: @Composable (() -> Unit)? = null,
    // [新增] 引入位置参数，默认 Middle
    position: CouiListItemPosition = CouiListItemPosition.Middle,
    onClick: () -> Unit
) {
    SuperArrow(
        title = title,
        summary = summary,
        rightText = rightText,
        leftAction = leftAction,
        // [修改] 传递 position，移除了 externalPadding
        position = position,
        modifier = modifier,
        titleModifier = titleModifier,
        onClick = onClick
    )
}

/**
 * FunArrow 的高级版本，提供更多的自定义选项。
 */
@Composable
fun SuperArrow(
    title: String,
    titleModifier: Modifier = Modifier,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    leftAction: @Composable (() -> Unit)? = null,
    rightText: String? = null,
    rightActionColor: RightActionColors = SuperArrowDefaults.rightActionColors(),
    modifier: Modifier = Modifier,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    onClick: (() -> Unit)? = null,
    position: CouiListItemPosition = CouiListItemPosition.Middle,
    holdDownState: Boolean = false,
    enabled: Boolean = true
) {
    BasicComponent(
        modifier = modifier,
        insideMargin = insideMargin,
        title = title,
        titleModifier = titleModifier,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        leftAction = leftAction,
        // [修改] 传递 position 给 BasicComponent
        position = position,
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
