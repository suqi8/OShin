package com.suqi8.oshin.tools

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut

class AnimTools {
    fun enterTransition(delayMillis: Int = 0): EnterTransition {
        val easing = CubicBezierEasing(0.36f, 1.44f, 0.48f, 1f)
        return fadeIn(animationSpec = tween(400, delayMillis, easing)) + scaleIn(initialScale = 0.95f, animationSpec = tween(400, delayMillis, easing))
    }

    fun exitTransition(delayMillis: Int = 0): ExitTransition {
        val easing = CubicBezierEasing(0.36f, 1.44f, 0.48f, 1f)
        return fadeOut(animationSpec = tween(300, delayMillis, easing)) + scaleOut(targetScale = 0.95f, animationSpec = tween(300, delayMillis, easing))
    }
}
