package com.suqi8.oshin.ui.activity.components.appselection

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hjq.permissions.permission.PermissionLists
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.components.Card
import com.suqi8.oshin.ui.activity.components.CardDefaults
import com.suqi8.oshin.ui.activity.components.FunArrow
import com.suqi8.oshin.utils.drawColoredShadow
import com.suqi8.oshin.utils.requestPermissions
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.extra.CheckboxLocation
import top.yukonga.miuix.kmp.extra.SuperCheckbox
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical


@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun FunAppSele(
    title: String,
    summary: String?,
    selectedApps: Set<String>,
    onSelectionChanged: (Set<String>) -> Unit,
    viewModel: AppSelectionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }

    val loadAppsAndShow = {
        // 2. 委托 ViewModel 加载
        viewModel.loadAllApps()
        showDialog.value = true
    }

    val summaryText = summary ?: "${stringResource(R.string.selected_app)}: ${selectedApps.size}"

    FunArrow(
        title = title,
        summary = summaryText,
        onClick = {
            requestPermissions(
                context,
                PermissionLists.getGetInstalledAppsPermission(),
                onGranted = {
                    loadAppsAndShow()
                }
            )
        }
    )

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val maxHeightDp = with(density) { (configuration.screenHeightDp.dp.toPx() * 0.85f).toDp() }

    // 3. Dialog 现在依赖 ViewModel 的状态
    if (showDialog.value) {
        SuperDialog(
            show = showDialog,
            onDismissRequest = { showDialog.value = false }
        ) {
            // 将 ViewModel 传递给 Dialog 内容
            AppSelectionDialogContent(
                viewModel = viewModel,
                selectedApps = selectedApps,
                onSelectionChanged = onSelectionChanged,
                maxHeightDp = maxHeightDp
            )
        }
    }
}

@Composable
private fun AppSelectionDialogContent(
    viewModel: AppSelectionViewModel,
    selectedApps: Set<String>,
    onSelectionChanged: (Set<String>) -> Unit,
    maxHeightDp: Dp
) {
    // 4. 从 ViewModel 收集状态
    val allApps by viewModel.allApps.collectAsState()
    val searchText by viewModel.searchText

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = maxHeightDp)
            .animateContentSize()
    ) {
        Column {
            // 5. 搜索框现在由 ViewModel 控制
            TextField(
                value = searchText,
                onValueChange = { viewModel.searchText.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            // 6. 过滤和排序逻辑 (这可以在 Composable 中，因为它现在是纯计算)
            val filteredAndSortedList by remember(searchText, allApps, selectedApps) {
                derivedStateOf {
                    allApps
                        .filter { (appName, pkgName) ->
                            appName.contains(searchText, ignoreCase = true) ||
                                    pkgName.contains(searchText, ignoreCase = true)
                        }
                        .sortedByDescending { (_, pkgName) ->
                            selectedApps.contains(pkgName)
                        }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .animateContentSize()
                    .overScrollVertical()
            ) {
                items(filteredAndSortedList, key = { it.second }) { (appName, pkgName) ->
                    val isChecked = selectedApps.contains(pkgName)

                    // 7. 将 ViewModel 传递给列表项
                    ResetAppList(
                        viewModel = viewModel,
                        packageName = pkgName,
                        appName = appName,
                        isChecked = isChecked,
                        onCheckedChange = { isSelected ->
                            val newSet = if (isSelected) {
                                selectedApps + pkgName
                            } else {
                                selectedApps - pkgName
                            }
                            onSelectionChanged(newSet)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ResetAppList(
    viewModel: AppSelectionViewModel, // 接收 ViewModel
    packageName: String,
    appName: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val defaultColor = MiuixTheme.colorScheme.primary

    val appUiInfo by viewModel.getAppUiInfo(packageName, defaultColor).collectAsState(null)

    val info = appUiInfo // 可能是 null (加载中) 或 AppUiInfo

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            colors = CardDefaults.defaultColors(
                color = info?.dominantColor
                    ?: MiuixTheme.colorScheme.onBackground.copy(alpha = 0.1f)
            ),
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                .drawColoredShadow(
                    color = info?.dominantColor ?: Color.Transparent,
                    alpha = 1f,
                    borderRadius = 13.dp,
                    shadowRadius = 7.dp,
                )
        ) {
            if (info != null) {
                Image(
                    bitmap = info.icon,
                    contentDescription = appName,
                    modifier = Modifier.size(45.dp)
                )
            } else {
                // 加载占位符
                Box(modifier = Modifier.size(45.dp))
            }
        }

        // 复选框
        SuperCheckbox(
            title = appName,
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            summary = packageName,
            checkboxLocation = CheckboxLocation.Right
        )
    }
}
