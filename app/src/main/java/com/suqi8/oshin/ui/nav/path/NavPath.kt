package com.suqi8.oshin.ui.nav.path

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.suqi8.oshin.ui.nav.transition.MergedTransitionProperties
import com.suqi8.oshin.ui.nav.transition.NavAnimationSpecs
import com.suqi8.oshin.ui.nav.transition.NavItemAppearance
import com.suqi8.oshin.ui.nav.transition.NavTransitionType
import com.suqi8.oshin.ui.nav.transition.navTransitionWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Stable
class NavPath(
    initialItems: List<Pair<Any, NavTransitionType>>,
    val animationScope: CoroutineScope,
    val navAnimationSpecs: NavAnimationSpecs
) {
    private val _states = SnapshotStateList<NavItemState>()
    val states: List<NavItemState> get() = _states

    private var increment by mutableIntStateOf(0)

    // 1. 将 onPop 的定义移到 init 之前
    private val onPop: () -> Unit = {
        synchronized(this) {
            // 在移除前，先找到所有已经退出的项目
            val exitedItems = _states.filter { it.transition.appearance == NavItemAppearance.Exited }
            if (exitedItems.isNotEmpty()) {
                _states.removeAll(exitedItems)
            }
        }
    }

    init {
        if (initialItems.isNotEmpty()) {
            increment = initialItems.lastIndex + 1
            _states.addAll(
                initialItems.mapIndexed { index, (item, type) ->
                    NavItemState(
                        key = index,
                        item = item,
                        transition = navTransitionWrapper(
                            type = type,
                            onPop = onPop, // 2. 现在这里可以正确引用 onPop
                            initialAppearance = NavItemAppearance.Entered,
                            animationScope = animationScope,
                            navAnimationSpecs = navAnimationSpecs,
                            mergedTransitionProperties = MergedTransitionProperties {
                                findPrevItemProperties(index)
                            }
                        )
                    )
                }
            )
        }
    }

    private var pushTimeMills = Long.MIN_VALUE

    @Synchronized
    fun push(item: Any, navTransitionType: NavTransitionType) {
        val currentTime = System.currentTimeMillis()
        if (pushTimeMills == Long.MIN_VALUE || currentTime - pushTimeMills >= 16) {
            pushTimeMills = currentTime
            val key = increment++
            val state = NavItemState(
                key = key,
                item = item,
                transition = navTransitionWrapper(
                    type = navTransitionType,
                    onPop = onPop,
                    initialAppearance = NavItemAppearance.WillEnter,
                    animationScope = animationScope,
                    navAnimationSpecs = navAnimationSpecs,
                    mergedTransitionProperties = MergedTransitionProperties {
                        findPrevItemProperties(key)
                    }
                )
            )
            _states.add(state)
            animationScope.launch {
                state.transition.enter()
            }
        }
    }

    @Synchronized
    fun pop() {
        // Pop the last item that is not already exiting.
        val state = _states.lastOrNull { it.transition.appearance < NavItemAppearance.Exiting } ?: return
        state.transition.appearance = NavItemAppearance.WillExit
        animationScope.launch {
            state.transition.exit()
        }
    }

    private fun findPrevItemProperties(key: Int) = _states
        .filter { it.key < key && !it.transition.isFloatingWindow }
        .map { it.transition.prevItemProperties }
}
