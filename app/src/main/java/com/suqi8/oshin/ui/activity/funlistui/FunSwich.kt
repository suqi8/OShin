package com.suqi8.oshin.ui.activity.funlistui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.highcapable.yukihookapi.hook.factory.prefs
import top.yukonga.miuix.kmp.extra.SuperSwitch

@Composable
fun FunSwich(title: String, summary: String? = null,category: String,key: String,defValue: Boolean = false, onCheckedChange: ((Boolean) -> Unit)? = null) {
    val context = LocalContext.current
    val isChecked = remember { mutableStateOf(context.prefs(category).getBoolean(key, defValue)) }
    SuperSwitch(
        title = title,
        checked = isChecked.value,
        onCheckedChange = {
            context.prefs(category).edit { putBoolean(key, it) }
            isChecked.value = it
            onCheckedChange?.invoke(it)
        },
        summary = summary
    )
}
