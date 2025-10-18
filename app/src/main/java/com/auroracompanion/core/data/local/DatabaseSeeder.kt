package com.auroracompanion.core.data.local

import android.content.Context
import com.auroracompanion.R
import com.auroracompanion.core.data.model.ProductDto
import com.auroracompanion.core.data.model.TaskDto
import com.auroracompanion.feature.product.data.local.entity.ProductEntity
import com.auroracompanion.feature.task.data.local.entity.TaskEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Database Seeder
 * 
 * Seeds the database with initial data from JSON files.
 * Runs on first app launch to populate products and tasks.
 * 
 * Why seed the database?
 * - Provides demo data immediately
 * - No need for backend initially
 * - Users can explore features right away
 * 
 * When is seeding triggered?
 * - Automatically on first launch (via DataStore flag)
 * - Manually via Settings (for reset/testing)
 * 
 * How it works:
 * 1. Read JSON files from res/raw
 * 2. Parse JSON to DTOs using Gson
 * 3. Convert DTOs to Entities
 * 4. Insert into Room database
 * 
 * @param context Application context for accessing resources
 * @param database Aurora database instance
 */
@Singleton
class DatabaseSeeder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AuroraDatabase
) {
    
    private val gson = Gson()
    
    /**
     * Seed database with all initial data
     * 
     * @return true if seeding successful, false if error
     */
    suspend fun seedDatabase(): Boolean = withContext(Dispatchers.IO) {
        try {
            seedProducts()
            seedTasks()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Seed products from JSON
     * 
     * Reads products.json from res/raw, parses it,
     * and inserts products into database.
     */
    private suspend fun seedProducts() {
        // Read JSON from resources
        val json = context.resources
            .openRawResource(R.raw.products)
            .bufferedReader()
            .use { it.readText() }
        
        // Parse JSON to list of ProductDto
        val productDtos: List<ProductDto> = gson.fromJson(
            json,
            object : TypeToken<List<ProductDto>>() {}.type
        )
        
        // Convert DTOs to Entities
        val productEntities = productDtos.map { dto ->
            ProductEntity(
                sku = dto.sku,
                name = dto.name,
                category = dto.category,
                price = dto.price,
                stockQty = dto.stockQty,
                description = dto.description,
                imageUri = dto.imageUri,
                lastModified = System.currentTimeMillis()
            )
        }
        
        // Insert into database
        database.productDao().insertAll(productEntities)
        
        println("✅ Seeded ${productEntities.size} products")
    }
    
    /**
     * Seed tasks from JSON
     * 
     * Reads tasks.json from res/raw, parses it,
     * and inserts tasks into database.
     */
    private suspend fun seedTasks() {
        // Read JSON from resources
        val json = context.resources
            .openRawResource(R.raw.tasks)
            .bufferedReader()
            .use { it.readText() }
        
        // Parse JSON to list of TaskDto
        val taskDtos: List<TaskDto> = gson.fromJson(
            json,
            object : TypeToken<List<TaskDto>>() {}.type
        )
        
        // Convert DTOs to Entities
        val taskEntities = taskDtos.map { dto ->
            TaskEntity(
                title = dto.title,
                description = dto.description,
                priority = dto.priority,
                assignedTo = dto.assignedTo,
                dueDate = dto.dueDate,
                isCompleted = false,
                createdAt = System.currentTimeMillis(),
                completedAt = null
            )
        }
        
        // Insert into database
        database.taskDao().insertAll(taskEntities)
        
        println("✅ Seeded ${taskEntities.size} tasks")
    }
    
    /**
     * Clear all data from database
     * Useful for reset/testing
     */
    suspend fun clearDatabase() = withContext(Dispatchers.IO) {
        try {
            database.productDao().deleteAllProducts()
            database.taskDao().deleteAllTasks()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Re-seed database (clear + seed)
     * Useful for reset functionality
     */
    suspend fun reseedDatabase(): Boolean {
        return if (clearDatabase()) {
            seedDatabase()
        } else {
            false
        }
    }
}
