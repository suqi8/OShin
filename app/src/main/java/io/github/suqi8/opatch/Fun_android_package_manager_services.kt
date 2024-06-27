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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Fun_android_package_manager_services(navController: NavController) {
    var downgr by remember {
        mutableStateOf(false)
    }
    var authcreak by remember {
        mutableStateOf(false)
    }
    var digestCreak by remember {
        mutableStateOf(false)
    }
    var UsePreSig by remember {
        mutableStateOf(false)
    }
    var enhancedMode by remember {
        mutableStateOf(false)
    }
    var bypassBlock by remember {
        mutableStateOf(false)
    }
    var shared_user by remember {
        mutableStateOf(false)
    }
    var disable_verification_agent by remember {
        mutableStateOf(false)
    }
    Scaffold(topBar = {
        LargeTopAppBar(
            title = { Text(text = stringResource(id = R.string.package_manager_services)) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Localized description"
                    )
                }
            },
        )
     }) {innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {
            Column {
                Row(modifier = Modifier
                    .fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween) {
                    // 你的文本列
                    Column(modifier = Modifier
                        .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)) {
                        Text(text = stringResource(id = R.string.downgr), fontSize = 19.sp)
                        Text(text = stringResource(id = R.string.downgr_summary), fontSize = 12.sp)
                    }
                    Switch(checked = downgr, onCheckedChange = { downgr = it },modifier = Modifier
                        .align(Alignment.CenterVertically) // 垂直居中对齐
                        .padding(end = 20.dp))
                }
                Row(modifier = Modifier
                    .fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween) {
                    // 你的文本列
                    Column(modifier = Modifier
                        .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)) {
                        Text(text = stringResource(id = R.string.authcreak), fontSize = 19.sp)
                        Text(text = stringResource(id = R.string.authcreak_summary), fontSize = 12.sp)
                    }
                    Switch(checked = authcreak, onCheckedChange = { authcreak = it },modifier = Modifier
                        .align(Alignment.CenterVertically) // 垂直居中对齐
                        .padding(end = 20.dp))
                }
                Row(modifier = Modifier
                    .fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween) {
                    // 你的文本列
                    Column(modifier = Modifier
                        .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)) {
                        Text(text = stringResource(id = R.string.digestCreak), fontSize = 19.sp)
                        Text(text = stringResource(id = R.string.digestCreak_summary), fontSize = 12.sp)
                    }
                    Switch(checked = digestCreak, onCheckedChange = { digestCreak = it },modifier = Modifier
                        .align(Alignment.CenterVertically) // 垂直居中对齐
                        .padding(end = 20.dp))
                }
                Row(modifier = Modifier
                    .fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween) {
                    // 你的文本列
                    Column(modifier = Modifier
                        .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)) {
                        Text(text = stringResource(id = R.string.UsePreSig), fontSize = 19.sp)
                        Text(text = stringResource(id = R.string.UsePreSig_summary), fontSize = 12.sp)
                    }
                    Switch(checked = UsePreSig, onCheckedChange = { UsePreSig = it },modifier = Modifier
                        .align(Alignment.CenterVertically) // 垂直居中对齐
                        .padding(end = 20.dp))
                }
                Row(modifier = Modifier
                    .fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween) {
                    // 你的文本列
                    Column(modifier = Modifier
                        .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)) {
                        Text(text = stringResource(id = R.string.enhancedMode), fontSize = 19.sp)
                        Text(text = stringResource(id = R.string.enhancedMode_summary), fontSize = 12.sp)
                    }
                    Switch(checked = enhancedMode, onCheckedChange = { enhancedMode = it },modifier = Modifier
                        .align(Alignment.CenterVertically) // 垂直居中对齐
                        .padding(end = 20.dp))
                }
                Row(modifier = Modifier
                    .fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween) {
                    // 你的文本列
                    Column(modifier = Modifier
                        .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)) {
                        Text(text = stringResource(id = R.string.bypassBlock), fontSize = 19.sp)
                        Text(text = stringResource(id = R.string.bypassBlock_summary), fontSize = 12.sp)
                    }
                    Switch(checked = bypassBlock, onCheckedChange = { bypassBlock = it },modifier = Modifier
                        .align(Alignment.CenterVertically) // 垂直居中对齐
                        .padding(end = 20.dp))
                }
                Row(modifier = Modifier
                    .fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween) {
                    // 你的文本列
                    Column(modifier = Modifier
                        .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)) {
                        Text(text = stringResource(id = R.string.shared_user_title), fontSize = 19.sp)
                        Text(text = stringResource(id = R.string.shared_user_summary), fontSize = 12.sp)
                    }
                    Switch(checked = shared_user, onCheckedChange = { shared_user = it },modifier = Modifier
                        .align(Alignment.CenterVertically) // 垂直居中对齐
                        .padding(end = 20.dp))
                }
                Row(modifier = Modifier
                    .fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween) {
                    // 你的文本列
                    Column(modifier = Modifier
                        .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)) {
                        Text(text = stringResource(id = R.string.disable_verification_agent_title), fontSize = 19.sp)
                        Text(text = stringResource(id = R.string.disable_verification_agent_summary), fontSize = 12.sp)
                    }
                    Switch(checked = disable_verification_agent, onCheckedChange = { disable_verification_agent = it },modifier = Modifier
                        .align(Alignment.CenterVertically) // 垂直居中对齐
                        .padding(end = 20.dp))
                }
            }
        }
    }
}
