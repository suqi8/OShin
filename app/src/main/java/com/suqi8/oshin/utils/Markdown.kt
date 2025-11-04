package com.suqi8.oshin.utils

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.coil3.Coil3ImageTransformerImpl
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.compose.elements.MarkdownHighlightedCodeBlock
import com.mikepenz.markdown.compose.elements.MarkdownHighlightedCodeFence
import com.mikepenz.markdown.compose.extendedspans.ExtendedSpans
import com.mikepenz.markdown.compose.extendedspans.RoundedCornerSpanPainter
import com.mikepenz.markdown.compose.extendedspans.SquigglyUnderlineSpanPainter
import com.mikepenz.markdown.compose.extendedspans.rememberSquigglyUnderlineAnimator
import com.mikepenz.markdown.model.DefaultMarkdownColors
import com.mikepenz.markdown.model.DefaultMarkdownTypography
import com.mikepenz.markdown.model.MarkdownColors
import com.mikepenz.markdown.model.MarkdownTypography
import com.mikepenz.markdown.model.markdownExtendedSpans
import dev.snipme.highlights.Highlights
import dev.snipme.highlights.model.SyntaxThemes
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun BaseMarkdown(
    content: String,
    typography: MarkdownTypography, // 将排版作为参数传入
    modifier: Modifier = Modifier
) {
    // 缓存昂贵的对象
    // 仅当暗黑模式切换时才重新创建 Builder 和 Components
    val isDark = isSystemInDarkTheme()
    val highlightsBuilder = remember(isDark) {
        Highlights.Builder().theme(SyntaxThemes.atom(darkMode = isDark))
    }
    val components = remember(highlightsBuilder) {
        markdownComponents(
            codeBlock = {
                MarkdownHighlightedCodeBlock(
                    it.content,
                    it.node,
                    highlightsBuilder = highlightsBuilder
                )
            },
            codeFence = {
                MarkdownHighlightedCodeFence(
                    it.content,
                    it.node,
                    highlightsBuilder = highlightsBuilder
                )
            },
        )
    }

    // 这部分逻辑在原始代码中也是可记忆的 (remember)，保持不变
    val extendedSpans = markdownExtendedSpans {
        val animator = rememberSquigglyUnderlineAnimator()
        remember {
            ExtendedSpans(
                RoundedCornerSpanPainter(),
                SquigglyUnderlineSpanPainter(animator = animator)
            )
        }
    }

    Markdown(
        content,
        colors = markdownColor(),
        modifier = modifier,
        extendedSpans = extendedSpans,
        components = components, // 使用缓存的 components
        imageTransformer = Coil3ImageTransformerImpl, // 应用通用转换器
        typography = typography // 使用传入的排版
    )
}

@Composable
fun markdownTypography(
    h1: TextStyle = TextStyle(fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.Bold),
    h2: TextStyle = TextStyle(fontSize = 19.sp, lineHeight = 24.sp, fontWeight = FontWeight.Bold),
    h3: TextStyle = TextStyle(fontSize = 17.sp, lineHeight = 21.sp, fontWeight = FontWeight.Bold),
    h4: TextStyle = TextStyle(fontSize = 15.sp, lineHeight = 19.sp, fontWeight = FontWeight.Bold),
    h5: TextStyle = TextStyle(fontSize = 14.sp, lineHeight = 18.sp, fontWeight = FontWeight.Bold),
    h6: TextStyle = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Bold),
    text: TextStyle = TextStyle(fontSize = 14.sp, lineHeight = 18.sp),
    code: TextStyle = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontFamily = FontFamily.Monospace
    ),
    inlineCode: TextStyle = TextStyle(
        fontSize = 14.sp,
        lineHeight = 18.sp,
        fontFamily = FontFamily.Monospace
    ),
    quote: TextStyle = TextStyle(
        fontSize = 14.sp,
        lineHeight = 18.sp,
        fontStyle = FontStyle.Italic
    ),
    paragraph: TextStyle = TextStyle(fontSize = 14.sp, lineHeight = 18.sp),
    ordered: TextStyle = TextStyle(fontSize = 14.sp, lineHeight = 18.sp),
    bullet: TextStyle = TextStyle(fontSize = 14.sp, lineHeight = 18.sp),
    list: TextStyle = TextStyle(fontSize = 14.sp, lineHeight = 18.sp),
    textLink: TextLinkStyles = TextLinkStyles(
        style = TextStyle(
            fontSize = 14.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline
        ).toSpanStyle()
    ),
    table: TextStyle = TextStyle(fontSize = 14.sp, lineHeight = 18.sp)
): MarkdownTypography = DefaultMarkdownTypography(
    h1,
    h2,
    h3,
    h4,
    h5,
    h6,
    text,
    quote,
    code,
    inlineCode,
    paragraph,
    ordered,
    bullet,
    list,
    textLink,
    table
)

@Composable
fun markdownColor(
    text: Color = MiuixTheme.colorScheme.onBackground,
    codeText: Color = MiuixTheme.colorScheme.background,
    inlineCodeText: Color = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.2f),
    dividerColor: Color = MiuixTheme.colorScheme.outline,
    tableBackground: Color = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.2f),
): MarkdownColors = DefaultMarkdownColors(
    text,
    codeText,
    inlineCodeText,
    dividerColor,
    tableBackground
)
