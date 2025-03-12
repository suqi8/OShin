package com.suqi8.oshin.ui.activity.funlistui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.highcapable.yukihookapi.hook.factory.prefs
import top.yukonga.miuix.kmp.extra.SuperDropdown

@Composable
fun FunDropdown(title: String, summary: String? = null, category: String, key: String, selectedList: List<String>, defValue: Int = 0, onCheckedChange: ((Int) -> Unit)? = null) {
    val context = LocalContext.current
    val selectedOption = remember { mutableIntStateOf(context.prefs(category).getInt(key, defValue)) }
    SuperDropdown(
        title = title,
        summary = summary,
        items = selectedList,
        selectedIndex = selectedOption.intValue,
        onSelectedIndexChange = {
            selectedOption.intValue = it
            context.prefs(category).edit { putInt(key, it) }
            onCheckedChange?.invoke(it)
        }
    )
}
