package com.suqi8.oshin.ui.activity.about

import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.funlistui.FunPage
import com.suqi8.oshin.ui.activity.funlistui.addline
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.BasicComponentColors
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun about_contributors(navController: NavController) {
    FunPage(
        title = stringResource(id = R.string.contributors),
        navController = navController
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            color = MiuixTheme.colorScheme.primaryVariant.copy(alpha = 0.1f)
        ) {
            BasicComponent(
                summary = stringResource(R.string.thanks_contributors),
                summaryColor = BasicComponentColors(
                    color = MiuixTheme.colorScheme.primaryVariant,
                    disabledColor = MiuixTheme.colorScheme.primaryVariant
                )
            )
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 6.dp)
        ) {
            item(
                name = "酸奶",
                coolapk = "Stracha酸奶菌",
                coolapkid = 15225420,
                github = "suqi8"
            )
            addline()
            item(
                name = "YuKong_A",
                github = "YuKongA"
            )
            addline()
            item(
                name = "天伞桜",
                coolapk = "天伞桜",
                coolapkid = 540690
            )
            addline()
            item(
                name = "shadow3",
                github = "shadow3aaa"
            )
            addline()
            item(
                name = "凌逸",
                coolapk = "网恋秀牛被骗",
                coolapkid = 34081897
            )
        }
    }
}

@Composable
private fun item(
    name: String,
    coolapk: String? = null,
    coolapkid: Int? = null,
    github: String? = null
) {
    val context = LocalContext.current
    val showtwo = remember { mutableStateOf(false) }
    val toastMessage = stringResource(R.string.please_install_cool_apk)
    SuperArrow(title = name,
        summary = buildString {
            coolapk?.let {
                append("${stringResource(R.string.coolapk)}@$it")
            }
            if (coolapk != null && github != null) {
                append(" | ")
            }
            github?.let {
                append("Github@$it")
            }
        },
        onClick = {
            if (coolapk != null && github != null) {
                showtwo.value = !showtwo.value
            } else if (coolapk != null) {
                val coolApkUri = "coolmarket://u/${coolapkid}".toUri()
                val intent = Intent(Intent.ACTION_VIEW, coolApkUri)

                try {
                    // 尝试启动酷安应用
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    // 如果酷安未安装，则提示用户
                    Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
                }
            } else if (github != null) {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    "https://github.com/${github}".toUri()
                )
                context.startActivity(intent)
            }
        }
    )
    AnimatedVisibility(showtwo.value) {
        Card(
            color = MiuixTheme.colorScheme.secondaryContainer,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
        ) {
            SuperArrow(title = stringResource(R.string.coolapk), leftAction = {
                Image(
                    painter = painterResource(R.drawable.coolapk),
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .padding(end = 8.dp),
                    colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurface)
                )
            },
                onClick = {
                    coolapkid?.let {
                        val coolApkUri = "coolmarket://u/${it}".toUri()
                        val intent = Intent(Intent.ACTION_VIEW, coolApkUri)

                        try {
                            // 尝试启动酷安应用
                            context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            // 如果酷安未安装，则提示用户
                            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
            addline()
            SuperArrow(title = "Github", leftAction = {
                Image(
                    painter = painterResource(R.drawable.github),
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .padding(end = 8.dp),
                    colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurface)
                )
            },
                onClick = {
                    github?.let {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            "https://github.com/${it}".toUri()
                        )
                        context.startActivity(intent)
                    }
                }
            )
        }
    }
}
