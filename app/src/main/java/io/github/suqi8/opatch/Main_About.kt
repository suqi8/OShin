package io.github.suqi8.opatch

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.navigation.NavController
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.YukiHookAPI_Impl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.extra.SuperDropdown
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.showDialog
import java.lang.reflect.Method
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import android.content.ActivityNotFoundException
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import com.highcapable.yukihookapi.hook.factory.prefs
import dev.chrisbanes.haze.HazeDefaults.blurRadius
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.extra.SuperSwitch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Main_About(topAppBarScrollBehavior: ScrollBehavior,
               padding: PaddingValues,
               colorMode: MutableState<Int>,
               context: Context,
               navController: NavController,
               alpha: MutableState<Float>,
               blur: MutableState<Dp>,
               noise: MutableState<Float>) {
    val showDeviceNameDialog = remember { mutableStateOf(false) }
    val deviceName: MutableState<String> = remember { mutableStateOf("未设置设备昵称") }
    val deviceNameCache: MutableState<String> = remember { mutableStateOf(deviceName.value) }
    val physicalTotalStorage = formatSize(getPhysicalTotalStorage(context))
    val usedStorage = formatSize(getUsedStorage())
    val isDebug = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val addline = remember { mutableStateOf(false) }
    val showAlphaDialog = remember { mutableStateOf(false) }
    val showBlurDialog = remember { mutableStateOf(false) }
    val showNoiseDialog = remember { mutableStateOf(false) }
    val auto_color = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        auto_color.value = context.prefs("settings").getBoolean("auto_color", false)
        val cachedName = getDeviceName(context) // 获取保存的设备名称
        if (cachedName != null) {
            deviceName.value = cachedName
            deviceNameCache.value = cachedName
        }
        isDebug.value = context.prefs("settings").getBoolean("Debug", false)
        addline.value = context.prefs("settings").getBoolean("addline", false)
    }
    Scaffold() {
        LazyColumn(contentPadding = PaddingValues(top = padding.calculateTopPadding()),
            topAppBarScrollBehavior = topAppBarScrollBehavior, modifier = Modifier.fillMaxSize()) {
            item {
                Spacer(modifier = Modifier.size(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxSize() // 让 Box 填满整个容器或屏幕
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        modifier = Modifier
                            .size(250.dp)
                            .align(Alignment.Center), // 将图片在 Box 中居中对齐
                        contentScale = ContentScale.Crop // 图片裁剪并放大，保持充满容器
                    )
                    Text(text = context.packageManager.getPackageInfo(context.packageName, 0).versionName.toString(), fontSize = 14.sp, color = Color.Gray,
                        modifier = Modifier.padding(top=180.dp)
                            .align(Alignment.Center))
                }
                Button(
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp)
                        .fillMaxWidth(),
                    text = stringResource(R.string.check_update),
                    submit = true,
                    onClick = {
                    }
                )
                Spacer(modifier = Modifier.size(12.dp))
                Card(modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 6.dp)) {
                    SuperArrow(title = stringResource(R.string.Device_Name), onClick = {
                        showDeviceNameDialog.value = true
                    }, rightText = deviceName.value)
                    addline()
                    //设备内存容量
                    SuperArrow(title = stringResource(R.string.Device_Memory), rightText = "$usedStorage / $physicalTotalStorage",
                        onClick = {
                            try {
                                // 创建 Intent 打开 StorageDashboardActivity
                                val intent = Intent().apply {
                                    setClassName("com.android.settings", "com.android.settings.Settings\$StorageDashboardActivity")
                                }
                                context.startActivity(intent) // 启动 Activity
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(context, "无法打开存储管理页面", Toast.LENGTH_SHORT).show()
                            }
                        })
                }
                Spacer(modifier = Modifier.size(12.dp))
                Card(modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 6.dp)) {

                    Text("OPatch", fontSize = 25.sp,fontWeight = FontWeight.Bold, modifier = Modifier.padding(20.dp))
                    Column(Modifier.padding(start = 20.dp, end = 20.dp, bottom = 10.dp)) {
                        Column(Modifier.padding( bottom = 10.dp)) {
                            Text(context.packageManager.getPackageInfo(context.packageName, 0).versionName.toString().substringAfterLast("."), fontSize = 14.sp)
                            Text("Commit ID", fontSize = 12.sp, color = Color.Gray)
                        }
                        Column(Modifier.padding( bottom = 10.dp)) {
                            Text(YukiHookAPI.VERSION, fontSize = 14.sp)
                            Text(stringResource(R.string.yuki_hook_api_version), fontSize = 12.sp, color = Color.Gray)
                        }
                        Column(Modifier.padding( bottom = 10.dp)) {
                            Text(YukiHookAPI_Impl.compiledTimestamp.toString(), fontSize = 14.sp)
                            Text(stringResource(R.string.compiled_timestamp), fontSize = 12.sp, color = Color.Gray)
                        }
                        Column(Modifier.padding( bottom = 10.dp)) {
                            Text(timestampToDateTime(YukiHookAPI_Impl.compiledTimestamp), fontSize = 14.sp)
                            Text(stringResource(R.string.compiled_time), fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
                SmallTitle(text = stringResource(R.string.app_settings))
                Card(modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 6.dp)) {
                    SuperDropdown(
                        title = stringResource(R.string.Color_Mode),
                        items = listOf(stringResource(R.string.Auto_Mode),stringResource(R.string.Light_Mode), stringResource(R.string.Night_Mode)),
                        selectedIndex = colorMode.value,
                        onSelectedIndexChange = { colorMode.value = it
                            CoroutineScope(Dispatchers.IO).launch {
                                saveColorMode(context, it)
                            }
                        }
                    )
                    addline()
                    SuperSwitch(
                        title = "Debug",
                        checked = isDebug.value,
                        onCheckedChange = {
                            isDebug.value = it
                            context.prefs("settings").edit { putBoolean("Debug", it) }
                        }
                    )
                    addline()
                    SuperSwitch(title = stringResource(R.string.addline),
                        checked = addline.value,
                        onCheckedChange = {
                            addline.value = it
                            context.prefs("settings").edit { putBoolean("addline", it) }
                        })
                    addline()
                    Column {
                        SuperArrow(
                            title = stringResource(R.string.alpha_setting),
                            onClick = {
                                showAlphaDialog.value = true
                            },
                            rightText = "${alpha.value}f"
                        )
                        Slider(
                            progress = alpha.value,
                            onProgressChange = { newProgress ->
                                alpha.value = newProgress
                                context.prefs("settings").edit { putFloat("AppAlpha", newProgress) }
                            },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                        )
                    }
                    addline()
                    Column {
                        SuperArrow(
                            title = stringResource(R.string.blur_radius_setting),
                            onClick = {
                                showBlurDialog.value = true
                            },
                            rightText = "${blur.value}"
                        )
                        Slider(
                            progress = (blur.value.value / 100f),
                            onProgressChange = { newProgress ->
                                blur.value =
                                    (newProgress * 100).dp
                                context.prefs("settings").edit { putInt("AppblurRadius", ((newProgress * 100).toInt())) }
                            },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                        )
                    }
                    addline()
                    Column {
                        SuperArrow(
                            title = stringResource(R.string.noise_factor_setting),
                            onClick = {
                                showNoiseDialog.value = true
                            },
                            rightText = "${noise.value}f"
                        )
                        Slider(
                            progress = noise.value,
                            onProgressChange = { newProgress ->
                                noise.value = newProgress
                                context.prefs("settings").edit { putFloat("AppnoiseFactor", newProgress) }
                            },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                        )
                    }
                    addline()
                    SuperSwitch(title = stringResource(R.string.feature_auto_color_picking_enabled),
                        summary = stringResource(R.string.feature_auto_color_picking_warning),
                        checked = auto_color.value,
                        onCheckedChange = {
                            auto_color.value = it
                            context.prefs("settings").edit { putBoolean("auto_color", it) }
                        })
                    addline()
                    SuperArrow(title = "Miuix",
                        onClick = {
                            navController.navigate("Miuix")
                        })
                }
                SmallTitle(text = stringResource(R.string.by_the_way))
                Card(modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp)) {
                    Card(Modifier.padding(10.dp)) {
                        Image(painter = painterResource(R.drawable.qq_pic_merged_1727926207595), contentDescription = null, modifier = Modifier.fillMaxWidth())
                    }
                    val toastMessage = stringResource(R.string.please_install_cool_apk)
                    SuperArrow(title = stringResource(R.string.go_to_his_homepage), onClick = {
                        val coolApkUri = Uri.parse("coolmarket://u/894238")
                        val intent = Intent(Intent.ACTION_VIEW, coolApkUri)

                        try {
                            // 尝试启动酷安应用
                            context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            // 如果酷安未安装，则提示用户
                            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                SmallTitle(text = stringResource(R.string.about))
                Card(modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 6.dp)) {
                    SuperArrow(
                        title = "GitHub",
                        summary = "https://github.com/suqi8/OPatch",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/suqi8/OPatch"))
                            context.startActivity(intent)
                        }
                    )
                    addline()
                    SuperArrow(
                        title = stringResource(R.string.official_channel) + " OPatch",
                        summary = "Telegram",
                        onClick = {
                            val telegramIntent = Intent(Intent.ACTION_VIEW)
                            telegramIntent.data = Uri.parse("tg://resolve?domain=OPatchA")
                            // 检查是否安装了 Telegram 应用
                            if (telegramIntent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(telegramIntent)
                            } else {
                                // 如果未安装 Telegram，可以显示一个提示或打开 Telegram 网页版
                                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/OPatchA"))
                                context.startActivity(webIntent)
                            }
                        }
                    )
                    addline()
                    SuperArrow(
                        title = stringResource(R.string.official_channel) + " OPatch Chat",
                        summary = "Telegram",
                        onClick = {
                            val telegramIntent = Intent(Intent.ACTION_VIEW)
                            telegramIntent.data = Uri.parse("tg://resolve?domain=OPatchB")
                            // 检查是否安装了 Telegram 应用
                            if (telegramIntent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(telegramIntent)
                            } else {
                                // 如果未安装 Telegram，可以显示一个提示或打开 Telegram 网页版
                                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/OPatchB"))
                                context.startActivity(webIntent)
                            }
                        }
                    )
                    addline()
                    SuperArrow(
                        title = stringResource(R.string.official_channel) + " OPatch Ci",
                        summary = "Telegram",
                        onClick = {
                            val telegramIntent = Intent(Intent.ACTION_VIEW)
                            telegramIntent.data = Uri.parse("tg://resolve?domain=OPatchC")
                            // 检查是否安装了 Telegram 应用
                            if (telegramIntent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(telegramIntent)
                            } else {
                                // 如果未安装 Telegram，可以显示一个提示或打开 Telegram 网页版
                                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/OPatchC"))
                                context.startActivity(webIntent)
                            }
                        }
                    )
                    addline()
                    SuperArrow(
                        title = stringResource(R.string.official_channel) + " OPatch QQ",
                        summary = "QQ",
                        onClick = {
                            val qqIntent = Intent(Intent.ACTION_VIEW)
                            // 使用 mqqwpa 协议来打开 QQ 群
                            qqIntent.data = Uri.parse("mqqapi://card/show_pslcard?src_type=internal&version=1&uin=740266099&card_type=group&source=qrcode")
                            // 检查是否安装了 QQ 应用
                            if (qqIntent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(qqIntent)
                            } else {
                                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=dbP78P2qCYuR2RxGtwmwCrlMCsh2MB2N&authKey=uTkJAGf0gg7%2Fx%2B3OBPrf%2F%2FnyZY2ntPNvnz6%2BTUo%2BHa0Pe%2F%2FqtXvK%2BSJ3%2B4PS0zbO&noverify=0&group_code=740266099"))
                                context.startActivity(webIntent)
                            }
                        }
                    )
                    addline()
                    SuperArrow(
                        title = stringResource(R.string.contribute_translation),
                        summary = stringResource(R.string.crowdin_contribute_summary),
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://crowdin.com/project/opatch"))
                            context.startActivity(intent)
                        }
                    )
                }
                Spacer(Modifier.size(65.dp))
            }
        }
    }
    DeviceNameDialog(showDeviceNameDialog,deviceNameCache, deviceName, focusManager)
}

fun timestampToDateTime(timestamp: Long): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.systemDefault())
    return formatter.format(Instant.ofEpochMilli(timestamp))
}

@SuppressLint("SoonBlockedPrivateApi")
fun getPhysicalTotalStorage(context: Context): Long {
    val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
    val storageVolumes: List<StorageVolume> = storageManager.storageVolumes

    for (volume in storageVolumes) {
        try {
            // 通过反射获取每个存储卷的总物理大小
            val getVolumeMethod: Method = volume.javaClass.getDeclaredMethod("getPath")
            val path = getVolumeMethod.invoke(volume) as String

            val statFs = StatFs(path)
            return statFs.blockCountLong * statFs.blockSizeLong
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 如果失败，返回0
    return 0
}

@SuppressLint("DefaultLocale")
fun formatSize(size: Long): String {
    val kb = 1024
    val mb = kb * 1024
    val gb = mb * 1024

    return when {
        size >= gb -> String.format("%.2f GB", size.toFloat() / gb)
        size >= mb -> String.format("%.2f MB", size.toFloat() / mb)
        size >= kb -> String.format("%.2f KB", size.toFloat() / kb)
        else -> String.format("%d B", size)
    }
}

fun getTotalStorage(): Long {
    val stat = StatFs(Environment.getDataDirectory().path)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
        stat.totalBytes
    } else {
        stat.blockCountLong * stat.blockSizeLong
    }
}

fun getAvailableStorage(): Long {
    val stat = StatFs(Environment.getDataDirectory().path)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
        stat.availableBytes
    } else {
        stat.availableBlocksLong * stat.blockSizeLong
    }
}

fun getUsedStorage(): Long {
    val totalStorage = getTotalStorage()
    val availableStorage = getAvailableStorage()
    return totalStorage - availableStorage
}

@Composable
fun DeviceNameDialog(showDeviceNameDialog: MutableState<Boolean>,deviceNameCache: MutableState<String>, deviceName: MutableState<String>, focusManager: androidx.compose.ui.focus.FocusManager) {
    val context = LocalContext.current
    if (!showDeviceNameDialog.value) return
    showDialog(content = {
        SuperDialog(title = stringResource(R.string.Device_Name),
            show = showDeviceNameDialog,
            onDismissRequest = {
                dismissDialog(showDeviceNameDialog)
            }) {
            TextField(
                value = deviceNameCache.value,
                onValueChange = { deviceNameCache.value = it },
                backgroundColor = MiuixTheme.colorScheme.secondaryContainer,
                label = "",
                modifier = Modifier.padding(),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.cancel),
                    onClick = {
                        dismissDialog(showDeviceNameDialog)
                    }
                )
                Spacer(Modifier.width(12.dp))
                Button(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.ok),
                    submit = true,
                    onClick = {
                        dismissDialog(showDeviceNameDialog)
                        deviceName.value = deviceNameCache.value
                        CoroutineScope(Dispatchers.IO).launch {
                            saveDeviceName(context, deviceName.value) // 保存设备名称
                        }
                    }
                )
            }
        }
    })
}

suspend fun saveDeviceName(context: Context, deviceName: String) {
    context.dataStore.edit { preferences ->
        val DEVICE_NAME_KEY = stringPreferencesKey("device_name")
        preferences[DEVICE_NAME_KEY] = deviceName
    }
}

suspend fun getDeviceName(context: Context): String? {
    val preferences = context.dataStore.data.first()
    val DEVICE_NAME_KEY = stringPreferencesKey("device_name")
    return preferences[DEVICE_NAME_KEY]
}
