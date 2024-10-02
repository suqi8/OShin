package io.github.suqi8.opatch

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlendModeColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.highcapable.yukihookapi.YukiHookAPI
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.InputField
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SearchBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.Search
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun Main_Function(
    topAppBarScrollBehavior: ScrollBehavior,
    navController: NavController,
    padding: PaddingValues
) {
    val features = listOf(
        Feature(stringResource(R.string.downgr), stringResource(R.string.downgr_summary), "Fun_android_package_manager_services"),
        Feature(stringResource(R.string.authcreak), stringResource(R.string.authcreak_summary), "Fun_android_package_manager_services"),
        Feature(stringResource(R.string.digestCreak), stringResource(R.string.digestCreak_summary), "Fun_android_package_manager_services"),
        Feature(stringResource(R.string.UsePreSig), stringResource(R.string.UsePreSig_summary), "Fun_android_package_manager_services"),
        Feature(stringResource(R.string.enhancedMode), stringResource(R.string.enhancedMode_summary), "Fun_android_package_manager_services"),
        Feature(stringResource(R.string.bypassBlock), stringResource(R.string.bypassBlock_summary), "Fun_android_package_manager_services"),
        Feature(stringResource(R.string.disable_verification_agent_title), stringResource(R.string.disable_verification_agent_summary), "Fun_android_package_manager_services")
    )
    var miuixSearchValue by remember { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }

    // 过滤符合搜索条件的功能
    val filteredFeatures = features.filter {
        it.nickname.contains(miuixSearchValue, ignoreCase = true) ||
                it.description.contains(miuixSearchValue, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .padding(top = padding.calculateTopPadding())
            .fillMaxSize()
    ) {
        SearchBar(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            inputField = {
                InputField(
                    query = miuixSearchValue,
                    onQueryChange = { miuixSearchValue = it },
                    onSearch = { expanded = false },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    label = stringResource(R.string.Search),
                    leadingIcon = {
                        Image(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            imageVector = MiuixIcons.Search,
                            colorFilter = BlendModeColorFilter(
                                MiuixTheme.colorScheme.onSurfaceContainer,
                                BlendMode.SrcIn
                            ),
                            contentDescription = stringResource(R.string.Search)
                        )
                    },
                )
            },
            outsideRightAction = {
                Text(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .clickable {
                            expanded = false
                            miuixSearchValue = ""
                        },
                    text = stringResource(R.string.cancel),
                    color = MiuixTheme.colorScheme.primary
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 6.dp, top = 6.dp)
            ) {
                LazyColumn {
                    if (filteredFeatures.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp), // 设置高度以帮助居中
                                contentAlignment = Alignment.Center // 设置内容居中
                            ) {
                                Text(text = "空空如也~")
                            }
                        }
                    }
                    filteredFeatures.forEach { feature ->
                        item {
                            AnimatedVisibility(
                                visible = true, // 控制动画显示或隐藏的条件
                                enter = fadeIn(animationSpec = tween(durationMillis = 300)),
                                exit = fadeOut(animationSpec = tween(durationMillis = 300))
                            ) {
                                BasicComponent(
                                    title = feature.nickname,
                                    summary = feature.description,
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        miuixSearchValue = feature.nickname
                                        expanded = false
                                        navController.navigate(feature.page)
                                    }
                                )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.size(65.dp))
        }
        // 根据展开状态添加搜索结果
        if (expanded) {
            // 如果 expanded 为 true，则显示搜索结果

        } else {
            // 如果 expanded 为 false，则显示 Card
            Column(Modifier.fillMaxSize()) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp)
                ) {
                    Column {
                        FunctionApp("android", "Fun_android", navController)
                        FunctionApp("com.android.systemui", "Fun_com_android_systemui", navController)
                    }
                }
                Spacer(Modifier.size(65.dp))
            }
        }
    }
}

@Composable
fun FunctionApp(packageName: String, activityName: String, navController: NavController) {
    GetAppIconAndName(packageName = packageName) { appName, icon ->
        if (appName != "noapp") {
            Row(modifier = Modifier
                .clickable {
                    navController.navigate(activityName)
                }
                .fillMaxWidth(),verticalAlignment =  Alignment.CenterVertically) {
                Card(color = if (YukiHookAPI.Status.isModuleActive) MiuixTheme.colorScheme.primary else MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                        .drawColoredShadow(
                            if (YukiHookAPI.Status.isModuleActive) MiuixTheme.colorScheme.primary else MaterialTheme.colorScheme.errorContainer,
                            1f,
                            borderRadius = 13.dp,
                            shadowRadius = 7.dp,
                            offsetX = 0.dp,
                            offsetY = 0.dp,
                            roundedRect = false
                        )) {
                    Image(bitmap = icon, contentDescription = "App Icon", modifier = Modifier
                        .size(48.dp))
                }
                Column(verticalArrangement =  Arrangement.Center, modifier = Modifier.padding(start = 16.dp)) {
                    Text(text = appName)
                    Text(text = packageName)
                }
            }
        } else {
            Text(text = "$packageName 没有安装", modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 4.dp))
        }
    }
}

data class Feature(
    val nickname: String,
    val description: String,
    val page: String
)
