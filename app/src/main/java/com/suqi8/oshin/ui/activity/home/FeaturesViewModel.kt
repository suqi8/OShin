package com.suqi8.oshin.ui.activity.home

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.highcapable.yukihookapi.YukiHookAPI
import com.suqi8.oshin.R
import com.suqi8.oshin.features
import com.suqi8.oshin.utils.GetFuncRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.UUID

// =================================================================================
// 1. 定义UI状态数据类 (Data Classes for UI State)
// =================================================================================

data class MainUiState(
    val statusInfo: StatusInfo = StatusInfo(),
    val deviceInfo: DeviceInfo = DeviceInfo(),
    val features: List<FeatureUI> = emptyList(),
    val isLoading: Boolean = true
)

data class StatusInfo(
    val isModuleActive: Boolean = false,
    val rootState: RootState = RootState.DETECTING,
    val rootVersion: String = ""
)

data class DeviceInfo(
    val country: String = "Loading...",
    val nvid: String = "0",
    val androidVersion: String = "${Build.VERSION.RELEASE} / ${Build.VERSION.SDK_INT}",
    val systemVersion: String = Build.DISPLAY,
    val batteryHealth: String = "Loading...",
    val batteryHealthRaw: String = "Unknown",
    val equivalentCapacity: String = "0 mAh",
    val currentCapacity: String = "0 mAh",
    val fullCapacity: String = "0 mAh",
    val batterySOH: String = "0% / 0%",
    val cycleCount: String = "0"
)

data class FeatureUI(
    val id: String,
    val title: String,
    val summary: String?,
    val category: String,
    val route: String
)

enum class RootState { DETECTING, GRANTED, DENIED }


// =================================================================================
// 2. ViewModel 主类 (The ViewModel Class)
// =================================================================================

class FeaturesViewModel(private val application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
        startBatteryUpdates()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val featuresJob = launch(Dispatchers.IO) { loadAllFeatures() }
            val statusJob = launch(Dispatchers.IO) { checkDeviceStatus() }
            val deviceInfoJob = launch(Dispatchers.IO) { loadInitialDeviceInfo() }

            joinAll(featuresJob, statusJob, deviceInfoJob)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun loadAllFeatures() {
        val context = application.applicationContext
        val featuresData = features(context)
            .shuffled()
            .map {
                val route = (if (it.summary != null) "\n" else "") + GetFuncRoute(it.category, context)
                FeatureUI(
                    id = UUID.randomUUID().toString(),
                    title = it.title,
                    summary = it.summary,
                    category = it.category,
                    route = route
                )
            }
        _uiState.update { it.copy(features = featuresData) }
    }

    private suspend fun checkDeviceStatus() {
        val isActive = YukiHookAPI.Status.isModuleActive

        try {
            val process = Runtime.getRuntime().exec("su -c exit")
            val isRooted = process.waitFor() == 0
            val rootState = if (isRooted) RootState.GRANTED else RootState.DENIED
            val rootVer = if (isRooted) {
                val ksuVersion = executeCommand("ksud -V")
                if (ksuVersion.isNotEmpty()) ksuVersion.substringAfter("ksud ").trim()
                else {
                    val magiskVersion = executeCommand("magisk -v")
                    "$magiskVersion ${executeCommand("magisk -V").trim()}"
                }
            } else ""

            _uiState.update { currentState ->
                currentState.copy(
                    statusInfo = currentState.statusInfo.copy(
                        isModuleActive = isActive,
                        rootState = rootState,
                        rootVersion = rootVer
                    )
                )
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(statusInfo = it.statusInfo.copy(isModuleActive = isActive, rootState = RootState.DENIED)) }
        }
    }

    private suspend fun loadInitialDeviceInfo() {
        val context = application.applicationContext
        val nvid = getSystemProperty("ro.build.oplus_nv_id")
        val health = executeCommand("cat /sys/class/power_supply/battery/health")
        val chargeFullDesign = (try { executeCommand("cat /sys/class/power_supply/battery/charge_full_design").toInt() } catch (e: Exception) { 0 }) / 1000
        val cycleCount = try { executeCommand("cat /sys/class/oplus_chg/battery/battery_cc") } catch (e: Exception) { "0" }

        _uiState.update {
            it.copy(
                deviceInfo = it.deviceInfo.copy(
                    nvid = nvid,
                    country = mapNvidToCountry(nvid, context),
                    batteryHealthRaw = health,
                    batteryHealth = mapHealthToString(health, context),
                    equivalentCapacity = "$chargeFullDesign mAh",
                    cycleCount = cycleCount
                )
            )
        }
    }

    private fun startBatteryUpdates() {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                delay(10000L)
                val chargeFull = (try { executeCommand("cat /sys/class/oplus_chg/battery/charge_full").toIntOrNull() ?: 0 } catch (_: Exception) { 0 }) / 1000
                val chargeCounter = (try { executeCommand("cat /sys/class/power_supply/battery/charge_counter").toIntOrNull() ?: 0 } catch (_: Exception) { 0 }) / 1000
                val currentCapacity = if (chargeFull != 0) chargeFull else chargeCounter
                val fcc = (try { executeCommand("cat /sys/class/oplus_chg/battery/battery_fcc").toIntOrNull() ?: 0 } catch (_: Exception) { 0 })

                val design = (try { executeCommand("cat /sys/class/power_supply/battery/charge_full_design").toFloatOrNull() ?: 1f } catch (_: Exception) { 1f })
                val soh = if(design > 0) (fcc.toFloat() / (design / 1000f)) * 100 else 0f
                val sohFormatted = String.format("%.2f", soh)

                _uiState.update {
                    it.copy(
                        deviceInfo = it.deviceInfo.copy(
                            currentCapacity = "$currentCapacity mAh",
                            fullCapacity = "$fcc mAh",
                            batterySOH = "${getSOH()}% / $sohFormatted%"
                        )
                    )
                }
            }
        }
    }

    // --- Helper & Placeholder Functions ---
    private fun mapNvidToCountry(nvid: String, context: Context): String {
        return when (nvid) {
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
    private fun mapHealthToString(health: String, context: Context): String {
        return when (health) {
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

// --- IO 和系统相关函数 ---
private const val TAG = "FeaturesViewModel"

private fun executeCommand(command: String): String {
    return try {
        val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val output = reader.readText()
        process.waitFor()
        reader.close()
        output.trim()
    } catch (e: Exception) {
        Log.e(TAG, "executeCommand failed for: $command", e)
        ""
    }
}

@SuppressLint("PrivateApi")
private fun getSystemProperty(name: String): String {
    return try {
        val method = Class.forName("android.os.SystemProperties").getMethod("get", String::class.java)
        (method.invoke(null, name) as String?) ?: ""
    } catch (e: Exception) {
        Log.e(TAG, "getSystemProperty failed for: $name", e)
        ""
    }
}

@SuppressLint("DefaultLocale")
private fun getSOH(): String {
    var soh = try { executeCommand("cat /sys/class/oplus_chg/battery/battery_soh").toDouble() } catch(e: Exception) { 0.0 }
    val fcc = try { executeCommand("cat /sys/class/oplus_chg/battery/battery_fcc").toDouble() } catch(e: Exception) { 0.0 }
    val getDesignCapacity = try { executeCommand("cat /sys/class/oplus_chg/battery/design_capacity").toDouble() } catch(e: Exception) { 0.0 }

    return when {
        soh < 50 || soh > 101 -> {
            if (getDesignCapacity > 0) {
                soh = (fcc * 100) / getDesignCapacity
            }
            String.format("%.1f", soh)
        }
        else -> String.format("%.1f", soh)
    }
}
