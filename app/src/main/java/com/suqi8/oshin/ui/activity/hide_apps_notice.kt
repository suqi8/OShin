package com.suqi8.oshin.ui.activity

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.funlistui.FunPage
import com.suqi8.oshin.ui.activity.module.ModuleViewModel
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import androidx.compose.runtime.getValue

@Composable
fun hide_apps_notice(
    navController: NavController,
    viewModel: ModuleViewModel = viewModel() // 获取共享的ViewModel实例
) {
    // 从ViewModel订阅UI状态
    val uiState by viewModel.uiState.collectAsState()

    // 从状态中获取未安装的应用列表
    val uninstalledApps = uiState.uninstalledApps

    FunPage(
        title = stringResource(R.string.help),
        navController = navController
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 6.dp, top = 15.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // ⭐ 使用从ViewModel中获取的数据
                Text(stringResource(R.string.hide_apps_notice, uninstalledApps.size))

                // ⭐ 使用从ViewModel中获取的数据
                if (uninstalledApps.isNotEmpty()) {
                    Text(uninstalledApps.joinToString(separator = ", "))
                }
            }
        }
    }
}
