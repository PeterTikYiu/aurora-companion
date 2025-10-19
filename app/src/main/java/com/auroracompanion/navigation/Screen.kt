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
     * Task Detail Screen
     * View individual task details with edit/delete options
     * 
     * @param taskId Task ID parameter
     */
    data object TaskDetail : Screen("tasks/{taskId}") {
        fun createRoute(taskId: Int) = "tasks/$taskId"
    }
    
    /**
     * Task Form Screen
     * Create or edit tasks
     * 
     * @param taskId Task ID parameter (-1 for new task)
     */
    data object TaskForm : Screen("tasks/form?taskId={taskId}") {
        fun createRoute(taskId: Int? = null) = if (taskId != null) {
            "tasks/form?taskId=$taskId"
        } else {
            "tasks/form?taskId=-1"  // -1 indicates new task
        }
    }
    
    /**
     * Settings Screen
     * App configuration
     */
    data object Settings : Screen("settings")
}
