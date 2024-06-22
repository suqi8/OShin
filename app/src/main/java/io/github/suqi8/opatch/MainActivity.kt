package io.github.suqi8.opatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
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

@Composable
fun Main(modifier: Modifier) {
    val navController = rememberNavController()
    var selectedItem by remember { mutableIntStateOf(1) }
    val items = listOf("功能", "主页", "关于")
    Scaffold(modifier = modifier, bottomBar = { NavigationBar {
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
                onClick = { selectedItem = index }
            )
        }
    } }) {
        NavHost(navController = navController, startDestination = "Main_Home") {
            composable("Main_Function") { Main_Function() }
            composable("Main_Home") { Main_Home() }
            composable("Main_About") { Main_About() }
        }
        Main_Home()

    }
}
