package com.kyant.liquidglass

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.runtime.Immutable

/**
 * 表示液态玻璃元素的视觉样式。
 *
 * 此数据类封装了定义液态玻璃效果外观的各种属性，
 * 例如其形状、内部折射、材质、边框和渗色。
 *
 * @property shape 定义液态玻璃轮廓的 [CornerBasedShape]。
 * @property innerRefraction 控制光线如何在玻璃内部弯曲的 [InnerRefraction] 属性。默认为 [InnerRefraction.Default]。
 * @property material 定义玻璃本身视觉特性（例如颜色和透明度）的 [GlassMaterial]。默认为 [GlassMaterial.Default]。
 * @property border 定义玻璃边框外观的 [GlassBorder] 属性。默认为 [GlassBorder.Default]。
 * @property bleed 控制背景颜色如何渗入玻璃的 [Bleed] 属性。默认为 [Bleed.None]。
 */
@Immutable
data class LiquidGlassStyle(
    val shape: CornerBasedShape,
    val innerRefraction: InnerRefraction = InnerRefraction.Default,
    val material: GlassMaterial = GlassMaterial.Default,
    val border: GlassBorder = GlassBorder.Default,
    val bleed: Bleed = Bleed.None
)
