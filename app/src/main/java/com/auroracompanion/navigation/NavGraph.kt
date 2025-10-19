package com.auroracompanion.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.auroracompanion.core.data.repository.InventoryRepository
import com.auroracompanion.feature.product.data.local.dao.ProductDao
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
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import com.auroracompanion.core.data.Result
import com.auroracompanion.feature.product.domain.model.Product
import kotlinx.coroutines.flow.first

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
            val viewModel: com.auroracompanion.feature.inventory.ui.viewmodel.InventoryViewModel = hiltViewModel()
            
            var product by remember { mutableStateOf<Product?>(null) }
            var isLoading by remember { mutableStateOf(true) }
            var errorMessage by remember { mutableStateOf<String?>(null) }
            
            LaunchedEffect(productId) {
                isLoading = true
                val result = viewModel.uiState.first()
                when (result) {
                    is com.auroracompanion.feature.inventory.ui.viewmodel.InventoryUiState.Success -> {
                        product = result.products.find { it.id == productId }
                        if (product == null) {
                            errorMessage = "Product not found"
                        }
                    }
                    is com.auroracompanion.feature.inventory.ui.viewmodel.InventoryUiState.Error -> {
                        errorMessage = result.message
                    }
                    else -> {}
                }
                isLoading = false
            }
            
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(errorMessage ?: "Error loading product")
                    }
                }
                product != null -> {
                    StockAdjustmentScreen(
                        product = product!!,
                        onNavigateBack = { navController.navigateUp() }
                    )
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
            val viewModel: com.auroracompanion.feature.inventory.ui.viewmodel.InventoryViewModel = hiltViewModel()
            
            var product by remember { mutableStateOf<Product?>(null) }
            var isLoading by remember { mutableStateOf(true) }
            var errorMessage by remember { mutableStateOf<String?>(null) }
            
            LaunchedEffect(productId) {
                isLoading = true
                val result = viewModel.uiState.first()
                when (result) {
                    is com.auroracompanion.feature.inventory.ui.viewmodel.InventoryUiState.Success -> {
                        product = result.products.find { it.id == productId }
                        if (product == null) {
                            errorMessage = "Product not found"
                        }
                    }
                    is com.auroracompanion.feature.inventory.ui.viewmodel.InventoryUiState.Error -> {
                        errorMessage = result.message
                    }
                    else -> {}
                }
                isLoading = false
            }
            
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(errorMessage ?: "Error loading product")
                    }
                }
                product != null -> {
                    val stockHistoryFlow = viewModel.getStockHistory(productId)
                    StockHistoryScreen(
                        product = product!!,
                        stockHistoryFlow = stockHistoryFlow,
                        onNavigateBack = { navController.navigateUp() }
                    )
                }
            }
        }
    }
}
