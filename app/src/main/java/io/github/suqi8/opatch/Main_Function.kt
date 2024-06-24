package io.github.suqi8.opatch

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable

@Composable
fun Main_Function() {
    Column {

    }
}

private fun getIcon(pakgename: String): Drawable? {
    val pm: PackageManager = getPackageManager()
    try {
        appInfo = pm.getApplicationInfo(pakgename, PackageManager.GET_META_DATA)

        //
//
// 应用名称
// pm.getApplicationLabel(appInfo)

//应用图标
        appIcon = pm.getApplicationIcon(appInfo)
        return appIcon
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return null
}
