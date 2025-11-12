package com.suqi8.oshin.ui.nav.transition

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import com.suqi8.oshin.ui.animation.FloatAnimation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SwipeNavTransition(
    private val orientation: Orientation,
    private val onPop: () -> Unit,
    initialAppearance: NavItemAppearance,
    override val animationScope: CoroutineScope,
    override val navAnimationSpecs: NavAnimationSpecs,
    private val mergedTransitionProperties: MergedTransitionProperties
) : NavTransition() {

    override var appearance by mutableStateOf(initialAppearance)

    private val progressAnimation = FloatAnimation(
        initialValue = when (initialAppearance) {
            NavItemAppearance.WillEnter -> 0f
            else -> 1f
        }
    )

    private var layoutSize by mutableStateOf(0f)

    override val shouldDrawBehind by derivedStateOf {
        appearance != NavItemAppearance.Entered
    }

    private fun onProgress(progress: Float) {
        val offset = (layoutSize * progress / 4f)
        if (orientation == Orientation.Horizontal) {
            prevItemProperties.setOffset(Offset(offset, 0f))
        } else {
            prevItemProperties.setOffset(Offset(0f, offset))
        }
    }

    override suspend fun enter() {
        snapshotFlow { layoutSize }
            .first { it > 0f }

        appearance = NavItemAppearance.Entering
        progressAnimation.animateTo(
            targetValue = 1f,
            animationSpec = navAnimationSpecs.swipeEnter
        ) {
            onProgress(value)
        }
        appearance = NavItemAppearance.Entered
    }

    override suspend fun exit() {
        try {
            appearance = NavItemAppearance.Exiting
            progressAnimation.animateTo(
                targetValue = 0f,
                animationSpec = navAnimationSpecs.swipeExit
            ) {
                onProgress(value)
            }
        } finally {
            appearance = NavItemAppearance.Exited
            onPop()
        }
    }

    @Composable
    override fun Content(content: @Composable () -> Unit) {
        val progress = progressAnimation.value

        val draggableState = rememberDraggableState { delta ->
            animationScope.launch {
                val currentProgress = progressAnimation.value
                val targetProgress = if (layoutSize > 0) {
                    currentProgress - (delta / layoutSize)
                } else {
                    currentProgress
                }
                progressAnimation.snapTo(targetProgress.coerceIn(0f, 1f))
                onProgress(progressAnimation.value)
            }
        }

        val gestureModifier = Modifier.draggable(
            state = draggableState,
            orientation = orientation,
            onDragStarted = {
                appearance = NavItemAppearance.WillExit
            },
            // vvvvvvvvvv FIX vvvvvvvvvv
            onDragStopped = { velocity ->
                animationScope.launch {
                    val currentProgress = progressAnimation.value
                    // 设置一个速度阈值，避免微小移动被误判为 fling
                    val flingThreshold = 100f

                    // 复刻旧版逻辑：结合速度和位置来判断最终状态
                    val willEnter = when {
                        // 速度足够大，向“进入”方向滑动
                        velocity > flingThreshold -> true
                        // 速度足够大，向“退出”方向滑动
                        velocity < -flingThreshold -> false
                        // 速度不大，根据位置判断
                        else -> currentProgress > 0.5f
                    }

                    if (willEnter) {
                        // 弹回
                        appearance = NavItemAppearance.Entering
                        progressAnimation.animateTo(
                            targetValue = 1f,
                            animationSpec = navAnimationSpecs.swipeEnter,
                            initialVelocity = velocity
                        ) {
                            onProgress(value)
                        }
                        appearance = NavItemAppearance.Entered
                    } else {
                        // 触发退出
                        // 为了让退出的动画也具有惯性，我们需要自己调用 animateTo 而不是直接调用 exit()
                        try {
                            appearance = NavItemAppearance.Exiting
                            progressAnimation.animateTo(
                                targetValue = 0f,
                                animationSpec = navAnimationSpecs.swipeExit,
                                initialVelocity = velocity
                            ) {
                                onProgress(value)
                            }
                        } finally {
                            appearance = NavItemAppearance.Exited
                            onPop()
                        }
                    }
                }
            }
            // ^^^^^^^^^^ FIX ^^^^^^^^^^
        )

        val modifier = Modifier
            .onSizeChanged {
                layoutSize = if (orientation == Orientation.Horizontal) {
                    it.width.toFloat()
                } else {
                    it.height.toFloat()
                }
            }
            .then(if (isNotExiting) gestureModifier else Modifier)
            .graphicsLayer {
                translationX = if (orientation == Orientation.Horizontal) {
                    layoutSize * (1f - progress)
                } else {
                    mergedTransitionProperties.offset.toOffset().x
                }
                translationY = if (orientation == Orientation.Vertical) {
                    layoutSize * (1f - progress)
                } else {
                    mergedTransitionProperties.offset.toOffset().y
                }
                scaleX = mergedTransitionProperties.scale
                scaleY = mergedTransitionProperties.scale
            }
            .drawBehind {
                if (shouldDrawBehind) {
                    drawRect(Color.Black.copy(alpha = (1f - progress) * 0.3f))
                }
            }

        androidx.compose.foundation.layout.Box(modifier = modifier) {
            content()
        }
    }
}

private fun Long.toOffset(): Offset {
    if (this == Offset.Unspecified.packedValue) {
        return Offset.Zero
    }
    return Offset(this)
}
