package com.suqi8.oshin.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.lifecycle.viewModelScope
import com.highcapable.yukihookapi.YukiHookAPI
import com.suqi8.oshin.R
import com.suqi8.oshin.data.repository.FeatureRepository
import com.suqi8.oshin.ui.module.SearchableItem
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    val cycleCount: Int
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
    val randomFeatures: List<SearchableItem> = emptyList()
)

// --- ViewModel ---

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val featureRepository: FeatureRepository
) : androidx.lifecycle.ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAllData()
        loadRandomFeatures()
    }

    private fun loadAllData() {
        viewModelScope.launch {
            // 并发获取所有数据
            coroutineScope {
                viewModelScope.launch {
                    val items = fetchCarouselData()
                    _uiState.update { it.copy(carouselItems = items) }
                }

                // 加载模块状态
                viewModelScope.launch {
                    val status = getModuleStatus()
                    _uiState.update { it.copy(moduleStatus = status) }
                }

                // 加载 Root 状态
                viewModelScope.launch {
                    val status = getRootStatus()
                    _uiState.update { it.copy(rootStatus = status) }
                }

                // 加载设备信息
                viewModelScope.launch {
                    val info = getDeviceInfo()
                    _uiState.update { it.copy(deviceInfo = info) }
                }
            }
        }
    }

    private fun loadRandomFeatures() {
        viewModelScope.launch {
            val allItems = featureRepository.getAllSearchableItems()
            val randomItems = allItems.shuffled()
            _uiState.update {
                it.copy(randomFeatures = randomItems)
            }
        }
    }

    /**
     * 安全地从 JSONObject 获取字符串，如果键不存在或值为 null，则返回 null。
     */
    private fun JSONObject.optStringOrNull(key: String): String? {
        // 检查键是否存在，并且值不是 JSON 的 null
        if (has(key) && !isNull(key)) {
            return getString(key)
        }
        return null
    }

    private suspend fun fetchCarouselData(): List<CarouselItem> = withContext(Dispatchers.IO) {
        try {
            val client = okhttp3.OkHttpClient.Builder().cache(null).build()
            val request = okhttp3.Request.Builder()
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
            emptyList()
        }
    }

    private suspend fun getModuleStatus(): ModuleStatus = withContext(Dispatchers.Default) {
        if (YukiHookAPI.Status.isModuleActive) {
            ModuleStatus(Status.SUCCESS, "LSPosed v" + YukiHookAPI.Status.Executor.apiLevel)
        } else {
            ModuleStatus(Status.ERROR, "未在LSPosed中激活")
        }
    }

    private suspend fun getRootStatus(): RootStatus = withContext(Dispatchers.IO) {
        try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "echo Connected"))
            if (process.waitFor() == 0) {
                val ksuVersion = executeCommand("/data/adb/ksud -V")
                val version = if (ksuVersion.isNotEmpty()) {
                    "KernelSU $ksuVersion"
                } else {
                    "Magisk " + executeCommand("magisk -v")
                }
                RootStatus(Status.SUCCESS, version.trim())
            } else {
                RootStatus(Status.ERROR, "授权失败")
            }
        } catch (e: Exception) {
            RootStatus(Status.ERROR, "无法获取Root权限")
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
    """.trimIndent()

        val rawData = executeCommand(command)

        // 2. 将 key=value 格式的输出解析到一个 Map 中
        val dataMap = rawData.lines()
            .filter { it.contains("=") }
            .associate {
                val parts = it.split("=", limit = 2)
                parts[0] to parts[1]
            }

        // 3. 从 Map 中安全地按 key 取值
        val chargeFull0 = dataMap["charge_full"]?.toIntOrNull() ?: 0
        val chargeFull1 = dataMap["charge_counter"]?.toIntOrNull() ?: 0
        val currentCapacity = (if (chargeFull0 != 0) chargeFull0 else chargeFull1) / 1000

        val fullCapacity = dataMap["fcc"]?.toIntOrNull() ?: 0
        val soh = dataMap["soh"]?.toFloatOrNull() ?: 0f
        val cycleCount = dataMap["cc"]?.toIntOrNull() ?: 0
        val designCapacity = (dataMap["charge_full_design"]?.toIntOrNull() ?: 0) / 1000
        val healthRaw = dataMap["health"]?.trim() ?: "Unknown"

        val calculatedHealth = if (designCapacity > 0 && fullCapacity > 0) {
            (fullCapacity.toFloat() / designCapacity.toFloat()) * 100f
        } else 0f

        // 构造 DeviceInfo 对象
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
            fullCapacity = fullCapacity
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
