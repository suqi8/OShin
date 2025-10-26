package com.suqi8.oshin.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.R
import com.suqi8.oshin.utils.executeCommand
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.extra.SuperDialog
import kotlin.system.exitProcess


val lspVersion = mutableStateOf("")

@Composable
fun PrivacyDialog() {
    val context = LocalContext.current
    val isPrivacyEnabled = remember { mutableStateOf(context.prefs("settings").getBoolean("privacy", true)) }

    LaunchedEffect(isPrivacyEnabled.value) {
        if (!isPrivacyEnabled.value) {
            UMConfigure.init(context, "67c7dea68f232a05f127781e", "android", UMConfigure.DEVICE_TYPE_PHONE, "")
            withContext(Dispatchers.IO) {
                val lsposedVersionName = executeCommand("awk -F= '/version=/ {print $2}' /data/adb/modules/zygisk_lsposed/module.prop")
                lspVersion.value = lsposedVersionName
                val savedLspVersion = context.prefs("settings").getString("privacy_lspvername", "")
                if (lsposedVersionName.isNotEmpty() && lsposedVersionName != savedLspVersion) {
                    val eventData = mapOf("version_name" to lsposedVersionName)
                    MobclickAgent.onEvent(context, "lsposed_usage", eventData)
                    context.prefs("settings").edit {
                        putString("privacy_lspvername", lsposedVersionName)
                    }
                }
            }
        }
    }

    SuperDialog(
        show = isPrivacyEnabled,
        title = stringResource(R.string.privacy_title),
        onDismissRequest = {}
    ) {
        Text(stringResource(R.string.privacy_content))
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.exit),
                onClick = {
                    exitProcess(0)
                }
            )
            Spacer(Modifier.width(12.dp))
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.ok),
                colors = ButtonDefaults.textButtonColorsPrimary(),
                onClick = {
                    isPrivacyEnabled.value = false
                    context.prefs("settings").edit { putBoolean("privacy", false) }
                }
            )
        }
    }
}
