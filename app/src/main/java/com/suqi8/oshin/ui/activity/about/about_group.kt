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
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.extra.SuperArrow

@Composable
fun about_group(navController: NavController) {
    val context = LocalContext.current
    FunPage(
        title = stringResource(id = R.string.discussion_group),
        navController = navController
    ) {
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
    }
}
