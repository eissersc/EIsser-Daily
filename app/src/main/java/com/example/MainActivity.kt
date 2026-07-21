package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HydrationCheckScreen
import com.example.ui.screens.RewardsCatalogScreen
import com.example.ui.screens.DietRecommendationScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.EisseryViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val viewModel: EisseryViewModel = viewModel()

                // Collect and show system status messages as Android Toast
                LaunchedEffect(Unit) {
                    var currentToast: Toast? = null
                    viewModel.statusMessage.collect { message ->
                        currentToast?.cancel()
                        val toast = Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT)
                        currentToast = toast
                        toast.show()
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "dashboard",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("dashboard") {
                            DashboardScreen(
                                viewModel = viewModel,
                                onNavigateToAssessment = { navController.navigate("assessment") },
                                onNavigateToCatalog = { navController.navigate("catalog") },
                                onNavigateToDiet = { navController.navigate("diet_recommendation") }
                            )
                        }
                        
                        composable("assessment") {
                            HydrationCheckScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("catalog") {
                            RewardsCatalogScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("diet_recommendation") {
                            DietRecommendationScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
