package com.suqi8.oshin.ui.activity.funlistui


import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * A basic component with Miuix style. Widely used in other extension components.
 *
 * @param modifier The modifier to be applied to the [BasicComponent].
 * @param insideMargin The margin inside the [BasicComponent].
 * @param title The title of the [BasicComponent].
 * @param titleColor The color of the title.
 * @param summary The summary of the [BasicComponent].
 * @param summaryColor The color of the summary.
 * @param leftAction The [Composable] content that on the left side of the [BasicComponent].
 * @param rightActions The [Composable] content on the right side of the [BasicComponent].
 * @param onClick The callback when the [BasicComponent] is clicked.
 * @param enabled Whether the [BasicComponent] is enabled.
 * @param interactionSource The [MutableInteractionSource] for the [BasicComponent].
 */
@Composable
fun SearchList(
    modifier: Modifier = Modifier,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    title: AnnotatedString? = null,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: AnnotatedString? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    leftAction: @Composable (() -> Unit?)? = null,
    rightActions: @Composable RowScope.() -> Unit = {},
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Row(
        modifier = if (onClick != null && enabled) {
            modifier
                .clickable(
                    indication = LocalIndication.current,
                    interactionSource = interactionSource
                ) {
                    onClick.invoke()
                }
        } else {
            modifier
        }
            .heightIn(min = 56.dp)
            .fillMaxWidth()
            .padding(insideMargin),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        leftAction?.let {
            it()
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            title?.let {
                Text(
                    text = it,
                    fontSize = MiuixTheme.textStyles.headline1.fontSize,
                    fontWeight = FontWeight.Medium,
                    color = titleColor.color(enabled)
                )
            }
            summary?.let {
                Text(
                    text = it,
                    fontSize = MiuixTheme.textStyles.body2.fontSize,
                    color = summaryColor.color(enabled)
                )
            }
        }
        Box(
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                content = rightActions
            )
        }
    }
}
