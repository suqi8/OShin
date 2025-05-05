package com.suqi8.oshin

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toColorInt
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.highcapable.yukihookapi.YukiHookAPI
import com.suqi8.oshin.ui.activity.funlistui.addline
import com.suqi8.oshin.utils.GetFuncRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import java.io.BufferedReader
import java.io.InputStreamReader

@SuppressLint("AutoboxingStateCreation")
@Composable
fun Main_Home(padding: PaddingValues, topAppBarScrollBehavior: ScrollBehavior, navController: NavController) {

    val cardVisible = rememberSaveable { mutableStateOf(false) }
    LazyColumn(
        contentPadding = padding,
        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
            .overScrollVertical()
    ) {
        item {
            LaunchedEffect(Unit) {
                cardVisible.value = true
                //loading.value = false
            }

            // 卡片1动画
            AnimatedVisibility(
                visible = cardVisible.value,
                enter = slideInVertically(
                    initialOffsetY = { -it }, // 从上方进入
                    animationSpec = tween(durationMillis = 500)
                ) + fadeIn(animationSpec = tween(durationMillis = 500))
            ) {
                Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min).padding(start = 20.dp, top = 10.dp, end = 20.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Card(modifier = Modifier.padding(end = 5.dp), color = if (YukiHookAPI.Status.isModuleActive) Color(0xffe6fff5) else Color(0xffffd4d6)) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Column {
                                    Text(text = if (YukiHookAPI.Status.isModuleActive) stringResource(R.string.module_is_activated) else stringResource(R.string.module_not_activated),
                                        fontSize = 20.sp,
                                        color = Color.Black,
                                        modifier = Modifier.padding(start = 15.dp, top = 15.dp),
                                        fontWeight = FontWeight.Bold)
                                    Text(text = if (YukiHookAPI.Status.isModuleActive) lspVersion.value else stringResource(R.string.please_activate),
                                        color = Color.Black.copy(alpha = 0.75f),
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(start = 15.dp, top = 5.dp))
                                }
                                val compositionResult = rememberLottieComposition(LottieCompositionSpec.RawRes(if (YukiHookAPI.Status.isModuleActive) R.raw.accept else R.raw.error))
                                val progress = animateLottieCompositionAsState(
                                    composition = compositionResult.value
                                )
                                LottieAnimation(
                                    composition = compositionResult.value,
                                    progress = { progress.value },
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(top = 50.dp)
                                        .size(110.dp)
                                        .offset(x = 35.dp,y = 35.dp)
                                )
                            }
                        }
                    }
                    val isGiveRoot = rememberSaveable { mutableStateOf(5) }
                    var versionMessage by rememberSaveable { mutableStateOf("Loading...") }
                    LaunchedEffect(Unit) {
                        if (isGiveRoot.value == 5) {
                            withContext(Dispatchers.IO) {
                                try {
                                    val process = Runtime.getRuntime().exec("su -c cat /system/build.prop")
                                    isGiveRoot.value = process.waitFor()
                                    if (versionMessage == "Loading...") {
                                        versionMessage = executeCommand("/data/adb/ksud -V").let {
                                            if (it.isEmpty()) {
                                                val magiskVersion = executeCommand("magisk -v")
                                                "$magiskVersion ${executeCommand("magisk -V").trim()}"
                                            } else {
                                                it.substringAfter("ksud ").take(4)
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    isGiveRoot.value = 3
                                    return@withContext
                                }
                            }
                        }
                    }
                    Column(modifier = Modifier.weight(1f).padding(start = 5.dp)) {
                        Card(modifier = Modifier.weight(1f).fillMaxSize(), color = if (isGiveRoot.value != 5 && isGiveRoot.value == 0) Color(0xffcffffb) else Color(0xffffd4d6)) {
                            Text(text = if (isGiveRoot.value == 5) stringResource(R.string.detecting_root) else if (isGiveRoot.value == 0) stringResource(R.string.root_granted) else stringResource(R.string.root_access_denied),
                                fontSize = 16.sp,
                                color = Color.Black,
                                modifier = Modifier.padding(start = 15.dp, top = 15.dp),
                                fontWeight = FontWeight.Bold)
                            Text(text = if (isGiveRoot.value != 5 && isGiveRoot.value == 0) versionMessage else stringResource(R.string.root_permission_error),
                                color = Color.Black.copy(alpha = 0.75f),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(start = 15.dp, top = 5.dp))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
        item {
            AnimatedVisibility(
                visible = cardVisible.value,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(durationMillis = 500)
                ) + fadeIn(animationSpec = tween(durationMillis = 500))
            ) {
                Card(
                    modifier = Modifier
                        .padding(start = 20.dp, top = 10.dp, end = 20.dp)
                        .fillMaxWidth()
                ) {
//                    Column {
//                        SuperArrow(
//                            title = stringResource(R.string.recent_update),
//                            leftAction = {
//                                Image(painter = painterResource(id = R.drawable.recent_update),
//                                    contentDescription = null,
//                                    modifier = Modifier
//                                        .size(20.dp),
//                                    colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurface))
//                            },
//                            onClick = {
//                                navController.navigate("recent_update")
//                            }
//                        )
//                    }
                }
            }
        }
        item {
            val context = LocalContext.current
            // 卡片2动画
            AnimatedVisibility(
                visible = cardVisible.value,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(durationMillis = 500)
                ) + fadeIn(animationSpec = tween(durationMillis = 500))
            ) {
                Card(
                    modifier = Modifier
                        .padding(start = 20.dp, top = 10.dp, end = 20.dp, bottom = 20.dp)
                        .fillMaxWidth()
                ) {
                    var nvid by rememberSaveable { mutableStateOf("0") } // 使用 rememberSaveable 保持状态
                    val country by remember(nvid) { // 使用记忆函数优化计算
                        derivedStateOf {
                            when (nvid) {
                                "10010111" -> context.getString(R.string.nvid_CN)
                                else -> context.getString(R.string.nvid_unknown, nvid)
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 20.dp,
                            bottom = 20.dp
                        )
                    ) {
                        Text(
                            text = stringResource(id = R.string.countries_and_regions),
                            modifier = Modifier.padding(bottom = 5.dp)
                        )
                        SmallTitle(
                            text = country,
                            insideMargin = PaddingValues(0.dp, 0.dp),
                            modifier = Modifier.padding(bottom = 5.dp)
                        )
                        addline(false)
                        Text(
                            text = stringResource(id = R.string.android_version) + " / " + stringResource(
                                id = R.string.android_api_version
                            ), modifier = Modifier.padding(top = 5.dp)
                        )
                        SmallTitle(
                            text = Build.VERSION.RELEASE + "/" + Build.VERSION.SDK_INT,
                            insideMargin = PaddingValues(0.dp, 0.dp),
                            modifier = Modifier.padding(bottom = 5.dp)
                        )
                        addline(false)
                        Text(
                            text = stringResource(id = R.string.system_version),
                            modifier = Modifier.padding(top = 5.dp)
                        )
                        SmallTitle(
                            text = Build.DISPLAY,
                            insideMargin = PaddingValues(0.dp, 0.dp),
                            modifier = Modifier.padding(bottom = 5.dp)
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
suspend fun getSOH(): String {
    var soh = executeCommand("cat /sys/class/oplus_chg/battery/battery_soh").trim().toDouble()
    val fcc = executeCommand("cat /sys/class/oplus_chg/battery/battery_fcc").trim().toDouble()

    val getDesignCapacity = executeCommand("cat /sys/class/oplus_chg/battery/design_capacity")
    return when {
        soh < 50 -> {
            val designCapacity = getDesignCapacity // Assume this function exists
            val fccs = fcc * 100
            soh = (fccs.toFloat() / designCapacity.toFloat()).toDouble()
            String.format("%.1f", soh)
        }

        soh > 101 -> {
            val designCapacity = getDesignCapacity // Assume this function exists
            val fccs = fcc * 100
            soh = (fccs.toFloat() / designCapacity.toFloat()).toDouble()
            String.format("%.1f", soh)
        }

        else -> String.format("%.1f", soh)
    }
}

@SuppressLint("PrivateApi")
fun getSystemProperty(name: String): String {
    return try {
        val method = Class.forName("android.os.SystemProperties")
            .getMethod("get", String::class.java)
        method.invoke(null, name) as String
    } catch (e: Exception) {
        "null"
    }
}

suspend fun executeCommand(command: String): String {
    return withContext(Dispatchers.IO) {
        try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }

            process.waitFor()
            reader.close()
            output.toString().trim()
        } catch (e: Exception) {
            Log.e(TAG, "executeCommand: $e")
            return@withContext "0"
        }
    }
}

object AppInfoCache {
    private val cache = mutableMapOf<String, Pair<String, ImageBitmap>>()

    fun getCached(packageName: String): Pair<String, ImageBitmap>? {
        return cache[packageName]
    }

    fun updateCache(packageName: String, info: Pair<String, ImageBitmap>) {
        cache[packageName] = info
    }
}

@Composable
fun GetAppIconAndName(
    packageName: String,
    onAppInfoLoaded: @Composable (String, ImageBitmap) -> Unit
) {
    val context = LocalContext.current

    // 使用 produceState 在 IO 线程加载数据并缓存
    val result by produceState<Pair<String, ImageBitmap>?>(initialValue = null, key1 = packageName) {
        withContext(Dispatchers.IO) {
            try {
                AppInfoCache.getCached(packageName)?.let {
                    value = it
                    return@withContext
                }
                val appInfo = context.packageManager.getApplicationInfo(packageName, 0)
                val icon = appInfo.loadIcon(context.packageManager)
                val appName = context.packageManager.getApplicationLabel(appInfo).toString()
                val bitmap = icon.toBitmap().asImageBitmap()
                // 更新缓存
                AppInfoCache.updateCache(packageName, appName to bitmap)
                value = appName to bitmap
            } catch (e: PackageManager.NameNotFoundException) {
                value = "noapp" to ImageBitmap(1, 1)
            } catch (e: Exception) { }
        }
    }

    result?.let { onAppInfoLoaded(it.first, it.second) }
}

@Composable
fun GetAppName(
    packageName: String
): String {
    val context = LocalContext.current
    val appNameCache = AppNameCache(context)
    return appNameCache.getAppName(packageName)
}

class AppNameCache(private val context: Context) {
    private val cache = mutableMapOf<String, String>()

    fun getAppName(packageName: String): String {
        // 如果缓存中有该应用名，直接返回
        cache[packageName]?.let { return it }

        // 否则查询应用名并缓存
        val appName = getAppNameFromPackage(packageName)
        cache[packageName] = appName
        return appName
    }

    private fun getAppNameFromPackage(packageName: String): String {
        val packageManager = context.packageManager
        return try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(applicationInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            "noapp" // 如果没有找到该应用，则返回默认值
        }
    }
}

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
    shadowRadius: Dp = 0.dp,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    roundedRect: Boolean = true
) = this.drawBehind {
    /**将颜色转换为Argb的Int类型*/
    val transparentColor = color.copy(alpha = .0f).value.toLong().toColorInt()
    val shadowColor = color.copy(alpha = alpha).value.toLong().toColorInt()
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
