package com.suqi8.oshin.ui.activity.components

import android.graphics.RenderEffect
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawPlainBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.effect
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun BlurredTopBarBackground(backdrop: Backdrop) {
    val background = MiuixTheme.colorScheme.background

    Box(
        Modifier
            .height(72.dp)
            .fillMaxWidth()
            .drawPlainBackdrop(
                backdrop = backdrop,
                shape = { RectangleShape },
                effects = {
                    blur(4f.dp.toPx())
                    effect(
                        RenderEffect.createRuntimeShaderEffect(
                            obtainRuntimeShader(
                                "TopBarAlphaMask",
                                """
uniform shader content;
uniform float2 size;
layout(color) uniform half4 tint;
uniform float tintIntensity;

half4 main(float2 coord) {
    float blurAlpha = smoothstep(size.y, size.y * 0.2, coord.y);
    float tintAlpha = smoothstep(size.y, size.y * 0.2, coord.y);
    return mix(content.eval(coord) * blurAlpha, tint * tintAlpha, tintIntensity);
}
""".trimIndent()
                            ).apply {
                                setFloatUniform("size", size.width, size.height)
                                setColorUniform("tint", background.value.toLong())
                                setFloatUniform("tintIntensity", 0.8f)
                            },
                            "content"
                        )
                    )
                }
            ),
        contentAlignment = Alignment.Center
    ) {}
}
