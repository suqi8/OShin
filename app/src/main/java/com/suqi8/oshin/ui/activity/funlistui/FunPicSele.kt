package com.suqi8.oshin.ui.activity.funlistui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
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
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.BasicComponent
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
                requestPermissions(context) {
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

fun requestPermissions(context: Context, onGranted: () -> Unit = {}) {
    val permissions = arrayOf(
        Permission.MANAGE_EXTERNAL_STORAGE
    )
    XXPermissions.with(context)
        .permission(*permissions)
        .request(object : OnPermissionCallback {
            override fun onGranted(grantedPermissions: MutableList<String>, allGranted: Boolean) {
                if (allGranted) {
                    onGranted()
                } else {
                    toast(
                        context,
                        "获取部分权限成功，但部分权限未正常授予\n这可能会导致部分功能无法正常使用"
                    )
                }
            }

            override fun onDenied(deniedPermissions: MutableList<String>, doNotAskAgain: Boolean) {
                if (doNotAskAgain) {
                    toast(context, "被永久拒绝授权，请手动授予读取和写入文件权限")
                    // 如果权限被永久拒绝，重定向到设置
                    XXPermissions.startPermissionActivity(context, deniedPermissions)
                } else {
                    toast(context, "获取读取和写入文件权限失败")
                }
            }
        })

}

fun toast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
