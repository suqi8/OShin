package com.suqi8.oshin.ui.about

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.components.Card
import com.suqi8.oshin.ui.activity.components.FunPage
import com.suqi8.oshin.ui.activity.components.addline
import com.suqi8.oshin.ui.activity.components.funArrow
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun about_group(
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val context = LocalContext.current

    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())

    FunPage(
        title = stringResource(id = R.string.discussion_group),
        navController = navController,
        scrollBehavior = scrollBehavior,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        animationKey = "about_group"
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .overScrollVertical()
                .scrollEndHaptic()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = padding
        ) {
            item { SmallTitle(text = "Telegram") }
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp)
                ) {
                    funArrow(title = stringResource(id = R.string.official_channel),
                        onClick = {
                            val telegramIntent = Intent(Intent.ACTION_VIEW)
                            telegramIntent.data = "tg://resolve?domain=OPatchA".toUri()
                            // 检查是否安装了 Telegram 应用
                            if (telegramIntent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(telegramIntent)
                            } else {
                                // 如果未安装 Telegram，可以显示一个提示或打开 Telegram 网页版
                                val webIntent =
                                    Intent(Intent.ACTION_VIEW, "https://t.me/OPatchA".toUri())
                                context.startActivity(webIntent)
                            }
                        })
                    addline()
                    funArrow(title = stringResource(id = R.string.discussion_group),
                        onClick = {
                            val telegramIntent = Intent(Intent.ACTION_VIEW)
                            telegramIntent.data = "tg://resolve?domain=OPatchB".toUri()
                            // 检查是否安装了 Telegram 应用
                            if (telegramIntent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(telegramIntent)
                            } else {
                                // 如果未安装 Telegram，可以显示一个提示或打开 Telegram 网页版
                                val webIntent =
                                    Intent(Intent.ACTION_VIEW, "https://t.me/OPatchB".toUri())
                                context.startActivity(webIntent)
                            }
                        })
                    addline()
                    funArrow(title = stringResource(id = R.string.auto_build_release),
                        onClick = {
                            val telegramIntent = Intent(Intent.ACTION_VIEW)
                            telegramIntent.data = "tg://resolve?domain=OPatchC".toUri()
                            // 检查是否安装了 Telegram 应用
                            if (telegramIntent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(telegramIntent)
                            } else {
                                // 如果未安装 Telegram，可以显示一个提示或打开 Telegram 网页版
                                val webIntent =
                                    Intent(Intent.ACTION_VIEW, "https://t.me/OPatchC".toUri())
                                context.startActivity(webIntent)
                            }
                        })
                }
            }
            item { SmallTitle(text = "QQ") }
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp)
                ) {
                    funArrow(title = stringResource(id = R.string.discussion_group),
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
            }
        }
    }
}
