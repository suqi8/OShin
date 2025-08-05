package com.kyant.liquidglass.refraction

import androidx.compose.runtime.Immutable

@Immutable
sealed interface Refraction {

    val height: RefractionValue

    val amount: RefractionValue
}
