package com.kyant.liquidglass.sampler

import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.layer.GraphicsLayer

@ExperimentalLuminanceSamplerApi
interface LuminanceSampler {

    val sampleIntervalMillis: Long

    @get:FloatRange(from = 0.0, to = 1.0)
    val luminance: Float

    suspend fun sample(graphicsLayer: GraphicsLayer)
}
