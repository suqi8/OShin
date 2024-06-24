package io.github.suqi8.opatch

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Main_Function() {
    Column {
        GetAppIconAndName(packageName = "android") { appName, icon ->
            Row(modifier = Modifier.padding(16.dp),verticalAlignment =  Alignment.CenterVertically) {
                Image(bitmap = icon, contentDescription = "App Icon")
                Column(verticalArrangement =  Arrangement.Center, modifier = Modifier.padding(start = 16.dp)) {
                    Text(text = appName)
                    Text(text = "Package Name: android")
                }
            }
        }
    }
}

