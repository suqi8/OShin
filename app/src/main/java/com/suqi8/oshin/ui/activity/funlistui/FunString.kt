package com.suqi8.oshin.ui.activity.funlistui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.R
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperDialog

@Composable
fun FunString(title: String, summary: String? = null, category: String, key: String, defValue: String = "", nullable: Boolean = false) {
    val context = LocalContext.current

    val value = remember { mutableStateOf(context.prefs(category).getString(key, defValue)) }
    val cachevalue = remember { mutableStateOf(value.value) }
    val Dialog = remember { mutableStateOf(false) }
    SuperArrow(
        title = title,
        summary = summary,
        rightText = value.value,
        onClick = {
            Dialog.value = true
        }
    )
    SuperDialog(
        show = Dialog,
        title = stringResource(R.string.settings) + " " + title,
        summary = summary,
        onDismissRequest = {
            Dialog.value = false
        }
    ) {
        TextField(
            value = cachevalue.value,
            onValueChange = { cachevalue.value = it },
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
                    Dialog.value = false
                }
            )
            Spacer(Modifier.width(12.dp))
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.ok),
                colors = ButtonDefaults.textButtonColorsPrimary(),
                enabled = (if (nullable) true else cachevalue.value.isNotEmpty()),
                onClick = {
                    Dialog.value = false
                    value.value = cachevalue.value
                    context.prefs(category).edit { putString(key, cachevalue.value) }
                }
            )
        }
    }
}
