package com.auroracompanion.di

import android.content.Context
import androidx.room.Room
import com.auroracompanion.core.data.local.AuroraDatabase
import com.auroracompanion.feature.product.data.local.dao.ProductDao
import com.auroracompanion.feature.task.data.local.dao.TaskDao
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
 * 
 * Why Singleton?
 * - Database should only be created once
 * - Prevents multiple connections
 * - Better performance and memory usage
 * 
 * Future Additions:
 * - Migration strategies for schema updates
 * - Database callbacks for seeding
 * - Query interceptors for logging
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
     * - Fallback to destructive migration (for development)
     * 
     * Production Note:
     * Replace fallbackToDestructiveMigration() with proper migrations
     * before releasing to users.
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
            // Development: Drop and recreate on schema changes
            // Production: Implement proper migrations
            .fallbackToDestructiveMigration()
            
            // Optional: Add callback for database creation
            // .addCallback(DatabaseCallback())
            
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
}
