package com.kyant.liquidglass

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.dp

@Immutable
sealed interface Refraction {

    val height: RefractionValue

    val amount: RefractionValue
}

@Immutable
data class InnerRefraction(
    override val height: RefractionValue,
    override val amount: RefractionValue,
    val eccentricFactor: Float = 1f
) : Refraction {

    companion object {

        @Stable
        val Default: InnerRefraction =
            InnerRefraction(
                height = RefractionValue(8.dp),
                amount = RefractionValue((-16).dp),
                eccentricFactor = 0.25f
            )
    }
}
