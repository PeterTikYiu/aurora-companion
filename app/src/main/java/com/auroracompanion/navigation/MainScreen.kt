package com.auroracompanion.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

/**
 * Main Screen with Bottom Navigation
 * 
 * Provides bottom navigation bar for switching between main app sections
 */
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.ProductList.route
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                // Products Tab
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Products") },
                    label = { Text("Products") },
                    selected = currentDestination?.hierarchy?.any { it.route == Screen.ProductList.route } == true,
                    onClick = {
                        navController.navigate(Screen.ProductList.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                
                // Tasks Tab
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Tasks") },
                    label = { Text("Tasks") },
                    selected = currentDestination?.hierarchy?.any { it.route == Screen.TaskList.route } == true,
                    onClick = {
                        navController.navigate(Screen.TaskList.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                
                // Inventory Tab
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Inventory2, contentDescription = "Inventory") },
                    label = { Text("Inventory") },
                    selected = currentDestination?.hierarchy?.any { it.route == Screen.InventoryList.route } == true,
                    onClick = {
                        navController.navigate(Screen.InventoryList.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        AuroraNavGraph(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        )
    }
}
