package com.suqi8.oshin.ui.nav.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import com.suqi8.oshin.ui.nav.path.NavPath

@Composable
fun NavStack(
    path: NavPath,
    modifier: Modifier = Modifier,
    content: @Composable (scope: NavStackScope, item: Any) -> Unit
) {
    val scope = remember(path) { NavStackScope(path) }
    val saveableStateHolder = rememberSaveableStateHolder()

    Box(modifier = modifier) {
        path.states.forEach { state ->
            val transition = state.transition

            key(state.key) {
                transition.Content {
                    // Keep the state of the composable alive
                    saveableStateHolder.SaveableStateProvider(key = state.key) {
                        content(scope, state.item)
                    }
                }
            }

            // Remove the state from the holder when the item is fully gone
            DisposableEffect(state.key) {
                onDispose {
                    if (transition.isExited) {
                        saveableStateHolder.removeState(state.key)
                    }
                }
            }
        }
    }
}
