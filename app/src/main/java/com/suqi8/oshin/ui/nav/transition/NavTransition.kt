package com.suqi8.oshin.ui.nav.transition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import kotlinx.coroutines.CoroutineScope

abstract class NavTransition(val isFloatingWindow: Boolean = false) {
    abstract var appearance: NavItemAppearance
    val isNotExiting by derivedStateOf { appearance < NavItemAppearance.Exiting }
    val isExited by derivedStateOf { appearance == NavItemAppearance.Exited }

    abstract val animationScope: CoroutineScope
    abstract val navAnimationSpecs: NavAnimationSpecs
    abstract val shouldDrawBehind: Boolean

    val prevItemProperties = MutableTransitionProperties()

    abstract suspend fun enter()
    abstract suspend fun exit()

    @Composable
    abstract fun Content(content: @Composable () -> Unit)
}
