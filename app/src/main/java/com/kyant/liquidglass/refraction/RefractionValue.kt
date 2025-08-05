package com.kyant.liquidglass.refraction

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density

@Immutable
sealed interface RefractionValue {

    @Stable
    fun toPx(size: Size, density: Density): Float
}
