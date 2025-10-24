package com.suqi8.oshin.ui.activity

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.components.Card
import com.suqi8.oshin.ui.activity.components.FunPage
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HideAppsNotice(
    navController: NavController,
    packages: String?,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    // 解析传递过来的包名列表
    val notInstalledApps = remember(packages) {
        packages?.split(',')?.filter { it.isNotBlank() } ?: emptyList()
    }

    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())

    FunPage(
        title = stringResource(R.string.help),
        navController = navController,
        scrollBehavior = scrollBehavior,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        animationKey = "hide_apps_notice"
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .overScrollVertical()
                .scrollEndHaptic()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = padding
        ) {
            item {
                Card {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(stringResource(R.string.hide_apps_notice, notInstalledApps.size))
                        if (notInstalledApps.isNotEmpty()) {
                            Text(notInstalledApps.joinToString(separator = "\n"))
                        }
                    }
                }
            }
        }
    }
}
