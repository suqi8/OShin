package com.suqi8.oshin.ui.activity.func.romworkshop

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.components.Card
import com.suqi8.oshin.ui.activity.components.FunPage
import com.suqi8.oshin.ui.mainscreen.about.openUrl
import com.suqi8.oshin.ui.mainscreen.home.ModernSectionTitle
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun RomWorkshop(
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    FunPage(
        navController = navController,
        scrollBehavior = scrollBehavior,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        animationKey = "func\\romworkshop"
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
                    title = stringResource(id = R.string.rom_workshop),
                    modifier = Modifier
                        .displayCutoutPadding()
                        .padding(top = padding.calculateTopPadding() + 72.dp, bottom = 8.dp)
                )
            }
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(top = 16.dp, bottom = 6.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val compositionResult =
                            rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.coming_soon))
                        val progress =
                            animateLottieCompositionAsState(
                                composition = compositionResult.value,
                                iterations = LottieConstants.IterateForever
                            )
                        LottieAnimation(
                            composition = compositionResult.value,
                            progress = { progress.progress },
                            modifier = Modifier.padding(1.dp)
                        )
                        TextButton("立即体验", onClick = {openUrl(context, "https://github.com/suqi8/ImageStudio/releases/latest")}, modifier = Modifier.padding(6.dp).fillMaxWidth().padding(32.dp), colors = ButtonDefaults.textButtonColorsPrimary())
                    }
                }
            }
        }
    }
}
