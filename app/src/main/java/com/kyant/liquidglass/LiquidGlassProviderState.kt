package com.kyant.liquidglass

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer

@Composable
fun rememberLiquidGlassProviderState(
    backgroundColor: Color?
): LiquidGlassProviderState {
    val graphicsLayer = rememberGraphicsLayer()
    return remember(backgroundColor, graphicsLayer) {
        LiquidGlassProviderState(
            backgroundColor = backgroundColor,
            graphicsLayer = graphicsLayer
        )
    }
}

@Stable
class LiquidGlassProviderState internal constructor(
    val backgroundColor: Color?,
    internal val graphicsLayer: GraphicsLayer
) {

    internal var position: Offset by mutableStateOf(Offset.Zero)
}
