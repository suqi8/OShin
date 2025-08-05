package com.suqi8.oshin.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kyant.expressa.prelude.labelMedium
import com.kyant.expressa.ui.ProvideTextStyle

class BottomTabsScope {
    inner class BottomTab(
        val icon: @Composable () -> Unit,
        val label: @Composable () -> Unit,
        val modifier: Modifier = Modifier
    ) {
        @Composable
        internal fun Content(modifier: Modifier = Modifier) {
            Column(
                modifier
                    .then(this.modifier)
                    .height(56.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically)
            ) {
                icon()
                ProvideTextStyle(labelMedium, label)
            }
        }
    }
}
