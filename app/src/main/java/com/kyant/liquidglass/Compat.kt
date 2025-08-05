package com.kyant.liquidglass

import androidx.compose.ui.unit.Dp
import com.kyant.liquidglass.highlight.GlassHighlight
import com.kyant.liquidglass.material.GlassMaterial
import com.kyant.liquidglass.refraction.InnerRefraction
import com.kyant.liquidglass.refraction.RefractionValue

@Deprecated(
    "Use GlassStyle instead, it will be removed in the next alpha release.",
    ReplaceWith("com.kyant.liquidglass.GlassStyle")
)
typealias LiquidGlassStyle = GlassStyle

@Deprecated(
    "Use InnerRefraction instead, it will be removed in the next alpha release.",
    ReplaceWith("com.kyant.liquidglass.refraction.InnerRefraction")
)
typealias InnerRefraction = InnerRefraction

@Deprecated(
    "Use GlassMaterial instead, it will be removed in the next alpha release.",
    ReplaceWith("com.kyant.liquidglass.material.GlassMaterial")
)
typealias GlassMaterial = GlassMaterial

@Deprecated(
    "Use GlassHighlight instead, it will be removed in the next alpha release.",
    ReplaceWith("com.kyant.liquidglass.highlight.GlassHighlight")
)
typealias GlassBorder = GlassHighlight

@Deprecated("Use RefractionHeight or RefractionAmount instead, it will be removed in the next alpha release.")
typealias RefractionValue = RefractionValue

@Deprecated(
    "Use RefractionHeight or RefractionAmount instead, it will be removed in the next alpha release.",
    level = DeprecationLevel.ERROR
)
@Suppress("FunctionName")
fun RefractionValue(value: Dp): Any = throw UnsupportedOperationException(
    "RefractionValue(value: Dp) is deprecated, use RefractionHeight or RefractionAmount instead."
)
