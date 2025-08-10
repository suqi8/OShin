package com.suqi8.oshin.ui.activity.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.funlistui.FunPage
import com.suqi8.oshin.ui.activity.funlistui.FunSwich
import com.suqi8.oshin.ui.activity.funlistui.addline
import com.suqi8.oshin.ui.activity.funlistui.Card
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.extra.SuperDialog

@Composable
fun package_manager_services(navController: NavController) {
    val showUsePreSigDialog = remember { mutableStateOf(false) }
    FunPage(
        title = stringResource(id = R.string.package_manager_services),
        appList = listOf("android"),
        navController = navController
    ) {
        Column {
            SmallTitle(
                text = stringResource(R.string.common_settings)
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 6.dp)
            ) {
                FunSwich(
                    title = stringResource(R.string.downgr),
                    summary = stringResource(R.string.downgr_summary),
                    category = "android\\package_manager_services",
                    key = "downgrade"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.authcreak),
                    summary = stringResource(R.string.authcreak_summary),
                    category = "android\\package_manager_services",
                    key = "authcreak"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.digestCreak),
                    summary = stringResource(R.string.digestCreak_summary),
                    category = "android\\package_manager_services",
                    key = "digestCreak"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.UsePreSig),
                    summary = stringResource(R.string.UsePreSig_summary),
                    category = "android\\package_manager_services",
                    key = "UsePreSig",
                    onCheckedChange = {
                        showUsePreSigDialog.value = it
                    }
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.enhancedMode),
                    summary = stringResource(R.string.enhancedMode_summary),
                    category = "android\\package_manager_services",
                    key = "enhancedMode"
                )
            }
            SmallTitle(
                text = stringResource(R.string.other_settings)
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 6.dp)
            ) {
                FunSwich(
                    title = stringResource(R.string.bypassBlock),
                    summary = stringResource(R.string.bypassBlock_summary),
                    category = "android\\package_manager_services",
                    key = "bypassBlock"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.shared_user_title),
                    summary = stringResource(R.string.shared_user_summary),
                    category = "android\\package_manager_services",
                    key = "sharedUser"
                )
                addline()
                FunSwich(
                    title = stringResource(R.string.disable_verification_agent_title),
                    summary = stringResource(R.string.disable_verification_agent_summary),
                    category = "android\\package_manager_services",
                    key = "disableVerificationAgent"
                )
            }
        }
    }
    UsePreSig(showUsePreSigDialog)
}


@Composable
fun UsePreSig(showDialog: MutableState<Boolean>) {
    if (!showDialog.value) return
    SuperDialog(title = stringResource(R.string.warn),
        titleColor = Color.Red,
        summary = stringResource(R.string.usepresig_warn),
        show = showDialog,
        onDismissRequest = {
            showDialog.value = false
        }) {
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.ok),
                enabled = true,
                onClick = {
                    showDialog.value = false
                }
            )
        }
    }
}
