package com.suqi8.oshin.ui.animation

import android.animation.ValueAnimator
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FloatSpringSpec
import androidx.compose.animation.core.TargetBasedAnimation
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.getVelocityFromNanos
import androidx.compose.foundation.MutatorMutex
import androidx.compose.runtime.MonotonicFrameClock
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope

class FloatAnimation(initialValue: Float) {
    var value by mutableStateOf(initialValue)
        private set

    var velocity by mutableStateOf(0f)
        private set

    var targetValue by mutableStateOf(initialValue)
        private set

    var isRunning by mutableStateOf(false)
        private set

    private val mutatorMutex = MutatorMutex()

    suspend fun snapTo(targetValue: Float) {
        mutatorMutex.mutate {
            this.value = targetValue
            this.targetValue = targetValue
            endAnimation()
        }
    }

    suspend fun animateTo(
        targetValue: Float,
        animationSpec: AnimationSpec<Float> = FloatSpringSpec(),
        initialVelocity: Float = velocity,
        block: (FloatAnimation.() -> Unit)? = null
    ) {
        mutatorMutex.mutate {
            val durationScale = ValueAnimator.getDurationScale()
            if (durationScale == 0f) {
                snapTo(targetValue)
                return@mutate
            }

            val animation = TargetBasedAnimation(
                animationSpec = animationSpec,
                typeConverter = Float.VectorConverter,
                initialValue = value,
                targetValue = targetValue,
                initialVelocity = initialVelocity
            )

            this.targetValue = targetValue
            isRunning = true

            try {
                var lastFrameTimeNanos = -1L
                var frame = 0L

                coroutineScope {
                    val clock = this.coroutineContext[MonotonicFrameClock] ?: return@coroutineScope
                    while (frame < animation.durationNanos) {
                        frame = clock.withFrameNanos { frameTimeNanos ->
                            if (lastFrameTimeNanos == -1L) {
                                lastFrameTimeNanos = frameTimeNanos
                            }
                            val playTimeNanos = (frameTimeNanos - lastFrameTimeNanos) / durationScale
                            (frame + playTimeNanos.toLong()).coerceAtMost(animation.durationNanos)
                        }

                        value = animation.getValueFromNanos(frame)
                        velocity = animation.getVelocityFromNanos(frame)
                        block?.invoke(this@FloatAnimation)
                    }
                }
                endAnimation()
            } catch (e: CancellationException) {
                endAnimation()
                throw e
            } finally {
                block?.invoke(this@FloatAnimation)
            }
        }
    }

    private fun endAnimation() {
        if (isRunning) {
            value = targetValue
            velocity = 0f
            isRunning = false
        }
    }
}

//
// 扩展函数，用于方便地调用 animateTo
//
suspend fun FloatAnimation.animateTo(
    targetValue: Float,
    animationSpec: AnimationSpec<Float> = FloatSpringSpec(),
    block: (FloatAnimation.() -> Unit)? = null
) {
    animateTo(targetValue, animationSpec, velocity, block)
}
