package io.github.suqi8.opatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import io.github.suqi8.opatch.ui.theme.OPatchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OPatchTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Main()
                }
            }
        }
    }
}

@Composable
fun Main() {

}

@Composable
fun SetupNavGraph(navController: NavHostController) {
    /*NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { Home(navController) }
        // 添加其他页面的composable
    }*/
}

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    OPatchTheme {
        Main()
    }
}
