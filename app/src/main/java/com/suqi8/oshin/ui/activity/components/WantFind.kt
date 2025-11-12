package com.suqi8.oshin.ui.activity.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suqi8.oshin.R
import com.suqi8.oshin.models.RelatedLinks
import com.suqi8.oshin.ui.nav.path.NavPath
import com.suqi8.oshin.ui.nav.transition.NavTransitionType
import com.suqi8.oshin.ui.nav.ui.NavStackScope
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

data class WantFind(
    val title: String,
    val category: String
)
@Composable
fun wantFind(
    links: List<RelatedLinks.Link>,
    navPath: NavPath,
    navStackScope: NavStackScope,
) {
    Card(colors = CardDefaults.defaultColors(MiuixTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.75f)),
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 12.dp)) {
        Surface(color = Color.Transparent) {
            Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)) {
                Text(stringResource(R.string.prompt_search_other_settings), fontSize = 16.sp, color = MiuixTheme.colorScheme.onSurfaceContainerHigh)
                Spacer(modifier = Modifier.height(6.dp))
                links.forEach { link ->
                    Text(stringResource(link.titleRes), color = MiuixTheme.colorScheme.primary, modifier = Modifier.padding(top = 6.dp, bottom = 6.dp)
                        .clickable {
                            navPath.push(item = "feature/${link.route}", navTransitionType = NavTransitionType.Zoom)
                        },fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
