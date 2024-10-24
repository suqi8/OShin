package io.github.suqi8.opatch

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.ScreenCaptureCallback
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.View
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.navigation.NavController
import com.highcapable.yukihookapi.hook.factory.prefs
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import io.github.suqi8.opatch.ui.tools.resetApp
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack
import top.yukonga.miuix.kmp.theme.MiuixTheme
import java.io.FileInputStream
import java.util.Properties
import androidx.compose.runtime.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperSwitch

@SuppressLint("SuspiciousIndentation")
@Composable
fun Fun_com_android_systemui(navController: NavController) {
    val context = LocalContext.current
    val one = MiuixScrollBehavior(top.yukonga.miuix.kmp.basic.rememberTopAppBarState())
    val appList = listOf("com.android.systemui")
    val RestartAPP = remember { mutableStateOf(false) }
    val resetApp = resetApp()
    val isDebug = context.prefs("settings").getBoolean("Debug", false)
    val hide_status_bar = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        hide_status_bar.value = context.prefs("settings").getBoolean("hide_status_bar", false)
    }

    val alpha = context.prefs("settings").getFloat("AppAlpha", 0.75f)
    val blurRadius: Dp = context.prefs("settings").getInt("AppblurRadius", 25).dp
    val noiseFactor = context.prefs("settings").getFloat("AppnoiseFactor", 0f)
    val containerColor: Color = MiuixTheme.colorScheme.background
    val hazeState = remember { HazeState() }
    val hazeStyle = remember(containerColor, alpha, blurRadius, noiseFactor) {
        HazeStyle(
            backgroundColor = containerColor,
            tint = HazeTint.Color(containerColor.copy(alpha)),
            blurRadius = blurRadius,
            noiseFactor = noiseFactor
        )
    }

    Scaffold(topBar = { GetAppIconAndName(packageName = "com.android.systemui") { appName, icon ->
        TopAppBar(
            color = Color.Transparent,
            modifier = Modifier.hazeChild(
                state = hazeState,
                style = hazeStyle),
            title = appName,
            scrollBehavior = one,
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                },
                    modifier = Modifier.padding(start = 18.dp)) {
                    Icon(
                        imageVector = MiuixIcons.ArrowBack,
                        contentDescription = null,
                        tint = MiuixTheme.colorScheme.onBackground
                    )
                }
            }, actions = {
                IconButton(onClick = {
                    RestartAPP.value = true
                },
                    modifier = Modifier.padding(end = 18.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = null,
                        tint = MiuixTheme.colorScheme.onBackground
                    )
                }
            }
        )
    } }) {padding ->
        LazyColumn(contentPadding = PaddingValues(top = padding.calculateTopPadding()),
            topAppBarScrollBehavior = one, modifier = Modifier
                .fillMaxSize()
                .haze(state = hazeState)) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp, top = 15.dp)
                ) {
                    SuperArrow(title = stringResource(id = R.string.status_bar_clock),
                        onClick = {
                            navController.navigate("Fun_com_android_systemui_status_bar_clock")
                        })
                    addline()
                    SuperArrow(title = stringResource(id = R.string.hardware_indicator),
                        onClick = {
                            navController.navigate("Fun_com_android_systemui_hardware_indicator")
                        })
                    addline()
                    SuperArrow(title = stringResource(id = R.string.status_bar_icon),
                        onClick = {
                            navController.navigate("Fun_com_android_systemui_statusbar_icon")
                        })
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp, top = 15.dp)
                ) {
                    SuperSwitch(title = stringResource(id = R.string.hide_status_bar),
                        checked = hide_status_bar.value,
                        onCheckedChange = {
                            hide_status_bar.value = it
                            context.prefs("settings").edit().putBoolean("hide_status_bar", it)
                        })
                }

                if (isDebug) {
                    // 创建 MutableStateList 存储广播日志
                    val broadcastLogs = remember { mutableStateListOf<String>() }
                    val context = LocalContext.current

                    DisposableEffect(Unit) {
                        val receiver = object : BroadcastReceiver() {
                            override fun onReceive(ctx: Context?, intent: Intent?) {
                                val action = intent?.action ?: "Unknown Action"
                                val extras = intent?.extras?.keySet()?.joinToString(", ") ?: "No extras"
                                val logMessage: String = "Action: $action, Extras: [$extras]"

                                Log.d(TAG, "onReceive: $logMessage")
                                broadcastLogs.add(0, logMessage)  // 最新的日志添加在顶部
                            }
                        }

                        // 注册一个不带过滤器的 IntentFilter 尽量捕获所有广播
                        val intentFilter = IntentFilter().apply {
                            priority = IntentFilter.SYSTEM_HIGH_PRIORITY  // 尽量提升优先级
                        }
                        context.registerReceiver(receiver, intentFilter)

                        // 在 Composable 销毁时注销接收器，避免内存泄漏
                        onDispose {
                            context.unregisterReceiver(receiver)
                        }
                    }
                    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                        if (result.resultCode == Activity.RESULT_OK) {
                            result.data?.data?.let { uri ->
                                // 在这里处理返回的 URI，例如读取文件
                                Log.d("FilePicker", "Selected file URI: $uri")
                            }
                        }
                    }
                    var fis: FileInputStream? = null
                    var currentNow = remember { mutableStateOf(0.0) }
                    var errorMessage = remember { mutableStateOf<String?>(null) }
                    Button(onClick = {
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "*/*" // 可以指定特定类型，例如 "image/*" 或 "video/*"
                        }
                        launcher.launch(intent)
                    },
                        text = "a")

                    LaunchedEffect(Unit) {
                        try {
                            val fis = FileInputStream("/sys/class/power_supply/battery/uevent")
                            val props = Properties()
                            props.load(fis)
                            val currentNowString = props.getProperty("POWER_SUPPLY_CURRENT_NOW")
                            currentNow.value = currentNowString?.toDoubleOrNull() ?: 0.0
                        } catch (e: Exception) {
                            errorMessage.value = e.message // 捕获异常并保存错误信息
                        } finally {
                            fis?.close()
                        }
                    }
                    var rawCurr = 0
                    /*
                                    rawCurr =
                                        (-1 * Math.round(props.getProperty("POWER_SUPPLY_CURRENT_NOW").toInt() / 1000f)).toInt()*/
                    // UI 组件
                    SmallTitle(text = "Error: ${errorMessage.value} ${currentNow.value}")
                }
            }
        }
    }
    resetApp.AppRestartScreen(appList,RestartAPP)
}
