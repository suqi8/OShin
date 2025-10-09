package com.suqi8.oshin.ui.activity.feature

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.features.FeatureRegistry
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.DisplayCondition
import com.suqi8.oshin.models.Dropdown
import com.suqi8.oshin.models.Operator
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.Picture
import com.suqi8.oshin.models.Slider
import com.suqi8.oshin.models.StringInput
import com.suqi8.oshin.models.Switch
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

/**
 * FeatureScreen 的 UI 状态
 */
data class featureUiState(
    val isLoading: Boolean = true,
    val pageDefinition: PageDefinition? = null,
    val itemStates: Map<String, Any> = emptyMap(),
    val highlightKey: String? = null
)

@HiltViewModel
class featureViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle // 用于从导航参数中获取 categoryId
) : ViewModel() {

    private val _uiState = MutableStateFlow(featureUiState())
    val uiState = _uiState.asStateFlow()

    private val categoryId: String = savedStateHandle.get<String>("categoryId")!!
    private val highlightKey: String? = savedStateHandle.get<String>("highlightKey")
    private val pageDefinition: PageDefinition? = FeatureRegistry.screenMap[categoryId]

    init {
        if (pageDefinition != null) {
            _uiState.update { it.copy(
                pageDefinition = pageDefinition,
                highlightKey = highlightKey
            )}
            loadInitialStates(pageDefinition)
        } else {
            _uiState.update { it.copy(isLoading = false) } // 页面定义未找到
        }
    }

    /**
     * 加载页面所有功能项的初始值
     */
    private fun loadInitialStates(pageDef: PageDefinition) {
        viewModelScope.launch(Dispatchers.IO) {
            val initialStates = mutableMapOf<String, Any>()
            val prefs = context.prefs(pageDef.category)

            pageDef.items.filterIsInstance<CardDefinition>()
                .flatMap { it.items }
                .forEach { item ->
                    when (item) {
                        is Switch -> initialStates[item.key] = prefs.getBoolean(item.key, item.defaultValue)
                        is Slider -> initialStates[item.key] = prefs.getFloat(item.key, item.defaultValue)
                        is Dropdown -> initialStates[item.key] = prefs.getInt(item.key, item.defaultValue)
                        is StringInput -> initialStates[item.key] = prefs.getString(item.key, item.defaultValue)
                        is Picture -> {
                            val file = File(item.targetPath)
                            if (file.exists()) {
                                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                                if (bitmap != null) {
                                    initialStates[item.key] = bitmap.asImageBitmap()
                                }
                            }
                        }
                        else -> {}
                    }
                }

            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(itemStates = initialStates, isLoading = false) }
            }
        }
    }

    fun saveImageFromUri(key: String, targetPath: String, uri: Uri?) {
        if (uri == null) return

        viewModelScope.launch(Dispatchers.IO) { // 在 IO 线程执行文件操作
            try {
                val targetFile = File(targetPath)
                targetFile.parentFile?.mkdirs()

                // 从 Uri 读取输入流，并写入到目标文件
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                    }
                }

                // 复制成功后，重新加载 Bitmap 并更新 UI State
                val bitmap = BitmapFactory.decodeFile(targetFile.absolutePath)
                if (bitmap != null) {
                    _uiState.update {
                        it.copy(itemStates = it.itemStates + (key to bitmap.asImageBitmap()))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 当 UI 组件状态变化时调用此方法
     */
    fun updateState(key: String, value: Any) {
        // 更新内存中的状态以立即响应 UI
        _uiState.update {
            it.copy(itemStates = it.itemStates + (key to value))
        }
        // 在后台线程将新值写入 SharedPreferences
        viewModelScope.launch(Dispatchers.IO) {
            pageDefinition?.let { pageDef ->
                context.prefs(pageDef.category).edit {
                    when (value) {
                        is Boolean -> putBoolean(key, value)
                        is Float -> putFloat(key, value)
                        is Int -> putInt(key, value)
                        is String -> putString(key, value)
                    }
                }
            }
        }
    }

    /**
     * 计算一个项的显示条件是否满足
     */
    fun evaluateCondition(
        condition: DisplayCondition?,
        allCurrentStates: Map<String, Any>
    ): Boolean {
        if (condition == null) {
            return true
        }

        // 2. 从传入的参数中获取依赖项的值，而不是从 _uiState.value 中获取
        val dependencyValue = allCurrentStates[condition.dependencyKey]

        return when (condition.operator) {
            Operator.EQUALS -> dependencyValue == condition.requiredValue
            Operator.NOT_EQUALS -> dependencyValue != condition.requiredValue
        }
    }
}
