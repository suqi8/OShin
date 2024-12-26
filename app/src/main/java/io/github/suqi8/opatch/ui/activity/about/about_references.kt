package io.github.suqi8.opatch.ui.activity.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.highcapable.yukihookapi.hook.factory.prefs
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import io.github.suqi8.opatch.R
import io.github.suqi8.opatch.addline
import top.yukonga.miuix.kmp.basic.BasicComponentColors
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun about_references(navController: NavController) {
    val context = LocalContext.current
    val one = MiuixScrollBehavior(top.yukonga.miuix.kmp.basic.rememberTopAppBarState())
    val alpha = context.prefs("settings").getFloat("AppAlpha", 0.75f)
    val blurRadius: Dp = context.prefs("settings").getInt("AppblurRadius", 25).dp
    val noiseFactor = context.prefs("settings").getFloat("AppnoiseFactor", 0f)
    val containerColor: Color = MiuixTheme.colorScheme.background
    val hazeState = remember { HazeState() }
    val hazeStyle = remember(containerColor, alpha, blurRadius, noiseFactor) {
        HazeStyle(
            backgroundColor = containerColor,
            tint = HazeTint(containerColor.copy(alpha)),
            blurRadius = blurRadius,
            noiseFactor = noiseFactor
        )
    }

    Scaffold(topBar = {
        TopAppBar(
            title = stringResource(id = R.string.references),
            color = Color.Transparent,
            modifier = Modifier.hazeChild(
                state = hazeState,
                style = hazeStyle
            ),
            scrollBehavior = one,
            navigationIcon = {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier.padding(start = 18.dp)
                ) {
                    Icon(
                        imageVector = MiuixIcons.ArrowBack,
                        contentDescription = null,
                        tint = MiuixTheme.colorScheme.onBackground
                    )
                }
            }
        )
    }) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(top = padding.calculateTopPadding()),
            topAppBarScrollBehavior = one, modifier = Modifier
                .fillMaxSize()
                .haze(state = hazeState)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    color = MiuixTheme.colorScheme.primaryVariant.copy(alpha = 0.1f)
                ) {
                    top.yukonga.miuix.kmp.basic.BasicComponent(
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
