package com.suqi8.oshin.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import com.suqi8.oshin.utils.AppInfoCache.getCached
import com.suqi8.oshin.utils.AppInfoCache.updateCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


// =================================================================================
// AppNameCache 类的定义
// =================================================================================

class AppNameCache(private val context: Context) {
    private val cache = mutableMapOf<String, String>()

    fun getAppName(packageName: String): String {
        cache[packageName]?.let { return it }
        val appName = getAppNameFromPackage(packageName)
        cache[packageName] = appName
        return appName
    }

    private fun getAppNameFromPackage(packageName: String): String {
        val packageManager = context.packageManager
        return try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(applicationInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown App"
        }
    }
}

/**
 * 专门负责提供应用信息的类
 * - 内部处理缓存，避免重复IO查询
 * - 提供一个清晰的接口来获取应用信息
 *
 * @param context 安卓上下文，用于访问PackageManager
 */
class AppInfoProvider(private val context: Context) {

    // 使用一个Map作为内存缓存
    private val cache = mutableMapOf<String, AppInfo?>()

    /**
     * 高效获取应用信息的挂起函数 (Suspend Function)
     * - 首先检查缓存
     * - 如果缓存未命中，则从系统加载，并存入缓存
     *
     * @param packageName 需要查询的应用包名
     * @return 返回包含名称和图标的 AppInfo 对象，如果找不到则返回 null
     */
    suspend fun getInfo(packageName: String): AppInfo? {
        // 1. 检查缓存
        if (cache.containsKey(packageName)) {
            return cache[packageName]
        }

        // 2. 如果缓存没有，则在IO线程中从系统加载
        return withContext(Dispatchers.IO) {
            try {
                val pm = context.packageManager
                val appInfo = pm.getApplicationInfo(packageName, 0)

                val name = pm.getApplicationLabel(appInfo).toString()
                val icon = appInfo.loadIcon(pm).toBitmap().asImageBitmap()

                val result = AppInfo(name = name, icon = icon)

                // 3. 将结果存入缓存
                cache[packageName] = result

                result
            } catch (e: PackageManager.NameNotFoundException) {
                // 如果应用未找到，也缓存一个null结果，避免重复查询不存在的应用
                cache[packageName] = null
                null
            }
        }
    }
}

/**
 * 一个简单的数据类，用于封装应用信息
 * @param name 应用名称
 * @param icon 应用图标
 */
data class AppInfo(
    val name: String,
    val icon: ImageBitmap
)

// =================================================================================
// 使用 AppNameCache 的 Composable 函数
// =================================================================================

@Composable
fun GetAppName(packageName: String): String {
    val context = LocalContext.current
    val appNameCache = remember { AppNameCache(context) }
    return appNameCache.getAppName(packageName)
}


// =================================================================================
// AppIcon 相关的功能 (为了完整性，将之前的相关代码也放在这里)
// =================================================================================

object AppInfoCache {
    private val cache = mutableMapOf<String, Pair<String, ImageBitmap>>()

    fun getCached(packageName: String): Pair<String, ImageBitmap>? {
        return cache[packageName]
    }

    fun updateCache(packageName: String, info: Pair<String, ImageBitmap>) {
        cache[packageName] = info
    }
}

@Composable
fun GetAppIconAndName(
    packageName: String,
    onAppInfoLoaded: @Composable (String, ImageBitmap) -> Unit
) {
    val context = LocalContext.current
    var result by remember(packageName) { mutableStateOf<Pair<String, ImageBitmap>?>(null) }

    LaunchedEffect(packageName) {
        val cached = getCached(packageName)
        if (cached != null) {
            result = cached
            return@LaunchedEffect
        }

        try {
            val appInfo = context.packageManager.getApplicationInfo(packageName, 0)
            val icon = appInfo.loadIcon(context.packageManager)
            val appName = context.packageManager.getApplicationLabel(appInfo).toString()

            // 获取安全尺寸
            val width = icon.intrinsicWidth.takeIf { it > 0 } ?: 1
            val height = icon.intrinsicHeight.takeIf { it > 0 } ?: 1
            icon.setBounds(0, 0, width, height)

            val bitmap = icon.toBitmap(width, height).asImageBitmap()
            updateCache(packageName, appName to bitmap)
            result = appName to bitmap
        } catch (e: PackageManager.NameNotFoundException) {
            result = "noapp" to ImageBitmap(1, 1)
        }
    }

    result?.let { onAppInfoLoaded(it.first, it.second) }
}
