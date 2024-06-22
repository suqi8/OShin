package io.github.suqi8.opatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
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
                    Main()
                }
            }
        }
    }
}

@Composable
fun Main() {
    val navController = rememberNavController()
    Column {
        NavHost(navController = navController, startDestination = "Main_Home") {
            composable("Main_Function") { Main_Function() }
            composable("Main_Home") { Main_Home() }
            composable("Main_About") { Main_About() }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    OPatchTheme {
        Main()
    }
}
