package com.suqi8.oshin.ui.activity.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun funPicSele(
    title: String,
    summary: String?,
    imageBitmap: ImageBitmap?,
    externalPadding: PaddingValues = PaddingValues(0.dp),
    onImageSelected: (Uri?) -> Unit
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            // 当用户选择图片后，通过回调将 Uri 通知给 ViewModel
            onImageSelected(uri)
        }
    )

    BasicComponent(
        title = title,
        summary = summary,
        externalPadding = externalPadding,
        rightActions = {
            imageBitmap?.let { bitmap ->
                Image(
                    painter = BitmapPainter(bitmap),
                    contentDescription = title,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(ContinuousRoundedRectangle(8.dp))
                )
            }
        },
        onClick = {
            imagePickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    )
}
