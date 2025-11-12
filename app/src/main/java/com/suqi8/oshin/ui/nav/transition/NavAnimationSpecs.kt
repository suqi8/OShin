package com.suqi8.oshin.ui.nav.transition

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.ui.geometry.Offset

// 这个版本 1:1 匹配你旧版(all.java.txt) 的物理参数[cite: 621-633]
data class NavAnimationSpecs(
    val swipeEnter: SpringSpec<Float> = spring(
        dampingRatio = 1.0f,
        stiffness = 300.0f,
        visibilityThreshold = 0.001f
    ), // [cite: 624]
    val swipeExit: SpringSpec<Float> = spring(
        dampingRatio = 1.0f,
        stiffness = 300.0f,
        visibilityThreshold = 0.001f
    ), // [cite: 624]
    val zoomEnter: SpringSpec<Float> = spring(
        dampingRatio = 1.0f,
        stiffness = 300.0f,
        visibilityThreshold = 0.001f
    ), // [cite: 624]
    val zoomExit: SpringSpec<Float> = spring(
        dampingRatio = 1.0f,
        stiffness = 300.0f,
        visibilityThreshold = 0.001f
    ), // [cite: 624]
    val zoomPan: SpringSpec<Offset> = spring(
        dampingRatio = 1.0f,
        stiffness = 300.0f,
        // (修正) 匹配旧版的 Offset 阈值[cite: 624]
        visibilityThreshold = Offset(0.5f, 0.5f)
    ),
    val dialogEnter: SpringSpec<Float> = spring(
        dampingRatio = 1.0f,
        stiffness = 600.0f, // [cite: 624]
        visibilityThreshold = 0.001f
    ),
    val dialogExit: SpringSpec<Float> = spring(
        dampingRatio = 1.0f,
        stiffness = 1000.0f, // [cite: 624]
        visibilityThreshold = 0.001f
    ),

    // vvvvvvvvvv FIX vvvvvvvvvv
    // *** 关键修复点 ***
    val backEnter: SpringSpec<Float> = spring(
        dampingRatio = 1.0f,
        stiffness = 300.0f, // (错误修正: 从 1000f 改回 300f) [cite: 624]
        visibilityThreshold = 0.001f
    ),
    // ^^^^^^^^^^ FIX ^^^^^^^^^^
    val backExit: SpringSpec<Float> = spring(
        dampingRatio = 1.0f,
        stiffness = 300.0f, // [cite: 624]
        visibilityThreshold = 0.001f
    )
) {
    companion object {
        val Default = NavAnimationSpecs()
    }
}
