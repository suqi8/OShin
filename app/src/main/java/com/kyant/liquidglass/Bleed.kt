package com.kyant.liquidglass

import androidx.annotation.FloatRange
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Bleed(
    val amount: RefractionValue = RefractionValue.None,
    val blurRadius: Dp = 0.dp,
    @param:FloatRange(from = 0.0, to = 1.0) val opacity: Float = 0f
) {

    companion object {

        @Stable
        val None: Bleed = Bleed()
    }
}
