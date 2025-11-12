package com.suqi8.oshin.ui.nav.transition.zoom

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.Velocity
import kotlin.math.PI
import kotlin.math.abs

/**
 * 这是一个基于你提供的反编译 Java 代码 逻辑的 Kotlin 重写版本。
 *
 * 它侦听多点触控手势，并计算平移 (pan)、缩放 (zoom) 和旋转 (rotation)。
 */
suspend fun PointerInputScope.detectTransformGestures(
    onGestureStarted: () -> Unit = {},
    onGestureStopped: (panVelocity: Velocity, zoomVelocity: Float, rotationVelocity: Float) -> Unit = { _, _, _ -> },
    onGesture: (centroid: Offset, pan: Offset, zoom: Float, rotation: Float) -> Unit
) {
    // 1. 初始化速度追踪器，对应旧代码中的 L$1, L$2, L$3
    val panVelocityTracker = VelocityTracker()
    val zoomVelocityTracker = VelocityTracker()
    val rotationVelocityTracker = VelocityTracker()

    awaitPointerEventScope {
        var rotation = 0f
        var zoom = 1f
        var pan = Offset.Zero
        var pastTouchSlop = false
        val touchSlop = viewConfiguration.touchSlop

        // 2. 等待第一次按下
        awaitFirstDown(requireUnconsumed = false)

        do {
            val event = awaitPointerEvent()
            val canceled = event.changes.any { it.isConsumed }

            if (!canceled) {
                // 3. 计算中心点、缩放和旋转
                val zoomChange = event.calculateCentroidSize(useCurrent = true) / event.calculateCentroidSize(useCurrent = false)
                val rotationChange = event.calculateRotation()
                val panChange = event.calculateCentroid(useCurrent = true) - event.calculateCentroid(useCurrent = false)

                if (!pastTouchSlop) {
                    // 累积变化量
                    zoom *= zoomChange
                    rotation += rotationChange
                    pan += panChange

                    // 4. 检查是否超过了 "touch slop" (最小手势距离)
                    val centroidSize = event.calculateCentroidSize(useCurrent = false)
                    val panDistance = pan.getDistance()
                    val zoomDistance = abs(1 - zoom) * centroidSize
                    val rotationDistance = abs(rotation * PI / 180f * centroidSize).toFloat()

                    if (panDistance > touchSlop || zoomDistance > touchSlop || rotationDistance > touchSlop) {
                        onGestureStarted() // 5. 触发手势开始回调
                        pastTouchSlop = true
                    }
                }

                if (pastTouchSlop) {
                    val centroid = event.calculateCentroid(useCurrent = false)
                    if (zoomChange != 1f || rotationChange != 0f || panChange != Offset.Zero) {
                        // 6. 触发手势进行中回调
                        onGesture(centroid, panChange, zoomChange, rotationChange)

                        // 7. 将当前数据添加到速度追踪器
                        val time = event.changes.first().uptimeMillis
                        panVelocityTracker.addPosition(time, event.calculateCentroid(useCurrent = true))
                        // 我们只关心 zoom 和 rotation 的 "x" 速度 (即 float 值)
                        zoomVelocityTracker.addPosition(time, Offset(event.calculateCentroidSize(useCurrent = true), 0f))
                        rotationVelocityTracker.addPosition(time, Offset(event.calculateRotation(), 0f))
                    }
                }
            }
        } while (!canceled && event.changes.any { it.pressed })

        if (pastTouchSlop) {
            // 8. 触发手势停止回调，并传递速度

            // !! 修复点 !!
            // 'calculateVelocity()' ALREADY returns the 'Velocity' inline class instance.
            // 我们不需要也不能调用它的 internal 构造函数。
            val panVelocity = panVelocityTracker.calculateVelocity()
            val zoomVelocity = zoomVelocityTracker.calculateVelocity().x
            val rotationVelocity = rotationVelocityTracker.calculateVelocity().x

            onGestureStopped(panVelocity, zoomVelocity, rotationVelocity)
        }
    }
}
