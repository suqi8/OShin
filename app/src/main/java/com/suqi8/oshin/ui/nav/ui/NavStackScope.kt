package com.suqi8.oshin.ui.nav.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import com.suqi8.oshin.ui.nav.path.NavPath
import com.suqi8.oshin.ui.nav.transition.ZoomNavTransition

class NavStackScope(
    val path: NavPath
) {
    @Composable
    private fun rememberTransitionStateOf(path: Any): State<ZoomNavTransition?> {
        return remember(path) {
            derivedStateOf {
                this.path.states
                    .find { it.item == path }
                    ?.transition as? ZoomNavTransition
            }
        }
    }

    fun zoomTransitionSource(
        modifier: Modifier = Modifier,
        path: Any,
        color: Color,
        shape: Shape
    ): Modifier = modifier.composed {
        val transitionState by rememberTransitionStateOf(path)

        onGloballyPositioned { coordinates ->
            if (coordinates.isAttached) {
                transitionState?.transitionState?.let {
                    val rect = com.suqi8.oshin.ui.nav.ui.findRootCoordinates(coordinates)
                        .localBoundingBoxOf(coordinates, clipBounds = true)

                    it.sourceRect = rect
                    it.sourceColor = color
                    it.sourceShape = shape
                }
            }
        }
    }
}

// Helper function to get root coordinates
fun findRootCoordinates(coordinates: androidx.compose.ui.layout.LayoutCoordinates): androidx.compose.ui.layout.LayoutCoordinates {
    var current = coordinates
    while (current.parentLayoutCoordinates != null) {
        current = current.parentLayoutCoordinates!!
    }
    return current
}
