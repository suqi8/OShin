package com.suqi8.oshin.hook.utils


/**
 * [最终方案] DexKit 探索结果的持久化缓存管理器。
 *
 * 实现了与您提供的参考项目 (HyperCeiler, XAutoDaily) 完全相同的缓存策略。
 * 遵循 Xposed 生命周期，在 Application 创建后再进行扫描和缓存，并使用 CountDownLatch 解决时序问题。
 *//*

object DexKitCacheManager {

    // 确保 MMKV 的初始化是线程安全的，并且每个进程只执行一次
    @Volatile
    private var isMmkvInitialized = false
    private val mmkvInitializationLock = Any()

    // 内存缓存，防止在同一次运行中反复创建 DexKitBridge
    private val bridgeCache = ConcurrentHashMap<String, DexKitBridge>()

    // 关键：用于同步的倒数锁存器
    private val initializationLatch = CountDownLatch(1)

    @Volatile
    private var isManagerInitialized = false

    */
/**
     * [在 Hook 入口调用一次] 负责 Hook Application.onCreate 并准备好所有资源。
     * @param param 来自 loadApp/loadSystem 的包参数。
     *//*

    fun init(param: PackageParam) {
        if (isManagerInitialized) return
        isManagerInitialized = true

        param.onAppLifecycle {
            onCreate {
                // "this" 是 Application 实例，它的 Context 是有效的
                val context = this
                YLog.info("Application.onCreate 触发 (宿主: '${param.packageName}'), 正在初始化缓存管理器...")
                try {
                    getMMKV(context)
                    getInMemoryBridge(param)
                    YLog.info("缓存管理器初始化成功。")
                } catch (e: Throwable) {
                    YLog.error("缓存管理器初始化失败。", e)
                } finally {
                    // 无论成功与否，都必须释放锁存器，以防其他线程永久等待
                    initializationLatch.countDown()
                }
            }
        }
    }

    */
/**
     * 获取 MMKV 实例，并在此过程中自动完成初始化。
     * @param context 必须是一个有效的 Context。
     * @return [MMKV] 实例。
     *//*

    private fun getMMKV(context: Context): MMKV {
        if (!isMmkvInitialized) {
            synchronized(mmkvInitializationLock) {
                if (!isMmkvInitialized) {
                    MMKV.initialize(context.applicationContext)
                    isMmkvInitialized = true
                }
            }
        }
        return MMKV.mmkvWithID("oshin_dex_cache", MMKV.MULTI_PROCESS_MODE)
    }

    */
/**
     * [推荐] 查找并 Hook 方法，全自动处理缓存和时序问题。
     *
     * @param param 当前 Hook 的包参数。
     * @param queryKey 此次查询的唯一标识符。
     * @param finder 实际执行 DexKit 探索的 lambda 表达式。
     * @param hooker 对查找到的每个方法进行 Hook 的 lambda 表达式。
     *//*

    fun findAndHookMethod(
        param: PackageParam,
        queryKey: String,
        finder: (DexKitBridge) -> List<MethodData>,
        hooker: (Method) -> Unit
    ) {
        // 在后台线程中执行，以避免阻塞主线程
        Thread {
            try {
                // 等待初始化完成，最多等待10秒
                val success = initializationLatch.await(10, java.util.concurrent.TimeUnit.SECONDS)
                if (!success) {
                    YLog.error("等待缓存管理器初始化超时 (Key: '$queryKey')。")
                    return@Thread
                }

                val context = param.appContext!! // 此时 appContext 必定不为 null
                val overallStartTime = System.currentTimeMillis()
                YLog.info("开始处理 Hook 任务 (Key: '$queryKey')")

                val mmkv = getMMKV(context)
                val packageName = context.packageName
                val packageInfo: PackageInfo? = try {
                    context.packageManager.getPackageInfo(packageName, 0)
                } catch (e: PackageManager.NameNotFoundException) {
                    YLog.error("无法获取包信息 for \"$packageName\"", e)
                    null
                }

                // 因为 minSdk >= 35, 我们可以安全地直接使用 longVersionCode
                val currentVersionCode = packageInfo?.longVersionCode ?: -1L
                val versionKey = "version#$packageName"
                val cacheKey = "cache#$packageName#$queryKey"
                val cachedVersionCode = mmkv.decodeLong(versionKey, -1L)

                var descriptors: Set<String>?

                // 1. 检查版本并尝试从缓存读取
                if (cachedVersionCode == currentVersionCode) {
                    descriptors = mmkv.decodeStringSet(cacheKey, null)
                    if (descriptors != null) {
                        YLog.info("DexKit 缓存命中 (Key: '$queryKey')，从缓存加载 ${descriptors.size} 条数据。")
                    }
                } else {
                    descriptors = null
                    YLog.info("DexKit 缓存版本不匹配 (Key: '$queryKey' | 缓存版本: $cachedVersionCode, 当前版本: $currentVersionCode)，需要重新扫描。")
                }

                // 2. 如果缓存不存在或版本已更新，则执行实时搜索
                if (descriptors == null) {
                    val searchStartTime = System.currentTimeMillis()

                    if (cachedVersionCode != -1L) {
                        val keysToRemove = mmkv.allKeys()?.filter { it.startsWith("cache#$packageName#") }
                        if (!keysToRemove.isNullOrEmpty()) {
                            mmkv.removeValuesForKeys(keysToRemove.toTypedArray())
                        }
                    }

                    val bridge = getInMemoryBridge(param)
                    val newDescriptors = finder(bridge).map { it.toDexMethod().serialize() }.toSet()

                    if (newDescriptors.isNotEmpty()) {
                        mmkv.encode(cacheKey, newDescriptors)
                        mmkv.encode(versionKey, currentVersionCode)
                    }
                    descriptors = newDescriptors
                    YLog.info("DexKit 搜索完成，耗时: ${System.currentTimeMillis() - searchStartTime}ms")
                }

                // 3. 对找到的所有方法执行 Hook
                val appClassLoader = param.appClassLoader ?: run {
                    YLog.error("ClassLoader 为空，无法执行 Hook (Key: '$queryKey')")
                    return@Thread
                }

                descriptors.forEach { descriptor ->
                    try {
                        val method = org.luckypray.dexkit.wrap.DexMethod(descriptor).getMethodInstance(appClassLoader)
                        hooker(method)
                    } catch (e: Throwable) {
                        YLog.error("从描述符 \"$descriptor\" 恢复并 Hook 失败", e)
                    }
                }
                YLog.info("Hook 任务 (Key: '$queryKey') 处理完毕，总耗时: ${System.currentTimeMillis() - overallStartTime}ms")

            } catch (e: InterruptedException) {
                YLog.error("等待缓存初始化时被中断", e)
                Thread.currentThread().interrupt()
            } catch (e: Throwable) {
                YLog.error("处理 Hook 任务时发生未知错误 (Key: '$queryKey')", e)
            }
        }.start()
    }

    */
/** 获取 DexKitBridge 实例（带内存缓存） *//*

    private fun getInMemoryBridge(param: PackageParam): DexKitBridge {
        val sourceDir = param.appInfo?.sourceDir ?: error("无法获取 App 的源路径")
        return bridgeCache.getOrPut(param.packageName) {
            DexKitBridge.create(sourceDir) ?: error("创建 DexKitBridge 失败")
        }
    }

    */
/** 因为 minSdk >= 35, 我们可以安全地直接使用 longVersionCode *//*

    @get:SuppressLint("NewApi")
    private val PackageInfo.longVersionCode: Long
        get() = this.longVersionCode
}
*/
