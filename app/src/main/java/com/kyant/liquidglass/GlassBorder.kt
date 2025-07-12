package com.kyant.liquidglass

import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI

@Immutable
sealed interface GlassBorder {

    val color: Color
        get() = Color.Unspecified

    val width: Dp
        get() = Dp.Hairline

    @Stable
    fun createBrush(density: Density, size: Size, cornerRadius: Float): Brush? {
        return null
    }

    @Immutable
    data object None : GlassBorder

    @Immutable
    data class Solid(
        override val color: Color = Color.White.copy(alpha = 0.6f),
        override val width: Dp = 1.dp
    ) : GlassBorder {

        override fun createBrush(density: Density, size: Size, cornerRadius: Float): Brush {
            return SolidColor(color)
        }
    }

    @Immutable
    data class Light(
        override val color: Color = Color.White.copy(alpha = 0.6f),
        override val width: Dp = 1.dp,
        val angle: Float = 45f,
        val decay: Float = 2f
    ) : GlassBorder {

        override fun createBrush(density: Density, size: Size, cornerRadius: Float): Brush {
            val widthPx = with(density) { width.toPx() }
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ShaderBrush(
                    RuntimeShader(
                        """// This file belongs to Kyant. You must not use it without permission.
        ${LiquidGlassShaders.sdRectangleShaderUtils}
        
        half4 main(float2 coord) {
            float cornerRadius = $cornerRadius;
            float2 size = float2(${size.width}, ${size.height});
            float2 halfSize = size * 0.5;
            float2 centeredCoord = coord - halfSize;
            
            float2 grad = gradSdRoundedRectangle(centeredCoord, halfSize, cornerRadius);
            float2 topLightNormal = float2(-cos(${angle / 180.0 * PI}), -sin(${angle / 180.0 * PI}));
            float topLightFraction = dot(topLightNormal, grad);
            float bottomLightFraction = -topLightFraction;
            float fraction = pow(max(topLightFraction, bottomLightFraction), $decay);
            
            float sd = sdRoundedRectangle(centeredCoord, halfSize, cornerRadius);
            sd = min(sd, 0.0);
            fraction = fraction * (1.0 - sqrt(-sd / $widthPx));
            
            return half4(${color.red}, ${color.green}, ${color.blue}, 1.0) * ${color.alpha} * fraction;
        }"""
                    )
                )
            } else {
                SolidColor(color)
            }
        }
    }

    companion object {

        @Stable
        val Default: Light = Light()
    }
}
