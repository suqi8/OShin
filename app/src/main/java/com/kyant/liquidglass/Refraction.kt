package com.kyant.liquidglass

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.dp

@Immutable
sealed interface Refraction {

    val height: RefractionValue

    val amount: RefractionValue
}

/**
 * 表示内部折射效果，通常在光线穿过透明材质（如玻璃）时可见。
 * 此类定义了控制光线在内部如何弯曲或扭曲的属性。
 *
 * @property height 折射区域的高度。这决定了折射效果的垂直范围。
 * @property amount 折射的程度或强度。负值通常表示光线向内弯曲。
 * @property eccentricFactor 影响折射效果形状或分布的因子。
 *                         较小的值可能会导致更集中或特定弯曲的折射。
 */
@Immutable
data class InnerRefraction(
    override val height: RefractionValue,
    override val amount: RefractionValue,
    val eccentricFactor: Float = 1f
) : Refraction {

    companion object {
        @Stable
        val Default: InnerRefraction =
            InnerRefraction(
                height = RefractionValue(20.dp),
                amount = RefractionValue((-16).dp),
                eccentricFactor = 0.25f
            )
    }
}
