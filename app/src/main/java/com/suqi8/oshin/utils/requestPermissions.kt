package com.suqi8.oshin.utils

import android.content.Context
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions

fun requestPermissions(context: Context, permissions: Array<String>, onGranted: () -> Unit = {}) {
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
