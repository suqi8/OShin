package io.github.suqi8.opatch

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController

@Composable
fun Main_Function(navController: NavController) {
    val context = LocalContext.current
    Column {
        FunctionApp("android", "Fun_android", navController)
        FunctionApp("com.android.systemui", "Fun_com_android_systemui", navController)
        FunctionApp("com.android.phone", "Fun_com_android_phone", navController)
        FunctionApp("com.android.contacts", "Fun_com_android_contacts", navController)
        FunctionApp("com.android.settings", "Fun_com_android_settings", navController)
        FunctionApp("com.android.calendar", "Fun_com_android_calendar", navController)
        FunctionApp("com.android.email", "Fun_com_android_email", navController)

        FunctionApp("com.android.browser", "Fun_com_android_browser", navController)

        FunctionApp("com.android.dialer", "Fun_com_android_dialer", navController)

        FunctionApp("com.android.deskclock", "Fun_com_android_deskclock", navController)

        FunctionApp("com.android.gallery3d", "Fun_com_android_gallery3d", navController)

        FunctionApp("com.android.music", "Fun_com_android_music", navController)

        FunctionApp("com.android.providers.downloads.ui", "Fun_com_android_providers_downloads_ui", navController)

        FunctionApp("com.android.providers.media", "Fun_com_android_providers_media", navController)
    }
}

@Composable
fun FunctionApp(packageName: String, activityName: String, navController: NavController) {
    GetAppIconAndName(packageName = packageName) { appName, icon ->
        if (appName != null) {
            Row(modifier = Modifier
                .clickable {
                    navController.navigate(activityName)
                }
                .fillMaxWidth(),verticalAlignment =  Alignment.CenterVertically) {
                Image(bitmap = icon, contentDescription = "App Icon", modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                    .size(48.dp))
                Column(verticalArrangement =  Arrangement.Center, modifier = Modifier.padding(start = 16.dp)) {
                    Text(text = appName)
                    Text(text = packageName)
                }
            }
        }
    }
}
