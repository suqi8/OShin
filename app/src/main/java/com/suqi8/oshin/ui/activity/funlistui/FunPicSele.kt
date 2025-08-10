package com.suqi8.oshin.ui.activity.funlistui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.hjq.permissions.permission.PermissionLists
import com.suqi8.oshin.utils.requestPermissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Composable
fun FunPicSele(title: String? = null, summary: String? = null, category: String, key: String, route: String) {
    val context = LocalContext.current
    var bitmapState by remember { mutableStateOf<Bitmap?>(null) }
    val reloadImg = remember { mutableStateOf(true) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val contentResolver = context.contentResolver

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                selectedImageUri = it
                val targetFile = File(route)
                if (!targetFile.exists()) {
                    targetFile.parentFile?.mkdirs()
                }

                try {
                    contentResolver.openInputStream(uri)?.use { input ->
                        FileOutputStream(targetFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    reloadImg.value = true
                } catch (_: IOException) { }
            }
        }
    )

    LaunchedEffect(reloadImg.value) {
        if (reloadImg.value) {
            withContext(Dispatchers.IO) {
                requestPermissions(context, PermissionLists.getManageExternalStoragePermission()) {
                    reloadImg.value = true
                }
                try {
                    val file = File(route)
                    bitmapState = BitmapFactory.decodeFile(file.absolutePath)
                } catch (_: Exception) { }
            }
            reloadImg.value = false
        }
    }
    BasicComponent(
        title = title,
        summary = summary,
        rightActions = {
            bitmapState?.let { bitmap ->
                Image(
                    painter = BitmapPainter(bitmap.asImageBitmap()),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
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
