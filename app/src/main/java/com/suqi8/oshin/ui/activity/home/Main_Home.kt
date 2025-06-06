package com.suqi8.oshin.ui.activity.home

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.suqi8.oshin.R
import com.suqi8.oshin.lspVersion
import com.suqi8.oshin.ui.activity.funlistui.addline
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical

// =================================================================================
// 主屏幕 Composable (Main Screen Composable)
// =================================================================================

@SuppressLint("AutoboxingStateCreation")
@Composable
fun Main_Home(
    padding: PaddingValues,
    topAppBarScrollBehavior: ScrollBehavior,
    navController: NavController,
    viewModel: FeaturesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 根据ViewModel中的isLoading状态显示加载中或内容
    AnimatedVisibility(!uiState.isLoading) {
        // ⭐ 整个屏幕只有一个懒加载容器
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .overScrollVertical()
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(
                start = 20.dp,  // 左边距
                end = 20.dp,    // 右边距
                top = padding.calculateTopPadding(), // 保留来自Scaffold的顶部安全边距
                bottom = padding.calculateBottomPadding() + 16.dp // 保留底部安全边距并额外增加一些，让内容不贴底
            ),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalItemSpacing = 10.dp
        ) {
            // 第一个项目：状态卡片，占据一整行
            item(span = StaggeredGridItemSpan.FullLine) {
                StatusAndRootCard(statusInfo = uiState.statusInfo)
            }

            // 第二个项目：最近更新，占据一整行
            item(span = StaggeredGridItemSpan.FullLine) {
                RecentUpdateCard(navController = navController)
            }

            // 第三个项目：设备信息，占据一整行
            item(span = StaggeredGridItemSpan.FullLine) {
                DeviceInfoCard(deviceInfo = uiState.deviceInfo)
            }

            // 第四个部分：功能列表，正常的网格布局
            items(
                items = uiState.features,
                key = { it.id }
            ) { feature ->
                FeatureCard(feature = feature, navController = navController)
            }
        }
    }
}

// =================================================================================
// 子组件 (Child Composables)
// =================================================================================

@Composable
private fun StatusAndRootCard(statusInfo: StatusInfo) {
    AnimatedVisibility(visible = true, enter = slideInVertically(initialOffsetY = { -it }, animationSpec = tween(500)) + fadeIn(tween(500))) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min).padding(top = 10.dp)) {
            // 模块状态卡片
            Card(modifier = Modifier.weight(1f).padding(end = 5.dp), color = if (statusInfo.isModuleActive) Color(0xffe6fff5) else Color(0xffffd4d6)) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column {
                        Text(text = if (statusInfo.isModuleActive) stringResource(R.string.module_is_activated) else stringResource(
                            R.string.module_not_activated),
                            fontSize = 20.sp, color = Color.Black, modifier = Modifier.padding(start = 15.dp, top = 15.dp), fontWeight = FontWeight.Bold)
                        Text(text = lspVersion.value,
                            color = Color.Black.copy(alpha = 0.75f), fontSize = 14.sp, modifier = Modifier.padding(start = 15.dp, top = 5.dp))
                    }
                    val compositionResult = rememberLottieComposition(LottieCompositionSpec.RawRes(if (statusInfo.isModuleActive) R.raw.accept else R.raw.error))
                    val progress by animateLottieCompositionAsState(composition = compositionResult.value)
                    LottieAnimation(
                        composition = compositionResult.value, progress = { progress },
                        modifier = Modifier.align(Alignment.BottomEnd).size(110.dp).offset(x = 35.dp, y = 35.dp)
                    )
                }
            }
            // Root 和 Frida 状态卡片
            Column(modifier = Modifier.weight(1f).padding(start = 5.dp)) {
                Card(modifier = Modifier.weight(1f).fillMaxSize(), color = if (statusInfo.rootState == RootState.GRANTED) Color(0xffcffffb) else Color(0xffffd4d6)) {
                    val rootStatusText = when (statusInfo.rootState) {
                        RootState.DETECTING -> stringResource(R.string.detecting_root)
                        RootState.GRANTED -> stringResource(R.string.root_granted)
                        RootState.DENIED -> stringResource(R.string.root_access_denied)
                    }
                    Text(text = rootStatusText, fontSize = 16.sp, color = Color.Black, modifier = Modifier.padding(start = 15.dp, top = 15.dp), fontWeight = FontWeight.Bold)
                    Text(text = if (statusInfo.rootState == RootState.GRANTED) statusInfo.rootVersion else stringResource(
                        R.string.root_permission_error),
                        color = Color.Black.copy(alpha = 0.75f), fontSize = 14.sp, modifier = Modifier.padding(start = 15.dp, top = 5.dp, bottom = 15.dp))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Card(modifier = Modifier.weight(1f).fillMaxSize(), color = Color(0xffffdbd8)) {
                    Text(text = "Frida Server", fontSize = 16.sp, color = Color.Black, modifier = Modifier.padding(start = 15.dp, top = 15.dp), fontWeight = FontWeight.Bold)
                    Text(text = "Connect Error", color = Color.Black.copy(alpha = 0.75f), fontSize = 14.sp, modifier = Modifier.padding(start = 15.dp, top = 5.dp, bottom = 15.dp))
                }
            }
        }
    }
}

@Composable
private fun RecentUpdateCard(navController: NavController) {
    AnimatedVisibility(visible = true, enter = fadeIn(tween(500))) {
        Card(modifier = Modifier.fillMaxWidth()) {
            SuperArrow(
                title = stringResource(R.string.recent_update),
                leftAction = { Image(painter = painterResource(id = R.drawable.recent_update), contentDescription = null, modifier = Modifier.size(20.dp), colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurface)) },
                onClick = { navController.navigate("recent_update") }
            )
        }
    }
}

@Composable
private fun DeviceInfoCard(deviceInfo: DeviceInfo) {
    AnimatedVisibility(visible = true, enter = fadeIn(tween(500))) {
        Card(modifier = Modifier.padding(bottom = 10.dp).fillMaxWidth()) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                InfoRow(title = stringResource(id = R.string.countries_and_regions), value = deviceInfo.country)
                InfoRow(title = stringResource(id = R.string.android_version) + " / " + stringResource(id = R.string.android_api_version), value = deviceInfo.androidVersion)
                InfoRow(title = stringResource(id = R.string.battery_status), value = "${deviceInfo.batteryHealth} / ${deviceInfo.batteryHealthRaw}")
                InfoRow(title = stringResource(id = R.string.system_version), value = deviceInfo.systemVersion)
                InfoRow(title = stringResource(id = R.string.battery_equivalent_capacity), value = deviceInfo.equivalentCapacity)
                InfoRow(title = stringResource(id = R.string.battery_current_capacity), value = deviceInfo.currentCapacity)
                InfoRow(title = stringResource(id = R.string.battery_full_capacity), value = deviceInfo.fullCapacity)
                InfoRow(title = stringResource(id = R.string.battery_health), value = deviceInfo.batterySOH)
                InfoRow(title = stringResource(id = R.string.battery_cycle_count), value = deviceInfo.cycleCount + "次", showLine = false)
            }
        }
    }
}

@Composable
private fun InfoRow(title: String, value: String, showLine: Boolean = true) {
    Text(text = title, modifier = Modifier.padding(top = 5.dp))
    SmallTitle(text = value, insideMargin = PaddingValues(0.dp), modifier = Modifier.padding(bottom = 5.dp))
    if (showLine) {
        addline(false)
    }
}

@Composable
private fun FeatureCard(feature: FeatureUI, navController: NavController) {
    Card(modifier = Modifier.fillMaxWidth().clickable { navController.navigate(feature.category) }) {
        Column {
            Text(feature.title, modifier = Modifier.padding(start = 15.dp, end = 10.dp, top = 10.dp), fontSize = 17.sp)
            Text((feature.summary ?: "") + feature.route,
                modifier = Modifier.padding(top = 10.dp, start = 15.dp, end = 10.dp, bottom = 10.dp),
                fontSize = 14.sp, color = MiuixTheme.colorScheme.onSurfaceContainerHigh)
        }
    }
}
