package com.example.dreammate.ui.theme.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("Profile", "profile", Icons.Default.Person),
        BottomNavItem("Study Plan", "studyplan", Icons.Default.DateRange),
        BottomNavItem("Home", "home", Icons.Default.Home),
        BottomNavItem("Tasks", "tasks", Icons.Default.List),
        BottomNavItem("Notifications", "notifications", Icons.Default.Notifications)
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                alwaysShowLabel = false  // Etiketleri gizle
            )
        }
    }
}

data class BottomNavItem(val title: String, val route: String, val icon: ImageVector)