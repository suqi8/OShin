package com.suqi8.oshin.hook.systemui.StatusBar

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.graphics.toColorInt
import androidx.core.view.isNotEmpty
import com.github.kyuubiran.ezxhelper.params
import com.google.gson.Gson
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import com.suqi8.oshin.ui.activity.func.StatusBarLayout.ViewConfig
import com.suqi8.oshin.ui.activity.func.StatusBarLayout.ViewNode
import java.util.concurrent.ConcurrentHashMap

/**
 * Xposed Hook 类，用于注入到 SystemUI 进程。
 * 负责解析和修改状态栏视图、与主 App 进行数据通信。
 */
class StatusBarLayout : YukiBaseHooker() {

    private val gson = Gson()
    private val statusBarViews = ConcurrentHashMap<Int, ViewGroup>()
    private val highlightOverlays = ConcurrentHashMap<ViewGroup, View>()
    private val mainHandler by lazy { Handler(Looper.getMainLooper()) }

    companion object {
        const val KEY_REQUEST_TREE = "request_view_tree"
        const val KEY_RECEIVE_TREE = "receive_view_tree"
        const val KEY_UPDATE_CONFIG = "update_view_config"
        const val KEY_HIGHLIGHT_ANCHOR = "highlight_anchor_view"

        const val PREFS_NAME = "systemui\\StatusBarLayout"
        const val PREFS_KEY = "statusbar_layout_configs"
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
                YLog.info("成功通过 Fragment Hook 找到 status_bar 视图: $statusBarView")
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

                    wait<Int>(KEY_HIGHLIGHT_ANCHOR) { hashCode ->
                        YLog.info("🔔 [Hook] 收到高亮指令, HashCode: $hashCode")
                        highlightView(hashCode) // 调用新的高亮方法
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
                            ViewConfig.Companion.MODE_ALWAYS_VISIBLE -> View.VISIBLE
                            ViewConfig.Companion.MODE_ALWAYS_HIDDEN -> View.GONE // 不可见且不占位
                            ViewConfig.Companion.MODE_ALWAYS_INVISIBLE -> View.INVISIBLE // 不可见但占位
                            //ViewConfig.Companion.MODE_NORMAL -> View.VISIBLE // 默认行为设为 VISIBLE
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
     * 一个递归函数，用于根据 hashCode 查找视图
     */
    private fun findView(root: ViewGroup, hashCode: Int): View? {
        if (root.hashCode() == hashCode) return root
        for (i in 0 until root.childCount) {
            val child = root.getChildAt(i)
            if (child.hashCode() == hashCode) {
                return child
            }
            if (child is ViewGroup) {
                val found = findView(child, hashCode)
                if (found != null) return found
            }
        }
        return null
    }

    /**
     * 在指定的状态栏视图上高亮某个子视图。
     * @param hashCode 要高亮的视图资源 hashCode。如果为空，则取消所有高亮。
     */
    private fun highlightView(hashCode: Int) {
        mainHandler.post {
            statusBarViews.values.forEach { statusBarView ->
                val overlay = highlightOverlays.getOrPut(statusBarView) {
                    FrameLayout(statusBarView.context).apply {
                        setBackgroundColor("#55FF0000".toColorInt())
                        visibility = View.GONE
                        isClickable = false
                        isFocusable = false
                        statusBarView.addView(this, FrameLayout.LayoutParams(0, 0))
                    }
                }
                if (hashCode == 0) { // 使用 0 作为取消高亮的信号
                    overlay.visibility = View.GONE
                    return@forEach
                }
                try {
                    val targetView = findView(statusBarView, hashCode)
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
        val configs = runCatching {
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
        val bounds = Rect().apply { view.getHitRect(this) }
        return ViewNode(
            id = nodeId,
            type = nodeType,
            children = children,
            visibility = visibilityString,
            bounds = bounds,
            hashCodeValue = view.hashCode()
        )
    }
}
