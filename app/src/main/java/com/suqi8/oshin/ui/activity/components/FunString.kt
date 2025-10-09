package com.suqi8.oshin.ui.activity.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
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
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.extra.SuperDialog

@Composable
fun funString(
    title: String,
    summary: String?,
    value: String,
    onValueChange: (String) -> Unit,
    nullable: Boolean = false,
    externalPadding: PaddingValues = PaddingValues(0.dp)
) {
    val showDialog = remember { mutableStateOf(false) }
    val cacheValue = remember { mutableStateOf(value) }

    // 当弹窗打开时，确保它显示的是最新的外部传入值
    LaunchedEffect(showDialog.value) {
        if (showDialog.value) {
            cacheValue.value = value
        }
    }

    funArrow(
        title = title,
        summary = summary,
        rightText = value,
        externalPadding = externalPadding,
        onClick = { showDialog.value = true }
    )

    SuperDialog(
        show = showDialog,
        title = stringResource(R.string.settings) + " " + title,
        summary = summary,
        onDismissRequest = { showDialog.value = false }
    ) {
        TextField(
            value = cacheValue.value,
            onValueChange = { cacheValue.value = it },
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
                onClick = { showDialog.value = false }
            )
            Spacer(Modifier.width(12.dp))
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.ok),
                colors = ButtonDefaults.textButtonColorsPrimary(),
                enabled = (if (nullable) true else cacheValue.value.isNotEmpty()),
                onClick = {
                    onValueChange(cacheValue.value) // 通过回调通知 ViewModel
                    showDialog.value = false
                }
            )
        }
    }
}
