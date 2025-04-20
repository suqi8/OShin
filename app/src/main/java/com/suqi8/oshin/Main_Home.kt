package com.suqi8.oshin

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toColorInt
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
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
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.random.Random

@SuppressLint("AutoboxingStateCreation")
@Composable
fun Main_Home(padding: PaddingValues, topAppBarScrollBehavior: ScrollBehavior, navController: NavController) {
    /*val loading = remember { mutableStateOf(true) }
    if (loading.value) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Transparent)) {
            Text(text = "Loading...", modifier = Modifier.align(Alignment.Center))
        }
    }*/
    val context = LocalContext.current
    val cardVisible = rememberSaveable { mutableStateOf(false) }
    LazyColumn(
        contentPadding = padding,
        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
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
                fun randomColor(): Color {
                    return Color(
                        red = Random.nextFloat(),
                        green = Random.nextFloat(),
                        blue = Random.nextFloat()
                    )
                }
                // 记住当前的颜色
                var currentStartColor by remember { mutableStateOf(randomColor()) }
                var currentEndColor by remember { mutableStateOf(randomColor()) }

                // 动态渐变颜色动画
                val infiniteTransition = rememberInfiniteTransition(label = "")

                // 生成的颜色从当前到目标
                val startColor by infiniteTransition.animateColor(
                    initialValue = currentStartColor,
                    targetValue = currentEndColor,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 2000, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ), label = ""
                )

                // 动画完成时更新目标颜色
                LaunchedEffect(Unit) {
                    while (true) {
                        delay(2000) // 动画时长后更新
                        currentStartColor = currentEndColor
                        currentEndColor = randomColor() // 更新新的随机颜色
                    }
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 10.dp)
                        .drawColoredShadow(
                            if (YukiHookAPI.Status.isModuleActive) startColor else MaterialTheme.colorScheme.errorContainer,
                            1f,
                            borderRadius = 0.dp,
                            shadowRadius = 15.dp,
                            offsetX = 0.dp,
                            offsetY = 0.dp,
                            roundedRect = false
                        ),
                    color = if (YukiHookAPI.Status.isModuleActive) startColor else MaterialTheme.colorScheme.errorContainer,
                ) {
                    Box(modifier = Modifier
                        .fillMaxWidth()) {
                        Image(
                            painter = painterResource(R.drawable.homebackground),
                            contentDescription = null
                        )
                        /*Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxSize().padding(
                                start = 30.dp,
                                end = 30.dp,
                                top = 30.dp,
                                bottom = 30.dp
                            ).align(Alignment.Center)
                        ) {
                            val compositionResult =
                                rememberLottieComposition(LottieCompositionSpec.RawRes(if (YukiHookAPI.Status.isModuleActive) R.raw.accept else R.raw.error))
                            val progress = animateLottieCompositionAsState(
                                composition = compositionResult.value
                            )
                            LottieAnimation(
                                composition = compositionResult.value,
                                progress = { progress.value },
                                modifier = Modifier
                                    .size(50.dp)
                            )
                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(start = 15.dp)
                            ) {
                                Text(
                                    text = if (YukiHookAPI.Status.isModuleActive)
                                        stringResource(R.string.module_is_activated)
                                    else stringResource(R.string.module_not_activated),
                                    color = Color.Black
                                )
                                Text(
                                    text = if (YukiHookAPI.Status.isModuleActive)
                                        "${YukiHookAPI.Status.Executor.name}-v${YukiHookAPI.Status.Executor.apiLevel}"
                                    else stringResource(R.string.please_activate),
                                    color = Color.Black
                                )
                            }
                        }
                        Image(
                            painter = painterResource(id = R.drawable.pic20250331_192109),
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .align(Alignment.BottomEnd)
                        )*/
                        val texts = listOf("做自己想做的事", "做自己喜欢的事", "做自己热爱的事")
                        var currentIndex by remember { mutableStateOf(0) }

                        // 每 3 秒切换一次文本
                        LaunchedEffect(Unit) {
                            while (true) {
                                delay(3000) // 3 秒延迟
                                currentIndex = (currentIndex + 1) % texts.size
                            }
                        }
                        Column {
                            AnimatedContent(
                                targetState = currentIndex,
                                transitionSpec = {
                                    if (targetState > initialState) {
                                        (slideInHorizontally { width -> width } + fadeIn()) togetherWith
                                                (slideOutHorizontally { width -> -width } + fadeOut())
                                    } else {
                                        (slideInHorizontally { width -> -width } + fadeIn()) togetherWith
                                                (slideOutHorizontally { width -> width } + fadeOut())
                                    }
                                }
                            ) { index ->
                                Text(
                                    text = texts[index],
                                    modifier = Modifier.padding(start = 20.dp, top = 20.dp),
                                    fontSize = 20.sp,
                                    color = Color.Black
                                )
                            }
                            Text(
                                text = "探索更多有趣功能",
                                modifier = Modifier.padding(start = 20.dp, top = 5.dp),
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
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
                    Column {
                        SuperArrow(
                            title = stringResource(R.string.recent_update),
                            leftAction = {
                                Image(painter = painterResource(id = R.drawable.recent_update),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(20.dp),
                                    colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurface))
                            },
                            onClick = {
                                navController.navigate("recent_update")
                            }
                        )
                    }
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
                    data class BatteryStatus(
                        val current: String = "0 mAh",
                        val full: String = "0 mAh",
                        val health: String = "0%"
                    )
                    var nvid by rememberSaveable { mutableStateOf("0") } // 使用 rememberSaveable 保持状态
                    val country by remember(nvid) { // 使用记忆函数优化计算
                        derivedStateOf {
                            when (nvid) {
                                "10010111" -> context.getString(R.string.nvid_CN)
                                "00011010" -> context.getString(R.string.nvid_TW)
                                "00110111" -> context.getString(R.string.nvid_RU)
                                "01000100" -> context.getString(R.string.nvid_GDPR_EU)
                                "10001101" -> context.getString(R.string.nvid_GDPR_Europe)
                                "00011011" -> context.getString(R.string.nvid_IN)
                                "00110011" -> context.getString(R.string.nvid_ID)
                                "00111000" -> context.getString(R.string.nvid_MY)
                                "00111001" -> context.getString(R.string.nvid_TH)
                                "00111110" -> context.getString(R.string.nvid_PH)
                                "10000011" -> context.getString(R.string.nvid_SA)
                                "10011010" -> context.getString(R.string.nvid_LATAM)
                                "10011110" -> context.getString(R.string.nvid_BR)
                                "10100110" -> context.getString(R.string.nvid_ME)
                                else -> context.getString(R.string.nvid_unknown, nvid)
                            }
                        }
                    }

                    var health by rememberSaveable { mutableStateOf("0") }
                    var versionMessage by rememberSaveable { mutableStateOf("0") }
                    var ksuVersion by rememberSaveable { mutableStateOf("0") }
                    var battery_cc by rememberSaveable { mutableIntStateOf(0) }
                    var charge_full_design by rememberSaveable { mutableIntStateOf(0) }

                    // 合并电池状态更新
                    val batteryStatus = remember { mutableStateOf(BatteryStatus()) }

                    // 生命周期管理
                    val lifecycleOwner = LocalLifecycleOwner.current
                    var isForeground by remember { mutableStateOf(false) }

                    // 初始化只执行一次的操作
                    LaunchedEffect(Unit) {
                        withContext(Dispatchers.IO) {
                            nvid = getSystemProperty("ro.build.oplus_nv_id")
                            health = executeCommand("cat /sys/class/power_supply/battery/health").trim()

                            // 合并版本信息获取
                            ksuVersion = executeCommand("/data/adb/ksud -V").let {
                                if (it.isEmpty()) {
                                    "0"
                                } else {
                                    it.substringAfter("ksud ").take(4)
                                }
                            }
                            versionMessage = executeCommand("/data/adb/ksud -V").let {
                                if (it.isEmpty()) {
                                    val magiskVersion = executeCommand("magisk -v")
                                    "$magiskVersion ${executeCommand("magisk -V").trim()}"
                                } else {
                                    it.substringAfter("ksud ").take(4)
                                }
                            }

                            // 合并电池信息获取
                            battery_cc = try {
                                executeCommand("cat /sys/class/oplus_chg/battery/battery_cc").trim().toInt()
                            } catch (e: Exception) { 0 }

                            charge_full_design = try {
                                executeCommand("cat /sys/class/power_supply/battery/charge_full_design")
                                    .trim().toInt() / 1000
                            } catch (e: Exception) { 0 }
                        }
                    }

                    // 优化电池信息更新
                    LaunchedEffect(isForeground) {
                        if (isForeground) {
                            while (true) {
                                withContext(Dispatchers.IO) {
                                    // 使用单次命令获取所有电池信息
                                    val rawData = executeCommand("""
                        echo "charge_full=$(cat /sys/class/oplus_chg/battery/charge_full)"
                        echo "charge_full1=$(cat /sys/class/power_supply/battery/charge_counter)"
                        echo "fcc=$(cat /sys/class/oplus_chg/battery/battery_fcc)"
                        echo "design=$(cat /sys/class/power_supply/battery/charge_full_design)"
                    """.trimIndent())

                                    // 解析数据
                                    val data = rawData.lines()
                                        .associate { it.split("=").let { parts -> parts[0] to parts[1] } }
                                    val charge_fulldata0 = try {
                                        (data["charge_full"]?.toIntOrNull() ?: 0) / 1000
                                    } catch (_: Exception) { 0 }
                                    val charge_fulldata1 = try {
                                        (data["charge_full1"]?.toIntOrNull() ?: 0) / 1000
                                    } catch (_: Exception) { 0 }
                                    val charge_fulldata = if (charge_fulldata0 != 0) {
                                        charge_fulldata0
                                    } else {
                                        charge_fulldata1
                                    }

                                    val newStatus = BatteryStatus(
                                        current = "$charge_fulldata mAh",
                                        full = (data["fcc"]?.toIntOrNull() ?: 0).toString() + " mAh",
                                        health = try {
                                            val design = data["design"]?.toFloatOrNull() ?: 1f
                                            val soh = (data["fcc"]?.toFloatOrNull() ?: 0f) / (design / 100000)
                                            "${getSOH()}% / ${soh}%"
                                        } catch (_: Exception) { "ERROR" }
                                    )

                                    // 单次状态更新
                                    batteryStatus.value = newStatus
                                }
                                delay(10000L)
                            }
                        }
                    }

                    // 生命周期观察器
                    DisposableEffect(lifecycleOwner) {
                        val observer = LifecycleEventObserver { _, event ->
                            isForeground = when (event) {
                                Lifecycle.Event.ON_START -> true
                                Lifecycle.Event.ON_STOP -> false
                                else -> isForeground
                            }
                        }
                        lifecycleOwner.lifecycle.addObserver(observer)
                        onDispose {
                            lifecycleOwner.lifecycle.removeObserver(observer)
                        }
                    }
                    val batteryHealthString by remember(health) {
                        derivedStateOf {
                            when (health) {
                                "Good" -> context.getString(R.string.battery_health_good)
                                "Overheat" -> context.getString(R.string.battery_health_overheat)
                                "Dead" -> context.getString(R.string.battery_health_dead)
                                "Over Voltage" -> context.getString(R.string.battery_health_over_voltage)
                                "Cold" -> context.getString(R.string.battery_health_cold)
                                "Unknown" -> context.getString(R.string.battery_health_unknown)
                                else -> context.getString(R.string.battery_health_not_found)
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
                            text = stringResource(id = R.string.battery_status),
                            modifier = Modifier.padding(top = 5.dp)
                        )
                        SmallTitle(
                            text = "$batteryHealthString / $health",
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
                        addline(false)
                        Text(
                            text = stringResource(id = R.string.battery_equivalent_capacity),
                            modifier = Modifier.padding(top = 5.dp)
                        )
                        SmallTitle(
                            text = charge_full_design.toString() + "mAh",
                            insideMargin = PaddingValues(0.dp, 0.dp),
                            modifier = Modifier.padding(bottom = 5.dp)
                        )
                        addline(false)
                        Text(
                            text = stringResource(id = R.string.battery_current_capacity),
                            modifier = Modifier.padding(top = 5.dp)
                        )
                        SmallTitle(
                            text = batteryStatus.value.current,
                            insideMargin = PaddingValues(0.dp, 0.dp),
                            modifier = Modifier.padding(bottom = 5.dp)
                        )
                        addline(false)
                        Text(
                            text = stringResource(id = R.string.battery_full_capacity),
                            modifier = Modifier.padding(top = 5.dp)
                        )
                        SmallTitle(
                            text = batteryStatus.value.full,
                            insideMargin = PaddingValues(0.dp, 0.dp),
                            modifier = Modifier.padding(bottom = 5.dp)
                        )
                        addline(false)
                        Text(
                            text = stringResource(id = R.string.battery_health),
                            modifier = Modifier.padding(top = 5.dp)
                        )
                        SmallTitle(
                            text = batteryStatus.value.health,
                            insideMargin = PaddingValues(0.dp, 0.dp),
                            modifier = Modifier.padding(bottom = 5.dp)
                        )
                        addline(false)
                        Text(
                            text = stringResource(id = R.string.battery_cycle_count),
                            modifier = Modifier.padding(top = 5.dp)
                        )
                        SmallTitle(
                            text = battery_cc.toString() + "次",
                            insideMargin = PaddingValues(0.dp, 0.dp),
                            modifier = Modifier.padding(bottom = 5.dp)
                        )
                        addline(false)
                        Text(
                            text = if (ksuVersion == "0") stringResource(id = R.string.magisk_version) else stringResource(
                                id = R.string.ksu_version
                            ), modifier = Modifier.padding(top = 5.dp)
                        )
                        SmallTitle(
                            text = versionMessage.trim(),
                            insideMargin = PaddingValues(0.dp, 0.dp),
                            modifier = Modifier.padding(bottom = 5.dp)
                        )
                    }
                }
            }
        }
        item {
            if(cardVisible.value) {
                val allFeatures = remember { features(context).shuffled() }
                var shownCount by remember { mutableStateOf(10) }  // 控制每次显示多少项
                val visibleFeatures = allFeatures.take(shownCount)
                var isFlowVisible by remember { mutableStateOf(false) } // 是否显示 FlowRow
                var isBottomReached by remember { mutableStateOf(false) } // 是否到达底部

// 控制显示更多的逻辑
                if (isFlowVisible && shownCount < allFeatures.size && isBottomReached) {
                    shownCount += 10
                    isBottomReached = false // 重置底部标记
                }

                FlowRow(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .offset(y = (-8).dp)
                        .onGloballyPositioned { coordinates ->
                            val height = coordinates.size.height
                            val position = coordinates.positionInParent().y
                            // 判断是否滑动到底部
                            isBottomReached =
                                position + height >= (coordinates.parentCoordinates?.size?.height ?: 0)
                            isFlowVisible = true // 一旦可见，就设置为 true
                        },
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    visibleFeatures.forEach { recent_Feature ->
                        Card(
                            modifier = Modifier
                                .widthIn(0.dp, LocalConfiguration.current.screenWidthDp.dp / 2 - 20.dp)
                        ) {
                            Column(modifier = Modifier.clickable {
                                navController.navigate(recent_Feature.category)
                            }) {
                                Text(
                                    recent_Feature.title,
                                    modifier = Modifier.padding(start = 15.dp, end = 10.dp, top = 10.dp),
                                    fontSize = 17.sp
                                )

                                // 提前准备 route 字段，避免每次都重新计算
                                val route = rememberSaveable { mutableStateOf("") }
                                if (route.value.isEmpty()) {
                                    LaunchedEffect(Unit) {
                                        route.value = (if (recent_Feature.summary != null) "\n" else "") + GetFuncRoute(recent_Feature.category, context)
                                    }
                                }

                                Text(
                                    if (recent_Feature.summary != null) recent_Feature.summary + route.value else route.value,
                                    modifier = Modifier.padding(top = 10.dp, start = 15.dp, end = 10.dp, bottom = 10.dp),
                                    fontSize = 14.sp,
                                    color = MiuixTheme.colorScheme.onSurfaceContainerHigh
                                )
                            }
                        }
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
