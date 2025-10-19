package com.auroracompanion.di

import android.content.Context
import androidx.room.Room
import com.auroracompanion.core.data.local.AuroraDatabase
import com.auroracompanion.feature.product.data.local.dao.ProductDao
import com.auroracompanion.feature.task.data.local.dao.TaskDao
import com.auroracompanion.feature.inventory.data.local.dao.StockMovementDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module for Database Dependencies
 * 
 * Provides database-related dependencies to the entire application.
 * 
 * @InstallIn(SingletonComponent::class)
 * - Makes dependencies available app-wide
 * - Lives as long as the application
 * - Single instance shared across app
 * 
 * Provided Dependencies:
 * - AuroraDatabase: Main Room database instance
 * - ProductDao: Product data access
 * - TaskDao: Task data access
 * - StockMovementDao: Inventory audit trail access
 * 
 * Why Singleton?
 * - Database should only be created once
 * - Prevents multiple connections
 * - Better performance and memory usage
 * 
 * Migration Strategy:
 * - Version 1 → 2: Added stock tracking fields and movements table
 * - Future migrations should be added to AuroraDatabase.kt
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    /**
     * Provides Room Database instance
     * 
     * Creates database with:
     * - Application context (not Activity context - prevents leaks)
     * - Database name from constants
     * - Migration from v1 to v2 for inventory features
     * 
     * @param context Application context from Hilt
     * @return Singleton database instance
     */
    @Provides
    @Singleton
    fun provideAuroraDatabase(
        @ApplicationContext context: Context
    ): AuroraDatabase {
        return Room.databaseBuilder(
            context,
            AuroraDatabase::class.java,
            AuroraDatabase.DATABASE_NAME
        )
            // Add migration from version 1 to 2
            .addMigrations(AuroraDatabase.MIGRATION_1_2)
            
            // Fallback for development (remove in production)
            .fallbackToDestructiveMigration()
            
            .build()
    }
    
    /**
     * Provides ProductDao from database
     * 
     * @param database Aurora database instance
     * @return ProductDao for product operations
     */
    @Provides
    @Singleton
    fun provideProductDao(database: AuroraDatabase): ProductDao {
        return database.productDao()
    }
    
    /**
     * Provides TaskDao from database
     * 
     * @param database Aurora database instance
     * @return TaskDao for task operations
     */
    @Provides
    @Singleton
    fun provideTaskDao(database: AuroraDatabase): TaskDao {
        return database.taskDao()
    }
    
    /**
     * Provides StockMovementDao from database
     * 
     * @param database Aurora database instance
     * @return StockMovementDao for inventory audit trail
     */
    @Provides
    @Singleton
    fun provideStockMovementDao(database: AuroraDatabase): StockMovementDao {
        return database.stockMovementDao()
    }
}
