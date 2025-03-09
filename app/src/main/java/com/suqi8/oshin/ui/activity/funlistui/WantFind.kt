package com.suqi8.oshin.ui.activity.funlistui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.suqi8.oshin.R
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

data class WantFind(
    val title: String,
    val category: String
)
@Composable
fun WantFind(funclist: List<WantFind>, navController: NavController) {
    val context = LocalContext.current
    Card(color = MiuixTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.75f),
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 12.dp)) {
        Surface(color = Color.Transparent) {
            Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)) {
                Text(stringResource(R.string.prompt_search_other_settings), fontSize = 16.sp, color = MiuixTheme.colorScheme.onSurfaceContainerHigh)
                funclist.forEach {
                    Text(it.title, color = MiuixTheme.colorScheme.primaryVariant, modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
                        .clickable {
                            navController.navigate(it.category)
                        })
                }
            }
        }
    }
}
