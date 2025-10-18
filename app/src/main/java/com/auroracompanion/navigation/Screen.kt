package com.auroracompanion.navigation

/**
 * Navigation Routes
 * 
 * Defines all navigation destinations in the app.
 */
sealed class Screen(val route: String) {
    /**
     * Welcome Screen
     * First time user setup
     */
    data object Welcome : Screen("welcome")
    
    /**
     * Product List Screen
     * Main product browsing screen
     */
    data object ProductList : Screen("products")
    
    /**
     * Product Detail Screen
     * Individual product details with stock management
     * 
     * @param productId Product ID parameter
     */
    data object ProductDetail : Screen("products/{productId}") {
        fun createRoute(productId: String) = "products/$productId"
    }
    
    /**
     * Task List Screen
     * Daily tasks and checklists
     */
    data object TaskList : Screen("tasks")
    
    /**
     * Settings Screen
     * App configuration
     */
    data object Settings : Screen("settings")
}
