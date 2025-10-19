package com.auroracompanion.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.auroracompanion.feature.inventory.ui.viewmodel.InventoryViewModel
import com.auroracompanion.feature.product.ui.screen.ProductDetailScreen
import com.auroracompanion.feature.product.ui.screen.ProductListScreen
import com.auroracompanion.feature.task.ui.screen.TaskDetailScreen
import com.auroracompanion.feature.task.ui.screen.TaskFormScreen
import com.auroracompanion.feature.task.ui.screen.TaskListScreen
import com.auroracompanion.feature.inventory.ui.screen.InventoryListScreen
import com.auroracompanion.feature.inventory.ui.screen.StockAdjustmentScreen
import com.auroracompanion.feature.inventory.ui.screen.StockHistoryScreen
import com.auroracompanion.feature.welcome.ui.screen.WelcomeScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import com.auroracompanion.core.data.Result

/**
 * Navigation Graph
 * 
 * Defines navigation structure and destinations.
 * 
 * @param navController Navigation controller
 * @param startDestination Initial screen to show
 * @param modifier Optional modifier
 */
@Composable
fun AuroraNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.ProductList.route,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Welcome Screen (First Launch)
        composable(route = Screen.Welcome.route) {
            WelcomeScreen(
                onSetupComplete = {
                    // Navigate to Product List and clear Welcome from back stack
                    navController.navigate(Screen.ProductList.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Product List Screen
        composable(route = Screen.ProductList.route) {
            ProductListScreen(
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                }
            )
        }
        
        // Product Detail Screen
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
            ProductDetailScreen(
                productId = productId,
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        // Task List Screen
        composable(route = Screen.TaskList.route) {
            TaskListScreen(
                onTaskClick = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                },
                onCreateTask = {
                    navController.navigate(Screen.TaskForm.createRoute())
                }
            )
        }
        
        // Task Detail Screen
        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(
                navArgument("taskId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: return@composable
            TaskDetailScreen(
                taskId = taskId,
                onNavigateBack = { navController.navigateUp() },
                onEdit = { taskId ->
                    navController.navigate(Screen.TaskForm.createRoute(taskId))
                }
            )
        }
        
        // Task Form Screen (Create/Edit)
        composable(
            route = Screen.TaskForm.route,
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.IntType
                    defaultValue = -1  // -1 indicates new task
                }
            )
        ) { backStackEntry ->
            val taskIdArg = backStackEntry.arguments?.getInt("taskId") ?: -1
            val taskId = if (taskIdArg == -1) null else taskIdArg
            TaskFormScreen(
                taskId = taskId,
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        // Inventory List Screen
        composable(route = Screen.InventoryList.route) {
            InventoryListScreen(
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId.toString()))
                },
                onAdjustStock = { productId ->
                    navController.navigate(Screen.StockAdjustment.createRoute(productId))
                }
            )
        }
        
        // Stock Adjustment Screen
        composable(
            route = Screen.StockAdjustment.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: return@composable
            val viewModel: InventoryViewModel = hiltViewModel()
            val productsState by viewModel.products.collectAsState()
            
            when (val state = productsState) {
                is Result.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is Result.Success -> {
                    val product = state.data.find { it.id == productId }
                    if (product != null) {
                        StockAdjustmentScreen(
                            product = product,
                            onNavigateBack = { navController.navigateUp() }
                        )
                    }
                }
                is Result.Error -> {
                    // Error state handled by screen
                }
            }
        }
        
        // Stock History Screen
        composable(
            route = Screen.StockHistory.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: return@composable
            val viewModel: InventoryViewModel = hiltViewModel()
            val productsState by viewModel.products.collectAsState()
            
            when (val state = productsState) {
                is Result.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is Result.Success -> {
                    val product = state.data.find { it.id == productId }
                    if (product != null) {
                        val stockHistoryFlow = viewModel.getStockHistory(productId)
                        StockHistoryScreen(
                            product = product,
                            stockHistoryFlow = stockHistoryFlow,
                            onNavigateBack = { navController.navigateUp() }
                        )
                    }
                }
                is Result.Error -> {
                    // Error state handled by screen
                }
            }
        }
    }
}
