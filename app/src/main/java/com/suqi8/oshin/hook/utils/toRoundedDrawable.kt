package com.suqi8.oshin.hook.utils

import androidx.core.graphics.createBitmap

fun android.graphics.drawable.Drawable.toRoundedDrawable(
    context: android.content.Context,
    cornerRadiusDp: Float
): androidx.core.graphics.drawable.RoundedBitmapDrawable {
    // 将Drawable转换为Bitmap
    val bitmap =
        createBitmap(this.intrinsicWidth.coerceAtLeast(1), this.intrinsicHeight.coerceAtLeast(1))
    val canvas = android.graphics.Canvas(bitmap)
    this.setBounds(0, 0, canvas.width, canvas.height)
    this.draw(canvas)

    // 将dp转换为px
    val cornerRadiusPx = cornerRadiusDp * context.resources.displayMetrics.density

    // 创建并返回带圆角的RoundedBitmapDrawable
    return androidx.core.graphics.drawable.RoundedBitmapDrawableFactory.create(
        context.resources,
        bitmap
    ).apply {
        cornerRadius = cornerRadiusPx
        setAntiAlias(true)
    }
}
