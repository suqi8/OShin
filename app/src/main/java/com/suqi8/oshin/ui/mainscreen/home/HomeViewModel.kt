package com.suqi8.oshin.ui.mainscreen

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.highcapable.yukihookapi.YukiHookAPI
import com.suqi8.oshin.R
import com.suqi8.oshin.data.repository.FeatureRepository
import com.suqi8.oshin.ui.mainscreen.module.SearchableItem
import com.suqi8.oshin.utils.RouteFormatter
import com.umeng.union.UMNativeAD
import com.umeng.union.UMUnionSdk
import com.umeng.union.api.UMAdConfig
import com.umeng.union.api.UMUnionApi
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

// --- 数据模型 ---

enum class Status { LOADING, SUCCESS, ERROR, WARNING }

data class ModuleStatus(val status: Status, val message: String = "")
data class RootStatus(val status: Status, val version: String = "")
data class FridaStatus(val status: Status, val version: String = "")

data class DeviceInfo(
    val country: String,
    val androidVersion: String,
    val sdkVersion: String,
    val systemVersion: String,
    val designCapacity: Int,
    val currentCapacity: Int,
    val fullCapacity: Int,
    val batteryHealthDisplay: String,
    val batteryHealthRaw: String,
    val batteryHealthPercent: Float,
    val calculatedHealth: Float,
    val cycleCount: Int,
    val chipSoc: Int
)

data class CarouselItem(
    val title: String?,
    val description: String?,
    val actionUrl: String?,
    val imageUrl: String?
)

data class HomeUiState(
    val carouselItems: List<CarouselItem>? = null,
    val moduleStatus: ModuleStatus = ModuleStatus(Status.LOADING),
    val rootStatus: RootStatus = RootStatus(Status.LOADING),
    val fridaStatus: FridaStatus = FridaStatus(Status.ERROR, "未连接"),
    val deviceInfo: DeviceInfo? = null,
    val randomFeatures: List<HighlightFeature> = emptyList(),
    val combinedCarouselItems: List<CarouselContent> = emptyList(),
    val isCarouselLoading: Boolean = true
)

data class HighlightFeature(
    val searchableItem: SearchableItem, // 原始数据
    val formattedRoute: String          // 预先格式化好的路由
)

sealed interface CarouselContent {
    // 为每个卡片提供一个唯一的 key，这对于 Pager 的性能和稳定性很重要
    val key: Any

    data class Promo(val item: CarouselItem) : CarouselContent {
        override val key: Any = item.actionUrl ?: item.imageUrl ?: item.title ?: "promo_${item.hashCode()}"
    }

    data class Ad(val ad: UMNativeAD) : CarouselContent {
        override val key: Any = ad // 广告对象本身就可以作为 key
    }
}

// --- ViewModel ---

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val featureRepository: FeatureRepository,
    private val routeFormatter: RouteFormatter
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private var tempPromoItems: List<CarouselItem>? = null
    private var tempAd: UMNativeAD? = null

    init {
        loadAllData()
        loadRandomFeatures()
        loadNativeBannerAd()
    }

    fun loadNativeBannerAd() {
        Log.d("UmengAd", "loadNativeBannerAd: 开始请求广告...")
        viewModelScope.launch {
            _uiState.update { it.copy(isCarouselLoading = true) }

            // 1. 创建广告请求配置
            val config = UMAdConfig.Builder()
                .setSlotId("100004273") // 请确保这是你的视频广告位ID
                .build()

            // 2. 创建广告加载监听器 (每次调用时都创建新的实例以避免潜在的内存问题)
            val listener = object : UMUnionApi.AdLoadListener<UMNativeAD> {

                override fun onSuccess(type: UMUnionApi.AdType, display: UMNativeAD) {
                    Log.d("UmengAd", "onSuccess: 广告加载成功. Title: ${display.title}, isVideo: ${display.isVideo}")

                    // 将获取到的广告对象暂存起来
                    tempAd = display

                    // 调用数据合并逻辑
                    combineAdAndPromoItems()

                    // 为广告对象设置曝光和点击监听
                    display.setAdEventListener(object : UMUnionApi.AdEventListener {
                        override fun onExposed() {
                            Log.d("UmengAd", "Ad Event: 广告曝光成功")
                        }
                        override fun onClicked(view: View) {
                            Log.d("UmengAd", "Ad Event: 广告点击成功")
                        }
                        override fun onError(code: Int, message: String) {
                            Log.e("UmengAd", "Ad Event Error: code=$code, msg=$message")
                        }
                    })

                    // 如果是视频广告，可以设置视频相关的监听
                    if (display.isVideo) {
                        display.setVideoListener(object : UMUnionApi.VideoListener {
                            override fun onReady() { Log.d("UmengAd", "Video Event: onReady") }
                            override fun onStart() { Log.d("UmengAd", "Video Event: onStart") }
                            override fun onPause() { Log.d("UmengAd", "Video Event: onPause") }
                            override fun onCompleted() { Log.d("UmengAd", "Video Event: onCompleted") }
                            override fun onError(message: String) { Log.e("UmengAd", "Video Event Error: $message") }
                        })
                    }
                }

                override fun onFailure(type: UMUnionApi.AdType, message: String) {
                    Log.e("UmengAd", "onFailure: 广告请求出错: $message")

                    // 确保失败时广告对象为null
                    tempAd = null

                    // 即使广告失败，也要调用一次合并，以便显示轮播图
                    combineAdAndPromoItems()
                }
            }

            // 3. 发起广告加载请求
            UMUnionSdk.loadNativeBannerAd(config, listener)
        }
    }

    /**
     * 合并推广内容和广告内容，并更新UI状态。
     * 这个函数是线程安全的，可以在任何时候被调用。
     */
    private fun combineAdAndPromoItems() {
        // 确保轮播图数据已经加载完成，否则等待下一次调用
        val promos = tempPromoItems ?: return

        // 1. 创建一个 mutableListOf<CarouselContent> 用于存放所有内容
        val combinedList = mutableListOf<CarouselContent>()

        // 2. 将普通推广项转换为 CarouselContent.Promo 并添加到列表中
        val promoContent = promos.map { CarouselContent.Promo(it) }
        combinedList.addAll(promoContent)

        // 3. 检查是否有有效广告，如果有，也添加到列表中
        val ad = tempAd
        if (ad != null && ad.isValid) {
            combinedList.add(CarouselContent.Ad(ad))
            Log.d("UmengAd", "combine: 成功添加一个广告到待打乱列表")
        } else {
            Log.d("UmengAd", "combine: 没有有效广告可添加")
        }

        combinedList.shuffle()
        Log.d("UmengAd", "combine: 列表已随机打乱，总数: ${combinedList.size}")

        // 5. 更新UI State
        _uiState.update { it.copy(combinedCarouselItems = combinedList, isCarouselLoading = false) }
    }

    private fun loadAllData() {
        viewModelScope.launch {
            coroutineScope {
                // 并发加载轮播数据
                launch {
                    val items = fetchCarouselData()
                    _uiState.update { it.copy(carouselItems = items) }
                }

                // 加载模块状态
                launch {
                    val status = getModuleStatus()
                    _uiState.update { it.copy(moduleStatus = status) }
                }

                // 加载 Root 状态
                launch {
                    val status = getRootStatus()
                    _uiState.update { it.copy(rootStatus = status) }
                }

                // 加载设备信息
                launch {
                    val info = getDeviceInfo()
                    _uiState.update { it.copy(deviceInfo = info) }
                }
            }
        }
    }

    private fun loadRandomFeatures() {
        viewModelScope.launch {
            val allItems = featureRepository.getAllSearchableItems()

            val highlightFeatures = allItems.shuffled().map { item ->
                val routeId = item.route.substringAfter("feature/")
                val formattedRoute = routeFormatter.formatRouteAsBreadcrumb(routeId)

                HighlightFeature(
                    searchableItem = item,
                    formattedRoute = formattedRoute
                )
            }

            _uiState.update {
                it.copy(randomFeatures = highlightFeatures)
            }
        }
    }

    /**
     * 安全地从 JSONObject 获取字符串，如果键不存在或值为 null，则返回 null。
     */
    private fun JSONObject.optStringOrNull(key: String): String? {
        if (has(key) && !isNull(key)) {
            return getString(key)
        }
        return null
    }

    private suspend fun fetchCarouselData(): List<CarouselItem> = withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient.Builder().cache(null).build()
            val request = Request.Builder()
                .url("https://gitee.com/yo-gurt/OShin/raw/master/lunbo.json")
                .header("Cache-Control", "no-cache")
                .build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@use emptyList()
                val body = response.body.string()
                val jsonArray = JSONArray(body)
                List(jsonArray.length()) { i ->
                    val obj = jsonArray.getJSONObject(i)
                    CarouselItem(
                        title = obj.optStringOrNull("title"),
                        description = obj.optStringOrNull("description"),
                        actionUrl = obj.optStringOrNull("action_url"),
                        imageUrl = obj.optStringOrNull("image_url")
                    )
                }
            }
        } catch (e: Exception) {
            // 返回默认数据以供展示
            listOf(
                CarouselItem(
                    title = "Freemium",
                    description = "A handpicked collection of stunning free wallpapers that fit your vibe perfectly.",
                    actionUrl = null,
                    imageUrl = null
                )
            )
        }
    }.also { fetchedItems ->
        // 保存原始轮播数据，并调用合并函数
        tempPromoItems = fetchedItems
        combineAdAndPromoItems()
    }

    private suspend fun getModuleStatus(): ModuleStatus = withContext(Dispatchers.Default) {
        if (YukiHookAPI.Status.isModuleActive) {
            ModuleStatus(Status.SUCCESS, "SUCCESS")
        } else {
            ModuleStatus(Status.ERROR, context.getString(R.string.status_module_error_inactive))
        }
    }

    private suspend fun getRootStatus(): RootStatus = withContext(Dispatchers.IO) {
        try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "echo Connected"))
            if (process.waitFor() == 0) {
                val ksuVersion = executeCommand("/data/adb/ksud -V")
                val version = if (ksuVersion.isNotEmpty()) {
                    context.getString(R.string.status_root_version_kernelsu, ksuVersion)
                } else {
                    context.getString(R.string.status_root_version_magisk, executeCommand("magisk -v"))
                }
                RootStatus(Status.SUCCESS, version.trim())
            } else {
                RootStatus(Status.ERROR, context.getString(R.string.status_root_error_denied))
            }
        } catch (e: Exception) {
            RootStatus(Status.ERROR, context.getString(R.string.status_root_error_unavailable))
        }
    }

    private suspend fun getDeviceInfo(): DeviceInfo = withContext(Dispatchers.IO) {
        val command = """
            echo "charge_full=$(cat /sys/class/oplus_chg/battery/charge_full 2>/dev/null)"
            echo "charge_counter=$(cat /sys/class/power_supply/battery/charge_counter 2>/dev/null)"
            echo "fcc=$(cat /sys/class/oplus_chg/battery/battery_fcc 2>/dev/null)"
            echo "soh=$(cat /sys/class/oplus_chg/battery/battery_soh 2>/dev/null)"
            echo "cc=$(cat /sys/class/oplus_chg/battery/battery_cc 2>/dev/null)"
            echo "charge_full_design=$(cat /sys/class/power_supply/battery/charge_full_design 2>/dev/null)"
            echo "health=$(cat /sys/class/power_supply/battery/health 2>/dev/null)"
            echo "chip_soc=$(cat /sys/class/oplus_chg/battery/chip_soc 2>/dev/null)"
        """.trimIndent()

        val rawData = executeCommand(command)

        val dataMap = rawData.lines()
            .filter { it.contains("=") }
            .associate {
                val parts = it.split("=", limit = 2)
                parts[0] to parts[1]
            }

        val chargeFull0 = dataMap["charge_full"]?.toIntOrNull() ?: 0
        val chargeFull1 = dataMap["charge_counter"]?.toIntOrNull() ?: 0
        val currentCapacity = (if (chargeFull0 != 0) chargeFull0 else chargeFull1) / 1000

        val fullCapacity = dataMap["fcc"]?.toIntOrNull() ?: 0
        val soh = dataMap["soh"]?.toFloatOrNull() ?: 0f
        val cycleCount = dataMap["cc"]?.toIntOrNull() ?: 0
        val designCapacity = (dataMap["charge_full_design"]?.toIntOrNull() ?: 0) / 1000
        val healthRaw = dataMap["health"]?.trim() ?: "Unknown"
        val chipSoc = dataMap["chip_soc"]?.toIntOrNull() ?: 0

        val calculatedHealth = if (designCapacity > 0 && fullCapacity > 0) {
            (fullCapacity.toFloat() / designCapacity.toFloat()) * 100f
        } else 0f

        DeviceInfo(
            country = mapNvidToCountry(getSystemProperty("ro.build.oplus_nv_id")),
            batteryHealthDisplay = mapHealthToString(healthRaw),
            batteryHealthRaw = healthRaw,
            batteryHealthPercent = soh,
            calculatedHealth = calculatedHealth,
            cycleCount = cycleCount,
            androidVersion = Build.VERSION.RELEASE,
            sdkVersion = Build.VERSION.SDK_INT.toString(),
            systemVersion = Build.DISPLAY,
            designCapacity = designCapacity,
            currentCapacity = currentCapacity,
            fullCapacity = fullCapacity,
            chipSoc = chipSoc
        )
    }

    // --- 辅助函数 ---
    private fun mapNvidToCountry(nvid: String): String = when (nvid) {
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

    private fun mapHealthToString(health: String): String = when (health) {
        "Good" -> context.getString(R.string.battery_health_good)
        "Overheat" -> context.getString(R.string.battery_health_overheat)
        "Dead" -> context.getString(R.string.battery_health_dead)
        "Over Voltage" -> context.getString(R.string.battery_health_over_voltage)
        "Cold" -> context.getString(R.string.battery_health_cold)
        "Unknown" -> context.getString(R.string.battery_health_unknown)
        else -> context.getString(R.string.battery_health_not_found)
    }

    private suspend fun executeCommand(command: String): String = withContext(Dispatchers.IO) {
        try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
            BufferedReader(InputStreamReader(process.inputStream)).use { it.readText() }
                .also { process.waitFor() }.trim()
        } catch (e: Exception) {
            ""
        }
    }

    @SuppressLint("PrivateApi")
    private fun getSystemProperty(name: String): String {
        return try {
            Class.forName("android.os.SystemProperties").getMethod("get", String::class.java)
                .invoke(null, name) as String
        } catch (e: Exception) {
            "null"
        }
    }
}
