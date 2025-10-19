package com.auroracompanion.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.auroracompanion.feature.product.data.local.dao.ProductDao
import com.auroracompanion.feature.product.data.local.entity.ProductEntity
import com.auroracompanion.feature.task.data.local.dao.TaskDao
import com.auroracompanion.feature.task.data.local.entity.TaskEntity
import com.auroracompanion.feature.inventory.data.local.dao.StockMovementDao
import com.auroracompanion.feature.inventory.data.local.entity.StockMovementEntity

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
 * - StockMovementEntity: Inventory audit trail (v2+)
 * 
 * Version Management:
 * - Version 1: Initial schema with products and tasks
 * - Version 2: Added stock movements and inventory tracking fields
 * - exportSchema = false: Don't export schema for now (enable for production)
 * 
 * Usage:
 * - Injected via Hilt (singleton)
 * - Access DAOs through this class
 * - Never create instance manually - use DI
 * 
 * Future Additions:
 * - IssueEntity: Store issue reports
 * - PetInfoEntity: Pet care information (if needed)
 */
@Database(
    entities = [
        ProductEntity::class,
        TaskEntity::class,
        StockMovementEntity::class
    ],
    version = 2,
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
    
    /**
     * Stock Movement DAO for inventory audit trail
     */
    abstract fun stockMovementDao(): StockMovementDao
    
    companion object {
        /**
         * Database name
         * Matches constant in Constants.kt
         */
        const val DATABASE_NAME = "aurora_companion_db"
        
        /**
         * Migration from version 1 to 2
         * 
         * Changes:
         * - Add minStockLevel and lastStockUpdate columns to products table
         * - Create stock_movements table for inventory audit trail
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new columns to products table
                database.execSQL(
                    "ALTER TABLE products ADD COLUMN minStockLevel INTEGER NOT NULL DEFAULT 10"
                )
                database.execSQL(
                    "ALTER TABLE products ADD COLUMN lastStockUpdate INTEGER"
                )
                
                // Create stock_movements table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS stock_movements (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        productId INTEGER NOT NULL,
                        quantityChange INTEGER NOT NULL,
                        movementType TEXT NOT NULL,
                        reason TEXT,
                        staffMember TEXT,
                        timestamp INTEGER NOT NULL,
                        FOREIGN KEY(productId) REFERENCES products(id) ON DELETE CASCADE
                    )
                """)
                
                // Create indices for stock_movements
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_stock_movements_productId ON stock_movements(productId)"
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_stock_movements_timestamp ON stock_movements(timestamp)"
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_stock_movements_movementType ON stock_movements(movementType)"
                )
            }
        }
    }
}
