package com.suqi8.oshin.ui.activity.about

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.YukiHookAPI_Impl
import com.suqi8.oshin.R
import com.suqi8.oshin.executeCommand
import com.suqi8.oshin.ui.activity.funlistui.addline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog
import java.lang.reflect.Method
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Main_About(
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues,
    colorMode: MutableState<Int>,
    context: Context,
    navController: NavController,
    alpha: MutableState<Float>,
    blur: MutableState<Dp>,
    noise: MutableState<Float>
) {
    val showDeviceNameDialog = remember { mutableStateOf(false) }
    val deviceName: MutableState<String> = remember {
        mutableStateOf(
            Settings.Global.getString(
                context.contentResolver,
                "revise_device_name"
            )
        )
    }
    val deviceNameCache: MutableState<String> = remember { mutableStateOf(deviceName.value) }
    val physicalTotalStorage = formatSize(getPhysicalTotalStorage(context))
    val usedStorage = formatSize(getUsedStorage())
    val focusManager = LocalFocusManager.current

    Scaffold() {
        LazyColumn(
            contentPadding = PaddingValues(top = padding.calculateTopPadding()),
            topAppBarScrollBehavior = topAppBarScrollBehavior, modifier = Modifier.fillMaxSize()
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        // 背景图片
                        Image(
                            painter = painterResource(id = R.drawable.aboutbackground),
                            contentDescription = null,
                            contentScale = ContentScale.Crop, // 图片裁剪以适应容器
                            modifier = Modifier.matchParentSize() // 使图片充满整个 Box
                        )
                        Column(modifier = Modifier.padding(bottom = 16.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp) // 设定 Box 的高度
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(250.dp)
                                        .align(Alignment.Center)
                                    /*.offset(y = (-20).dp)*/,
                                    contentScale = ContentScale.Crop
                                )
                                Text(
                                    text = context.packageManager.getPackageInfo(
                                        context.packageName,
                                        0
                                    ).versionName.toString(),
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 8.dp)
                                    /*.offset(y = (-20).dp)*/
                                )
                            }

                            /*Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .offset(y = (-10).dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.coloros),
                                    contentDescription = null,
                                    modifier = Modifier.size(width = 121.875.dp, height = 25.dp)
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Image(
                                    painter = painterResource(id = R.drawable.coloros15logo),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .offset(y = (-2.5).dp)
                                )
                            }
                            Button(
                                modifier = Modifier
                                    .padding(start = 16.dp, end = 16.dp)
                                    .fillMaxWidth(),
                                text = stringResource(R.string.check_update),
                                submit = true,
                                onClick = {})*/
                        }
                    }
                }
                Spacer(modifier = Modifier.size(12.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp)
                ) {
                    SuperArrow(title = stringResource(R.string.Device_Name), onClick = {
                        showDeviceNameDialog.value = true
                    }, rightText = deviceName.value + "")
                    DeviceNameDialog(showDeviceNameDialog, deviceNameCache, deviceName, focusManager)
                    addline()
                    //设备内存容量
                    SuperArrow(title = stringResource(R.string.Device_Memory),
                        rightText = "$usedStorage / $physicalTotalStorage",
                        onClick = {
                            try {
                                // 创建 Intent 打开 StorageDashboardActivity
                                val intent = Intent().apply {
                                    setClassName(
                                        "com.android.settings",
                                        "com.android.settings.Settings\$StorageDashboardActivity"
                                    )
                                }
                                context.startActivity(intent) // 启动 Activity
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(context, "无法打开存储管理页面", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        })
                }
                Spacer(modifier = Modifier.size(12.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp)
                ) {

                    Text(
                        deviceName.value + "",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(20.dp)
                    )
                    Column(Modifier.padding(start = 20.dp, end = 20.dp, bottom = 10.dp)) {
                        Column(Modifier.padding(bottom = 10.dp)) {
                            Text(
                                context.packageManager.getPackageInfo(
                                    context.packageName,
                                    0
                                ).versionName.toString().substringAfterLast("."), fontSize = 14.sp
                            )
                            Text("Commit ID", fontSize = 12.sp, color = Color.Gray)
                        }
                        Column(Modifier.padding(bottom = 10.dp)) {
                            Text(YukiHookAPI.VERSION, fontSize = 14.sp)
                            Text(
                                stringResource(R.string.yuki_hook_api_version),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        Column(Modifier.padding(bottom = 10.dp)) {
                            Text(YukiHookAPI_Impl.compiledTimestamp.toString(), fontSize = 14.sp)
                            Text(
                                stringResource(R.string.compiled_timestamp),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        Column(Modifier.padding(bottom = 10.dp)) {
                            Text(
                                timestampToDateTime(YukiHookAPI_Impl.compiledTimestamp),
                                fontSize = 14.sp
                            )
                            Text(
                                stringResource(R.string.compiled_time),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
                SmallTitle(text = stringResource(R.string.by_the_way))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp)
                ) {
                    Card(Modifier.padding(10.dp)) {
                        Image(
                            painter = painterResource(R.drawable.qq_pic_merged_1727926207595),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth()
                        )
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
                SmallTitle(text = stringResource(R.string.thank))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp)
                ) {
                    SuperArrow(title = stringResource(R.string.donors_list))
                    addline()
                    SuperArrow(title = stringResource(R.string.contributors), onClick = {
                        navController.navigate("about_contributors")
                    })
                    addline()
                    SuperArrow(title = stringResource(R.string.references), onClick = {
                        navController.navigate("about_references")
                    })
                }
                SmallTitle(text = stringResource(R.string.other))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp)
                ) {
                    SuperArrow(title = stringResource(R.string.settings), leftAction = {
                        Image(
                            painter = painterResource(R.drawable.settings),
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                                .padding(end = 8.dp),
                            colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurface)
                        )
                    }, onClick = {
                        navController.navigate("about_setting")
                    })
                    addline()
                    SuperArrow(title = stringResource(R.string.official_channel), leftAction = {
                        Image(
                            painter = painterResource(R.drawable.group),
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                                .padding(end = 8.dp),
                            colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurface)
                        )
                    }, onClick = {
                        navController.navigate("about_group")
                    })
                    addline()
                    SuperArrow(title = stringResource(R.string.official_website), leftAction = {
                        Image(
                            painter = painterResource(R.drawable.website),
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                                .padding(end = 8.dp),
                            colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurface)
                        )
                    }, onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://oshin.mikusignal.top/")
                        )
                        context.startActivity(intent)
                    })
                    addline()
                    SuperArrow(
                        title = "GitHub",
                        summary = stringResource(R.string.github_summary),
                        leftAction = {
                            Image(
                                painter = painterResource(R.drawable.github),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(32.dp)
                                    .padding(end = 8.dp),
                                colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurface)
                            )
                        }, onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://github.com/suqi8/OPatch")
                            )
                            context.startActivity(intent)
                        })
                    addline()
                    SuperArrow(title = stringResource(R.string.contribute_translation),
                        summary = stringResource(R.string.crowdin_contribute_summary),
                        leftAction = {
                            Image(
                                painter = painterResource(R.drawable.translators),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(32.dp)
                                    .padding(end = 8.dp),
                                colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurface)
                            )
                        },
                        onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://crowdin.com/project/opatch")
                            )
                            context.startActivity(intent)
                        })
                }
                Spacer(Modifier.size(20.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Powered By SYC Team",
                    fontSize = MiuixTheme.textStyles.subtitle.fontSize,
                    fontWeight = FontWeight.Medium,
                    color = MiuixTheme.colorScheme.onBackgroundVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.size(89.dp))
                Spacer(
                    Modifier.height(
                        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                    )
                )
            }
        }
    }
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
fun DeviceNameDialog(
    showDeviceNameDialog: MutableState<Boolean>,
    deviceNameCache: MutableState<String>,
    deviceName: MutableState<String>,
    focusManager: androidx.compose.ui.focus.FocusManager
) {
    val context = LocalContext.current
    if (!showDeviceNameDialog.value) return
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
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.cancel),
                onClick = {
                    dismissDialog(showDeviceNameDialog)
                }
            )
            Spacer(Modifier.width(12.dp))
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.ok),
                colors = ButtonDefaults.textButtonColorsPrimary(),
                onClick = {
                    dismissDialog(showDeviceNameDialog)
                    deviceName.value = deviceNameCache.value
                    CoroutineScope(Dispatchers.IO).launch {
                        executeCommand("settings put global revise_device_name \"${deviceName.value}\"")
                    }
                }
            )
        }
    }
}
