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
import com.highcapable.yukihookapi.hook.factory.prefs
import top.yukonga.miuix.kmp.extra.SuperSwitch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Main_About(topAppBarScrollBehavior: ScrollBehavior, padding: PaddingValues, colorMode: MutableState<Int>,context: Context, navController: NavController) {
    val showDeviceNameDialog = remember { mutableStateOf(false) }
    val deviceName: MutableState<String> = remember { mutableStateOf("未设置设备昵称") }
    val deviceNameCache: MutableState<String> = remember { mutableStateOf(deviceName.value) }
    val physicalTotalStorage = formatSize(getPhysicalTotalStorage(context))
    val usedStorage = formatSize(getUsedStorage())
    val context = LocalContext.current
    var isDebug = context.prefs("settings").getBoolean("Debug", false)
    val focusManager = LocalFocusManager.current
    LaunchedEffect(Unit) {
        val cachedName = getDeviceName(context) // 获取保存的设备名称
        if (cachedName != null) {
            deviceName.value = cachedName
            deviceNameCache.value = cachedName
        }
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
                    SuperSwitch(
                        title = "Debug",
                        checked = isDebug,
                        onCheckedChange = {
                            isDebug = it
                            context.prefs("settings").edit { putBoolean("Debug", it) }
                        }
                    )
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
                    SuperArrow(
                        title = stringResource(R.string.official_channel),
                        summary = "Telegram、QQ",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/OPatch"))
                            context.startActivity(intent)
                        }
                    )
                    SuperArrow(
                        title = stringResource(R.string.contribute_translation),
                        summary = stringResource(R.string.crowdin_contribute_summary),
                        onClick = {
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
                showDeviceNameDialog.value = false
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
                        dismissDialog()
                        showDeviceNameDialog.value = false
                    }
                )
                Spacer(Modifier.width(12.dp))
                Button(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.ok),
                    submit = true,
                    onClick = {
                        dismissDialog()
                        deviceName.value = deviceNameCache.value
                        CoroutineScope(Dispatchers.IO).launch {
                            saveDeviceName(context, deviceName.value) // 保存设备名称
                        }
                        showDeviceNameDialog.value = false
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
