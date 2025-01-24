package com.suqi8.oshin.ui.activity.com.android.systemui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.funlistui.FunNoEnable
import com.suqi8.oshin.ui.activity.funlistui.FunPage
import com.suqi8.oshin.ui.activity.funlistui.FunSwich
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.extra.SuperDropdown

@SuppressLint("RtlHardcoded")
@Composable
fun statusbar_icon(navController: NavController) {
    val context = LocalContext.current
    val showlist = listOf(stringResource(R.string.default_), stringResource(R.string.hide))
    val show_Wifi_icon = remember { mutableIntStateOf(context.prefs("systemui\\statusbar_icon").getInt("show_Wifi_icon", 0)) }
    val show_Wifi_arrow = remember { mutableIntStateOf(context.prefs("systemui\\statusbar_icon").getInt("show_Wifi_arrow", 0)) }
    FunPage(
        title = stringResource(id = R.string.status_bar_icon),
        appList = listOf("com.android.systemui"),
        navController = navController
    ) {
        Column {
            val statusbar_icon = remember { mutableStateOf(context.prefs("settings").getBoolean("statusbar_icon", false)) }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 6.dp, top = 15.dp)
            ) {
                FunSwich(
                    title = stringResource(R.string.status_bar_icon),
                    category = "systemui\\statusbar_icon",
                    key = "statusbar_icon",
                    defValue = false,
                    onCheckedChange = {
                        statusbar_icon.value = it
                    }
                )
            }
            AnimatedVisibility(
                visible = !statusbar_icon.value
            ) {
                FunNoEnable()
            }
            AnimatedVisibility(
                visible = statusbar_icon.value
            ) {
                Column {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row {
                            SuperDropdown(title = stringResource(R.string.wifi_icon),
                                items = showlist,
                                selectedIndex = show_Wifi_icon.intValue,
                                onSelectedIndexChange = {
                                    show_Wifi_icon.intValue = it
                                    context.prefs("systemui\\statusbar_icon").edit {
                                        putInt(
                                            "icon_show_Wifi_icon",
                                            it
                                        )
                                    }
                                })
                        }
                        SuperDropdown(title = stringResource(R.string.wifi_arrow),
                            items = showlist,
                            selectedIndex = show_Wifi_arrow.intValue,
                            onSelectedIndexChange = {
                                show_Wifi_arrow.intValue = it
                                context.prefs("systemui\\statusbar_icon").edit {
                                    putInt(
                                        "show_Wifi_arrow",
                                        it
                                    )
                                }
                            })
                    }
                }
            }
        }
    }
}
