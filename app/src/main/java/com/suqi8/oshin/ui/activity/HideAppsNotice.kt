package com.suqi8.oshin.ui.activity

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.components.Card
import com.suqi8.oshin.ui.activity.components.FunPage
import com.suqi8.oshin.ui.home.ModernSectionTitle
import com.suqi8.oshin.ui.nav.path.NavPath
import com.suqi8.oshin.ui.nav.ui.NavStackScope
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HideAppsNotice(
    navPath: NavPath,
    navStackScope: NavStackScope,
    packages: String?,
) {
    // 解析传递过来的包名列表
    val notInstalledApps = remember(packages) {
        packages?.split(',')?.filter { it.isNotBlank() } ?: emptyList()
    }

    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())

    FunPage(
        navPath = navPath,
        navStackScope = navStackScope,
        scrollBehavior = scrollBehavior,
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
                ModernSectionTitle(
                    title = stringResource(id = R.string.help),
                    modifier = Modifier
                        .displayCutoutPadding()
                        .padding(top = padding.calculateTopPadding() + 72.dp, bottom = 8.dp)
                )
            }
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
