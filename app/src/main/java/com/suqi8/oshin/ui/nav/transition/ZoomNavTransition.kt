package com.suqi8.oshin.ui.nav.transition

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.suqi8.oshin.ui.animation.AnyAnimation
import com.suqi8.oshin.ui.animation.FloatAnimation
import com.suqi8.oshin.ui.nav.transition.zoom.detectTransformGestures
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.abs

private val EmphasizedEasing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
private const val ZOOM_ENTER_DURATION_MS = 500
private const val ZOOM_EXIT_DURATION_MS = 450

class ZoomNavTransition(
    private val onPop: () -> Unit,
    initialAppearance: NavItemAppearance,
    override val animationScope: CoroutineScope,
    override val navAnimationSpecs: NavAnimationSpecs,
    private val mergedTransitionProperties: MergedTransitionProperties
) : NavTransition() {

    override var appearance by mutableStateOf(initialAppearance)
    val transitionState = ZoomNavTransitionState()

    private val progressAnimatable = Animatable(
        initialValue = when (initialAppearance) {
            NavItemAppearance.WillEnter -> 0f
            else -> 1f
        }
    )
    private val progress: Float by progressAnimatable.asState()

    private val panAnimation = AnyAnimation(Offset.VectorConverter, Offset.Zero)
    private val zoomAnimation = FloatAnimation(1f)
    private val rotationAnimation = FloatAnimation(0f)

    private var centroid by mutableStateOf(Offset.Zero)
    private var layoutSize by mutableStateOf(Size.Zero)
    private val targetRect: Rect get() = Rect(Offset.Zero, layoutSize)

    private val isInteracting by derivedStateOf {
        panAnimation.isRunning || zoomAnimation.isRunning || rotationAnimation.isRunning ||
                panAnimation.value != Offset.Zero || zoomAnimation.value != 1f || rotationAnimation.value != 0f
    }

    override val shouldDrawBehind by derivedStateOf {
        !(appearance == NavItemAppearance.Entered && !isInteracting)
    }

    private fun onProgress(currentProgress: Float) {
        val easedProgress = EmphasizedEasing.transform(currentProgress)
        prevItemProperties.setScale(lerp(1f, 0.9f, easedProgress))
        prevItemProperties.setBlurRadius(24.dp * easedProgress)
    }

    init {
        animationScope.launch {
            snapshotFlow { progressAnimatable.value }.collect {
                onProgress(it)
            }
        }
    }

    override suspend fun enter() {
        snapshotFlow { transitionState.sourceRect != null && layoutSize != Size.Zero }.first { it }
        appearance = NavItemAppearance.Entering

        progressAnimatable.animateTo(
            targetValue = 1f,
            animationSpec = navAnimationSpecs.zoomEnter // <--- 修复1: 恢复 Spring 动画
        )

        appearance = NavItemAppearance.Entered
    }

    override suspend fun exit() {
        try {
            appearance = NavItemAppearance.Exiting
            panAnimation.snapTo(Offset.Zero)
            zoomAnimation.snapTo(1f)
            rotationAnimation.snapTo(0f)

            progressAnimatable.animateTo(
                targetValue = 0f,
                animationSpec = navAnimationSpecs.zoomExit // <--- 修复2: 恢复 Spring 动画
            )
        } finally {
            appearance = NavItemAppearance.Exited
            onPop()
        }
    }

    @Composable
    override fun Content(content: @Composable () -> Unit) {
        val sourceRect = transitionState.sourceRect ?: targetRect
        val sourceShape = transitionState.sourceShape

        val gestureModifier = Modifier.pointerInput(Unit) {
            // ... (手势部分保持不变) ...
            detectTransformGestures(
                onGestureStarted = {
                    animationScope.launch { progressAnimatable.stop() }
                    if (appearance < NavItemAppearance.WillExit) appearance = NavItemAppearance.WillExit
                },
                onGestureStopped = { panVelocity, zoomVelocity, rotationVelocity ->
                    if (zoomAnimation.value < 0.85f && appearance >= NavItemAppearance.WillExit) {
                        animationScope.launch { exit() }
                    } else {
                        animationScope.launch {
                            if (progressAnimatable.value < 1f) progressAnimatable.animateTo(1f, tween(ZOOM_ENTER_DURATION_MS, easing = EmphasizedEasing))
                            panAnimation.animateTo(Offset.Zero, navAnimationSpecs.zoomPan, Offset(-panVelocity.x, -panVelocity.y))
                        }
                        animationScope.launch { zoomAnimation.animateTo(1f, navAnimationSpecs.backEnter, -zoomVelocity) }
                        animationScope.launch { rotationAnimation.animateTo(0f, navAnimationSpecs.backEnter, -rotationVelocity) }
                    }
                },
                onGesture = { gestureCentroid, gesturePan, gestureZoom, gestureRotation ->
                    animationScope.launch { if (progressAnimatable.value < 1f) progressAnimatable.snapTo(1f) }
                    centroid = gestureCentroid
                    animationScope.launch {
                        panAnimation.snapTo(panAnimation.value + gesturePan)
                        zoomAnimation.snapTo(zoomAnimation.value * gestureZoom)
                        rotationAnimation.snapTo(rotationAnimation.value + gestureRotation)
                    }
                }
            )
        }

        // vvvvvvvvvv FIX: 彻底重构 graphicsLayer 逻辑 vvvvvvvvvv
        val modifier = Modifier
            .onSizeChanged { layoutSize = it.toSize() }
            .then(if (isNotExiting) gestureModifier else Modifier)
            // 外层 Layer: 仅处理父级变换和手势交互
            .graphicsLayer {
                val targetWidth = targetRect.width.coerceAtLeast(1f)
                val targetHeight = targetRect.height.coerceAtLeast(1f)

                // 统一处理 alpha，交互时为 1，否则根据 progress
                alpha = if (isInteracting) 1f else EmphasizedEasing.transform(progress)

                if (isInteracting) {
                    // --- A. 交互模式 (复刻 Java 版物理效果 ) ---
                    val pan = panAnimation.value
                    val zoom = zoomAnimation.value
                    val rotation = rotationAnimation.value

                    // Java 版的视差缩放：基于 X 轴平移的衰减
                    val panParallaxFactor = (1.0f - (abs(pan.x) / targetWidth) * 0.75f).coerceIn(0f, 1f)
                    val finalInteractionScale = zoom * panParallaxFactor

                    scaleX = finalInteractionScale
                    scaleY = finalInteractionScale
                    rotationZ = rotation

                    // Java 版的平移：X 轴 1:1，Y 轴压缩 [cite: 1696]
                    translationX = pan.x
                    translationY = pan.y * 0.25f

                    // 变换原点基于手势中心点
                    val originCentroid = if (centroid == Offset.Zero) targetRect.center else centroid
                    val currentWidth = targetWidth * scaleX
                    val currentHeight = targetHeight * scaleY
                    val pivotX = if (currentWidth > 0) (originCentroid.x - translationX) / currentWidth else 0f
                    val pivotY = if (currentHeight > 0) (originCentroid.y - translationY) / currentHeight else 0f
                    transformOrigin = TransformOrigin(pivotX.coerceIn(0f, 1f), pivotY.coerceIn(0f, 1f))

                } else {
                    // --- B. 非交互模式 (进入/退出动画) ---
                    val easedProgress = EmphasizedEasing.transform(progress)
                    val rect = lerp(sourceRect, targetRect, easedProgress)

                    // 计算进入/退出时的缩放和平移
                    scaleX = if (targetWidth == 0f) 1f else rect.width / targetWidth
                    scaleY = if (targetHeight == 0f) 1f else rect.height / targetHeight
                    translationX = rect.left
                    translationY = rect.top
                    rotationZ = 0f
                    transformOrigin = TransformOrigin(0f, 0f)
                }

                // 裁剪逻辑保持不变
                if (sourceShape != null) {
                    clip = true
                    shape = sourceShape
                }
            }
            // 内层 Layer: 仅处理进入/退出的位置和大小变换
            .graphicsLayer {
                // 仅在非交互模式下，此层才生效
                if (!isInteracting) {
                    val easedProgress = EmphasizedEasing.transform(progress)
                    val rect = lerp(sourceRect, targetRect, easedProgress)

                    val targetWidth = targetRect.width.coerceAtLeast(1f)
                    val targetHeight = targetRect.height.coerceAtLeast(1f)

                    // 这一层的变换是相对于外层 Layer 的，所以是纯粹的进入/退出变换
                    translationX = rect.left
                    translationY = rect.top
                    scaleX = rect.width / targetWidth
                    scaleY = rect.height / targetHeight
                    transformOrigin = TransformOrigin(0f, 0f)
                }

                // 裁剪形状始终在最内层应用
                if (sourceShape != null) {
                    clip = true
                    shape = sourceShape
                }
            }
            .drawBehind {
                if (shouldDrawBehind) {
                    drawRect(Color.Black.copy(alpha = (1f - progress) * 0.3f))
                }
            }
        // ^^^^^^^^^^ FIX ^^^^^^^^^^

        Box(modifier = modifier) {
            content()
        }
    }
}

class ZoomNavTransitionState {
    var sourceRect by mutableStateOf<Rect?>(null)
    var sourceShape by mutableStateOf<Shape?>(null)
    var sourceColor by mutableStateOf<Color?>(null)
}

private fun Long.toOffset(): Offset {
    if (this == Offset.Unspecified.packedValue) {
        return Offset.Zero
    }
    return Offset(this)
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return (1 - fraction) * start + fraction * stop
}
