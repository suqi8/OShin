package com.suqi8.oshin

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.navigation.NavController
import com.suqi8.oshin.ui.activity.components.addline
import com.suqi8.oshin.ui.activity.components.funArrow
import com.suqi8.oshin.ui.home.ModernSectionTitle
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.utils.overScrollVertical

@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun Main_Function(
    topAppBarScrollBehavior: ScrollBehavior,
    navController: NavController,
    padding: PaddingValues,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
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
                    with(sharedTransitionScope) {
                        Box(
                            modifier = Modifier
                                .sharedBounds(
                                    sharedContentState = rememberSharedContentState(key = "func\\cpu_freq"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                                .fillMaxWidth().wrapContentHeight()
                        ) {
                            funArrow(
                                title = stringResource(id = R.string.cpu_freq_main),
                                onClick = { navController.navigate("func\\cpu_freq") }
                            )
                        }
                    }
                    addline()
                    with(sharedTransitionScope) {
                        Box(
                            modifier = Modifier
                                .sharedBounds(
                                    sharedContentState = rememberSharedContentState(key = "func\\romworkshop"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                                .fillMaxWidth().wrapContentHeight()
                        ) {
                            funArrow(
                                title = stringResource(id = R.string.rom_workshop),
                                onClick = { navController.navigate("func\\romworkshop") }
                            )
                        }
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.padding(bottom = padding.calculateBottomPadding()))
        }
    }
}
