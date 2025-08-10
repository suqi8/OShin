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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import kotlin.reflect.KClass

@Composable
fun FunSlider(
    title: String,
    summary: String? = null,
    category: String,
    key: String,
    defValue: Any = 0,
    endtype: String? = "",
    max: Float = 1f,
    min: Float = 0f,
    decimalPlaces: Int = 2,
    titlecolor: Color = SuperDialogDefaults.titleColor()
) {
    val context = LocalContext.current
    val prefs = context.prefs(category)

    val type = when (defValue) {
        is Int -> Int::class
        is Float -> Float::class
        is Boolean -> Boolean::class
        else -> Int::class
    }

    val defaultString = when (defValue) {
        is Int -> prefs.getInt(key, defValue).toString()
        is Float -> prefs.getFloat(key, defValue).toString()
        is Boolean -> prefs.getBoolean(key, defValue).toString()
        else -> "0"
    }

    val value = remember { mutableStateOf(defaultString) }
    val cacheValue = remember { mutableStateOf(value.value) }
    var showDialog = remember { mutableStateOf(false) }

    FunArrow(
        title = title,
        summary = summary,
        rightText = value.value + endtype,
        onClick = { showDialog.value = true }
    )

    SuperDialog(
        show = showDialog,
        title = stringResource(R.string.settings) + " " + title,
        titleColor = titlecolor,
        summary = summary,
        onDismissRequest = { showDialog.value = false }
    ) {
        SliderWithInput(
            value = cacheValue.value,
            type = type,
            decimalPlaces = decimalPlaces,
            onValueChange = { cacheValue.value = it },
            min = min,
            max = max
        )

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.cancel),
                onClick = { showDialog.value = false }
            )
            Spacer(Modifier.width(12.dp))
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.ok),
                colors = ButtonDefaults.textButtonColorsPrimary(),
                enabled = cacheValue.value.isNotEmpty(),
                onClick = {
                    value.value = cacheValue.value
                    showDialog.value = false
                    prefs.edit {
                        when (defValue) {
                            is Int -> putInt(key, cacheValue.value.toInt())
                            is Float -> putFloat(key, cacheValue.value.toFloat())
                            is Boolean -> putBoolean(key, cacheValue.value.toBoolean())
                            else -> putInt(key, cacheValue.value.toInt())
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun SliderWithInput(
    value: String,
    type: KClass<*>,
    decimalPlaces: Int,
    onValueChange: (String) -> Unit,
    min: Float,
    max: Float
) {
    val sliderPosition = remember { mutableStateOf(value.toFloatOrNull() ?: 0f) }

    Slider(
        progress = sliderPosition.value,
        onProgressChange = {
            sliderPosition.value = it
            onValueChange(
                if (type == Int::class) {
                    it.toInt().toString()
                } else {
                    String.format("%.${decimalPlaces}f", it)
                }
            )
        },
        minValue = min,
        maxValue = max,
        effect = true,
        decimalPlaces = decimalPlaces
    )

    Spacer(Modifier.height(12.dp))

    TextField(
        value = value,
        onValueChange = { newValue ->
            onValueChange(newValue)
            sliderPosition.value = newValue.toFloatOrNull() ?: 0f
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
    )
}
