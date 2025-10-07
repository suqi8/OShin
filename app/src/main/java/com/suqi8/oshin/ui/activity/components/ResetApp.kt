package com.suqi8.oshin.ui.activity.components

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
import com.highcapable.yukihookapi.YukiHookAPI
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.colorControls
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.highlight.Highlight
import com.kyant.capsule.ContinuousCapsule
import com.kyant.capsule.ContinuousRoundedRectangle
import com.suqi8.oshin.R
import com.suqi8.oshin.utils.GetAppIconAndName
import com.suqi8.oshin.utils.drawColoredShadow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun AppRestartScreen(appList: List<String>, showresetAppDialog: MutableState<Boolean>, backdrop: Backdrop) {
    ConfirmationDialog(
        appPackage = appList,
        onConfirm = {
            appList.forEach {
                restartApp(it)
            }
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
    appPackage: List<String>,
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
                itemsIndexed(appPackage) { index, pkg ->
                    ResetAppList(pkg)
                    if (index < appPackage.lastIndex) {
                        addline()
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
}

private fun restartApp(packageName: String) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            if (packageName == "android") {
                val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "reboot"))
                process.waitFor()
            } else {
                val command = "pkill -f " + packageName
                val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
                process.waitFor()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ResetAppList(packageName: String) {
    GetAppIconAndName(packageName = packageName) { appName, icon ->
        if (appName != "noapp") {
            val defaultColor = MiuixTheme.colorScheme.primary
            val dominantColor = remember { mutableStateOf(colorCache[packageName] ?: defaultColor) }
            val isLoading = remember { mutableStateOf(dominantColor.value == defaultColor) }

            LaunchedEffect(icon, dominantColor.value) {
                if (isLoading.value) {
                    val newColor = withContext(Dispatchers.Default) {
                        if (!YukiHookAPI.Status.isModuleActive) defaultColor else getAutoColor(icon)
                    }
                    dominantColor.value = newColor
                    colorCache[packageName] = newColor
                    isLoading.value = false
                }
            }

            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isLoading.value) {
                    Card(
                        colors = CardDefaults.defaultColors(dominantColor.value),
                        modifier = Modifier
                            .padding(top = 16.dp, bottom = 16.dp)
                            .drawColoredShadow(
                                dominantColor.value,
                                1f,
                                borderRadius = 13.dp,
                                shadowRadius = 7.dp,
                                offsetX = 0.dp,
                                offsetY = 0.dp,
                                roundedRect = false
                            )
                    ) {
                        Image(
                            bitmap = icon,
                            contentDescription = "App Icon",
                            modifier = Modifier.size(45.dp)
                        )
                    }
                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        Text(text = appName)
                        Text(
                            text = packageName,
                            fontSize = MiuixTheme.textStyles.subtitle.fontSize,
                            fontWeight = FontWeight.Medium,
                            color = MiuixTheme.colorScheme.onBackgroundVariant
                        )
                    }
                }
            }
        } else {
            Text(text = "$packageName 没有安装", modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 4.dp))
        }
    }
}
