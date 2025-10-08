package com.suqi8.oshin

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.google.accompanist.flowlayout.FlowRow
import com.highcapable.yukihookapi.YukiHookAPI
import com.suqi8.oshin.ui.module.HUDModuleContainer
import com.suqi8.oshin.utils.GetFuncRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

// --- 数据模型 ---
data class FeatureUI(val title: String, val summary: String? = null, val category: String)
enum class Status { LOADING, SUCCESS, ERROR, WARNING }
data class ModuleStatus(val status: Status, val lspVersion: String = "")
data class RootStatus(val status: Status, val version: String = "")
data class FridaStatus(val status: Status, val version: String = "")
data class DeviceInfo(
    val country: String = "加载中...", val androidVersion: String = Build.VERSION.RELEASE, val sdkVersion: String = Build.VERSION.SDK_INT.toString(),
    val systemVersion: String = Build.DISPLAY, val designCapacity: Int = 0, val currentCapacity: Int = 0, val fullCapacity: Int = 0,
    val batteryHealthRaw: String = "加载中...", val batteryHealthDisplay: String = "加载中...",
    val batteryHealthPercent: Float = 0f,
    val calculatedHealth: Float = 0f,
    val cycleCount: Int = 0
)
data class CarouselItem(
    val title: String?,
    val description: String?,
    val actionUrl: String?,
    val imageUrl: String?
)


var carouselItems by mutableStateOf<List<CarouselItem>?>(null)

// --- UI (Composable) ---
@Composable
fun Main_Home(
    padding: PaddingValues,
    topAppBarScrollBehavior: ScrollBehavior,
    navController: NavController
) {
    val context = LocalContext.current
    var visible by rememberSaveable { mutableStateOf(false) }

    var moduleStatus by remember { mutableStateOf(ModuleStatus(Status.LOADING)) }
    var rootStatus by remember { mutableStateOf(RootStatus(Status.LOADING)) }
    val fridaStatus by remember { mutableStateOf(FridaStatus(Status.ERROR, "未连接")) }
    var deviceInfo by remember { mutableStateOf(DeviceInfo()) }
    var features by remember { mutableStateOf<List<FeatureUI>>(emptyList()) }

    LaunchedEffect(Unit) {
        launch {
            visible = true
        }

        launch(Dispatchers.IO) {
            val carouselDeferred = async { fetchCarouselData() }
            val moduleStatusDeferred = async { getModuleStatus() }
            val rootStatusDeferred = async { getRootStatus() }
            val deviceInfoDeferred = async { getDeviceInfo(context) }
            //val featuresDeferred = async { getFeatures(context) }

            carouselItems = carouselDeferred.await()
            moduleStatus = moduleStatusDeferred.await()
            rootStatus = rootStatusDeferred.await()
            deviceInfo = deviceInfoDeferred.await()
            //features = featuresDeferred.await()
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MiuixTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding() + 16.dp,
                bottom = padding.calculateBottomPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                AnimatedVisibility(
                    visible = visible && carouselItems != null,
                    enter = slideInVertically(animationSpec = tween(800)) { -it } + fadeIn()
                ) {
                    if (!carouselItems.isNullOrEmpty()) {
                        CarouselSection(items = carouselItems!!)
                    }
                }
                AnimatedVisibility(visible = visible && carouselItems == null) {
                    CarouselSkeleton()
                }
            }

            item {
                AnimatedVisibility(visible, enter = slideInVertically(animationSpec = tween(800, 100)) { -it } + fadeIn()) {
                    DashboardSection(moduleStatus, rootStatus, fridaStatus)
                }
            }
            item {
                AnimatedVisibility(visible, enter = slideInVertically(animationSpec = tween(800, 200)) { -it } + fadeIn()) {
                    RecentUpdatesModule(navController)
                }
            }
            item {
                AnimatedVisibility(visible, enter = slideInVertically(animationSpec = tween(800, 300)) { -it } + fadeIn()) {
                    DeviceInfoSection(deviceInfo, visible)
                }
            }
            item {
                AnimatedVisibility(visible, enter = slideInVertically(animationSpec = tween(800, 450)) { -it } + fadeIn()) {
                    SectionTitle(titleResId = R.string.section_title_features)
                }
            }
            items(features) { feature ->
                AnimatedVisibility(visible, enter = slideInVertically(animationSpec = tween(800, 450)) { -it } + fadeIn()) {
                    FeatureItem(feature = feature, navController = navController)
                }
            }
        }
    }
}

// --- 数据获取逻辑 ---

suspend fun fetchCarouselData(): List<CarouselItem> = withContext(Dispatchers.IO) {
    val client = OkHttpClient.Builder()
        .cache(null)
        .build()

    val request = Request.Builder()
        .url("https://gitee.com/yo-gurt/OShin/raw/master/lunbo.json")
        .header("Cache-Control", "no-cache")
        .build()

    try {
        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val body = response.body.string()
                val jsonArray = JSONArray(body)
                val items = mutableListOf<CarouselItem>()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    items.add(
                        CarouselItem(
                            title = jsonObject.optString("title", null),
                            description = jsonObject.optString("description", null),
                            actionUrl = jsonObject.optString("action_url", null),
                            imageUrl = jsonObject.optString("image_url", null)
                        )
                    )
                }
                items
            } else {
                emptyList()
            }
        }
    } catch (e: Exception) {
        Log.e("CarouselFetch", "Error fetching carousel data", e)
        emptyList()
    }
}

suspend fun getModuleStatus(): ModuleStatus = withContext(Dispatchers.IO) {
    if (YukiHookAPI.Status.isModuleActive) ModuleStatus(Status.SUCCESS, "LSPosed v" + YukiHookAPI.Status.Executor.apiLevel)
    else ModuleStatus(Status.ERROR, "未在LSPosed中激活")
}

suspend fun getRootStatus(): RootStatus = withContext(Dispatchers.IO) {
    try {
        val result = withTimeoutOrNull(2000L) {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "cat /system/build.prop"))
            if (process.waitFor() == 0) {
                val ksuVersion = executeCommand("/data/adb/ksud -V")
                val version = if (ksuVersion.isNotEmpty()) {
                    "KernelSU ${ksuVersion.substringAfter("ksud ").trim()}"
                } else {
                    val magiskVersion = executeCommand("magisk -V")
                    "Magisk $magiskVersion"
                }
                RootStatus(Status.SUCCESS, version)
            } else {
                RootStatus(Status.ERROR, "授权失败")
            }
        }
        result ?: RootStatus(Status.WARNING, "检测超时")
    } catch (e: Exception) {
        RootStatus(Status.ERROR, "无法获取Root权限")
    }
}

suspend fun getDeviceInfo(context: Context): DeviceInfo = withContext(Dispatchers.IO) {
    val rawData = executeCommand("""
        echo "charge_full=$(cat /sys/class/oplus_chg/battery/charge_full)"
        echo "charge_full1=$(cat /sys/class/power_supply/battery/charge_counter)"
        echo "fcc=$(cat /sys/class/oplus_chg/battery/battery_fcc)"
        echo "soh=$(cat /sys/class/oplus_chg/battery/battery_soh)"
        echo "cc=$(cat /sys/class/oplus_chg/battery/battery_cc)"
        echo "design_capacity=$(cat /sys/class/power_supply/battery/charge_full_design)"
        echo "health=$(cat /sys/class/power_supply/battery/health)"
    """.trimIndent())

    val dataMap = rawData.lines()
        .mapNotNull { line ->
            val parts = line.split("=")
            if (parts.size >= 2) parts[0] to parts[1] else null
        }.toMap()

    val chargeFull0 = dataMap["charge_full"]?.toIntOrNull() ?: 0
    val chargeFull1 = dataMap["charge_full1"]?.toIntOrNull() ?: 0
    val currentCapacity = (if (chargeFull0 != 0) chargeFull0 else chargeFull1) / 1000

    val designCapacity = (dataMap["design_capacity"]?.toIntOrNull() ?: 0) / 1000
    val fullCapacity = dataMap["fcc"]?.toIntOrNull() ?: 0
    val soh = dataMap["soh"]?.toFloatOrNull() ?: 0f
    val cycleCount = dataMap["cc"]?.toIntOrNull() ?: 0
    val healthRaw = dataMap["health"]?.trim() ?: "Unknown"
    val nvid = getSystemProperty("ro.build.oplus_nv_id")

    val calculatedHealth = if (designCapacity > 0 && fullCapacity > 0) {
        (fullCapacity.toFloat() / designCapacity.toFloat()) * 100f
    } else {
        0f
    }

    DeviceInfo(
        country = mapNvidToCountry(context, nvid),
        androidVersion = Build.VERSION.RELEASE,
        sdkVersion = Build.VERSION.SDK_INT.toString(),
        systemVersion = Build.DISPLAY,
        designCapacity = designCapacity,
        currentCapacity = currentCapacity,
        fullCapacity = fullCapacity,
        batteryHealthRaw = healthRaw,
        batteryHealthDisplay = mapHealthToString(context, healthRaw),
        batteryHealthPercent = soh,
        calculatedHealth = calculatedHealth,
        cycleCount = cycleCount
    )
}

fun mapNvidToCountry(context: Context, nvid: String): String = when (nvid) {
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

fun mapHealthToString(context: Context, health: String): String = when (health) {
    "Good" -> context.getString(R.string.battery_health_good)
    "Overheat" -> context.getString(R.string.battery_health_overheat)
    "Dead" -> context.getString(R.string.battery_health_dead)
    "Over Voltage" -> context.getString(R.string.battery_health_over_voltage)
    "Cold" -> context.getString(R.string.battery_health_cold)
    "Unknown" -> context.getString(R.string.battery_health_unknown)
    else -> context.getString(R.string.battery_health_not_found)
}


// --- 视觉组件 ---

class CutCornerShape(private val cut: Dp) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val cutPx = with(density) { cut.toPx() }
        val path = Path().apply {
            moveTo(cutPx, 0f)
            lineTo(size.width - cutPx, 0f)
            lineTo(size.width, cutPx)
            lineTo(size.width, size.height - cutPx)
            lineTo(size.width - cutPx, size.height)
            lineTo(cutPx, size.height)
            lineTo(0f, size.height - cutPx)
            lineTo(0f, cutPx)
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun SectionTitle(titleResId: Int) {
    val primaryColor = MiuixTheme.colorScheme.primary.copy(alpha = 0.5f)
    val textColor = MiuixTheme.colorScheme.onBackground
    var animated by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) { animated = true }

    val lineWidth by animateFloatAsState(
        targetValue = if (animated) 1f else 0.00001f,
        animationSpec = tween(durationMillis = 700)
    )
    val textAlpha by animateFloatAsState(
        targetValue = if (animated) 1f else 0f,
        animationSpec = tween(durationMillis = 500, delayMillis = 200)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier
            .weight(lineWidth)
            .height(1.dp)
            .background(Brush.horizontalGradient(listOf(Color.Transparent, primaryColor))))
        Text(
            text = " ${stringResource(id = titleResId)} ",
            fontSize = 20.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = textColor.copy(alpha = textAlpha),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Box(modifier = Modifier
            .weight(lineWidth)
            .height(1.dp)
            .background(Brush.horizontalGradient(listOf(primaryColor, Color.Transparent))))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CarouselSection(items: List<CarouselItem>) {
    val pagerState = rememberPagerState { items.size }
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(pagerState.pageCount) {
        while (true) {
            delay(5000)
            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.animateScrollToPage(nextPage, animationSpec = tween(durationMillis = 600))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 40.dp)
        ) { page ->
            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction

            val scale = lerp(1f, 0.85f, abs(pageOffset))
            val alpha = lerp(1f, 0.5f, abs(pageOffset))

            val item = items[page]
            val isClickable = item.actionUrl != null

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    }
                    .fillMaxWidth()
                    .clip(CutCornerShape(8.dp))
                    .background(MiuixTheme.colorScheme.onBackground.copy(alpha = 0.03f))
                    .border(
                        1.dp,
                        MiuixTheme.colorScheme.primary.copy(alpha = 0.3f),
                        CutCornerShape(8.dp)
                    )
                    .then(if (isClickable) Modifier.clickable { uriHandler.openUri(item.actionUrl) } else Modifier)
            ) {
                Column(
                    modifier = Modifier
                    .fillMaxWidth()
                ) {
                    if (item.imageUrl != null) {
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = item.title,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }

                    if (item.title != null || item.description != null) {
                        Column(modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth()) {
                            item.title?.let {
                                Spacer(Modifier.height(if (item.imageUrl != null) 8.dp else 0.dp))
                                Text(
                                    text = it,
                                    color = MiuixTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            item.description?.let {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = it,
                                    color = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            Modifier.height(20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                val width by animateFloatAsState(targetValue = if (pagerState.currentPage == iteration) 20f else 8f, animationSpec = tween(300))
                Box(
                    modifier = Modifier
                        .size(width.dp, 8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}

// 线性插值函数，用于平滑动画过渡
private fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return (1 - fraction) * start + fraction * stop
}


@Composable
fun CarouselSkeleton() {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse)
    )
    HUDModuleContainer(modifier = Modifier.padding(horizontal = 16.dp)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(CutCornerShape(8.dp))
                    .background(MiuixTheme.colorScheme.onBackground.copy(alpha = alpha))
            )
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(16.dp)
                    .clip(CutCornerShape(4.dp))
                    .background(MiuixTheme.colorScheme.onBackground.copy(alpha = alpha))
            )
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(CutCornerShape(4.dp))
                    .background(MiuixTheme.colorScheme.onBackground.copy(alpha = alpha))
            )
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(12.dp)
                    .clip(CutCornerShape(4.dp))
                    .background(MiuixTheme.colorScheme.onBackground.copy(alpha = alpha))
            )
        }
    }
}

@Composable
fun DashboardSection(moduleStatus: ModuleStatus, rootStatus: RootStatus, fridaStatus: FridaStatus) {
    Column(modifier = Modifier
        .animateContentSize()
        .padding(horizontal = 16.dp)) {
        SectionTitle(titleResId = R.string.section_title_status)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            HUDStatusModule(Modifier.weight(1f), moduleStatus.status, Icons.Default.VerifiedUser, stringResource(id = R.string.module_status), moduleStatus.lspVersion.ifEmpty { stringResource(id = R.string.module_not_activated) })
            HUDStatusModule(Modifier.weight(1f), rootStatus.status, Icons.Default.Security, stringResource(id = R.string.root_status), rootStatus.version)
        }
        Spacer(Modifier.height(16.dp))
        HUDStatusModule(Modifier.fillMaxWidth(), fridaStatus.status, Icons.Default.BugReport, "Frida Server", fridaStatus.version)
    }
}

@Composable
fun HUDStatusModule(modifier: Modifier = Modifier, status: Status, icon: ImageVector, title: String, message: String) {
    val baseColor = when (status) {
        Status.SUCCESS -> Color(0xFF22C55E)
        Status.ERROR -> Color(0xFFEF4444)
        Status.WARNING -> Color(0xFFF59E0B)
        Status.LOADING -> MiuixTheme.colorScheme.primary
    }
    val contentColor = MiuixTheme.colorScheme.onBackground
    val shape = CutCornerShape(8.dp)
    val moduleBgColor = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.03f)

    Box(
        modifier = modifier
            .clip(shape)
            .background(moduleBgColor)
            .border(1.dp, baseColor.copy(alpha = 0.3f), shape)
            .padding(1.dp)
            .border(1.dp, baseColor.copy(alpha = 0.4f), shape)
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusIndicatorRing(status = status, color = baseColor)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = contentColor, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 14.sp)
                Text(
                    text = message,
                    color = contentColor.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun RecentUpdatesModule(navController: NavController) {
    val shape = CutCornerShape(8.dp)
    val moduleBgColor = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.03f)
    val primaryColor = MiuixTheme.colorScheme.primary

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(shape)
                .background(moduleBgColor)
                .border(1.dp, primaryColor.copy(alpha = 0.3f), shape)
                .clickable { navController.navigate("recent_update") }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Update, contentDescription = "Recent Updates", tint = primaryColor)
                Spacer(Modifier.width(12.dp))
                Text(
                    text = stringResource(id = R.string.recent_update),
                    color = MiuixTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(shape)
                .background(moduleBgColor)
                .border(1.dp, primaryColor.copy(alpha = 0.3f), shape)
                .clickable { navController.navigate("about_group") }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(R.drawable.group), contentDescription = null, colorFilter = ColorFilter.tint(primaryColor), modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(12.dp))
                Text(
                    text = stringResource(id = R.string.official_channel),
                    color = MiuixTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun StatusIndicatorRing(status: Status, color: Color) {
    val infiniteTransition = rememberInfiniteTransition()

    val ringAlpha by if (status == Status.LOADING) {
        infiniteTransition.animateFloat(0.3f, 1f, infiniteRepeatable(tween(800), RepeatMode.Reverse))
    } else { remember { mutableStateOf(1f) } }

    val rotation by if(status == Status.LOADING) {
        infiniteTransition.animateFloat(0f, 360f, infiniteRepeatable(tween(1500), RepeatMode.Restart))
    } else { remember { mutableStateOf(0f) } }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(36.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(color = color, startAngle = rotation, sweepAngle = if(status == Status.LOADING) 90f else 360f, useCenter = false, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round), alpha = ringAlpha)
            if (status != Status.LOADING) {
                drawCircle(color.copy(alpha = 0.2f), radius = center.x * 0.7f)
            }
        }
        Icon(
            imageVector = when(status) {
                Status.SUCCESS -> Icons.Default.VerifiedUser
                Status.ERROR -> Icons.Default.BugReport
                Status.WARNING -> Icons.Default.Warning
                Status.LOADING -> Icons.Default.Security
            },
            contentDescription = "Status",
            tint = color,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun DeviceInfoSection(info: DeviceInfo, isVisible: Boolean) {
    Column(modifier = Modifier
        .animateContentSize()
        .padding(horizontal = 16.dp)) {
        SectionTitle(titleResId = R.string.section_title_device_info)

        var startAnimation by remember { mutableStateOf(false) }

        LaunchedEffect(info.country, isVisible) {
            if (isVisible && info.country != "加载中...") {
                delay(300)
                startAnimation = true
            }
        }

        if (!startAnimation) {
            DeviceInfoSkeleton()
        } else {
            HUDModuleContainer {
                Row(Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp), Arrangement.SpaceEvenly, Alignment.CenterVertically) {
                    HUDCircularGauge(
                        value = info.batteryHealthPercent,
                        title = stringResource(id = R.string.gauge_title_health),
                        color = Color(0xFF22C55E),
                        startAnimation = startAnimation
                    )

                    val currentCapacityValue = if (info.designCapacity > 0) (info.currentCapacity.toFloat() / info.designCapacity.toFloat()) * 100 else 0f
                    val currentCapacityText = if (info.designCapacity > 0 && info.currentCapacity > 0) "${info.currentCapacity} mAh" else "N/A"
                    HUDCircularGauge(
                        value = currentCapacityValue,
                        title = stringResource(id = R.string.gauge_title_capacity),
                        color = MiuixTheme.colorScheme.primary,
                        valueText = currentCapacityText,
                        startAnimation = startAnimation
                    )

                    val cycleCountMax = if (info.cycleCount == 0) 100 else (ceil(info.cycleCount / 100.0).toInt() * 100)
                    val cycleProgress = if (cycleCountMax > 0) (info.cycleCount.toFloat() / cycleCountMax.toFloat()) * 100f else 0f
                    HUDCircularGauge(
                        value = cycleProgress,
                        title = stringResource(id = R.string.gauge_title_cycles),
                        color = Color(0xFFF59E0B),
                        valueText = info.cycleCount.toString(),
                        startAnimation = startAnimation
                    )
                }
                Box(Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MiuixTheme.colorScheme.primary.copy(alpha = 0.2f))
                    .padding(horizontal = 16.dp))

                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextInfoRow(stringResource(id = R.string.text_info_design_capacity), "${info.designCapacity} mAh")
                    TextInfoRow(stringResource(id = R.string.text_info_actual_capacity), "${info.fullCapacity} mAh")
                    TextInfoRow(stringResource(id = R.string.text_info_calculated_health), "%.1f".format(info.calculatedHealth) + "%")
                }

                Box(Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MiuixTheme.colorScheme.primary.copy(alpha = 0.2f))
                    .padding(horizontal = 16.dp))

                FlowRow(
                    modifier = Modifier.padding(12.dp),
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 8.dp
                ) {
                    InfoChip(icon = Icons.Default.Public, label = stringResource(id = R.string.chip_label_region), value = info.country)
                    InfoChip(icon = Icons.Default.PhoneAndroid, label = stringResource(id = R.string.chip_label_android), value = "${info.androidVersion} (API ${info.sdkVersion})")
                    InfoChip(icon = Icons.Default.Layers, label = stringResource(id = R.string.chip_label_system), value = info.systemVersion)
                    InfoChip(icon = Icons.Default.Storage, label = stringResource(id = R.string.chip_label_status), value = "${info.batteryHealthDisplay} (${info.batteryHealthRaw})")
                }
            }
        }
    }
}

@Composable
fun DeviceInfoSkeleton() {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse)
    )
    HUDModuleContainer {
        Row(Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), Arrangement.SpaceEvenly, Alignment.CenterVertically) {
            Box(Modifier
                .size(80.dp)
                .clip(CutCornerShape(8.dp))
                .background(MiuixTheme.colorScheme.onBackground.copy(alpha = alpha)))
            Box(Modifier
                .size(80.dp)
                .clip(CutCornerShape(8.dp))
                .background(MiuixTheme.colorScheme.onBackground.copy(alpha = alpha)))
            Box(Modifier
                .size(80.dp)
                .clip(CutCornerShape(8.dp))
                .background(MiuixTheme.colorScheme.onBackground.copy(alpha = alpha)))
        }
        Box(Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MiuixTheme.colorScheme.primary.copy(alpha = 0.2f))
            .padding(horizontal = 16.dp))

        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(10.dp)
                .clip(CutCornerShape(4.dp))
                .background(MiuixTheme.colorScheme.onBackground.copy(alpha = alpha)))
            Box(modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(10.dp)
                .clip(CutCornerShape(4.dp))
                .background(MiuixTheme.colorScheme.onBackground.copy(alpha = alpha)))
            Box(modifier = Modifier
                .fillMaxWidth(0.55f)
                .height(10.dp)
                .clip(CutCornerShape(4.dp))
                .background(MiuixTheme.colorScheme.onBackground.copy(alpha = alpha)))
        }

        Box(Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MiuixTheme.colorScheme.primary.copy(alpha = 0.2f))
            .padding(horizontal = 16.dp))

        FlowRow(
            modifier = Modifier.padding(12.dp),
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp
        ) {
            repeat(4) {
                Box(modifier = Modifier
                    .height(40.dp)
                    .width(120.dp)
                    .clip(CutCornerShape(4.dp))
                    .background(MiuixTheme.colorScheme.onBackground.copy(alpha = alpha)))
            }
        }
    }
}

@Composable
fun TextInfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, color = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.7f), fontFamily = FontFamily.Monospace, fontSize = 12.sp)
        Spacer(Modifier.weight(1f))
        Text(text = value, color = MiuixTheme.colorScheme.onBackground, fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun HUDModuleContainer(content: @Composable ColumnScope.() -> Unit) {
    val shape = CutCornerShape(8.dp)
    Column(
        modifier = Modifier
            .clip(shape)
            .background(MiuixTheme.colorScheme.onBackground.copy(alpha = 0.03f))
            .border(1.dp, MiuixTheme.colorScheme.primary.copy(alpha = 0.3f), shape)
            .padding(8.dp)
    ) { content() }
}

@Composable
fun HUDCircularGauge(value: Float, title: String, color: Color, strokeWidth: Dp = 6.dp, valueText: String? = null, startAnimation: Boolean = false) {
    val safeValue = if (value.isNaN() || value.isInfinite()) 0f else value
    val animatedValue by animateFloatAsState(
        targetValue = if(startAnimation) safeValue else 0f,
        animationSpec = tween(1000, 300)
    )
    val textColor = MiuixTheme.colorScheme.onBackground

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val sweepAngle = (animatedValue / 100f) * 360f
                val angleRad = (sweepAngle - 90) * (Math.PI / 180f).toFloat()

                for(i in 0..360 step 15) {
                    val tickLength = if (i % 45 == 0) 6.dp.toPx() else 3.dp.toPx()
                    val angle = i * (Math.PI / 180f).toFloat()
                    drawLine(color = textColor.copy(alpha = 0.2f), start = Offset(center.x + (center.x - tickLength) * cos(angle), center.y + (center.y-tickLength) * sin(angle)), end = Offset(center.x + center.x * cos(angle), center.y + center.y * sin(angle)), strokeWidth = 1.dp.toPx())
                }

                drawArc(color.copy(alpha = 0.2f), -90f, 360f, false, style = Stroke(strokeWidth.toPx()))
                if(animatedValue > 0) drawArc(color, -90f, sweepAngle, false, style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round))

                drawCircle(brush = Brush.radialGradient(listOf(color.copy(alpha = 0.8f), Color.Transparent)), radius = strokeWidth.toPx() * 1.5f, center = Offset(center.x + (center.x - strokeWidth.toPx()/2) * cos(angleRad), center.y + (center.y-strokeWidth.toPx()/2) * sin(angleRad)))
            }
            Text(valueText ?: "${animatedValue.roundToInt()}%", color = textColor, fontSize = if (valueText != null && valueText.length > 4) 12.sp else 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Text(title, color = textColor.copy(alpha = 0.7f), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun InfoChip(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .clip(CutCornerShape(4.dp))
            .background(MiuixTheme.colorScheme.onBackground.copy(alpha = 0.1f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = MiuixTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Column {
            Text(text = label, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.7f))
            Text(text = value, fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.9f))
        }
    }
}

@Composable
fun FeatureItem(feature: FeatureUI, navController: NavController) {
    val context = LocalContext.current
    val summaryWithRoute = remember {
        val route = GetFuncRoute(feature.category, context)
        (feature.summary ?: "") + if (route.isNotEmpty() && feature.summary?.isNotEmpty() == true) "\n$route" else route
    }

    var isHovered by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(CutCornerShape(4.dp))
            .clickable { navController.navigate(feature.category) }
            .background(if (isHovered) MiuixTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = ">", color = MiuixTheme.colorScheme.primary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = feature.title, color = MiuixTheme.colorScheme.onBackground, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
            if (summaryWithRoute.isNotEmpty()) {
                Text(text = summaryWithRoute, color = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.6f), fontSize = 11.sp, lineHeight = 13.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

// --- 辅助函数 ---
@SuppressLint("PrivateApi")
fun getSystemProperty(name: String): String { return try { Class.forName("android.os.SystemProperties").getMethod("get", String::class.java).invoke(null, name) as String } catch (e: Exception) { "null" } }
suspend fun executeCommand(command: String): String { return withContext(Dispatchers.IO) { try { val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command)); BufferedReader(InputStreamReader(process.inputStream)).use { it.readText() }.also { process.waitFor() }.trim() } catch (e: Exception) { "" } } }
