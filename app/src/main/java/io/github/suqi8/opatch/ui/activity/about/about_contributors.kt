package io.github.suqi8.opatch.ui.activity.about

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.BasicComponentColors
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun about_contributors(navController: NavController) {
    val context = LocalContext.current
    val one = MiuixScrollBehavior(rememberTopAppBarState())
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
                    about_contributors_item(
                        name = "酸奶",
                        coolapk = "Stracha酸奶菌",
                        coolapkid = 15225420,
                        github = "suqi8"
                    )
                    addline()
                    about_contributors_item(
                        name = "凌逸",
                        coolapk = "网恋秀牛被骗",
                        coolapkid = 34081897
                    )
                    addline()
                    about_contributors_item(
                        name = "YuKong_A",
                        coolapk = "YuKong_A",
                        coolapkid = 27385711,
                        github = "YuKongA"
                    )
                }
            }
        }
    }
}

@Composable
fun about_contributors_item(
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
                val coolApkUri = Uri.parse("coolmarket://u/${coolapkid}")
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
                    Uri.parse("https://github.com/${github}")
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
                        val coolApkUri = Uri.parse("coolmarket://u/${it}")
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
                            Uri.parse("https://github.com/${it}")
                        )
                        context.startActivity(intent)
                    }
                }
            )
        }
    }
}
