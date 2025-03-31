package com.suqi8.oshin.ui.activity.about

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil3.compose.AsyncImage
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
internal fun item(
    name: String,
    coolapk: String? = null,
    coolapkid: Int? = null,
    github: String? = null,
    qq: Long? = null
) {
    val context = LocalContext.current
    var showExtra by remember { mutableStateOf(false) }
    val toastMessage = stringResource(R.string.please_install_cool_apk)

    // 公共启动函数
    fun launchUri(uri: Uri) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
        }
    }

    // 拼接summary字符串
    val summaryText = buildString {
        coolapk?.let { append("${stringResource(R.string.coolapk)}@$it ") }
        github?.let { append("Github@$it ") }
        qq?.let { append("QQ@$it ") }
    }

    SuperArrow(
        title = name,
        leftAction = {
            qq?.let {
                Column(modifier = Modifier
                    .padding(end = 10.dp)) {
                    AsyncImage(
                        model = "https://q.qlogo.cn/headimg_dl?dst_uin=$it&spec=640&img_type=jpg",
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(15.dp))
                    )
                }
            }
        },
        summary = summaryText,
        onClick = {
            // 如果两个及以上信息存在，则弹出卡片，否则直接跳转
            val infoCount = listOfNotNull(coolapk, github, qq).size
            if (infoCount >= 2) {
                showExtra = !showExtra
            } else {
                when {
                    coolapk != null -> coolapkid?.let { launchUri("coolmarket://u/$it".toUri()) }
                    github != null -> launchUri("https://github.com/$github".toUri())
                    qq != null -> launchUri("mqqapi://card/show_pslcard?src_type=internal&version=1&uin=$qq".toUri())
                }
            }
        }
    )

    AnimatedVisibility(visible = showExtra) {
        Card(
            color = MiuixTheme.colorScheme.secondaryContainer,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Column {
                coolapk?.let {
                    SuperArrow(
                        title = stringResource(R.string.coolapk),
                        leftAction = {
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
                            coolapkid?.let { id ->
                                launchUri("coolmarket://u/$id".toUri())
                            }
                        }
                    )
                    addline()
                }
                github?.let {
                    SuperArrow(
                        title = "Github",
                        leftAction = {
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
                            launchUri("https://github.com/$it".toUri())
                        }
                    )
                    addline()
                }
                qq?.let {
                    SuperArrow(
                        title = "QQ",
                        leftAction = {
                            Image(
                                painter = painterResource(R.drawable.qq),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(32.dp)
                                    .padding(end = 8.dp),
                                colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurface)
                            )
                        },
                        onClick = {
                            launchUri("mqqapi://card/show_pslcard?src_type=internal&version=1&uin=$it".toUri())
                        }
                    )
                }
            }
        }
    }
}
