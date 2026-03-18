package com.suqi8.oshin.ui.activity.components.apprestart

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.colorControls
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.highlight.Highlight
import com.kyant.capsule.ContinuousCapsule
import com.kyant.capsule.ContinuousRoundedRectangle
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.components.Card
import com.suqi8.oshin.ui.activity.components.CardDefaults
import com.suqi8.oshin.ui.mainscreen.module.AppUiInfo
import com.suqi8.oshin.utils.drawColoredShadow
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun AppRestartScreen(
    appList: List<String>,
    showresetAppDialog: MutableState<Boolean>,
    backdrop: Backdrop,
    viewModel: AppRestartViewModel = hiltViewModel()
) {
    val defaultColor = MiuixTheme.colorScheme.primary

    // 2. 在 LaunchedEffect 中触发 ViewModel 加载数据
    LaunchedEffect(appList, defaultColor) {
        viewModel.loadAppsInfo(appList, defaultColor)
    }

    ConfirmationDialog(
        appPackageList = appList, // 传递原始列表
        appInfoMap = viewModel.appInfoCache, // 传递从 VM 拿到的缓存
        defaultColor = defaultColor,
        onConfirm = {
            // 3. 将确认事件委托给 ViewModel
            viewModel.restartApps(appList)
            showresetAppDialog.value = false
        },
        show = showresetAppDialog,
        onDismiss = {
            showresetAppDialog.value = false
        },
        backdrop = backdrop
    )
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun ConfirmationDialog(
    appPackageList: List<String>,
    appInfoMap: Map<String, AppUiInfo?>,
    defaultColor: Color,
    show: MutableState<Boolean>,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    backdrop: Backdrop
) {
    val isLightTheme = !isSystemInDarkTheme()
    val contentColor = if (isLightTheme) Color.Black else Color.White
    val accentColor =
        if (isLightTheme) Color(0xFF0088FF)
        else Color(0xFF0091FF)
    val containerColor =
        if (isLightTheme) Color(0xFFFAFAFA).copy(0.6f)
        else Color(0xFF121212).copy(0.4f)
    val dimColor =
        if (isLightTheme) Color(0xFF29293A).copy(0.23f)
        else Color(0xFF121212).copy(0.56f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(dimColor) // 背景变暗（不会影响弹窗）
            .clickable(
                onClick = { onDismiss() },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier
                .padding(40f.dp)
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { ContinuousRoundedRectangle(48f.dp) },
                    highlight = { Highlight.Plain },
                    effects = {
                        colorControls(
                            brightness = if (isLightTheme) 0.2f else 0f,
                            saturation = 1.5f
                        )
                        blur(if (isLightTheme) 16f.dp.toPx() else 8f.dp.toPx())
                        lens(24f.dp.toPx(), 48f.dp.toPx(), true)
                    },
                    onDrawSurface = { drawRect(containerColor) }
                )
                .fillMaxWidth()
        ) {
            BasicText(
                stringResource(R.string.Researt_app),
                Modifier.padding(28f.dp, 24f.dp, 28f.dp, 12f.dp),
                style = TextStyle(contentColor, 24f.sp, FontWeight.Medium)
            )

            BasicText(
                stringResource(R.string.confirm_restart_applications),
                Modifier
                    .then(
                        if (isLightTheme) {
                            // plus darker
                            Modifier
                        } else {
                            // plus lighter
                            Modifier.graphicsLayer(blendMode = BlendMode.Plus)
                        }
                    )
                    .padding(24f.dp, 12f.dp, 24f.dp, 12f.dp),
                style = TextStyle(contentColor.copy(0.68f), 15f.sp),
                maxLines = 5
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // 4. LazyColumn 现在从 appInfoMap 中读取数据
                items(appPackageList, key = { it }) { pkg ->
                    val appInfo = appInfoMap[pkg] // 从 VM 的 Map 中获取状态
                    ResetAppList(
                        appInfo = appInfo,
                        packageName = pkg,
                        defaultColor = defaultColor
                    )
                    if (appPackageList.indexOf(pkg) < appPackageList.lastIndex) {
                        // 假设 addline() 是一个 @Composable 函数
                        // addline()
                    }
                }
            }

            Row(
                Modifier
                    .padding(24f.dp, 12f.dp, 24f.dp, 24f.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16f.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    Modifier
                        .clip(ContinuousCapsule)
                        .background(containerColor.copy(0.2f))
                        .clickable { onDismiss() }
                        .height(48f.dp)
                        .weight(1f)
                        .padding(horizontal = 16f.dp),
                    horizontalArrangement = Arrangement.spacedBy(4f.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicText(
                        stringResource(R.string.cancel),
                        style = TextStyle(contentColor, 16f.sp)
                    )
                }
                Row(
                    Modifier
                        .clip(ContinuousCapsule)
                        .background(accentColor)
                        .clickable { onConfirm() }
                        .height(48f.dp)
                        .weight(1f)
                        .padding(horizontal = 16f.dp),
                    horizontalArrangement = Arrangement.spacedBy(4f.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicText(
                        stringResource(R.string.ok),
                        style = TextStyle(Color.White, 16f.sp)
                    )
                }
            }
        }
    }

    // --- 保留 SuperDialog 注释 ---
    /*SuperDialog(
        title = stringResource(R.string.Researt_app),
        show = show,
        onDismissRequest = {
            onDismiss()
        },
        enableWindowDim = false,
        summary = stringResource(R.string.confirm_restart_applications),
        backgroundColor = Color.Transparent,
        modifier = Modifier.liquidGlass(
            liquidGlassProviderState,
            dialogGlassStyle // 应用提取的样式
        )
    ) {
        LazyColumn {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .liquidGlass(
                            liquidGlassProviderState,
                            cardAndCancelButtonStyle // 应用提取的样式
                        ),
                    colors = CardDefaults.defaultColors(Color.Transparent)
                ) {
                    appPackage.forEachIndexed { index, it ->
                        ResetAppList(it)
                        if (index < appPackage.size - 1) {
                            addline()
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                modifier = Modifier
                    .weight(1f)
                    .liquidGlass(
                        liquidGlassProviderState,
                        cardAndCancelButtonStyle // 应用提取的样式
                    ),
                colors = ButtonDefaults.textButtonColors(color = Color.Transparent),
                text = stringResource(R.string.cancel),
                onClick = {
                    onDismiss()
                }
            )
            Spacer(Modifier.width(12.dp))
            TextButton(
                modifier = Modifier
                    .weight(1f)
                    .liquidGlass(
                        liquidGlassProviderState,
                        confirmButtonStyle // 应用提取的样式
                    ),
                text = stringResource(R.string.ok),
                colors = ButtonDefaults.textButtonColors(color = Color.Transparent),
                onClick = {
                    onConfirm()
                }
            )
        }
    }*/
    // --- 注释结束 ---
}

@Composable
fun ResetAppList(
    appInfo: AppUiInfo?, // 接收来自 ViewModel 的状态
    packageName: String,  // 仍然需要包名作为兜底
    defaultColor: Color   // 接收默认颜色用于比较
) {
    if (appInfo != null)  {
        val isLoadingColor = appInfo.dominantColor == defaultColor // 检查是否仍在等待主色

        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                colors = CardDefaults.defaultColors(appInfo.dominantColor),
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 16.dp)
                    .drawColoredShadow(
                        color = appInfo.dominantColor,
                        alpha = if (isLoadingColor) 0f else 1f, // 颜色加载完成前不显示阴影
                        borderRadius = 13.dp,
                        shadowRadius = 7.dp,
                        roundedRect = false
                    )
            ) {
                Image(
                    bitmap = appInfo.icon,
                    contentDescription = "App Icon",
                    modifier = Modifier.size(45.dp)
                )
            }
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(text = appInfo.name)
                Text(
                    text = packageName,
                    fontSize = MiuixTheme.textStyles.subtitle.fontSize,
                    fontWeight = FontWeight.Medium,
                    color = MiuixTheme.colorScheme.onBackgroundVariant
                )
            }
        }
    }
}
