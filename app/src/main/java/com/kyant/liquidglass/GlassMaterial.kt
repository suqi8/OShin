package com.kyant.liquidglass

import androidx.annotation.FloatRange
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 代表玻璃般效果的材质属性。
 *
 * 此数据类定义了玻璃材质的视觉特性，
 * 允许自定义其外观。它被设计为不可变的
 * 以确保在 Compose 中的稳定性。
 *
 * @property blurRadius 应用于玻璃后面内容的模糊效果的半径。
 *                      默认为 `4.dp`。
 * @property tint 应用于玻璃的颜色色调。`Color.Unspecified` 表示没有色调。
 *                默认为 `Color.Unspecified`。
 * @property contrast 调整通过玻璃可见内容的对比度。
 *                    取值范围从 -1.0（最小对比度）到 1.0（最大对比度）。
 *                    默认为 `0f`（对比度不变）。
 * @property whitePoint 调整通过玻璃可见内容的白点。
 *                      取值范围从 -1.0（较暗的白色）到 1.0（较亮的白色）。
 *                      默认为 `0f`（白点不变）。
 * @property chromaMultiplier 乘以通过玻璃可见内容的色度（色彩饱和度）。
 *                           取值范围从 0.5（较低饱和度）到 2.0（较高饱和度）。
 *                           默认为 `1f`（色度不变）。
 */
@Immutable
data class GlassMaterial(
    val blurRadius: Dp = 4.dp,
    val tint: Color = Color.Unspecified,
    @param:FloatRange(from = -1.0, to = 1.0) val contrast: Float = 0f,
    @param:FloatRange(from = -1.0, to = 1.0) val whitePoint: Float = 0f,
    @param:FloatRange(from = 0.5, to = 2.0) val chromaMultiplier: Float = 1f
) {

    companion object {

        @Stable
        val Default: GlassMaterial = GlassMaterial()
    }
}
