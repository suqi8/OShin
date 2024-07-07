package io.github.suqi8.opatch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

object Global {
    var downgr = false
    var authcreak = false
    var digestCreak = false
    var UsePreSig = false
    var enhancedMode = false
    var bypassBlock = false
    var shared_user = false
    var disable_verification_agent = false
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Fun_android_package_manager_services(navController: NavController) {
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
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 你的文本列
                    Column(
                        modifier = Modifier
                            .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)
                    ) {
                        Text(text = stringResource(id = R.string.downgr), fontSize = 19.sp)
                        Text(text = stringResource(id = R.string.downgr_summary), fontSize = 12.sp)
                    }
                    Switch(
                        checked = Global.downgr, onCheckedChange = {
                            Global.downgr = it
                        }, modifier = Modifier
                            .align(Alignment.CenterVertically) // 垂直居中对齐
                            .padding(end = 20.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 你的文本列
                    Column(
                        modifier = Modifier
                            .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)
                    ) {
                        Text(text = stringResource(id = R.string.authcreak), fontSize = 19.sp)
                        Text(
                            text = stringResource(id = R.string.authcreak_summary),
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = Global.authcreak,
                        onCheckedChange = { Global.authcreak = it },
                        modifier = Modifier
                            .align(Alignment.CenterVertically) // 垂直居中对齐
                            .padding(end = 20.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 你的文本列
                    Column(
                        modifier = Modifier
                            .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)
                    ) {
                        Text(text = stringResource(id = R.string.digestCreak), fontSize = 19.sp)
                        Text(
                            text = stringResource(id = R.string.digestCreak_summary),
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = Global.digestCreak,
                        onCheckedChange = { Global.digestCreak = it },
                        modifier = Modifier
                            .align(Alignment.CenterVertically) // 垂直居中对齐
                            .padding(end = 20.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 你的文本列
                    Column(
                        modifier = Modifier
                            .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)
                    ) {
                        Text(text = stringResource(id = R.string.UsePreSig), fontSize = 19.sp)
                        Text(
                            text = stringResource(id = R.string.UsePreSig_summary),
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = Global.UsePreSig,
                        onCheckedChange = { Global.UsePreSig = it },
                        modifier = Modifier
                            .align(Alignment.CenterVertically) // 垂直居中对齐
                            .padding(end = 20.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 你的文本列
                    Column(
                        modifier = Modifier
                            .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)
                    ) {
                        Text(text = stringResource(id = R.string.enhancedMode), fontSize = 19.sp)
                        Text(
                            text = stringResource(id = R.string.enhancedMode_summary),
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = Global.enhancedMode,
                        onCheckedChange = { Global.enhancedMode = it },
                        modifier = Modifier
                            .align(Alignment.CenterVertically) // 垂直居中对齐
                            .padding(end = 20.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 你的文本列
                    Column(
                        modifier = Modifier
                            .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)
                    ) {
                        Text(text = stringResource(id = R.string.bypassBlock), fontSize = 19.sp)
                        Text(
                            text = stringResource(id = R.string.bypassBlock_summary),
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = Global.bypassBlock,
                        onCheckedChange = { Global.bypassBlock = it },
                        modifier = Modifier
                            .align(Alignment.CenterVertically) // 垂直居中对齐
                            .padding(end = 20.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 你的文本列
                    Column(
                        modifier = Modifier
                            .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.shared_user_title),
                            fontSize = 19.sp
                        )
                        Text(
                            text = stringResource(id = R.string.shared_user_summary),
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = Global.shared_user,
                        onCheckedChange = { Global.shared_user = it },
                        modifier = Modifier
                            .align(Alignment.CenterVertically) // 垂直居中对齐
                            .padding(end = 20.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 你的文本列
                    Column(
                        modifier = Modifier
                            .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.disable_verification_agent_title),
                            fontSize = 19.sp
                        )
                        Text(
                            text = stringResource(id = R.string.disable_verification_agent_summary),
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = Global.disable_verification_agent,
                        onCheckedChange = { Global.disable_verification_agent = it },
                        modifier = Modifier
                            .align(Alignment.CenterVertically) // 垂直居中对齐
                            .padding(end = 20.dp)
                    )
                }
            }
        }
    }
}
