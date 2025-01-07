package com.suqi8.oshin.ui.activity.funlistui

import android.content.Context
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.R
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.extra.SuperDialogDefaults
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog

@Composable
fun FunSlider(title: String, summary: String? = null, category: String, key: String, defValue: Any = 0, endtype: String? = "", max: Float = 1f, min: Float = 0f,decimalPlaces: Int = 2, titlecolor: Color = SuperDialogDefaults.titleColor(), context: Context) {
    val type = when (defValue) {
        is Int -> Int::class
        is Float -> Float::class
        is Boolean -> Boolean::class
        else -> Int::class
    }
    val value = remember { mutableStateOf(when (type) {
        Int::class -> context.prefs(category).getInt(key, defValue as Int)
        Float::class -> context.prefs(category).getFloat(key, defValue as Float)
        Boolean::class -> context.prefs(category).getBoolean(key, defValue as Boolean)
        else -> context.prefs(category).getInt(key, defValue as Int)
    }.toString()) }
    val cachevalue = remember { mutableStateOf(value.value) }
    val Dialog = remember { mutableStateOf(false) }
    SuperArrow(
        title = title,
        summary = summary,
        rightText = value.value + endtype,
        onClick = {
            Dialog.value = true
        }
    )
    SuperDialog(
        show = Dialog,
        title = stringResource(R.string.settings) + " " + title,
        titleColor = titlecolor,
        summary = summary,
        onDismissRequest = {
            dismissDialog(Dialog)
        }
    ) {
        Slider(
            progress = if (cachevalue.value.isNotEmpty()) cachevalue.value.toFloat() else 0f,
            onProgressChange = {
                cachevalue.value = it.toString()
            },
            minValue = min,
            maxValue = max,
            effect = true,
            decimalPlaces = decimalPlaces
        )
        Spacer(Modifier.height(12.dp))
        TextField(
            value = cachevalue.value,
            onValueChange = { cachevalue.value = it},
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
                    dismissDialog(Dialog)
                }
            )
            Spacer(Modifier.width(12.dp))
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.ok),
                colors = ButtonDefaults.textButtonColorsPrimary(),
                enabled = (cachevalue.value.isNotEmpty()),
                onClick = {
                    dismissDialog(Dialog)
                    value.value = cachevalue.value
                    when (defValue) {
                        is Int -> context.prefs(category).edit { putInt(key, cachevalue.value.toInt()) }
                        is Float -> context.prefs(category)
                            .edit { putFloat(key, cachevalue.value.toFloat()) }
                        is Boolean -> context.prefs(category)
                            .edit { putBoolean(key, cachevalue.value.toBoolean()) }
                        else -> context.prefs(category).edit { putInt(key, cachevalue.value.toInt()) }
                    }
                }
            )
        }
    }
}
