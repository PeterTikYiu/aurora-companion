package com.auroracompanion.core.data.repository

import com.auroracompanion.core.data.Result
import com.auroracompanion.core.data.asResult
import com.auroracompanion.feature.product.domain.model.Product
import com.auroracompanion.core.data.model.StockMovement
import com.auroracompanion.core.data.model.MovementType
import com.auroracompanion.feature.product.data.local.dao.ProductDao
import com.auroracompanion.feature.product.data.local.entity.ProductEntity
import com.auroracompanion.feature.inventory.data.local.dao.StockMovementDao
import com.auroracompanion.feature.inventory.data.local.entity.StockMovementEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Inventory Repository
 * 
 * Manages inventory operations including stock adjustments and audit trail.
 * Acts as single source of truth for inventory data between database and UI.
 * 
 * Responsibilities:
 * - Stock level adjustments with validation
 * - Audit trail creation for all stock changes
 * - Low stock detection and filtering
 * - Stock history retrieval and analysis
 * - Inventory search and filtering
 * 
 * Architecture:
 * - Combines ProductDao and StockMovementDao
 * - Returns Flow for reactive UI updates
 * - Wraps operations in Result for error handling
 * - Converts entities to domain models
 * 
 * @param productDao DAO for product database operations
 * @param stockMovementDao DAO for stock movement audit trail
 */
@Singleton
class InventoryRepository @Inject constructor(
    private val productDao: ProductDao,
    private val stockMovementDao: StockMovementDao
) {
    
    /**
     * Adjust stock level for a product
     * 
     * Creates audit trail entry and updates product stock quantity.
     * Validates that resulting stock is non-negative.
     * 
     * @param productId Product to adjust
     * @param quantityChange Amount to add (positive) or remove (negative)
     * @param movementType Type of movement (RECEIVED, SOLD, etc.)
     * @param reason Optional detailed explanation
     * @param staffMember Name of staff performing adjustment
     * @return Success with new stock level, or Error if validation fails
     */
    suspend fun adjustStock(
        productId: Int,
        quantityChange: Int,
        movementType: MovementType,
        reason: String?,
        staffMember: String?
    ): Result<Int> {
        return try {
            // Get current product
            val productEntity = productDao.getProductById(productId).first()
                ?: return Result.Error("Product not found")
            
            // Calculate new stock level
            val newStockQty = productEntity.stockQty + quantityChange
            
            // Validate non-negative stock
            if (newStockQty < 0) {
                return Result.Error(
                    "Insufficient stock. Current: ${productEntity.stockQty}, Attempted removal: ${-quantityChange}"
                )
            }
            
            // Create stock movement record (audit trail)
            val movement = StockMovementEntity(
                productId = productId,
                quantityChange = quantityChange,
                movementType = movementType.name,
                reason = reason,
                staffMember = staffMember,
                timestamp = System.currentTimeMillis()
            )
            stockMovementDao.insertMovement(movement)
            
            // Update product stock level
            val updatedProduct = productEntity.copy(
                stockQty = newStockQty,
                lastStockUpdate = System.currentTimeMillis(),
                lastModified = System.currentTimeMillis()
            )
            productDao.updateProduct(updatedProduct)
            
            Result.Success(newStockQty)
        } catch (e: Exception) {
            Result.Error("Failed to adjust stock: ${e.message}", e)
        }
    }
    
    /**
     * Get all products for inventory list
     * 
     * @return Flow of products wrapped in Result
     */
    fun getAllProducts(): Flow<Result<List<Product>>> {
        return productDao.getAllProducts()
            .map { entities -> entities.map { it.toProduct() } }
            .asResult()
    }
    
    /**
     * Search inventory by product name or SKU
     * 
     * @param query Search term
     * @return Flow of matching products
     */
    fun searchInventory(query: String): Flow<Result<List<Product>>> {
        return if (query.isBlank()) {
            getAllProducts()
        } else {
            productDao.searchProducts(query)
                .map { entities -> entities.map { it.toProduct() } }
                .asResult()
        }
    }
    
    /**
     * Get products filtered by category
     * 
     * @param category Product category (Dog, Cat, Fish, etc.)
     * @return Flow of products in category
     */
    fun getProductsByCategory(category: String): Flow<Result<List<Product>>> {
        return productDao.getProductsByCategory(category)
            .map { entities -> entities.map { it.toProduct() } }
            .asResult()
    }
    
    /**
     * Get low stock products
     * 
     * Products where stockQty <= minStockLevel
     * 
     * @return Flow of low stock products
     */
    fun getLowStockProducts(): Flow<Result<List<Product>>> {
        return productDao.getAllProducts()
            .map { entities ->
                entities
                    .filter { it.stockQty <= it.minStockLevel }
                    .map { it.toProduct() }
            }
            .asResult()
    }
    
    /**
     * Get out of stock products
     * 
     * Products where stockQty = 0
     * 
     * @return Flow of out of stock products
     */
    fun getOutOfStockProducts(): Flow<Result<List<Product>>> {
        return productDao.getAllProducts()
            .map { entities ->
                entities
                    .filter { it.stockQty == 0 }
                    .map { it.toProduct() }
            }
            .asResult()
    }
    
    /**
     * Get stock history for a product
     * 
     * @param productId Product ID
     * @return Flow of stock movements for this product
     */
    fun getStockHistory(productId: Int): Flow<Result<List<StockMovement>>> {
        return stockMovementDao.getMovementsByProduct(productId)
            .map { entities -> entities.map { it.toStockMovement() } }
            .asResult()
    }
    
    /**
     * Get stock movements within date range
     * 
     * @param startTimestamp Start of range (milliseconds)
     * @param endTimestamp End of range (milliseconds)
     * @return Flow of movements in range
     */
    fun getStockMovementsByDateRange(
        startTimestamp: Long,
        endTimestamp: Long
    ): Flow<Result<List<StockMovement>>> {
        return stockMovementDao.getMovementsByDateRange(startTimestamp, endTimestamp)
            .map { entities -> entities.map { it.toStockMovement() } }
            .asResult()
    }
    
    /**
     * Get recent stock movements across all products
     * 
     * @param limit Maximum number of movements to return
     * @return Flow of recent movements
     */
    fun getRecentStockMovements(limit: Int = 50): Flow<Result<List<StockMovement>>> {
        return stockMovementDao.getRecentMovements(limit)
            .map { entities -> entities.map { it.toStockMovement() } }
            .asResult()
    }
    
    /**
     * Get inventory statistics
     * 
     * @return Map of statistics (total products, low stock count, out of stock count, total inventory value)
     */
    suspend fun getInventoryStats(): Result<Map<String, Any>> {
        return try {
            val allProducts = productDao.getAllProducts()
            val productList = allProducts
            
            // Wait for Flow to emit
            var products: List<ProductEntity> = emptyList()
            productList.collect { products = it }
            
            val totalProducts = products.size
            val lowStockCount = products.count { it.stockQty <= it.minStockLevel }
            val outOfStockCount = products.count { it.stockQty == 0 }
            val totalInventoryValue = products.sumOf { it.price * it.stockQty }
            val totalStockUnits = products.sumOf { it.stockQty }
            
            val stats = mapOf(
                "totalProducts" to totalProducts,
                "lowStockCount" to lowStockCount,
                "outOfStockCount" to outOfStockCount,
                "totalInventoryValue" to totalInventoryValue,
                "totalStockUnits" to totalStockUnits
            )
            
            Result.Success(stats)
        } catch (e: Exception) {
            Result.Error("Failed to get inventory stats: ${e.message}", e)
        }
    }
    
    /**
     * Update product's minimum stock level threshold
     * 
     * @param productId Product to update
     * @param minStockLevel New minimum stock threshold
     */
    suspend fun updateMinStockLevel(productId: Int, minStockLevel: Int): Result<Unit> {
        return try {
            val productEntity = productDao.getProductById(productId).first()
                ?: return Result.Error("Product not found")
            
            val updated = productEntity.copy(
                minStockLevel = minStockLevel,
                lastModified = System.currentTimeMillis()
            )
            productDao.updateProduct(updated)
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to update min stock level: ${e.message}", e)
        }
    }
    
    /**
     * Convert ProductEntity to Product domain model
     */
    private fun ProductEntity.toProduct(): Product {
        return Product(
            id = this.id,
            sku = this.sku,
            name = this.name,
            category = this.category,
            price = this.price,
            stockQty = this.stockQty,
            description = this.description,
            imageUri = this.imageUri,
            lastModified = this.lastModified
        )
    }
    
    /**
     * Convert StockMovementEntity to StockMovement domain model
     */
    private fun StockMovementEntity.toStockMovement(): StockMovement {
        return StockMovement(
            id = this.id,
            productId = this.productId,
            quantityChange = this.quantityChange,
            movementType = MovementType.valueOf(this.movementType),
            reason = this.reason,
            staffMember = this.staffMember,
            timestamp = this.timestamp
        )
    }
}
