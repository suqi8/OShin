package com.suqi8.oshin.data.repository

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.suqi8.oshin.utils.AppInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppInfoProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val pm: PackageManager = context.packageManager

    private val NULL_CACHE_ENTRY = Any()

    // 使用线程安全的 ConcurrentHashMap 作为缓存
    private val cache = ConcurrentHashMap<String, Any>()

    /**
     * 高效获取应用信息（名称 + 图标）
     * - 优先读缓存，失败则从系统加载
     * - 找不到应用会缓存 null，防止重复查询
     */
    suspend fun getInfo(packageName: String): AppInfo? {
        // 1. 检查缓存
        val cachedValue = cache[packageName]
        if (cachedValue != null) {
            return if (cachedValue === NULL_CACHE_ENTRY) {
                null // 这是一个缓存的 "未找到" 结果
            } else {
                cachedValue as AppInfo // 这是一个缓存的 AppInfo
            }
        }

        // 2. 缓存未命中，在 IO 线程加载
        return withContext(Dispatchers.IO) {
            try {
                val appInfo = pm.getApplicationInfo(packageName, 0)
                val name = pm.getApplicationLabel(appInfo).toString()
                val icon = appInfo.loadIcon(pm).toBitmap().asImageBitmap()

                val result = AppInfo(name = name, icon = icon)

                // 3. 存入缓存
                cache[packageName] = result
                result
            } catch (e: PackageManager.NameNotFoundException) {
                // 缓存 null 结果，避免重复查询不存在的应用
                cache[packageName] = NULL_CACHE_ENTRY
                null
            }
        }
    }

    /**
     * 仅获取应用名称的便捷函数
     */
    suspend fun getAppName(packageName: String): String {
        // 如果找不到应用，返回包名作为兜底
        return getInfo(packageName)?.name ?: packageName
    }
}
