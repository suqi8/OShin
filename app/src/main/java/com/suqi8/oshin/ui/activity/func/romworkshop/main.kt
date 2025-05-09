package com.suqi8.oshin.ui.activity.func.romworkshop

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.funlistui.FunPage
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text

@SuppressLint("SuspiciousIndentation")
@Composable
fun Rom_workshop(navController: NavController) {
    FunPage(
        title = stringResource(R.string.rom_workshop),
        navController = navController
    ) {
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
                Text(stringResource(R.string.coming_soon), modifier = Modifier.padding(6.dp))
            }
        }
    }
}
