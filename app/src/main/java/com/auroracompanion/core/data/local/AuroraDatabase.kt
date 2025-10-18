package com.auroracompanion.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.auroracompanion.feature.product.data.local.dao.ProductDao
import com.auroracompanion.feature.product.data.local.entity.ProductEntity
import com.auroracompanion.feature.task.data.local.dao.TaskDao
import com.auroracompanion.feature.task.data.local.entity.TaskEntity

/**
 * Aurora Companion Room Database
 * 
 * Main database for the application using Room persistence library.
 * 
 * Features:
 * - SQLite database with type-safe queries
 * - Automatic schema management
 * - Migration support for future updates
 * - Reactive queries with Flow
 * 
 * Entities:
 * - ProductEntity: Store products (inventory items)
 * - TaskEntity: Daily store tasks
 * 
 * Version Management:
 * - Version 1: Initial schema with products and tasks
 * - exportSchema = false: Don't export schema for now (enable for production)
 * 
 * Usage:
 * - Injected via Hilt (singleton)
 * - Access DAOs through this class
 * - Never create instance manually - use DI
 * 
 * Future Additions:
 * - StockChangeEntity: Audit trail for inventory changes
 * - IssueEntity: Store issue reports
 * - PetInfoEntity: Pet care information (if needed)
 */
@Database(
    entities = [
        ProductEntity::class,
        TaskEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AuroraDatabase : RoomDatabase() {
    
    /**
     * Product DAO for product-related operations
     */
    abstract fun productDao(): ProductDao
    
    /**
     * Task DAO for task-related operations
     */
    abstract fun taskDao(): TaskDao
    
    companion object {
        /**
         * Database name
         * Matches constant in Constants.kt
         */
        const val DATABASE_NAME = "aurora_companion_db"
    }
}
