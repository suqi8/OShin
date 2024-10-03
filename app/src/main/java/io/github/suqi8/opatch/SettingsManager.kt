package io.github.suqi8.opatch

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

class SettingsManager(private val context: Context) {

    private val dataStore = context.dataStore // 初始化一次

    suspend fun getSetting(name: String): String? {
        val preferences = dataStore.data.first()
        val KEY = stringPreferencesKey(name)
        return preferences[KEY]
    }
}
