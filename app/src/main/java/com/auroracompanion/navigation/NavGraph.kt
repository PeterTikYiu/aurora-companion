package com.auroracompanion.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.auroracompanion.feature.product.ui.screen.ProductDetailScreen
import com.auroracompanion.feature.product.ui.screen.ProductListScreen

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
        
        // TODO: Add more screens as features are implemented
        // - Welcome Screen
        // - Task List Screen
        // - Settings Screen
    }
}
