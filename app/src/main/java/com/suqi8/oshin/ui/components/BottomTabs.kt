package com.suqi8.oshin.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.lerp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberCombinedBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.DefaultChromaticAberration
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.InnerShadow
import com.kyant.backdrop.shadow.Shadow
import com.kyant.capsule.ContinuousCapsule
import com.suqi8.oshin.ui.main.LocalColorMode
import com.suqi8.oshin.ui.main.NavigationItem
import com.suqi8.oshin.utils.DampedDragAnimation
import com.suqi8.oshin.utils.InteractiveHighlight
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sign

/**
 * 为了让图标和文字也能缩放，需要定义这个 CompositionLocal
 * 这是原版示例 `LiquidBottomTabs` 的一部分
 */
val LocalLiquidBottomTabScale = compositionLocalOf { 1f }

@Composable
fun BottomTabs(
    tabs: List<NavigationItem>,
    pagerState: PagerState,
    onTabSelected: (Int) -> Unit,
    backdrop: Backdrop,
    modifier: Modifier = Modifier
) {
    val isLightTheme = !when (LocalColorMode.current.value) {
        1 -> false // 1 = 白天
        2 -> true  // 2 = 黑夜
        else -> isSystemInDarkTheme() // 0 = 跟随系统
    }
    // --- FIX 1: 添加 contentColor 定义 ---
    val contentColor = if (isLightTheme) Color.Black else Color.White
    val accentColor = if (isLightTheme) Color(0xFF0088FF) else Color(0xFF0091FF)
    val containerColor = if (isLightTheme) Color(0xFFFAFAFA).copy(0.4f) else Color(0xFF121212).copy(0.4f)
    val iconColorFilter = ColorFilter.tint(contentColor)
    val tabsBackdrop = rememberLayerBackdrop()

    BoxWithConstraints(
        modifier = modifier
            .padding(bottom = 16.dp)
            .padding(horizontal = 40.dp)
            .height(64.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // --- FIX 2 & 3: 修正单位转换 ---
        val density = LocalDensity.current
        val tabWidth = with(density) {
            (constraints.maxWidth.toFloat() - 8f.dp.toPx()) / tabs.size
        }

        val animationScope = rememberCoroutineScope()

        val offsetAnimation = remember { Animatable(0f) }
        val panelOffset by remember(density) {
            derivedStateOf {
                val fraction = (offsetAnimation.value / constraints.maxWidth.toFloat()).fastCoerceIn(-1f, 1f)
                with(density) {
                    4f.dp.toPx() * fraction.sign * EaseOut.transform(abs(fraction))
                }
            }
        }

        var didDrag by remember { mutableStateOf(false) }
        val dampedDragAnimation = remember(animationScope) {
            DampedDragAnimation(
                animationScope = animationScope,
                initialValue = pagerState.currentPage.toFloat(),
                valueRange = 0f..(tabs.size - 1).toFloat(),
                visibilityThreshold = 0.001f,
                initialScale = 1f,
                pressedScale = 78f / 56f,
                onDragStarted = {},
                onDragStopped = {
                    if (didDrag) {
                        val targetIndex = targetValue.roundToInt().coerceIn(0, tabs.size - 1)
                        if (pagerState.currentPage != targetIndex) {
                            onTabSelected(targetIndex)
                        }
                        didDrag = false
                    }
                    animationScope.launch {
                        offsetAnimation.animateTo(0f, spring(1f, 300f, 0.5f))
                    }
                },
                onDrag = { _, dragAmount ->
                    if (!didDrag) {
                        didDrag = dragAmount.x != 0f
                    }
                    updateValue(
                        (targetValue + dragAmount.x / tabWidth).fastCoerceIn(
                            0f,
                            (tabs.size - 1).toFloat()
                        )
                    )
                    animationScope.launch {
                        offsetAnimation.snapTo(offsetAnimation.value + dragAmount.x)
                    }
                }
            )
        }

        LaunchedEffect(dampedDragAnimation, pagerState) {
            snapshotFlow { pagerState.currentPage + pagerState.currentPageOffsetFraction }
                .collectLatest { position ->
                    if (dampedDragAnimation.targetValue != position) {
                        dampedDragAnimation.animateToValue(position)
                    }
                }
        }

        val interactiveHighlight = remember(animationScope) {
            InteractiveHighlight(
                animationScope = animationScope,
                position = { size, _ ->
                    Offset(
                        (dampedDragAnimation.value + 0.5f) * tabWidth + panelOffset,
                        size.height / 2f
                    )
                }
            )
        }

        val content: @Composable RowScope.() -> Unit = {
            tabs.forEachIndexed { index, screen ->
                val scale = lerp(1f, 1.05f, dampedDragAnimation.pressProgress)
                Column(
                    Modifier
                        .clip(ContinuousCapsule)
                        .clickable { onTabSelected(index) }
                        .fillMaxHeight()
                        .weight(1f)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        },
                    verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val contentScale = LocalLiquidBottomTabScale.current
                    Image(
                        painter = painterResource(screen.icon),
                        contentDescription = screen.label,
                        modifier = Modifier
                            .size(28.dp)
                            .graphicsLayer {
                                scaleX = contentScale
                                scaleY = contentScale
                            },
                        colorFilter = iconColorFilter
                    )
                    BasicText(
                        screen.label,
                        style = TextStyle(color = contentColor, fontSize = 12.sp),
                        modifier = Modifier.graphicsLayer {
                            scaleX = contentScale
                            scaleY = contentScale
                        }
                    )
                }
            }
        }

        Row(
            Modifier
                .graphicsLayer { translationX = panelOffset }
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { ContinuousCapsule },
                    effects = {
                        vibrancy()
                        blur(8f.dp.toPx())
                        lens(24f.dp.toPx(), 24f.dp.toPx())
                    },
                    layerBlock = {
                        val progress = dampedDragAnimation.pressProgress
                        val scale = lerp(1f, 1f + 16f.dp.toPx() / this.size.width, progress)
                        scaleX = scale
                        scaleY = scale
                    },
                    onDrawSurface = { drawRect(containerColor) }
                )
                .then(interactiveHighlight.modifier)
                .height(64f.dp)
                .fillMaxWidth()
                .padding(4f.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )

        CompositionLocalProvider(
            LocalLiquidBottomTabScale provides lerp(1f, 1.2f, dampedDragAnimation.pressProgress),
            content = {
                Row(
                    Modifier
                        .clearAndSetSemantics {}
                        .alpha(0f)
                        .layerBackdrop(tabsBackdrop)
                        .graphicsLayer { translationX = panelOffset }
                        .drawBackdrop(
                            backdrop = backdrop,
                            shape = { ContinuousCapsule },
                            highlight = { Highlight.Default.copy(alpha = dampedDragAnimation.pressProgress) },
                            effects = {
                                val progress = dampedDragAnimation.pressProgress
                                vibrancy()
                                blur(8f.dp.toPx())
                                lens(24f.dp.toPx() * progress, 24f.dp.toPx() * progress)
                            },
                            onDrawSurface = { drawRect(containerColor) }
                        )
                        .then(interactiveHighlight.modifier)
                        .height(56f.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 4f.dp)
                        .graphicsLayer(colorFilter = ColorFilter.tint(accentColor)),
                    verticalAlignment = Alignment.CenterVertically,
                    content = content
                )
            }
        )

        Box(
            Modifier
                .padding(horizontal = 4f.dp)
                .graphicsLayer {
                    translationX = dampedDragAnimation.value * tabWidth + panelOffset
                }
                .then(interactiveHighlight.gestureModifier)
                .then(dampedDragAnimation.modifier)
                .drawBackdrop(
                    backdrop = rememberCombinedBackdrop(backdrop, tabsBackdrop),
                    shape = { ContinuousCapsule },
                    highlight = { Highlight.Default.copy(alpha = dampedDragAnimation.pressProgress) },
                    shadow = { Shadow(alpha = dampedDragAnimation.pressProgress) },
                    innerShadow = {
                        InnerShadow(radius = 8f.dp * dampedDragAnimation.pressProgress, alpha = dampedDragAnimation.pressProgress)
                    },
                    effects = {
                        lens(
                            12f.dp.toPx() * dampedDragAnimation.pressProgress,
                            12f.dp.toPx() * dampedDragAnimation.pressProgress,
                            chromaticAberration = DefaultChromaticAberration
                        )
                    },
                    layerBlock = {
                        scaleX = dampedDragAnimation.scaleX
                        scaleY = dampedDragAnimation.scaleY
                        val velocity = dampedDragAnimation.velocity / 10f
                        scaleX /= 1f - (velocity * 0.75f).fastCoerceIn(-0.2f, 0.2f)
                        scaleY *= 1f + (velocity * 0.25f).fastCoerceIn(-0.15f, 0.15f)
                    },
                    onDrawSurface = {
                        val progress = dampedDragAnimation.pressProgress
                        drawRect(
                            if (isLightTheme) Color.Black.copy(0.1f) else Color.White.copy(0.1f),
                            alpha = 1f - progress
                        )
                        drawRect(Color.Black.copy(alpha = 0.03f * progress))
                    }
                )
                .height(56f.dp)
                .fillMaxWidth(1f / tabs.size)
        )
    }
}
