package com.suqi8.oshin.ui.about

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.R
import com.suqi8.oshin.features.FeatureRegistry
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val gson = GsonBuilder().setPrettyPrinting().create()

    private val allPrefsFiles = FeatureRegistry.screenMap.values
        .map { it.category.substringBefore('\\') }
        .distinct() + "settings"

    fun exportSettings(uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val allSettings = mutableMapOf<String, Map<String, *>>()
                allPrefsFiles.forEach { prefName ->
                    val prefs = context.prefs(prefName)
                    if (prefs.all().isNotEmpty()) {
                        allSettings[prefName] = prefs.all()
                    }
                }
                val jsonString = gson.toJson(allSettings)
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(jsonString.toByteArray())
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, context.getString(R.string.export_success), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, context.getString(R.string.export_fail, e.message), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun importSettings(uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).readText()
                } ?: throw IllegalStateException("无法读取文件")

                val type = com.google.gson.reflect.TypeToken.getParameterized(
                    Map::class.java,
                    String::class.java,
                    com.google.gson.reflect.TypeToken.get(Map::class.java).type
                ).type
                val allSettings: Map<String, Map<String, *>> = gson.fromJson(jsonString, type)

                allSettings.forEach { (prefName, settings) ->
                    val editor = context.prefs(prefName).edit()
                    settings.forEach { (key, value) ->
                        when (value) {
                            is String -> editor.putString(key, value)
                            is Boolean -> editor.putBoolean(key, value)
                            is Number -> {
                                val doubleValue = value.toDouble()
                                if (doubleValue % 1 == 0.0 && doubleValue <= Int.MAX_VALUE && doubleValue >= Int.MIN_VALUE)
                                    editor.putInt(key, doubleValue.toInt())
                                else
                                    editor.putFloat(key, doubleValue.toFloat())
                            }
                        }
                    }
                    editor.apply()
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, context.getString(R.string.import_success), Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, context.getString(R.string.import_fail, e.message), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun clearAllSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            allPrefsFiles.forEach { prefName ->
                context.prefs(prefName).edit().clear().apply()
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(context, context.getString(R.string.clear_success), Toast.LENGTH_LONG).show()
            }
        }
    }
}
