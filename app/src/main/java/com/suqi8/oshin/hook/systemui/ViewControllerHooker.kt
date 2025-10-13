package com.suqi8.oshin.hook.systemui

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isNotEmpty
import com.github.kyuubiran.ezxhelper.params
import com.google.gson.Gson
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import com.suqi8.oshin.models.ViewConfig
import com.suqi8.oshin.models.ViewNode
import java.util.concurrent.ConcurrentHashMap

/**
 * Xposed Hook 类，用于注入到 SystemUI 进程。
 * 负责解析和修改状态栏视图、与主 App 进行数据通信。
 */
class ViewControllerHooker : YukiBaseHooker() {

    private val gson = Gson()
    private val statusBarViews = ConcurrentHashMap<Int, ViewGroup>()
    private val highlightOverlays = ConcurrentHashMap<ViewGroup, View>()
    private val mainHandler by lazy { Handler(Looper.getMainLooper()) }

    companion object {
        const val KEY_REQUEST_TREE = "request_view_tree"
        const val KEY_RECEIVE_TREE = "receive_view_tree"
        const val KEY_UPDATE_CONFIG = "update_view_config"
        const val KEY_HIGHLIGHT_ANCHOR = "highlight_anchor_view"
        const val KEY_REORDER_VIEWS = "reorder_views"

        const val PREFS_NAME = "oshin_view_controller"
        const val PREFS_KEY = "view_controller_configs"
    }

    override fun onHook() {
        if (packageName != "com.android.systemui") return
        YLog.info("视图控制器Hooker已初始化, 目标包 $packageName")

        "com.android.systemui.statusbar.phone.fragment.CollapsedStatusBarFragment".toClass().resolve().firstMethod {
            name = "onViewCreated"
            params(View::class.java, Bundle::class.java)
        }.hook {
            after {
                val statusBarView = args[0] as? ViewGroup ?: return@after
                YLog.info("!!! [新方案] 成功通过 Fragment Hook 找到 status_bar 视图: $statusBarView")
                statusBarViews[statusBarView.hashCode()] = statusBarView

                // 初始化 DataChannel 监听器
                dataChannel.apply {
                    wait(KEY_REQUEST_TREE) {
                        YLog.info("🔔 [Hook] 收到请求树的指令")
                        val mainStatusBarView = statusBarViews.values.firstOrNull()
                        val jsonTree = if (mainStatusBarView != null) {
                            try {
                                gson.toJson(parseView(mainStatusBarView))
                            } catch (e: Throwable) {
                                YLog.error("解析视图树时出错", e)
                                "{}"
                            }
                        } else {
                            YLog.warn("尚未找到状态条视图，返回空树。")
                            "{}"
                        }
                        put(KEY_RECEIVE_TREE, jsonTree)
                    }

                    wait(KEY_UPDATE_CONFIG) {
                        YLog.info("🔔 [Hook] 收到更新配置的指令")
                        handleUpdateConfig()
                    }

                    wait<String>(KEY_HIGHLIGHT_ANCHOR) { viewId ->
                        YLog.info("🔔 [Hook] 收到高亮指令: $viewId")
                        highlightView(viewId)
                    }

                    // 排序功能相关的监听器虽然保留，但由于 App 端不再发送指令，实际上不会被触发。
                    wait<ArrayList<String>>(KEY_REORDER_VIEWS) { _ ->
                        YLog.warn("收到一个重排序指令，但此功能已被禁用，不执行任何操作。")
                    }
                }
                YLog.info("✅ YukiHookDataChannel 监听器已设置。")
                applyAllViewConfigs()
            }
        }
    }

    /**
     * 将单个视图配置应用到指定的根视图中。
     * 此方法会在主线程中执行。
     * @param rootView 根视图容器，通常是 PhoneStatusBarView。
     * @param config 要应用的视图配置。
     */
    private fun applyConfigToView(rootView: ViewGroup, config: ViewConfig) {
        if (config.id.isBlank()) return
        mainHandler.post {
            try {
                val resId = findResId(rootView.context, config.id)
                if (resId != 0) {
                    val targetView = rootView.findViewById<View>(resId)
                    if (targetView != null) {
                        val newVisibility = when (config.mode) {
                            ViewConfig.MODE_ALWAYS_VISIBLE -> View.VISIBLE
                            ViewConfig.MODE_ALWAYS_HIDDEN -> View.GONE // 不可见且不占位
                            ViewConfig.MODE_ALWAYS_INVISIBLE -> View.INVISIBLE // 不可见但占位
                            ViewConfig.MODE_NORMAL -> View.VISIBLE // 默认行为设为 VISIBLE
                            else -> -1 // -1 表示无效模式，不作处理
                        }
                        if (newVisibility != -1 && targetView.visibility != newVisibility) {
                            targetView.visibility = newVisibility
                            YLog.info("已应用配置到 ${config.id}: 模式=${config.mode}, 可见性设置为=${newVisibility}")
                        }
                    } else { YLog.warn("应用配置失败: 未在 $rootView 中找到视图 ID: ${config.id}") }
                } else { YLog.warn("应用配置失败: 未找到资源 ID: ${config.id}") }
            } catch (e: Exception) { YLog.error("应用配置时发生异常, ID: ${config.id}", e) }
        }
    }

    /**
     * 处理视图重排序。
     * 注意：此功能在 App 端已被禁用，此方法理论上不会被调用。
     * @param orderedIds 从 App 端接收到的、包含新顺序的视图资源 ID 列表。
     */
    private fun handleReorderViews(orderedIds: List<String>) {
        YLog.info("📍 handleReorderViews() 被调用 (功能已停用)")
        mainHandler.post {
            YLog.info("📍 mainHandler 中开始处理重排序... (功能已停用)")
            statusBarViews.values.forEachIndexed { viewIndex, statusBarView ->
                try {
                    val viewIdMap = mutableMapOf<String, View>()
                    orderedIds.forEach { viewId ->
                        val resId = findResId(statusBarView.context, viewId)
                        if (resId != 0) {
                            statusBarView.findViewById<View>(resId)?.let { view ->
                                viewIdMap[viewId] = view
                            }
                        }
                    }
                    val viewsToReorder = viewIdMap.values.toList()
                    if (viewsToReorder.isEmpty()) return@forEachIndexed

                    val parentToViews = viewsToReorder.groupBy { it.parent as? ViewGroup }
                    parentToViews.forEach { (parent, childViews) ->
                        if (parent == null) return@forEach
                        val allChildren = (0 until parent.childCount).map { parent.getChildAt(it) }
                        val reorderedChildren = mutableListOf<View>()
                        val handledViews = mutableSetOf<View>()

                        orderedIds.forEach { id ->
                            val view = viewIdMap[id]
                            if (view != null && view.parent == parent) {
                                reorderedChildren.add(view)
                                handledViews.add(view)
                            }
                        }
                        allChildren.forEach { child ->
                            if (child !in handledViews) {
                                reorderedChildren.add(child)
                            }
                        }
                        val layoutParams = reorderedChildren.associate { it to it.layoutParams }
                        parent.removeAllViews()
                        reorderedChildren.forEach { child ->
                            parent.addView(child, layoutParams[child])
                        }
                        parent.requestLayout()
                        parent.invalidate()
                    }
                } catch (e: Exception) {
                    YLog.error("❌ 重排序视图时发生异常: ${e.message}", e)
                }
            }
        }
    }

    /**
     * 在指定的状态栏视图上高亮某个子视图。
     * @param viewId 要高亮的视图资源 ID。如果为空，则取消所有高亮。
     */
    private fun highlightView(viewId: String) {
        mainHandler.post {
            statusBarViews.values.forEach { statusBarView ->
                val overlay = highlightOverlays.getOrPut(statusBarView) {
                    FrameLayout(statusBarView.context).apply {
                        setBackgroundColor(Color.parseColor("#55FF0000"))
                        visibility = View.GONE
                        isClickable = false
                        isFocusable = false
                        statusBarView.addView(this, FrameLayout.LayoutParams(0, 0))
                    }
                }
                if (viewId.isBlank()) {
                    overlay.visibility = View.GONE
                    return@forEach
                }
                try {
                    val resId = findResId(statusBarView.context, viewId)
                    val targetView = if (resId != 0) statusBarView.findViewById<View>(resId) else null
                    if (targetView != null && targetView.isAttachedToWindow) {
                        val location = IntArray(2).also { targetView.getLocationInWindow(it) }
                        val statusBarLocation = IntArray(2).also { statusBarView.getLocationInWindow(it) }
                        val x = location[0] - statusBarLocation[0]
                        val y = location[1] - statusBarLocation[1]
                        val params = overlay.layoutParams as FrameLayout.LayoutParams
                        params.width = targetView.width
                        params.height = targetView.height
                        params.leftMargin = x
                        params.topMargin = y
                        overlay.layoutParams = params
                        overlay.visibility = View.VISIBLE
                        overlay.bringToFront()
                    } else {
                        overlay.visibility = View.GONE
                    }
                } catch (_: Exception) {
                    overlay.visibility = View.GONE
                }
            }
        }
    }

    /**
     * 根据字符串形式的资源ID（例如 "com.android.systemui:id/wifi_combo"）查找其实际整型ID。
     * @param context 上下文。
     * @param resIdString 字符串资源ID。
     * @return 成功则返回整型ID，失败则返回 0。
     */
    private fun findResId(context: Context, resIdString: String): Int {
        if (resIdString.isBlank()) return 0
        return try {
            val resName = resIdString.substringAfterLast('/')
            val resType = resIdString.substringAfter(':').substringBefore('/')
            val resPackage = resIdString.substringBefore(':')
            context.resources.getIdentifier(resName, resType, resPackage)
        } catch (e: Exception) { 0 }
    }

    /**
     * 处理来自 App 的配置更新请求。
     */
    private fun handleUpdateConfig() {
        YLog.info("正在处理更新配置的请求。")
        applyAllViewConfigs()
    }

    /**
     * 从 SharedPreferences 加载所有配置，并应用到当前已 hook 到的所有状态栏视图上。
     */
    private fun applyAllViewConfigs() {
        val jsonConfigs = prefs(PREFS_NAME).getString(PREFS_KEY, "[]")
        val configs = kotlin.runCatching {
            gson.fromJson(jsonConfigs, Array<ViewConfig>::class.java).toList()
        }.getOrNull() ?: emptyList()

        if (configs.isEmpty()) {
            YLog.debug("配置为空，无需应用。")
            return
        }
        statusBarViews.values.forEach { statusBarView ->
            YLog.info("正在应用配置到 $statusBarView")
            configs.forEach { config ->
                applyConfigToView(statusBarView, config)
            }
        }
    }

    /**
     * 递归解析一个视图及其所有子视图，构建成 ViewNode 树形结构。
     * @param view 要解析的根视图。
     * @return 构建好的 ViewNode 对象。
     */
    private fun parseView(view: View): ViewNode {
        val nodeId = try {
            if (view.id != View.NO_ID) view.resources.getResourceName(view.id) else ""
        } catch (e: Resources.NotFoundException) { "" }
        val nodeType = view.javaClass.name
        val children = if (view is ViewGroup && view.isNotEmpty()) {
            (0 until view.childCount).mapNotNull {
                view.getChildAt(it)?.let { child -> parseView(child) }
            }
        } else { emptyList() }
        val visibilityString = when (view.visibility) {
            View.VISIBLE -> "Visible"
            View.INVISIBLE -> "Invisible"
            View.GONE -> "Gone"
            else -> "Unknown"
        }
        val bounds = android.graphics.Rect().apply { view.getHitRect(this) }
        return ViewNode(id = nodeId, type = nodeType, children = children, visibility = visibilityString, bounds = bounds)
    }
}
