package com.suqi8.oshin

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
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
import com.suqi8.oshin.utils.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.BasicComponentColors
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
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
    val cardVisible = rememberSaveable { mutableStateOf(false) }
    val cardVisible1 = rememberSaveable { mutableStateOf(false) }
    LazyColumn(
        contentPadding = padding,
        topAppBarScrollBehavior = topAppBarScrollBehavior
    ) {
        item {
            LaunchedEffect(Unit) {
                cardVisible.value = true
                //loading.value = false
            }
            LaunchedEffect(cardVisible1.value) {
                if (!cardVisible.value) {
                    cardVisible1.value = true
                    delay(6000)
                    cardVisible1.value = false
                }
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(
                            start = 30.dp,
                            end = 30.dp,
                            top = 30.dp,
                            bottom = 30.dp
                        )
                    ) {
                        val compositionResult =
                            rememberLottieComposition(LottieCompositionSpec.RawRes(if (YukiHookAPI.Status.isModuleActive) R.raw.accept else R.raw.error))
                        val progress = animateLottieCompositionAsState(
                            composition = compositionResult.value
                        )
                        LottieAnimation(
                            composition = compositionResult.value,
                            progress = progress.value,
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
                }
            }
            AnimatedVisibility(
                visible = cardVisible1.value,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(durationMillis = 500)
                ) + fadeIn(animationSpec = tween(durationMillis = 500))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    color = MiuixTheme.colorScheme.primaryVariant.copy(alpha = 0.1f)
                ) {
                    top.yukonga.miuix.kmp.basic.BasicComponent(
                        summary = stringResource(R.string.module_notice),
                        summaryColor = BasicComponentColors(
                            color = MiuixTheme.colorScheme.primaryVariant,
                            disabledColor = MiuixTheme.colorScheme.primaryVariant
                        )
                    )
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
                LazyRow(modifier = Modifier.fillMaxWidth()) {
                    item {
                        Card(modifier = Modifier.width(230.dp).height(165.dp).padding(start = 20.dp, top = 10.dp, end = 10.dp, bottom = 10.dp)) {
                            val randomFeature = features(LocalContext.current)
                                .takeIf { it.isNotEmpty() }
                                ?.random()
                            Column(modifier = Modifier.clickable {
                                navController.navigate(randomFeature!!.category)
                            }.fillMaxSize()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(painter = painterResource(id = R.drawable.recommend),
                                        contentDescription = null,
                                        modifier = Modifier.padding(top = 10.dp, start = 10.dp, bottom = 5.dp).size(30.dp),
                                        colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurface))
                                    Text(text = stringResource(R.string.recommended_features), modifier = Modifier.padding(top = 10.dp, end = 10.dp), fontSize = 15.sp)
                                }
                                Text(randomFeature!!.title+"",
                                    modifier = Modifier.padding(start = 15.dp, end = 10.dp),
                                    fontSize = 17.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis)
                                //Spacer(modifier = Modifier.weight(1f))
                                Text(randomFeature.summary ?: stringResource(R.string.no_introduction),
                                    modifier = Modifier.padding(top = 10.dp, start = 15.dp, end = 10.dp, bottom = 10.dp),
                                    fontSize = 14.sp,
                                    color = MiuixTheme.colorScheme.onSurfaceContainerHigh,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                    item {
                        Card(modifier = Modifier.width(230.dp).height(165.dp).padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp)) {
                            val recent_Feature = features(LocalContext.current)
                                .takeIf { it.isNotEmpty() }
                                ?.last()
                            Column(modifier = Modifier.clickable {
                                navController.navigate("recent_update")
                            }.fillMaxSize()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(painter = painterResource(id = R.drawable.recent_update),
                                        contentDescription = null,
                                        modifier = Modifier.padding(top = 10.dp, start = 10.dp, bottom = 5.dp).size(30.dp).padding(5.dp),
                                        colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurface))
                                    Text(text = stringResource(R.string.recent_update), modifier = Modifier.padding(top = 10.dp, end = 10.dp), fontSize = 15.sp)
                                }
                                Text(recent_Feature!!.title+"",
                                    modifier = Modifier.padding(start = 15.dp, end = 10.dp),
                                    fontSize = 17.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis)
                                //Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    recent_Feature.summary ?: stringResource(R.string.no_introduction),
                                    modifier = Modifier.padding(top = 10.dp, start = 15.dp, end = 10.dp, bottom = 10.dp),
                                    fontSize = 14.sp,
                                    color = MiuixTheme.colorScheme.onSurfaceContainerHigh,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis)
                            }
                        }
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
                        toast(context,"您的设备可能已Root，不建议在风险环境下运行本应用")
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
                                    } catch (e: Exception) { 0 }
                                    val charge_fulldata1 = try {
                                        (data["charge_full1"]?.toIntOrNull() ?: 0) / 1000
                                    } catch (e: Exception) { 0 }
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
                                        } catch (e: Exception) { "ERROR" }
                                    )

                                    // 单次状态更新
                                    batteryStatus.value = newStatus
                                    println(batteryStatus.value.toString() + "111" + newStatus.toString())
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
    val coroutineScope = rememberCoroutineScope()

    // 当前加载状态（非缓存状态）
    val loadingState = remember(packageName) {
        mutableStateOf<Pair<String, ImageBitmap>?>(AppInfoCache.getCached(packageName))
    }

    DisposableEffect(packageName) {
        if (loadingState.value == null) {
            val job = coroutineScope.launch {
                try {
                    // 真实加载逻辑
                    val appInfo = context.packageManager.getApplicationInfo(packageName, 0)
                    val icon = appInfo.loadIcon(context.packageManager)
                    val appName = context.packageManager.getApplicationLabel(appInfo).toString()
                    val bitmap = icon.toBitmap().asImageBitmap()

                    // 更新缓存和状态
                    AppInfoCache.updateCache(packageName, appName to bitmap)
                    loadingState.value = appName to bitmap
                } catch (e: PackageManager.NameNotFoundException) {
                    // 应用未安装的特殊处理
                    loadingState.value = "noapp" to ImageBitmap(1, 1)
                } catch (e: Exception) {
                    // 其他错误保持加载状态
                }
            }

            onDispose {
                job.cancel()
            }
        }

        onDispose {}
    }

    when (val result = loadingState.value) {
        null -> {
            // 加载中状态
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        }
        else -> {
            if (result.first == "noapp") {
                // 触发未安装UI
                onAppInfoLoaded("noapp", result.second)
            } else {
                // 正常显示应用信息
                onAppInfoLoaded(result.first, result.second)
            }
        }
    }
}

@Composable
fun GetAppName(
    packageName: String
): String {
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
        val appName = packageManager.getApplicationLabel(info).toString()

        return appName
    } else {
        return "noapp"
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
