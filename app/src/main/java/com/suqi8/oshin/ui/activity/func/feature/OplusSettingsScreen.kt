package com.suqi8.oshin.ui.activity.func.feature

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.highcapable.yukihookapi.hook.factory.dataChannel
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.R
import com.suqi8.oshin.hook.settings.SettingsFeature
import com.suqi8.oshin.ui.activity.components.Card
import com.suqi8.oshin.ui.activity.components.FunDropdown
import com.suqi8.oshin.ui.activity.components.FunPage
import com.suqi8.oshin.ui.mainscreen.home.ModernSectionTitle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun OplusSettingsScreen(
    navController: NavController,
    viewModel: OplusSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val prefs = context.prefs(SettingsFeature.OPLUS_SETTINGS_PREFS_NAME)
            val cachedMethods = prefs.getStringSet(SettingsFeature.KEY_CACHED_METHODS, emptySet())

            if (cachedMethods.isNotEmpty()) {
                viewModel.loadItemsState(cachedMethods.toList().sorted())
            } else {
                viewModel.setLoading(true)
            }
        }

        val dataChannel = context.dataChannel(packageName = "com.android.settings")
        dataChannel.wait<ArrayList<String>>(SettingsFeature.KEY_RETURN_METHODS) { newMethods ->
            val sortedMethods = newMethods.sorted()
            viewModel.loadItemsState(sortedMethods)
            if (newMethods.isNotEmpty()) {
                context.prefs(SettingsFeature.OPLUS_SETTINGS_PREFS_NAME).edit {
                    putStringSet(SettingsFeature.KEY_CACHED_METHODS, newMethods.toSet())
                }
            }
        }
        dataChannel.put(SettingsFeature.KEY_GET_METHODS)
    }

    FunPage(
        appList = listOf("com.android.settings"),
        navController = navController,
        scrollBehavior = scrollBehavior
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .overScrollVertical()
                .scrollEndHaptic()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = padding
        ) {
            item {
                ModernSectionTitle(
                    title = stringResource(id = R.string.oplus_settings_features),
                    modifier = Modifier
                        .displayCutoutPadding()
                        .padding(top = padding.calculateTopPadding() + 72.dp, bottom = 8.dp)
                )
            }
            if (uiState.isLoading && uiState.methodNames.isEmpty()) {
                item {
                    Card(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = stringResource(id = R.string.loading_oplus_features),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else if (uiState.methodNames.isNotEmpty()) {
                // --- 优化 1 & 2: 正确使用 LazyColumn 的 itemsIndexed 并提供 key ---
                itemsIndexed(
                    items = uiState.methodNames,
                    key = { _, uniqueKey -> uniqueKey }
                ) { index, uniqueKey ->
                    val title = uniqueKey.substringAfterLast('.')
                    val summary = uniqueKey.substringBeforeLast('.').substringAfterLast('.')
                    val currentValue = uiState.itemStates[uniqueKey] ?: 0

                    Card(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                        FunDropdown(
                            title = title,
                            summary = stringResource(id = R.string.feature_from, summary),
                            selectedIndex = currentValue,
                            options = stringArrayResource(id = R.array.oplus_feature_options).toList(),
                            onSelectedIndexChange = { newIndex -> viewModel.updateState(uniqueKey, newIndex) }
                        )
                    }
                }
            } else {
                item {
                    Card(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = stringResource(id = R.string.oplus_features_not_found),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}
