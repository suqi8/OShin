package com.kyant.liquidglass

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawModifierNode
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import kotlin.math.ceil
import kotlin.math.min

@Composable
fun Modifier.liquidGlass(
    style: LiquidGlassStyle,
    providerState: LiquidGlassProviderState = LocalLiquidGlassProviderState.current
): Modifier =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this then LiquidGlassElement(
            style = style,
            providerState = providerState
        )
    } else {
        this
    }

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private class LiquidGlassElement(
    val style: LiquidGlassStyle,
    val providerState: LiquidGlassProviderState
) : ModifierNodeElement<LiquidGlassModifierNode>() {

    override fun create(): LiquidGlassModifierNode {
        return LiquidGlassModifierNode(
            style = style,
            providerState = providerState
        )
    }

    override fun update(node: LiquidGlassModifierNode) {
        node.update(
            style = style,
            providerState = providerState
        )
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "liquidGlass"
        properties["style"] = style
        properties["providerState"] = providerState
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LiquidGlassElement) return false

        if (style != other.style) return false
        if (providerState != other.providerState) return false

        return true
    }

    override fun hashCode(): Int {
        var result = style.hashCode()
        result = 31 * result + providerState.hashCode()
        return result
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal class LiquidGlassModifierNode(
    var style: LiquidGlassStyle,
    var providerState: LiquidGlassProviderState
) : LayoutModifierNode, GlobalPositionAwareModifierNode, DelegatingNode() {

    override val shouldAutoInvalidate: Boolean = false

    private val shadersCache = LiquidGlassShadersCache()
    private var rect: Rect? by mutableStateOf(null)

    private val drawWithCacheModifierNode =
        delegate(
            CacheDrawModifierNode {
                val contentBlurRadiusPx = style.material.blurRadius.toPx()
                val contentRenderEffect =
                    if (contentBlurRadiusPx > 0f) {
                        RenderEffect.createBlurEffect(
                            contentBlurRadiusPx,
                            contentBlurRadiusPx,
                            Shader.TileMode.DECAL
                        )
                    } else {
                        RenderEffect.createOffsetEffect(0f, 0f)
                    }

                val cornerRadiusPx = style.shape.topStart.toPx(size, this)

                val hasBleed = style.bleed.opacity > 0f
                val refractionRenderEffect =
                    RenderEffect.createChainEffect(
                        RenderEffect.createRuntimeShaderEffect(
                            shadersCache.getRefractionShader(hasBleed).apply {
                                setFloatUniform("size", size.width, size.height)
                                setFloatUniform("cornerRadius", cornerRadiusPx)

                                setFloatUniform(
                                    "refractionHeight",
                                    style.innerRefraction.height.toPx(this@CacheDrawModifierNode, size)
                                )
                                setFloatUniform(
                                    "refractionAmount",
                                    style.innerRefraction.amount.toPx(this@CacheDrawModifierNode, size)
                                )
                                setFloatUniform(
                                    "eccentricFactor",
                                    style.innerRefraction.eccentricFactor
                                )

                                if (hasBleed) {
                                    setFloatUniform(
                                        "bleedOpacity",
                                        style.bleed.opacity
                                    )
                                }
                            },
                            "image"
                        ),
                        contentRenderEffect
                    )

                val refractionWithBleedRenderEffect =
                    if (hasBleed) {
                        val bleedRenderEffect =
                            RenderEffect.createChainEffect(
                                RenderEffect.createRuntimeShaderEffect(
                                    shadersCache.getBleedShader().apply {
                                        setFloatUniform("size", size.width, size.height)
                                        setFloatUniform("cornerRadius", cornerRadiusPx)

                                        setFloatUniform(
                                            "eccentricFactor",
                                            style.innerRefraction.eccentricFactor
                                        )
                                        setFloatUniform(
                                            "bleedAmount",
                                            style.bleed.amount.toPx(this@CacheDrawModifierNode, size)
                                        )
                                    },
                                    "image"
                                ),
                                contentRenderEffect
                            )

                        val bleedBlurRadiusPx = style.bleed.blurRadius.toPx()
                        val blurredBleedRenderEffect =
                            if (bleedBlurRadiusPx > 0f) {
                                RenderEffect.createChainEffect(
                                    bleedRenderEffect,
                                    RenderEffect.createBlurEffect(
                                        bleedBlurRadiusPx,
                                        bleedBlurRadiusPx,
                                        Shader.TileMode.CLAMP
                                    )
                                )
                            } else {
                                bleedRenderEffect
                            }

                        RenderEffect.createBlendModeEffect(
                            blurredBleedRenderEffect,
                            refractionRenderEffect,
                            android.graphics.BlendMode.SRC_OVER
                        )
                    } else {
                        refractionRenderEffect
                    }

                val materialRenderEffect =
                    if (style.material != GlassMaterial.Default) {
                        RenderEffect.createRuntimeShaderEffect(
                            shadersCache.getMaterialShader().apply {
                                setFloatUniform(
                                    "contrast",
                                    style.material.contrast
                                )
                                setFloatUniform(
                                    "whitePoint",
                                    style.material.whitePoint
                                )
                                setFloatUniform(
                                    "chromaMultiplier",
                                    style.material.chromaMultiplier
                                )
                            },
                            "image"
                        )
                    } else {
                        null
                    }

                val renderEffect =
                    if (materialRenderEffect != null) {
                        RenderEffect.createChainEffect(
                            materialRenderEffect,
                            refractionWithBleedRenderEffect
                        ).asComposeRenderEffect()
                    } else {
                        refractionWithBleedRenderEffect.asComposeRenderEffect()
                    }

                val graphicsLayer = obtainGraphicsLayer()
                graphicsLayer.renderEffect = renderEffect

                val strokeWidthPx = ceil(min(style.border.width.toPx(), size.minDimension / 2))
                val halfStroke = strokeWidthPx / 2
                val borderTopLeft = Offset(halfStroke, halfStroke)
                val borderSize = Size(size.width - strokeWidthPx, size.height - strokeWidthPx)
                val border =
                    if (strokeWidthPx > 0f) {
                        val borderBrush = style.border.createBrush(this, borderSize, cornerRadiusPx)
                        if (borderBrush != null) {
                            val borderOutline = style.shape.createOutline(borderSize, layoutDirection, this)
                            Pair(borderOutline, borderBrush)
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                val stroke = Stroke(strokeWidthPx)

                onDrawBehind {
                    val rect = rect ?: return@onDrawBehind
                    graphicsLayer.record {
                        translate(-rect.left, -rect.top) {
                            drawLayer(providerState.graphicsLayer)
                        }
                    }
                    drawLayer(graphicsLayer)

                    if (style.material.tint.isSpecified) {
                        drawRect(style.material.tint)
                    }

                    if (border != null) {
                        val (borderOutline, borderBrush) = border

                        translate(borderTopLeft.x, borderTopLeft.y) {
                            drawOutline(
                                outline = borderOutline,
                                brush = borderBrush,
                                style = stroke,
                                blendMode = BlendMode.Plus
                            )
                        }
                    }
                }
            }
        )

    val layerBlock: GraphicsLayerScope.() -> Unit = {
        compositingStrategy = CompositingStrategy.Offscreen
        clip = true
        shape = style.shape
    }

    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            placeable.placeWithLayer(0, 0, layerBlock = layerBlock)
        }
    }

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        rect = providerState.rect?.let {
            coordinates.boundsInRoot().translate(-it.topLeft)
        }
    }

    fun update(
        style: LiquidGlassStyle,
        providerState: LiquidGlassProviderState
    ) {
        this.style = style
        this.providerState = providerState
        drawWithCacheModifierNode.invalidateDrawCache()
    }
}
