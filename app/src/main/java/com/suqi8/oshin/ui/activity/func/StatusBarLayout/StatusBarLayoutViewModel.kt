package com.suqi8.oshin.ui.activity.func.StatusBarLayout

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.highcapable.yukihookapi.hook.factory.dataChannel
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.hook.systemui.StatusBar.StatusBarLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private val ViewNode.expandableId: String
    get() = id.ifBlank { "group_${this.hashCode()}" }

/**
 * ViewController 功能的 ViewModel。
 * 负责：
 * 1. 通过 DataChannel 与 Hook 端通信，请求和接收视图树。
 * 2. 管理视图树的UI状态，包括加载状态、展开/折叠状态。
 * 3. 构建用于UI渲染的扁平化列表 (visibleNodes)。
 * 4. 处理和持久化用户对视图的显示配置。
 */
@HiltViewModel
class StatusBarLayoutViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatusBarLayoutUiState())
    val uiState = _uiState.asStateFlow()

    /** 持有转换后的、用于UI展示的扁平化节点列表。 */
    val visibleNodes = mutableStateListOf<VisibleNodeInfo>()

    private val gson = Gson()
    private val dataChannel = context.dataChannel("com.android.systemui")

    companion object {
        const val TAG = "视图控制器VM"
    }

    init {
        loadInitialConfigs()
        setupDataChannelListener()
        requestTree()
    }

    /**
     * 设置 DataChannel 监听器，用于接收来自 Hook 端的数据。
     */
    private fun setupDataChannelListener() {
        dataChannel.wait<String>(StatusBarLayout.KEY_RECEIVE_TREE) { jsonTree ->
            Log.d(TAG, "DataChannel 已收到 JSON 树")
            viewModelScope.launch {
                val tree = runCatching { gson.fromJson(jsonTree, ViewNode::class.java) }.getOrNull()
                if (tree != null) {
                    val allParentNodes = getAllParentNodeIds(tree)
                    _uiState.update { it.copy(isLoading = false, viewTree = tree, expandedNodes = allParentNodes) }
                    rebuildVisibleTree()
                } else {
                    _uiState.update { it.copy(isLoading = false, viewTree = null) }
                }
            }
        }
        Log.d(TAG, "App 端 DataChannel 监听器已设置。")
    }

    /**
     * 向 Hook 端发送指令，请求获取最新的视图树。
     */
    fun requestTree() {
        Log.d(TAG, "正在通过 DataChannel 请求视图树...")
        _uiState.update { it.copy(isLoading = true) }
        dataChannel.put(StatusBarLayout.KEY_REQUEST_TREE)
        viewModelScope.launch {
            delay(1000L)

            // 5秒后，如果仍然处于加载状态，说明超时了
            if (_uiState.value.isLoading) {
                Log.w(TAG, "请求视图树超时！请检查 Hook 是否已激活。")
                _uiState.update { it.copy(isLoading = false, viewTree = null) }
            }
        }
    }

    /**
     * 切换一个节点的展开/折叠状态。
     * @param nodeId 节点的唯一标识符 (expandableId)。
     */
    fun toggleNode(nodeId: String) {
        _uiState.update {
            val newExpanded = it.expandedNodes.toMutableSet()
            if (newExpanded.contains(nodeId)) newExpanded.remove(nodeId) else newExpanded.add(nodeId)
            it.copy(expandedNodes = newExpanded)
        }
        rebuildVisibleTree() // 状态更新后需要重建可见列表
    }

    /**
     * 更新指定视图的显示配置。
     * @param viewId 视图的资源 ID。
     * @param mode 新的显示模式。
     */
    fun updateConfig(viewId: String, mode: Int) {
        _uiState.update {
            val newConfigs = it.configs.toMutableMap()
            newConfigs[viewId] = mode
            it.copy(configs = newConfigs)
        }
        saveConfigs()
    }

    /**
     * 将当前的所有配置保存到 SharedPreferences，并通知 Hook 端更新。
     */
    private fun saveConfigs() {
        viewModelScope.launch(Dispatchers.IO) {
            val configList = _uiState.value.configs.map { (id, mode) -> ViewConfig(id, mode) }
            val json = gson.toJson(configList)
            val editor = context.prefs(StatusBarLayout.PREFS_NAME).edit()
            editor.putString(StatusBarLayout.PREFS_KEY, json)
            editor.commit()
            Log.d(TAG, "配置已同步保存, 正在发送更新广播...")
            dataChannel.put(StatusBarLayout.KEY_UPDATE_CONFIG)
        }
    }

    /**
     * 向 Hook 端发送指令，高亮指定的视图。
     * @param viewId 要高亮的视图资源 ID，如果为空字符串则取消高亮。
     */
    fun highlightView(hashCode: Int) {
        dataChannel.put(StatusBarLayout.KEY_HIGHLIGHT_ANCHOR, hashCode)
        Log.d(TAG, "发送高亮指令, HashCode: $hashCode")
    }

    /**
     * 取消高亮
     */
    fun clearHighlight() {
        // 发送一个无效的 hashCode (例如 0) 来取消高亮
        dataChannel.put(StatusBarLayout.KEY_HIGHLIGHT_ANCHOR, 0)
        Log.d(TAG, "发送取消高亮指令")
    }

    /**
     * 当UI状态（如展开节点）或原始树更新时，重建 [visibleNodes] 列表。
     */
    private fun rebuildVisibleTree() {
        _uiState.value.viewTree?.let { root ->
            val allVisible = buildVisibleTree(root, _uiState.value.expandedNodes)
            visibleNodes.clear()
            visibleNodes.addAll(allVisible)
        }
    }

    /**
     * 递归遍历原始视图树，根据展开状态构建一个扁平化的、供UI使用的列表。
     * @param root 视图树的根节点。
     * @param expandedNodes 当前所有已展开节点的 ID 集合。
     * @return 一个包含层级信息的 [VisibleNodeInfo] 列表。
     */
    private fun buildVisibleTree(root: ViewNode, expandedNodes: Set<String>): List<VisibleNodeInfo> {
        val visibleList = mutableListOf<VisibleNodeInfo>()

        // 递归辅助函数
        fun traverse(node: ViewNode, level: Int, parentPath: String, index: Int) {
            val currentPath = "$parentPath/$index"
            val uniqueKey = "path_key_$currentPath"
            if (node.id.isBlank() && node.type.isBlank() && node.children.isEmpty()) return

            visibleList.add(VisibleNodeInfo(node, level, uniqueKey))

            // 如果当前节点是展开状态且有子节点，则继续递归其子节点
            if (expandedNodes.contains(node.expandableId) && node.children.isNotEmpty()) {
                node.children.forEachIndexed { childIndex, child ->
                    traverse(child, level + 1, currentPath, childIndex)
                }
            }
        }

        // 从根节点的直接子节点开始遍历，它们的层级为 0
        root.children.forEachIndexed { index, childNode ->
            traverse(childNode, 0, "root", index)
        }
        return visibleList
    }

    /**
     * 遍历整个树，获取所有包含子节点的父节点的 ID。
     * 用于默认将所有父节点设置为展开状态。
     */
    private fun getAllParentNodeIds(node: ViewNode): Set<String> {
        val nodeIds = mutableSetOf<String>()
        fun traverse(currentNode: ViewNode) {
            if (currentNode.children.isNotEmpty()) {
                nodeIds.add(currentNode.expandableId)
                currentNode.children.forEach { traverse(it) }
            }
        }
        traverse(node)
        return nodeIds
    }

    /**
     * 从 SharedPreferences 加载初始的视图配置。
     */
    private fun loadInitialConfigs() {
        val jsonConfigs = context.prefs(StatusBarLayout.PREFS_NAME).getString(StatusBarLayout.PREFS_KEY, "[]")
        val configs = runCatching {
            gson.fromJson(jsonConfigs, Array<ViewConfig>::class.java)
        }.getOrNull() ?: emptyArray()
        _uiState.update { it.copy(configs = configs.associate { cfg -> cfg.id to cfg.mode }) }
    }
}
