package com.suqi8.oshin.ui.nav.transition

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class MutableTransitionProperties : TransitionProperties {
    private var _offset by mutableStateOf(Offset.Zero)
    override val offset: Long get() = _offset.packedValue
    fun setOffset(offset: Offset) { _offset = offset }

    private var _scale by mutableFloatStateOf(1f)
    override val scale: Float get() = _scale
    fun setScale(scale: Float) { _scale = scale }

    private var _blurRadius by mutableStateOf(0.dp)
    override val blurRadius: Dp get() = _blurRadius
    fun setBlurRadius(blurRadius: Dp) { _blurRadius = blurRadius }
}
