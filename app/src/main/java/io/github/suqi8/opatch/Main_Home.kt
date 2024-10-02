package io.github.suqi8.opatch

import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.highcapable.yukihookapi.YukiHookAPI
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme


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
){
    companion object : ShadowElevation()
}

/**
 * 绘制基础阴影
 * @param color 颜色
 * @param alpha 颜色透明度
 * @param borderRadius 阴影便捷圆角
 * @param shadowRadius 阴影圆角
 * @param offsetX 偏移X轴
 * @param offsetY 偏移Y轴
 * @param roundedRect 是否绘制圆角就行
 */
fun Modifier.drawColoredShadow(
    color: Color,
    alpha: Float = 0.2f,
    borderRadius: Dp = 0.dp,
    shadowRadius: Dp = 0.dp,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    roundedRect: Boolean = true
) = this.drawBehind {
    /**将颜色转换为Argb的Int类型*/
    val transparentColor = android.graphics.Color.toArgb(color.copy(alpha = .0f).value.toLong())
    val shadowColor = android.graphics.Color.toArgb(color.copy(alpha = alpha).value.toLong())
    /**调用Canvas绘制*/
    this.drawIntoCanvas {
        val paint = Paint()
        paint.color = Color.Transparent
        /**调用底层fragment Paint绘制*/
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
            0f,
            0f,
            this.size.width,
            this.size.height,
            if (roundedRect) this.size.height / 2 else borderRadius.toPx(),
            if (roundedRect) this.size.height / 2 else borderRadius.toPx(),
            paint
        )
    }
}

@Composable
fun Main_Home(padding: PaddingValues,topAppBarScrollBehavior: ScrollBehavior) {
    LazyColumn(
        contentPadding = PaddingValues(top = padding.calculateTopPadding()),
        topAppBarScrollBehavior = topAppBarScrollBehavior
    ) {
        item {
            val cardVisible = remember { mutableStateOf(false) }

            // 首次加载时触发动画
            LaunchedEffect(Unit) {
                cardVisible.value = true
            }

            // 卡片1动画
            AnimatedVisibility(
                visible = cardVisible.value,
                enter = slideInVertically(
                    initialOffsetY = { -it }, // 从上方进入
                    animationSpec = tween(durationMillis = 500)
                ) + fadeIn(animationSpec = tween(durationMillis = 500))
            ) {
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 20.dp, end = 20.dp)
                    .drawColoredShadow(
                        if (YukiHookAPI.Status.isModuleActive) MiuixTheme.colorScheme.primary else MaterialTheme.colorScheme.errorContainer,
                        1f,
                        borderRadius = 0.dp,
                        shadowRadius = 15.dp,
                        offsetX = 0.dp,
                        offsetY = 0.dp,
                        roundedRect = false
                    ),
                    color = if (YukiHookAPI.Status.isModuleActive) MiuixTheme.colorScheme.primary else MaterialTheme.colorScheme.errorContainer
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 30.dp, end = 30.dp, top = 30.dp, bottom = 30.dp)
                    ) {
                        Image(
                            painter = painterResource(
                                id = if (YukiHookAPI.Status.isModuleActive)
                                    R.drawable.twotone_check_circle_24
                                else R.drawable.twotone_unpublished_24
                            ),
                            contentDescription = null
                        )
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(start = 30.dp)
                        ) {
                            Text(
                                text = if (YukiHookAPI.Status.isModuleActive)
                                    "模块已激活"
                                else "模块未激活",
                                color = Color.Black
                            )
                            Text(
                                text = if (YukiHookAPI.Status.isModuleActive)
                                    "${YukiHookAPI.Status.Executor.name}-v${YukiHookAPI.Status.Executor.apiLevel}"
                                else "请到LSPosed中激活本模块",
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            // 卡片2动画
            AnimatedVisibility(
                visible = cardVisible.value,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(durationMillis = 500)
                ) + fadeIn(animationSpec = tween(durationMillis = 500))
            ) {
                Card(modifier = Modifier
                    .padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 20.dp)
                    .fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(start = 30.dp, end = 30.dp, top = 30.dp, bottom = 30.dp)) {
                        Text(text = "SOC 型号")
                        Text(text = Build.SOC_MODEL, fontSize = 13.sp, color = Color.Gray)

                        Text(text = "Android 版本", modifier = Modifier.padding(top = 10.dp))
                        Text(text = Build.VERSION.RELEASE, fontSize = 13.sp, color = Color.Gray)

                        Text(text = "Android API 版本", modifier = Modifier.padding(top = 10.dp))
                        Text(text = Build.VERSION.SDK_INT.toString(), fontSize = 13.sp, color = Color.Gray)

                        Text(text = "系统版本", modifier = Modifier.padding(top = 10.dp))
                        Text(text = Build.DISPLAY, fontSize = 13.sp, color = Color.Gray)

                        Text(text = "CPU 代号", modifier = Modifier.padding(top = 10.dp))
                        Text(text = Build.BOARD.substringAfter("_"), fontSize = 13.sp, color = Color.Gray)

                        Text(text = "设备制造商", modifier = Modifier.padding(top = 10.dp))
                        Text(text = Build.MANUFACTURER, fontSize = 13.sp, color = Color.Gray)

                        Text(text = "支持的 Abi", modifier = Modifier.padding(top = 10.dp))
                        Text(text = Build.SUPPORTED_ABIS.joinToString(), fontSize = 13.sp, color = Color.Gray)

                        Text(text = "Android 安全补丁", modifier = Modifier.padding(top = 10.dp))
                        Text(text = Build.VERSION.SECURITY_PATCH, fontSize = 13.sp, color = Color.Gray)

                        Text(text = "设备指纹", modifier = Modifier.padding(top = 10.dp))
                        Text(text = Build.FINGERPRINT, fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(Modifier.size(65.dp))
        }
    }

}

@Composable
fun GetAppIconAndName(packageName: String, onAppInfoLoaded: @Composable (String, ImageBitmap) -> Unit) {
    val context = LocalContext.current
    val packageManager = context.packageManager
    val applicationInfo = remember { mutableStateOf<android.content.pm.ApplicationInfo?>(null) }

    LaunchedEffect(packageName) {
        try {
            applicationInfo.value = packageManager.getApplicationInfo(packageName, 0)
        } catch (_: PackageManager.NameNotFoundException) {
        }
    }

    if (applicationInfo.value != null) {
        val info = applicationInfo.value!!
        val icon = info.loadIcon(packageManager)
        val appName = packageManager.getApplicationLabel(info).toString()

        onAppInfoLoaded(appName, icon.toBitmap().asImageBitmap())
    } else {
        onAppInfoLoaded("noapp", ImageBitmap(1, 1))
    }
}
