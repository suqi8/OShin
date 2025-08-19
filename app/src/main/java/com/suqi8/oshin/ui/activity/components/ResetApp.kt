package com.suqi8.oshin.ui.activity.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.highcapable.yukihookapi.YukiHookAPI
import com.kyant.capsule.G2RoundedCornerShape
import com.kyant.expressa.m3.shape.CornerShape
import com.kyant.liquidglass.GlassStyle
import com.kyant.liquidglass.LiquidGlassProviderState
import com.kyant.liquidglass.liquidGlass
import com.kyant.liquidglass.material.GlassMaterial
import com.kyant.liquidglass.refraction.InnerRefraction
import com.kyant.liquidglass.refraction.RefractionAmount
import com.kyant.liquidglass.refraction.RefractionHeight
import com.suqi8.oshin.R
import com.suqi8.oshin.utils.GetAppIconAndName
import com.suqi8.oshin.utils.drawColoredShadow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun AppRestartScreen(appList: List<String>, showresetAppDialog: MutableState<Boolean>,liquidGlassProviderState: LiquidGlassProviderState) {
    if (showresetAppDialog.value) {
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
            liquidGlassProviderState = liquidGlassProviderState
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun ConfirmationDialog(
    appPackage: List<String>,
    show: MutableState<Boolean>,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    liquidGlassProviderState: LiquidGlassProviderState
) {
    if (!show.value) return
    val dialogGlassStyle = GlassStyle(
        G2RoundedCornerShape(28.dp),
        innerRefraction = InnerRefraction(
            height = RefractionHeight(28.dp),
            amount = RefractionAmount(-28.dp)
        ),
        material = GlassMaterial(
            blurRadius = 2.dp,
            brush = SolidColor(MiuixTheme.colorScheme.background),
            alpha = 0.2f
        )
    )

    val cardAndCancelButtonStyle = GlassStyle(
        CornerShape.full,
        innerRefraction = InnerRefraction(
            height = RefractionHeight(8.dp),
            amount = RefractionAmount((-28).dp)
        ),
        material = GlassMaterial(
            blurRadius = 8.dp,
            brush = SolidColor(MiuixTheme.colorScheme.surface),
            alpha = 0.5f
        )
    )

    val confirmButtonStyle = GlassStyle(
        CornerShape.full,
        innerRefraction = InnerRefraction(
            height = RefractionHeight(8.dp),
            amount = RefractionAmount((-28).dp)
        ),
        material = GlassMaterial(
            blurRadius = 8.dp,
            brush = SolidColor(MiuixTheme.colorScheme.primaryVariant),
            alpha = 0.5f
        )
    )

    SuperDialog(
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
    }
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
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isLoading.value) {
                    Card(
                        colors = CardDefaults.defaultColors(dominantColor.value),
                        modifier = Modifier
                            .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
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
