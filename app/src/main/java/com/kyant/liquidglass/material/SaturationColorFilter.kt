package com.kyant.liquidglass.material

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix

fun saturationColorFilter(saturation: Float): ColorFilter {
    return ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(saturation) })
}
