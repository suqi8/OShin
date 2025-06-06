package com.suqi8.oshin.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
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

    val result by produceState<Pair<String, ImageBitmap>?>(initialValue = null, key1 = packageName) {
        withContext(Dispatchers.IO) {
            try {
                AppInfoCache.getCached(packageName)?.let {
                    value = it
                    return@withContext
                }
                val appInfo = context.packageManager.getApplicationInfo(packageName, 0)
                val icon = appInfo.loadIcon(context.packageManager)
                val appName = context.packageManager.getApplicationLabel(appInfo).toString()
                val bitmap = icon.toBitmap().asImageBitmap()
                // 更新缓存
                AppInfoCache.updateCache(packageName, appName to bitmap)
                value = appName to bitmap
            } catch (e: PackageManager.NameNotFoundException) {
                value = "noapp" to ImageBitmap(1, 1)
            } catch (e: Exception) {
                // 其他异常处理
            }
        }
    }

    result?.let { onAppInfoLoaded(it.first, it.second) }
}
