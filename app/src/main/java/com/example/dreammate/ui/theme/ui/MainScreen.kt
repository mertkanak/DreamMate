package com.example.dreammate.ui.theme.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dreammate.ui.*
import com.example.dreammate.viewmodel.StudyPlanViewModel

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val studyVm: StudyPlanViewModel = viewModel()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    onNavigateToStudyPlan = { navController.navigate("studyplan") },
                    onNavigateToTest = { navController.navigate("tasks") },
                    onNavigateToStats = { navController.navigate("notifications") }
                )
            }
            composable("profile") {
                ProfileScreen(
                    onSignOut = {
                        navController.navigate("auth") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable("studyplan") { StudyPlanScreen(viewModel = studyVm) }
            composable("tasks") { TasksScreen() }
            composable("notifications") { NotificationsScreen() }
        }
    }
}