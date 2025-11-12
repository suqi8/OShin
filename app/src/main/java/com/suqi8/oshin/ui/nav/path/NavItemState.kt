package com.suqi8.oshin.ui.nav.path

import com.suqi8.oshin.ui.nav.transition.NavTransition

data class NavItemState(
    val key: Int,
    val item: Any,
    val transition: NavTransition
)
