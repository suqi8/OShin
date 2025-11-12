package com.suqi8.oshin

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.suqi8.oshin.ui.activity.components.FunArrow
import com.suqi8.oshin.ui.activity.components.addline
import com.suqi8.oshin.ui.home.ModernSectionTitle
import com.suqi8.oshin.ui.nav.path.NavPath
import com.suqi8.oshin.ui.nav.transition.NavTransitionType
import com.suqi8.oshin.ui.nav.ui.NavStackScope
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.utils.overScrollVertical

@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun Main_Function(
    topAppBarScrollBehavior: ScrollBehavior,
    navPath: NavPath,
    navStackScope: NavStackScope,
    padding: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .overScrollVertical()
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
    ) {
        item {
            ModernSectionTitle(
                title = stringResource(id = R.string.module),
                modifier = Modifier
                    .displayCutoutPadding()
                    .padding(top = padding.calculateTopPadding() + 80.dp)
            )
        }
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(vertical = 6.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth().wrapContentHeight()
                    ) {
                        FunArrow(
                            title = stringResource(id = R.string.cpu_freq_main),
                            onClick = { navPath.push(item = "func\\cpu_freq", navTransitionType = NavTransitionType.Zoom) }
                        )
                    }
                    addline()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth().wrapContentHeight()
                    ) {
                        FunArrow(
                            title = stringResource(id = R.string.rom_workshop),
                            onClick = { navPath.push(item = "func\\romworkshop", navTransitionType = NavTransitionType.Zoom) }
                        )
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.padding(bottom = padding.calculateBottomPadding()))
        }
    }
}
