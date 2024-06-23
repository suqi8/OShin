package io.github.suqi8.opatch

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.suqi8.opatch.ui.theme.OPatchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OPatchTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Main(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Main(modifier: Modifier) {
    val navController = rememberNavController()
    var selectedItem by remember { mutableIntStateOf(1) }
    val items = listOf("功能", "主页", "关于")
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(bottomBar = { NavigationBar() {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    val icon: Painter
                    if (selectedItem == index) {
                        // 启用状态的图标
                        when (index) {
                            0 -> icon = painterResource(id = R.drawable.twotone_widgets_24)
                            1 -> icon = painterResource(id = R.drawable.twotone_home_24)
                            else -> icon = painterResource(id = R.drawable.twotone_pending_24)
                        }
                    } else {
                        // 未启用状态的图标
                        when (index) {
                            0 -> icon = painterResource(id = R.drawable.outline_widgets_24)
                            1 -> icon = painterResource(id = R.drawable.outline_home_24)
                            else -> icon = painterResource(id = R.drawable.outline_pending_24)
                        }
                    }
                    Icon(painter = icon, contentDescription = item)
                },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = { selectedItem = index
                    navController.navigate(when (index) {
                        0 -> "Main_Function"
                        1 -> "Main_Home"
                        else -> "Main_About"
                    })}
            )
        }
    } }, topBar = { LargeTopAppBar(scrollBehavior = scrollBehavior,colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.primary,
    ),title = { Column {
        Text(when (selectedItem) {
            0 -> "功能"
            1 -> "主页"
            else -> "关于"
        },overflow = TextOverflow.Ellipsis)
        Text(
            text = "我要玩原神",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    } },navigationIcon = {
        IconButton(onClick = { /* do something */ }) {
            Image(painter = painterResource(id = R.drawable.icon), contentDescription = null)
        }
    })}) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            NavHost(navController = navController, startDestination = "Main_Home") {
                composable("Main_Function") { Main_Function() }
                composable("Main_Home") { Main_Home() }
                composable("Main_About") { Main_About() }
            }
        }
    }
}
