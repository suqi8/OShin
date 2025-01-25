package com.suqi8.oshin.ui.activity.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.funlistui.FunPage
import com.suqi8.oshin.ui.activity.funlistui.addline
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.BasicComponentColors
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun about_references(navController: NavController) {
    FunPage(
        title = stringResource(id = R.string.references),
        navController = navController
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            color = MiuixTheme.colorScheme.primaryVariant.copy(alpha = 0.1f)
        ) {
            BasicComponent(
                summary = stringResource(R.string.thanks_open_source_projects),
                summaryColor = BasicComponentColors(
                    color = MiuixTheme.colorScheme.primaryVariant,
                    disabledColor = MiuixTheme.colorScheme.primaryVariant
                )
            )
        }
        SmallTitle(text = stringResource(R.string.open_source_project))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 6.dp)
        ) {
            about_references_item(
                name = "Android",
                username = "Android Open Source Project, Google Inc.",
                url = "https://source.android.google.cn/license",
                license = "Apache-2.0"
            )
            addline()
            about_references_item(
                name = "AndroidX",
                username = "Android Open Source Project, Google Inc.",
                url = "https://github.com/androidx/androidx",
                license = "Apache-2.0"
            )
            addline()
            about_references_item(
                name = "CorePatch",
                username = "LSPosed",
                url = "https://github.com/LSPosed/CorePatch",
                license = "GPL-2.0"
            )
            addline()
            about_references_item(
                name = "Gson",
                username = "Android Open Source Project, Google Inc.",
                url = "https://github.com/google/gson",
                license = "Apache-2.0"
            )
            addline()
            about_references_item(
                name = "Kotlin",
                username = "JetBrains",
                url = "https://github.com/JetBrains/kotlin"
            )
            addline()
            about_references_item(
                name = "Xposed",
                username = "rovo89, Tungstwenty",
                url = "https://github.com/rovo89/XposedBridge"
            )
            addline()
            about_references_item(
                name = "YukiHookAPI",
                username = "HighCapable",
                url = "https://github.com/HighCapable/YukiHookAPI",
                license = "Apache-2.0"
            )
            addline()
            about_references_item(
                name = "Compose",
                username = "JetBrains",
                url = "https://github.com/JetBrains/compose",
                license = "Apache-2.0"
            )
            addline()
            about_references_item(
                name = "Miuix",
                username = "YuKongA",
                url = "https://github.com/miuix-kotlin-multiplatform/miuix",
                license = "Apache-2.0"
            )
            addline()
            about_references_item(
                name = "Magisk",
                username = "topjohnwu",
                url = "https://github.com/topjohnwu/Magisk",
                license = "GPL-3.0"
            )
            addline()
            about_references_item(
                name = "LSPosed",
                username = "LSPosed",
                url = "https://github.com/LSPosed/LSPosed",
                license = "GPL-3.0"
            )
            addline()
            about_references_item(
                name = "coloros-aod",
                username = "Flyfish233",
                url = "https://github.com/Flyfish233/coloros-aod"
            )
        }
        SmallTitle(text = stringResource(R.string.closed_source_project))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 6.dp)
        ) {
            about_references_item(
                name = "LuckyTool",
                username = "luckyzyx",
                url = "https://github.com/Xposed-Modules-Repo/com.luckyzyx.luckytool"
            )
        }
    }
}

@Composable
fun about_references_item(
    name: String,
    username: String,
    url: String? = null,
    license: String? = null
) {
    val context = LocalContext.current
    SuperArrow(title = name,
        summary = username + if (license != null) " | $license" else " | " + stringResource(R.string.no_license),
        onClick = {
            if (url != null) {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(url)
                )
                context.startActivity(intent)
            }
        }
    )
}
