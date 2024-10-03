package io.github.suqi8.opatch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.suqi8.opatch.application.RestartApp
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperSwitch
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun Fun_com_android_systemui(navController: NavController) {
    val context = LocalContext.current
    val one = MiuixScrollBehavior(top.yukonga.miuix.kmp.basic.rememberTopAppBarState())
    Scaffold(topBar = { GetAppIconAndName(packageName = "com.android.systemui") { appName, icon ->
        TopAppBar(
            title = appName,
            scrollBehavior = one,
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                },
                    modifier = Modifier.padding(start = 18.dp)) {
                    Icon(
                        imageVector = MiuixIcons.ArrowBack,
                        contentDescription = null,
                        tint = MiuixTheme.colorScheme.onBackground
                    )
                }
            },
        )
    } }) {padding ->
        LazyColumn(contentPadding = PaddingValues(top = padding.calculateTopPadding()),
            topAppBarScrollBehavior = one, modifier = Modifier.fillMaxSize()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp,top = 15.dp)
                ) {
                    SuperArrow(title = stringResource(id = R.string.status_bar_clock),
                        onClick = {
                            navController.navigate("Fun_com_android_systemui_status_bar_clock")
                        })
                }
            }
        }
    }
}
