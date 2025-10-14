package com.suqi8.oshin.ui.activity.func.feature

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.hook.settings.SettingsFeature
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * OplusSettingsScreen 的专属 UI 状态。
 *
 * @param isLoading 是否正在加载功能列表。
 * @param methodNames 动态扫描到的方法名列表。
 * @param itemStates 存储每个方法名（key）对应的下拉菜单选项索引（value）。
 */
data class OplusSettingsUiState(
    val isLoading: Boolean = true,
    val methodNames: List<String> = emptyList(),
    val itemStates: Map<String, Int> = emptyMap()
)

/**
 * 专为 OplusSettingsScreen 设计的 ViewModel。
 * 负责管理动态扫描的功能列表及其状态，独立于通用的 featureViewModel。
 */
@HiltViewModel
class OplusSettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(OplusSettingsUiState())
    val uiState = _uiState.asStateFlow()

    private val prefs = context.prefs(SettingsFeature.OPLUS_SETTINGS_PREFS_NAME)

    /**
     * 当从 DataChannel 接收到新的方法列表时，加载或刷新所有设置项的初始状态。
     *
     * @param keys 从 Hook 端获取到的最新方法名列表。
     */
    fun loadItemsState(keys: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            val initialStates = mutableMapOf<String, Int>()
            keys.forEach { key ->
                // 所有动态项都是下拉菜单，其值为 Int 类型。默认值为 0 ("默认")。
                initialStates[key] = prefs.getInt(key, 0)
            }

            // 更新 UI 状态
            _uiState.update {
                it.copy(
                    isLoading = false,
                    methodNames = keys,
                    itemStates = initialStates
                )
            }
        }
    }

    /**
     * 当用户在 UI 上更改了某个下拉菜单的选项时，调用此方法更新状态。
     *
     * @param key 发生变化的方法名。
     * @param newIndex 新选中的选项索引。
     */
    fun updateState(key: String, newIndex: Int) {
        // 1. 立即更新内存中的 UI 状态，确保 UI 快速响应
        _uiState.update { currentState ->
            currentState.copy(itemStates = currentState.itemStates + (key to newIndex))
        }

        // 2. 在后台线程将新值写入 SharedPreferences 持久化
        viewModelScope.launch(Dispatchers.IO) {
            prefs.edit {
                putInt(key, newIndex)
            }
        }
    }

    /**
     * 设置加载状态。
     */
    fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }
}
