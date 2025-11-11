package com.suqi8.oshin.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * [suspend]
 * 从 ImageBitmap 中异步提取主色调。
 *
 * @param icon 要分析的 ImageBitmap
 * @param defaultColor 提取失败时的兜底颜色
 * @return 提取到的 Color
 */
suspend fun getAutoColor(
    icon: ImageBitmap,
    defaultColor: Color = Color.White // 提供一个默认的兜底值
): Color {
    // 在 IO 线程上执行颜色提取，因为它可能耗时
    return withContext(Dispatchers.IO) {
        try {
            val bitmap = icon.asAndroidBitmap()
            // 使用 Palette 库提取
            Palette.from(bitmap).generate().dominantSwatch?.rgb?.let {
                Color(it) // 成功提取到主色
            } ?: defaultColor // 提取失败，使用兜底颜色
        } catch (e: Exception) {
            defaultColor // 发生异常，使用兜底颜色
        }
    }
}
