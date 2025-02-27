package com.suqi8.oshin.ui.activity

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.suqi8.oshin.R
import com.suqi8.oshin.features
import com.suqi8.oshin.item
import com.suqi8.oshin.ui.activity.funlistui.FunPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.extra.SuperArrow

@Composable
fun recent_update(navController: NavController) {
    FunPage(
        title = stringResource(R.string.recent_update),
        navController = navController
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 6.dp,top = 15.dp)
        ) {
            val recentFeatureState = remember { mutableStateOf<List<item>?>(null) }
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                recentFeatureState.value = withContext(Dispatchers.IO) {
                    features(context)
                        .takeIf { it.isNotEmpty() }
                        ?.toList()
                        ?.reversed()
                }
            }
            recentFeatureState.value?.forEach { feature ->
                key(feature.title) {  // 使用唯一标识作为 key
                    val onClick by remember(feature.category) {
                        mutableStateOf({
                            navController.navigate(feature.category)
                        })
                    }

                    SuperArrow(
                        title = feature.title,
                        summary = feature.summary,
                        onClick = onClick
                    )
                }
            }
        }
    }
}
