package com.kyant.liquidglass

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

@Suppress("FunctionName")
@Stable
fun RefractionValue(value: Dp): RefractionValue.Fixed {
    return RefractionValue.Fixed(value)
}

@Immutable
sealed interface RefractionValue {

    @Stable
    fun toPx(density: Density, size: Size): Float

    @Immutable
    @JvmInline
    value class Fixed(val value: Dp) : RefractionValue {

        override fun toPx(density: Density, size: Size): Float {
            return with(density) { value.toPx() }
        }
    }

    @Immutable
    data object Full : RefractionValue {

        override fun toPx(density: Density, size: Size): Float {
            return -size.minDimension
        }
    }

    @Immutable
    data object Half : RefractionValue {

        override fun toPx(density: Density, size: Size): Float {
            return -size.minDimension / 2f
        }
    }

    @Immutable
    data object None : RefractionValue {

        override fun toPx(density: Density, size: Size): Float {
            return 0f
        }
    }
}
