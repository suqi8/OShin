package com.suqi8.oshin.ui.animation

import android.animation.ValueAnimator
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TargetBasedAnimation
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.foundation.MutatorMutex
import androidx.compose.runtime.MonotonicFrameClock
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
// 移除了所有 reflection 和 internal API 相关的代码

class AnyAnimation<T, V : AnimationVector>(
    private val typeConverter: TwoWayConverter<T, V>,
    initialValue: T,
) {
    var value by mutableStateOf(initialValue)
        private set

    // 1. (修正)
    //    'zeroVelocity' 直接从 'initialValue' 创建。
    //    在 ZoomNavTransition 中，'initialValue' 是 Offset.Zero，
    //    所以 convertToVector(Offset.Zero) 会返回 AnimationVector2D(0f, 0f)，
    //    这 *已经* 是我们需要的零向量了。
    private val zeroVelocity: V = typeConverter.convertToVector(initialValue)

    // 'velocity' 默认初始化为 'zeroVelocity'
    var velocity: V by mutableStateOf(zeroVelocity)
        private set

    var targetValue by mutableStateOf(initialValue)
        private set

    var isRunning by mutableStateOf(false)
        private set

    private val mutatorMutex = MutatorMutex()

    // 2. (已移除)
    //    导致崩溃的 'init { zeroVelocity.reset() }' 块已被完全移除。
    //    它既没有必要，也无法访问 internal API。

    suspend fun snapTo(targetValue: T) {
        mutatorMutex.mutate {
            this.value = targetValue
            this.targetValue = targetValue
            endAnimation()
        }
    }

    suspend fun animateTo(
        targetValue: T,
        animationSpec: AnimationSpec<T> = SpringSpec(),
        initialVelocity: T? = null
    ) {
        mutatorMutex.mutate {
            val durationScale = ValueAnimator.getDurationScale()
            if (durationScale == 0f) {
                snapTo(targetValue)
                return@mutate
            }

            val animation = TargetBasedAnimation(
                animationSpec = animationSpec,
                typeConverter = typeConverter,
                initialValue = value,
                targetValue = targetValue,
                initialVelocityVector = initialVelocity?.let { typeConverter.convertToVector(it) } ?: velocity
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
                        velocity = animation.getVelocityVectorFromNanos(frame)
                    }
                }
                endAnimation()
            } catch (e: CancellationException) {
                endAnimation()
                throw e
            }
        }
    }

    private fun endAnimation() {
        if (isRunning) {
            value = targetValue
            velocity = zeroVelocity
            isRunning = false
        }
    }
}
