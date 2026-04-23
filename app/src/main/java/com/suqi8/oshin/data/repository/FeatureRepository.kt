package com.suqi8.oshin.data.repository

import android.content.Context
import android.content.pm.PackageManager
import com.suqi8.oshin.features.FeatureRegistry
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PlainText
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Title
import com.suqi8.oshin.models.TitledScreenItem
import com.suqi8.oshin.ui.mainscreen.module.SearchableItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

// 1. 定义仓库接口
interface FeatureRepository {
    suspend fun getAllSearchableItems(): List<SearchableItem>
}

@Singleton
class FeatureRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : FeatureRepository {

    // 互斥锁，防止多个协程同时执行耗时的首次构建操作
    private val mutex = Mutex()

    // 缓存结果，初始为 null
    @Volatile
    private var cachedItems: List<SearchableItem>? = null

    override suspend fun getAllSearchableItems(): List<SearchableItem> {
        // 快速路径：已有缓存，直接返回
        cachedItems?.let { return it }

        // 慢速路径：加锁后再次检查，防止多个协程重复构建
        return mutex.withLock {
            cachedItems?.let { return it }

            withContext(Dispatchers.Default) {
                val searchableItemsJobs = FeatureRegistry.screenMap.flatMap { (routeId, pageDef) ->
                    pageDef.items.filterIsInstance<CardDefinition>()
                        .flatMap { it.items }
                        .filterIsInstance<TitledScreenItem>()
                        .map { item ->
                            async {
                                SearchableItem(
                                    title = resolveTitle(item.title),
                                    summary = item.summary?.let { context.getString(it) } ?: "",
                                    route = "feature/$routeId",
                                    key = item.key
                                )
                            }
                        }
                }

                val items = searchableItemsJobs.awaitAll()

                cachedItems = items
                items
            }
        }
    }

    private suspend fun resolveTitle(title: Title): String {
        return when (title) {
            is StringResource -> context.getString(title.id)
            is PlainText -> title.text
            is AppName -> getAppName(title.packageName)
        }
    }

    private suspend fun getAppName(packageName: String): String = withContext(Dispatchers.IO) {
        try {
            val pm = context.packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(appInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            packageName
        }
    }
}
