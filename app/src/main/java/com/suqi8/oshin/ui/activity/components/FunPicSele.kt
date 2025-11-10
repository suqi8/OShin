package com.suqi8.oshin.ui.activity.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.hjq.permissions.permission.PermissionLists
import com.kyant.capsule.ContinuousRoundedRectangle
import com.suqi8.oshin.utils.requestPermissions

@Composable
fun FunPicSele(
    title: String,
    summary: String?,
    imageBitmap: ImageBitmap?,
    // [修改] 替换 externalPadding 为 position
    position: CouiListItemPosition = CouiListItemPosition.Middle,
    onImageSelected: (Uri?) -> Unit
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            onImageSelected(uri)
        }
    )
    val context = LocalContext.current
    // 权限请求逻辑保持不变 (注意：requestPermissions 需要确保存在)
    LaunchedEffect(Unit) {
        requestPermissions(context, PermissionLists.getManageExternalStoragePermission()) {}
    }

    BasicComponent(
        title = title,
        summary = summary,
        // [修改] 传递 position
        position = position,
        rightActions = {
            imageBitmap?.let { bitmap ->
                Image(
                    painter = BitmapPainter(bitmap),
                    contentDescription = title,
                    modifier = Modifier
                        .size(48.dp)
                        // 使用平滑圆角，更符合 ColorOS 风格 (如果 ContinuousRoundedRectangle 不可用，可用 RoundedCornerShape(8.dp) 代替)
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
