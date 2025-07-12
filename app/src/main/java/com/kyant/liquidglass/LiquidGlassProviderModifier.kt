package com.kyant.liquidglass

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo

fun Modifier.liquidGlassProvider(
    state: LiquidGlassProviderState,
    backgroundColor: Color? = null
): Modifier =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this then LiquidGlassProviderElement(
            state = state,
            backgroundColor = backgroundColor
        )
    } else {
        this
    }

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private class LiquidGlassProviderElement(
    val state: LiquidGlassProviderState,
    val backgroundColor: Color?
) : ModifierNodeElement<LiquidGlassProviderModifierNode>() {

    override fun create(): LiquidGlassProviderModifierNode {
        return LiquidGlassProviderModifierNode(
            state = state,
            backgroundColor = backgroundColor
        )
    }

    override fun update(node: LiquidGlassProviderModifierNode) {
        node.update(
            state = state,
            backgroundColor = backgroundColor
        )
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "liquidGlassProvider"
        properties["state"] = state
        properties["backgroundColor"] = backgroundColor
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LiquidGlassProviderElement) return false

        if (state != other.state) return false
        if (backgroundColor != other.backgroundColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = state.hashCode()
        result = 31 * result + backgroundColor.hashCode()
        return result
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal class LiquidGlassProviderModifierNode(
    var state: LiquidGlassProviderState,
    var backgroundColor: Color?
) : DrawModifierNode, GlobalPositionAwareModifierNode, Modifier.Node() {

    override val shouldAutoInvalidate: Boolean = false

    override fun ContentDrawScope.draw() {
        drawContent()
        state.graphicsLayer.record {
            backgroundColor?.let { drawRect(it) }
            this@draw.drawContent()
        }
    }

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        state.rect = coordinates.boundsInRoot()
    }

    fun update(
        state: LiquidGlassProviderState,
        backgroundColor: Color?
    ) {
        this.state = state
        this.backgroundColor = backgroundColor
    }
}
