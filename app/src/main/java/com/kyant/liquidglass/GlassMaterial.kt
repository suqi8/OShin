package com.kyant.liquidglass

import androidx.annotation.FloatRange
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
