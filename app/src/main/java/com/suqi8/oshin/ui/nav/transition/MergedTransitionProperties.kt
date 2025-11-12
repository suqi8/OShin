package com.suqi8.oshin.ui.nav.transition

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class MergedTransitionProperties(
    private val findProperties: () -> List<TransitionProperties>
) : TransitionProperties {
    private val propertiesState by derivedStateOf { findProperties() }

    override val offset: Long by derivedStateOf {
        propertiesState.fold(Offset.Zero) { acc, properties -> acc + Offset(properties.offset) }.packedValue
    }
    override val scale: Float by derivedStateOf {
        propertiesState.fold(1f) { acc, properties -> acc * properties.scale }
    }
    override val blurRadius: Dp by derivedStateOf {
        propertiesState.maxOfOrNull { it.blurRadius } ?: 0.dp
    }
}
