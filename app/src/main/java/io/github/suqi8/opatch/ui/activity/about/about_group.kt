package io.github.suqi8.opatch.ui.activity.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
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
fun about_group(navController: NavController) {
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
            title = stringResource(id = R.string.official_channel),
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
                SmallTitle(text = "Telegram")
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp)
                ) {
                    SuperArrow(title = stringResource(id = R.string.official_channel),
                        onClick = {
                            val telegramIntent = Intent(Intent.ACTION_VIEW)
                            telegramIntent.data = Uri.parse("tg://resolve?domain=OPatchA")
                            // 检查是否安装了 Telegram 应用
                            if (telegramIntent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(telegramIntent)
                            } else {
                                // 如果未安装 Telegram，可以显示一个提示或打开 Telegram 网页版
                                val webIntent =
                                    Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/OPatchA"))
                                context.startActivity(webIntent)
                            }
                        })
                    addline()
                    SuperArrow(title = stringResource(id = R.string.discussion_group),
                        onClick = {
                            val telegramIntent = Intent(Intent.ACTION_VIEW)
                            telegramIntent.data = Uri.parse("tg://resolve?domain=OPatchB")
                            // 检查是否安装了 Telegram 应用
                            if (telegramIntent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(telegramIntent)
                            } else {
                                // 如果未安装 Telegram，可以显示一个提示或打开 Telegram 网页版
                                val webIntent =
                                    Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/OPatchB"))
                                context.startActivity(webIntent)
                            }
                        })
                    addline()
                    SuperArrow(title = stringResource(id = R.string.auto_build_release),
                        onClick = {
                            val telegramIntent = Intent(Intent.ACTION_VIEW)
                            telegramIntent.data = Uri.parse("tg://resolve?domain=OPatchC")
                            // 检查是否安装了 Telegram 应用
                            if (telegramIntent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(telegramIntent)
                            } else {
                                // 如果未安装 Telegram，可以显示一个提示或打开 Telegram 网页版
                                val webIntent =
                                    Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/OPatchC"))
                                context.startActivity(webIntent)
                            }
                        })
                }
                SmallTitle(text = "QQ")
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp)
                ) {
                    SuperArrow(title = stringResource(id = R.string.discussion_group),
                        onClick = {
                            val qqIntent = Intent(Intent.ACTION_VIEW)
                            // 使用 mqqwpa 协议来打开 QQ 群
                            qqIntent.data =
                                Uri.parse("mqqapi://card/show_pslcard?src_type=internal&version=1&uin=740266099&card_type=group&source=qrcode")
                            // 检查是否安装了 QQ 应用
                            if (qqIntent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(qqIntent)
                            } else {
                                val webIntent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=dbP78P2qCYuR2RxGtwmwCrlMCsh2MB2N&authKey=uTkJAGf0gg7%2Fx%2B3OBPrf%2F%2FnyZY2ntPNvnz6%2BTUo%2BHa0Pe%2F%2FqtXvK%2BSJ3%2B4PS0zbO&noverify=0&group_code=740266099")
                                )
                                context.startActivity(webIntent)
                            }
                        })
                }
                Spacer(
                    Modifier.height(
                        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                    )
                )
            }
        }
    }
}
