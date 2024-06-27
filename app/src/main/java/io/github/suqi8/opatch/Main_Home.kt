package io.github.suqi8.opatch

import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.core.graphics.drawable.toBitmap
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.param.PackageParam

@Composable
fun Main_Home() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ElevatedCard(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 20.dp, end = 20.dp),
            colors = CardDefaults.cardColors(containerColor = if (YukiHookAPI.Status.isModuleActive) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.errorContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),) {
            Row(verticalAlignment =  Alignment.CenterVertically,modifier = Modifier.padding(start = 30.dp, end = 30.dp, top = 30.dp, bottom = 30.dp)) {
                Image(painter = painterResource(id = if (YukiHookAPI.Status.isModuleActive) R.drawable.twotone_check_circle_24 else R.drawable.twotone_unpublished_24), contentDescription = null)
                Column(verticalArrangement =  Arrangement.Center,modifier = Modifier.padding(start = 30.dp)) {
                Text(text = if (YukiHookAPI.Status.isModuleActive) "模块已激活" else "模块未激活")
                    Text(text = if (YukiHookAPI.Status.isModuleActive) "${YukiHookAPI.Status.Executor.name}-v${YukiHookAPI.Status.Executor.apiLevel}" else "请到LSPosed中激活本模块  ")
                }
            }
        }
        OutlinedCard(modifier = Modifier
            .padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 20.dp)
            .fillMaxWidth()) {
            Column(modifier = Modifier.padding(start = 30.dp, end = 30.dp, top = 30.dp, bottom = 30.dp)) {
                Text(text = "SOC 型号")
                Text(text = Build.SOC_MODEL, fontSize = 13.sp, color = Color.Gray)

                Text(text = "Android 版本", modifier = Modifier.padding(top = 10.dp))
                Text(text = Build.VERSION.RELEASE, fontSize = 13.sp, color = Color.Gray)

                Text(text = "Android API 版本", modifier = Modifier.padding(top = 10.dp))
                Text(text = Build.VERSION.SDK_INT.toString(), fontSize = 13.sp, color = Color.Gray)

                Text(text = "系统版本", modifier = Modifier.padding(top = 10.dp))
                Text(text = Build.DISPLAY, fontSize = 13.sp, color = Color.Gray)

                Text(text = "CPU 代号", modifier = Modifier.padding(top = 10.dp))
                Text(text = Build.BOARD.substringAfter("_"), fontSize = 13.sp, color = Color.Gray)

                Text(text = "设备制造商", modifier = Modifier.padding(top = 10.dp))
                Text(text = Build.MANUFACTURER, fontSize = 13.sp, color = Color.Gray)

                Text(text = "支持的 Abi", modifier = Modifier.padding(top = 10.dp))
                Text(text = Build.SUPPORTED_ABIS.joinToString(), fontSize = 13.sp, color = Color.Gray)

                Text(text = "Android 安全补丁", modifier = Modifier.padding(top = 10.dp))
                Text(text = Build.VERSION.SECURITY_PATCH, fontSize = 13.sp, color = Color.Gray)

                Text(text = "设备指纹", modifier = Modifier.padding(top = 10.dp))
                Text(text = Build.FINGERPRINT, fontSize = 13.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun GetAppIconAndName(packageName: String, onAppInfoLoaded: @Composable (String, ImageBitmap) -> Unit) {
    val context = LocalContext.current
    val packageManager = context.packageManager
    val applicationInfo = remember { mutableStateOf<android.content.pm.ApplicationInfo?>(null) }

    LaunchedEffect(packageName) {
        try {
            applicationInfo.value = packageManager.getApplicationInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            // 应用未找到，可以处理异常情况
        }
    }

    applicationInfo.value?.let { info ->
        val icon = info.loadIcon(packageManager)
        val appName = packageManager.getApplicationLabel(info).toString()

        onAppInfoLoaded(appName, icon.toBitmap().asImageBitmap())
    }
}
