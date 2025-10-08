package com.suqi8.oshin.ui.activity.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.suqi8.oshin.R
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.extra.SuperDialog
import java.util.Locale

@Composable
fun funSlider(
    title: String,
    summary: String?,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    unit: String,
    decimalPlaces: Int
) {
    // 1. 弹窗的显示状态，这是临时的UI状态，可以保留在组件内部
    val showDialog = remember { mutableStateOf(false) }

    // 2. 用于在弹窗中临时编辑的值
    val cacheValue = remember { mutableStateOf(value.toString()) }

    // 3. 当弹窗打开时，确保它显示的是最新的外部传入值
    LaunchedEffect(showDialog.value) {
        if (showDialog.value) {
            val formattedValue = if (decimalPlaces <= 0) {
                value.toInt().toString()
            } else {
                String.format(Locale.US, "%.${decimalPlaces}f", value)
            }
            cacheValue.value = formattedValue
        }
    }

    // 格式化当前值以在主页面显示
    val formattedDisplayValue = if (decimalPlaces <= 0) {
        value.toInt().toString()
    } else {
        String.format(Locale.US, "%.${decimalPlaces}f", value)
    }

    // 主页面上的显示项，点击后打开弹窗
    funArrow(
        title = title,
        summary = summary,
        rightText = "$formattedDisplayValue$unit",
        onClick = { showDialog.value = true }
    )

    SuperDialog(
        show = showDialog,
        title = stringResource(R.string.settings) + " " + title,
        summary = summary,
        onDismissRequest = { showDialog.value = false }
    ) {
        SliderWithInput(
            value = cacheValue.value,
            onValueChange = { cacheValue.value = it },
            valueRange = valueRange,
            decimalPlaces = decimalPlaces
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
                enabled = cacheValue.value.toFloatOrNull() != null,
                onClick = {
                    cacheValue.value.toFloatOrNull()?.let {
                        onValueChange(it)
                    }
                    showDialog.value = false
                }
            )
        }
    }
}

@Composable
private fun SliderWithInput(
    value: String,
    onValueChange: (String) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    decimalPlaces: Int
) {
    val sliderPosition = value.toFloatOrNull() ?: valueRange.start

    Column {
        Slider(
            progress = sliderPosition,
            onProgressChange = {
                val newValue = if (decimalPlaces <= 0) {
                    it.toInt().toString()
                } else {
                    String.format(Locale.US, "%.${decimalPlaces}f", it)
                }
                onValueChange(newValue)
            },
            minValue = valueRange.start,
            maxValue = valueRange.endInclusive,
            effect = true
        )

        Spacer(Modifier.height(12.dp))

        TextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )
    }
}
