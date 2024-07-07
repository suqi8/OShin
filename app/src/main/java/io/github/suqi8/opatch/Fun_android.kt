package io.github.suqi8.opatch

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.KeyEventDispatcher.dispatchKeyEvent
import io.github.suqi8.opatch.ui.theme.OPatchTheme
import androidx.activity.OnBackPressedCallback
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import io.github.suqi8.opatch.hook.corepatch.SettingsActivity
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Fun_android(navController: NavController) {
    val context = LocalContext.current

    Scaffold(topBar = { GetAppIconAndName(packageName = "android") { appName, icon ->
        LargeTopAppBar(
            title = { Text(text = appName) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                    //startActivity(context, Intent(context, MainActivity::class.java), null)
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Localized description"
                    )
                }
            },
        )
    } }) {innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {
            Column {
                Row(modifier = Modifier
                    .clickable { navController.navigate("Fun_android_package_manager_services")
                         }
                    .fillMaxWidth()) {
                    // 你的文本列
                    Column(modifier = Modifier
                        .weight(1f) // 让这个列占据剩余空间
                        .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)) {
                        Text(text = stringResource(id = R.string.package_manager_services), fontSize = 19.sp)
                    }
                    // 用于推挤图标到最右边的Spacer
                    Spacer(modifier = Modifier
                        .weight(1f) // 这个Spacer也占据剩余空间
                    )
                    // 箭头图标
                    Icon(
                        painter = painterResource(id = R.drawable.round_arrow_forward_ios_24), // 确保你有这个资源
                        contentDescription = null, // 为图标添加描述
                        modifier = Modifier
                            .align(Alignment.CenterVertically) // 垂直居中对齐
                            .padding(end = 20.dp)
                            .alpha(0.8f)
                    )

                }
            }
        }
    }
}
