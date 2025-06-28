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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
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

// 数据类保持不变
data class FeatureUI(
    val title: String,
    val summary: String? = null,
    val category: String,
    val route: String = ""
)

private data class BatteryStatus(
    val current: String = "0 mAh",
    val full: String = "0 mAh",
    val health: String = "0%"
)

// --- 优化后的主界面 ---
@Composable
fun Main_Home(padding: PaddingValues, topAppBarScrollBehavior: ScrollBehavior, navController: NavController) {
    val context = LocalContext.current
    val cardVisible = rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100) // 延迟一小段时间，让动画更自然
        cardVisible.value = true
    }

    // 将 features 列表的创建和记忆逻辑上提到 Composable 父级中
    val allFeatures = remember(context) {
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

    // 使用 LazyVerticalStaggeredGrid 作为根滚动布局，避免嵌套滚动
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
            .overScrollVertical(),
        contentPadding = PaddingValues(
            top = padding.calculateTopPadding(),
            bottom = padding.calculateBottomPadding(),
            start = 20.dp,
            end = 20.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalItemSpacing = 10.dp
    ) {
        // 以下函数为非 Composable 的扩展函数，用于构建列表内容
        statusAndRootSection(cardVisible)
        recentUpdateSection(cardVisible, navController)
        deviceInfoSection(cardVisible)
        featuresSection(cardVisible, navController, allFeatures)
    }
}

// --- 模块化 UI 区块 ---

/**
 * 模块状态和 Root 状态卡片区。
 * 注意：此函数没有 @Composable 注解。
 */
private fun LazyStaggeredGridScope.statusAndRootSection(cardVisible: MutableState<Boolean>) {
    item(span = StaggeredGridItemSpan.FullLine) {
        AnimateAppearance(visible = cardVisible.value) {
            Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                ModuleStatusCard(modifier = Modifier.weight(1f).padding(end = 5.dp))
                RootAndFridaStatusCards(modifier = Modifier.weight(1f).padding(start = 5.dp))
            }
        }
    }
}

/**
 * 最近更新卡片区。
 * 注意：此函数没有 @Composable 注解。
 */
private fun LazyStaggeredGridScope.recentUpdateSection(cardVisible: MutableState<Boolean>, navController: NavController) {
    item(span = StaggeredGridItemSpan.FullLine) {
        AnimateAppearance(visible = cardVisible.value) {
            Card(modifier = Modifier.fillMaxWidth()) {
                SuperArrow(
                    title = stringResource(R.string.recent_update),
                    leftAction = {
                        Image(
                            painter = painterResource(id = R.drawable.recent_update),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurface)
                        )
                    },
                    onClick = { navController.navigate("recent_update") }
                )
            }
        }
    }
}

/**
 * 设备信息卡片区。
 * 注意：此函数没有 @Composable 注解。
 */
private fun LazyStaggeredGridScope.deviceInfoSection(cardVisible: MutableState<Boolean>) {
    item(span = StaggeredGridItemSpan.FullLine) {
        AnimateAppearance(visible = cardVisible.value) {
            DeviceInfoCard()
        }
    }
}

/**
 * 功能列表区。
 * 注意：此函数没有 @Composable 注解。
 */
private fun LazyStaggeredGridScope.featuresSection(
    cardVisible: MutableState<Boolean>,
    navController: NavController,
    allFeatures: List<FeatureUI> // 从父级接收已创建好的列表
) {
    // 移除了 key 参数以避免因标题重复导致的崩溃
    items(allFeatures) { feature ->
        AnimateAppearance(visible = cardVisible.value) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.clickable { navController.navigate(feature.category) }) {
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


// --- 独立的 UI 组件 (这些是真正的 @Composable 函数) ---

@Composable
private fun ModuleStatusCard(modifier: Modifier = Modifier) {
    val isModuleActive = YukiHookAPI.Status.isModuleActive

    Card(
        modifier = modifier,
        color = if (isModuleActive) Color(0xffe6fff5) else Color(0xffffd4d6)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                Text(
                    text = if (isModuleActive) stringResource(R.string.module_is_activated) else stringResource(R.string.module_not_activated),
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 15.dp, top = 15.dp),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isModuleActive) lspVersion.value else stringResource(R.string.please_activate),
                    color = Color.Black.copy(alpha = 0.75f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 15.dp, top = 5.dp)
                )
            }
            val compositionResult = rememberLottieComposition(LottieCompositionSpec.RawRes(if (isModuleActive) R.raw.accept else R.raw.error))
            val progress by animateLottieCompositionAsState(composition = compositionResult.value)
            LottieAnimation(
                composition = compositionResult.value,
                progress = { progress },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(110.dp)
                    .offset(x = 35.dp, y = 35.dp)
            )
        }
    }
}

@Composable
private fun RootAndFridaStatusCards(modifier: Modifier = Modifier) {
    var isGiveRoot by rememberSaveable { mutableIntStateOf(5) } // 5:检测中, 0:已授予, other:失败
    var versionMessage by rememberSaveable { mutableStateOf("Loading...") }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val process = Runtime.getRuntime().exec("su -c cat /system/build.prop")
                isGiveRoot = process.waitFor()
                if (versionMessage == "Loading...") {
                    versionMessage = executeCommand("/data/adb/ksud -V").let {
                        if (it.isEmpty() || it == "0") {
                            val magiskVersion = executeCommand("magisk -v")
                            if (magiskVersion.isNotEmpty() && magiskVersion != "0") {
                                "$magiskVersion ${executeCommand("magisk -V").trim()}"
                            } else {
                                ""
                            }
                        } else {
                            it.substringAfter("ksud ").trim()
                        }
                    }
                }
            } catch (e: Exception) {
                isGiveRoot = 3 // 标记为失败
            }
        }
    }

    Column(modifier = modifier) {
        val hasRoot = isGiveRoot == 0
        Card(
            modifier = Modifier.weight(1f).fillMaxSize(),
            color = when (isGiveRoot) {
                5 -> MiuixTheme.colorScheme.surfaceContainer // 检测中
                0 -> Color(0xffcffffb) // 成功
                else -> Color(0xffffd4d6) // 失败
            }
        ) {
            Text(
                text = when (isGiveRoot) {
                    5 -> stringResource(R.string.detecting_root)
                    0 -> stringResource(R.string.root_granted)
                    else -> stringResource(R.string.root_access_denied)
                },
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.padding(start = 15.dp, top = 15.dp),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (hasRoot) versionMessage else stringResource(R.string.root_permission_error),
                color = Color.Black.copy(alpha = 0.75f),
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 15.dp, top = 5.dp) // 恢复原始布局
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Card(modifier = Modifier.weight(1f).fillMaxSize(), color = Color(0xffffdbd8)) {
            Text(
                text = "Frida Server",
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.padding(start = 15.dp, top = 15.dp),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Connect Error",
                color = Color.Black.copy(alpha = 0.75f),
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 15.dp, top = 5.dp) // 恢复原始布局
            )
        }
    }
}

@SuppressLint("AutoboxingStateCreation")
@Composable
private fun DeviceInfoCard(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Card(modifier = modifier.fillMaxWidth()) {
        // --- State Management ---
        var nvid by rememberSaveable { mutableStateOf("0") }
        var health by rememberSaveable { mutableStateOf("0") }
        var batteryCycleCount by rememberSaveable { mutableIntStateOf(0) }
        var chargeFullDesign by rememberSaveable { mutableIntStateOf(0) }
        val batteryStatus = remember { mutableStateOf(BatteryStatus()) }

        val lifecycleOwner = LocalLifecycleOwner.current
        var isForeground by remember { mutableStateOf(false) }

        // --- Data Fetching Effects ---
        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                nvid = getSystemProperty("ro.build.oplus_nv_id")
                health = executeCommand("cat /sys/class/power_supply/battery/health").trim()
                batteryCycleCount = executeCommand("cat /sys/class/oplus_chg/battery/battery_cc").trim().toIntOrNull() ?: 0
                chargeFullDesign = (executeCommand("cat /sys/class/power_supply/battery/charge_full_design").trim().toIntOrNull() ?: 0) / 1000
            }
        }

        LaunchedEffect(isForeground) {
            if (isForeground) {
                while (true) {
                    withContext(Dispatchers.IO) {
                        val chargeFull = (executeCommand("cat /sys/class/oplus_chg/battery/charge_full").toIntOrNull() ?: 0) / 1000
                        val fcc = executeCommand("cat /sys/class/oplus_chg/battery/battery_fcc").toIntOrNull() ?: 0
                        val sohRaw = executeCommand("cat /sys/class/oplus_chg/battery/battery_soh").trim()
                        val designCapacity = executeCommand("cat /sys/class/power_supply/battery/charge_full_design").toFloatOrNull() ?: 1f

                        val sohCalculated = if (designCapacity > 0) (fcc.toFloat() * 100 / (designCapacity/1000)) else 0f

                        batteryStatus.value = BatteryStatus(
                            current = "$chargeFull mAh",
                            full = "$fcc mAh",
                            health = "${sohRaw}% / ${"%.1f".format(sohCalculated)}%"
                        )
                    }
                    delay(10000L)
                }
            }
        }

        // --- Lifecycle Observer ---
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                isForeground = when (event) {
                    Lifecycle.Event.ON_START -> true
                    Lifecycle.Event.ON_STOP -> false
                    else -> isForeground
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
        }

        // --- Derived State for UI ---
        val country by remember(nvid) {
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

        // --- 恢复原始 UI 布局 ---
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
                text = "$chargeFullDesign mAh",
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
                text = "$batteryCycleCount 次",
                insideMargin = PaddingValues(0.dp, 0.dp),
                modifier = Modifier.padding(bottom = 5.dp)
            )
        }
    }
}


// --- 辅助组件和函数 ---

/**
 * 统一的入场动画组件
 */
@Composable
private fun AnimateAppearance(visible: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(durationMillis = 500)
        ) + fadeIn(animationSpec = tween(durationMillis = 500))
    ) {
        content()
    }
}


/**
 * 优化后的 SOH 计算, 增加健壮性
 */
@SuppressLint("DefaultLocale")
suspend fun getSOH(): String {
    return withContext(Dispatchers.IO) {
        try {
            var soh = executeCommand("cat /sys/class/oplus_chg/battery/battery_soh").trim().toDoubleOrNull() ?: 0.0
            val fcc = executeCommand("cat /sys/class/oplus_chg/battery/battery_fcc").trim().toDoubleOrNull() ?: 0.0
            val designCapacityStr = executeCommand("cat /sys/class/oplus_chg/battery/design_capacity")

            if (soh < 50 || soh > 101) {
                val designCapacity = designCapacityStr.toFloatOrNull()
                if (designCapacity != null && designCapacity > 0) {
                    val fccs = fcc * 100
                    soh = (fccs / designCapacity).toDouble()
                }
            }
            String.format("%.1f", soh)
        } catch (e: Exception) {
            "N/A"
        }
    }
}

/**
 * 获取系统属性
 */
@SuppressLint("PrivateApi")
fun getSystemProperty(name: String): String {
    return try {
        val method = Class.forName("android.os.SystemProperties").getMethod("get", String::class.java)
        method.invoke(null, name) as String
    } catch (e: Exception) {
        "null"
    }
}

/**
 * 执行 Shell 命令
 */
suspend fun executeCommand(command: String): String {
    return withContext(Dispatchers.IO) {
        try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
            BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                val output = reader.readText()
                process.waitFor()
                output.trim()
            }
        } catch (e: Exception) {
            Log.e("Oshin", "executeCommand failed for: $command", e)
            "0"
        }
    }
}
