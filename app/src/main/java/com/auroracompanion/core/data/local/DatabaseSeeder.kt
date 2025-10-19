package com.auroracompanion.core.data.local

import android.content.Context
import com.auroracompanion.R
import com.auroracompanion.core.data.model.ProductDto
import com.auroracompanion.core.data.model.TaskDto
import com.auroracompanion.feature.product.data.local.entity.ProductEntity
import com.auroracompanion.feature.task.data.local.entity.TaskEntity
import com.auroracompanion.feature.inventory.data.local.entity.StockMovementEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

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
            // Check if already seeded (products exist)
            val productCount = database.productDao().getProductCount().first()
            if (productCount > 0) {
                // Already seeded, skip
                return@withContext true
            }
            
            seedProducts()
            seedTasks()
            seedStockMovements()  // Optional: seed sample stock history
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
     * Also sets realistic stock levels and minimum thresholds.
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
        
        // Convert DTOs to Entities with realistic stock levels
        val productEntities = productDtos.mapIndexed { index, dto ->
            // Vary stock levels for testing
            val baseStock = dto.stockQty
            val adjustedStock = when {
                index % 5 == 0 -> 0  // Some out of stock
                index % 5 == 1 -> (baseStock * 0.3).toInt().coerceAtMost(5)  // Some low stock
                index % 5 == 2 -> (baseStock * 0.6).toInt()  // Medium stock
                else -> baseStock  // Full stock
            }
            
            // Set minimum stock levels based on category
            val minStock = when (dto.category.lowercase()) {
                "dogs", "cats" -> 15  // Popular categories need higher min
                "fish", "birds" -> 10  // Medium categories
                else -> 5  // Specialty categories
            }
            
            ProductEntity(
                sku = dto.sku,
                name = dto.name,
                category = dto.category,
                price = dto.price,
                stockQty = adjustedStock,
                description = dto.description,
                imageUri = dto.imageUri,
                lastModified = System.currentTimeMillis(),
                minStockLevel = minStock,
                lastStockUpdate = if (adjustedStock > 0) System.currentTimeMillis() else null
            )
        }
        
        // Insert into database
        database.productDao().insertAll(productEntities)
        
        println("✅ Seeded ${productEntities.size} products with varied stock levels")
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
     * Seed sample stock movements
     * 
     * Creates realistic stock movement history for first 5 products
     * to demonstrate the stock history feature.
     */
    private suspend fun seedStockMovements() {
        val productCount = database.productDao().getProductCount().first()
        if (productCount == 0) return
        
        val movements = mutableListOf<StockMovementEntity>()
        val now = System.currentTimeMillis()
        val staffMembers = listOf("Alice Johnson", "Bob Smith", "Carol Davis", "David Wilson")
        
        // Create movements for first 5 products
        for (productId in 1..5.coerceAtMost(productCount)) {
            // Received stock 30 days ago
            movements.add(
                StockMovementEntity(
                    productId = productId,
                    quantityChange = 50,
                    movementType = "RECEIVED",
                    reason = "Initial stock delivery",
                    staffMember = staffMembers.random(),
                    timestamp = now - (30L * 24 * 60 * 60 * 1000)
                )
            )
            
            // Some sales over the past month
            for (i in 1..Random.nextInt(3, 8)) {
                movements.add(
                    StockMovementEntity(
                        productId = productId,
                        quantityChange = -Random.nextInt(1, 5),
                        movementType = "SOLD",
                        reason = "Customer purchase",
                        staffMember = staffMembers.random(),
                        timestamp = now - (Random.nextLong(1, 30) * 24 * 60 * 60 * 1000)
                    )
                )
            }
            
            // Maybe a return
            if (Random.nextBoolean()) {
                movements.add(
                    StockMovementEntity(
                        productId = productId,
                        quantityChange = Random.nextInt(1, 3),
                        movementType = "RETURNED",
                        reason = "Customer return - unopened",
                        staffMember = staffMembers.random(),
                        timestamp = now - (Random.nextLong(1, 15) * 24 * 60 * 60 * 1000)
                    )
                )
            }
            
            // Maybe a damaged item
            if (Random.nextBoolean()) {
                movements.add(
                    StockMovementEntity(
                        productId = productId,
                        quantityChange = -1,
                        movementType = "DAMAGED",
                        reason = "Package damaged during display",
                        staffMember = staffMembers.random(),
                        timestamp = now - (Random.nextLong(1, 20) * 24 * 60 * 60 * 1000)
                    )
                )
            }
        }
        
        // Insert all movements
        database.stockMovementDao().insertMovements(movements)
        
        println("✅ Seeded ${movements.size} stock movements for demonstration")
    }
    
    /**
     * Clear all data from database
     * Useful for reset/testing
     */
    suspend fun clearDatabase() = withContext(Dispatchers.IO) {
        try {
            database.productDao().deleteAllProducts()
            database.taskDao().deleteAllTasks()
            database.stockMovementDao().deleteAllMovements()
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
