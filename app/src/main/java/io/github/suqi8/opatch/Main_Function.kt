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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController

const val PACKAGE_android = "android"

@Composable
fun Main_Function(navController: NavController) {
    val context = LocalContext.current
    Column {
        GetAppIconAndName(packageName = PACKAGE_android) { appName, icon ->
            Row(modifier = Modifier
                .clickable { //跳转到Fun_android页面
                    navController.navigate("Fun_android")
                    //startActivity(context, Intent(context, Fun_android::class.java), null)
                     }
                .fillMaxWidth(),verticalAlignment =  Alignment.CenterVertically) {
                Image(bitmap = icon, contentDescription = "App Icon", modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp))
                Column(verticalArrangement =  Arrangement.Center, modifier = Modifier.padding(start = 16.dp)) {
                    Text(text = appName)
                    Text(text = PACKAGE_android)
                }
            }
        }
    }
}

