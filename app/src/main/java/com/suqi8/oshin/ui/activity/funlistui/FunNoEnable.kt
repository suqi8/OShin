package com.suqi8.oshin.ui.activity.funlistui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.suqi8.oshin.R
import com.suqi8.oshin.drawColoredShadow
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text

@Composable
fun FunNoEnable() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .drawColoredShadow(
                Color.Red,
                0.07f,
                borderRadius = 0.dp,
                shadowRadius = 15.dp,
                offsetX = 0.dp,
                offsetY = 0.dp,
                roundedRect = false
            ),
        color = Color.Red.copy(alpha = 0.03f)
    ) {
        val compositionResult = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.error))
        val progress = animateLottieCompositionAsState(
            composition = compositionResult.value
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            LottieAnimation(
                composition = compositionResult.value,
                progress = progress.value,
                modifier = Modifier
                    .size(50.dp)
            )
            Text(
                text = stringResource(R.string.no_start_func),
                color = Color.Red,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
