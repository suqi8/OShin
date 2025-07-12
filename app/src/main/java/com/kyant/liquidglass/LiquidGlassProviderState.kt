package com.kyant.liquidglass

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer

val LocalLiquidGlassProviderState: ProvidableCompositionLocal<LiquidGlassProviderState> =
    staticCompositionLocalOf { error("CompositionLocal LocalLiquidGlassProviderState not present") }

@Composable
fun rememberLiquidGlassProviderState(): LiquidGlassProviderState {
    val graphicsLayer = rememberGraphicsLayer()
    return remember(graphicsLayer) {
        LiquidGlassProviderState(graphicsLayer)
    }
}

class LiquidGlassProviderState internal constructor(
    internal val graphicsLayer: GraphicsLayer
) {

    internal var rect: Rect? by mutableStateOf(null)
}
