package com.suqi8.oshin.ui.nav.transition

import androidx.compose.foundation.gestures.Orientation
import kotlinx.coroutines.CoroutineScope

fun navTransitionWrapper(
    type: NavTransitionType,
    onPop: () -> Unit,
    initialAppearance: NavItemAppearance,
    animationScope: CoroutineScope,
    navAnimationSpecs: NavAnimationSpecs,
    mergedTransitionProperties: MergedTransitionProperties
): NavTransition {
    return when (type) {
        NavTransitionType.HorizontalSwipe -> SwipeNavTransition(
            orientation = Orientation.Horizontal,
            onPop = onPop,
            initialAppearance = initialAppearance,
            animationScope = animationScope,
            navAnimationSpecs = navAnimationSpecs,
            mergedTransitionProperties = mergedTransitionProperties
        )
        NavTransitionType.VerticalSwipe -> SwipeNavTransition(
            orientation = Orientation.Vertical,
            onPop = onPop,
            initialAppearance = initialAppearance,
            animationScope = animationScope,
            navAnimationSpecs = navAnimationSpecs,
            mergedTransitionProperties = mergedTransitionProperties
        )
        NavTransitionType.Zoom -> ZoomNavTransition(
            onPop = onPop,
            initialAppearance = initialAppearance,
            animationScope = animationScope,
            navAnimationSpecs = navAnimationSpecs,
            mergedTransitionProperties = mergedTransitionProperties
        )
        else -> TODO("Transition for $type not implemented yet")
    }
}
