package com.auroracompanion.core.util

/**
 * Application-wide constants
 * 
 * Centralized location for all constant values used throughout the app.
 * This makes it easy to update values and prevents magic numbers/strings.
 */
object Constants {
    
    // Database
    const val DATABASE_NAME = "aurora_companion_db"
    const val DATABASE_VERSION = 1
    
    // DataStore
    const val DATASTORE_NAME = "aurora_preferences"
    
    // Preferences Keys
    const val PREF_STORE_NAME = "store_name"
    const val PREF_STAFF_NAME = "staff_name"
    const val PREF_IS_FIRST_LAUNCH = "is_first_launch"
    const val PREF_THEME_MODE = "theme_mode"
    
    // Product Categories
    val PRODUCT_CATEGORIES = listOf(
        "Dog",
        "Cat",
        "Fish",
        "Bird",
        "Reptile",
        "Small Pets"
    )
    
    // Task Priorities
    enum class TaskPriority {
        LOW,
        MEDIUM,
        HIGH
    }
    
    // Stock Thresholds
    const val LOW_STOCK_THRESHOLD = 10
    const val OUT_OF_STOCK = 0
    
    // UI Constants
    const val DEBOUNCE_TIME_MS = 300L
    const val ANIMATION_DURATION_MS = 300
    
    // Sample Data
    const val SAMPLE_PRODUCTS_JSON = "products.json"
    const val SAMPLE_TASKS_JSON = "tasks.json"
    
    // Navigation Routes (will be expanded later)
    object Routes {
        const val SPLASH = "splash"
        const val LOGIN = "login"
        const val HOME = "home"
        const val PRODUCT_LIST = "product_list"
        const val PRODUCT_DETAIL = "product_detail/{productId}"
        const val TASK_LIST = "task_list"
        const val INVENTORY = "inventory"
        const val SETTINGS = "settings"
    }
}
