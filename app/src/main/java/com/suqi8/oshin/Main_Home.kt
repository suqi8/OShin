package com.suqi8.oshin

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val context = LocalContext.current
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
                                                it.substringAfter("ksud ").trim()
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
                        Card(modifier = Modifier.weight(1f).fillMaxSize(), color = Color(0xffffdbd8)) {
                            Text(text = "Frida Server",
                                fontSize = 16.sp,
                                color = Color.Black,
                                modifier = Modifier.padding(start = 15.dp, top = 15.dp),
                                fontWeight = FontWeight.Bold)
                            Text(text = "Connect Error",
                                color = Color.Black.copy(alpha = 0.75f),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(start = 15.dp, top = 5.dp))
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
                                        .mapNotNull { line ->
                                            val parts = line.split("=")
                                            if (parts.size >= 2) parts[0] to parts[1] else null
                                        }
                                        .toMap()
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
                    }
                }
            }
        }
        item {
            val allFeatures = remember {
                features(context).shuffled().map {
                    val route = (if (it.summary != null) "\n" else "") + GetFuncRoute(it.category, context)
                    FeatureUI(
                        title = it.title,
                        summary = it.summary,
                        category = it.category,
                        route = route
                    )
                }
            }
            AnimatedVisibility(cardVisible.value) {
                LazyVerticalStaggeredGrid(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .heightIn(20.dp, 10000.dp),
                    columns = StaggeredGridCells.Fixed(2),
                    userScrollEnabled = false,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalItemSpacing = 8.dp
                ) {
                    items(allFeatures) { feature ->
                        Card(
                            modifier = Modifier
                                .widthIn(0.dp, LocalConfiguration.current.screenWidthDp.dp / 2 - 20.dp)
                        ) {
                            Column(modifier = Modifier.clickable {
                                navController.navigate(feature.category)
                            }) {
                                Text(
                                    feature.title,
                                    modifier = Modifier.padding(start = 15.dp, end = 10.dp, top = 10.dp),
                                    fontSize = 17.sp
                                )
                                Text(
                                    (feature.summary ?: "") + feature.route,
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

data class FeatureUI(
    val title: String,
    val summary: String? = null,
    val category: String,
    val route: String = ""
)

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
