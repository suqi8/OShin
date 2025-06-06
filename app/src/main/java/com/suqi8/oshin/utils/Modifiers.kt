package com.suqi8.oshin.utils

import android.annotation.SuppressLint
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 绘制阴影范围
 * [top] 顶部范围
 * [start] 开始范围
 * [bottom] 底部范围
 * [end] 结束范围
 * Create empty Shadow elevation
 */
open class ShadowElevation(
    val top: Dp = 0.dp,
    private val start: Dp = 0.dp,
    private val bottom: Dp = 0.dp,
    private val end: Dp = 0.dp
) {
    companion object : ShadowElevation()
}


/**
 * 自定义彩色阴影绘制修饰符
 *
 * @param color 阴影颜色
 * @param alpha 阴影透明度（0f~1f）
 * @param borderRadius 组件圆角半径（仅在非圆形绘制时生效）
 * @param shadowRadius 阴影模糊半径（控制阴影扩散范围）
 * @param offsetX 阴影水平方向偏移量
 * @param offsetY 阴影垂直方向偏移量
 * @param roundedRect 是否自动使用圆形绘制（true 则自动使用高度的一半作为圆角）
 */
@SuppressLint("UseKtx")
fun Modifier.drawColoredShadow(
    color: Color,
    alpha: Float = 0.2f,
    borderRadius: Dp = 0.dp,
    shadowRadius: Dp = 20.dp, // 给一个默认的模糊半径
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    roundedRect: Boolean = true
): Modifier = this.drawBehind {
    /**将颜色转换为Argb的Int类型*/
    val transparentColor = color.copy(alpha = 0f).toArgb() // 使用 toArgb() 更符合 Compose 习惯
    val shadowColor = color.copy(alpha = alpha).toArgb()

    /**调用Canvas绘制*/
    this.drawIntoCanvas {
        val paint = Paint()
        paint.color = Color.Transparent // 画笔本身是透明的
        /**调用底层 framework Paint 绘制*/
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = transparentColor
        /**绘制阴影*/
        frameworkPaint.setShadowLayer(
            shadowRadius.toPx(),
            offsetX.toPx(),
            offsetY.toPx(),
            shadowColor
        )
        /**形状绘制*/
        it.drawRoundRect(
            left = 0f,
            top = 0f,
            right = this.size.width,
            bottom = this.size.height,
            // 如果是圆形，则圆角半径为高度的一半，否则使用传入的 borderRadius
            radiusX = if (roundedRect) this.size.height / 2 else borderRadius.toPx(),
            radiusY = if (roundedRect) this.size.height / 2 else borderRadius.toPx(),
            paint = paint
        )
    }
}
