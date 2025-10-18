package com.auroracompanion.feature.product.data.repository

import com.auroracompanion.core.data.Result
import com.auroracompanion.core.util.asResult
import com.auroracompanion.feature.product.data.local.dao.ProductDao
import com.auroracompanion.feature.product.data.local.entity.ProductEntity
import com.auroracompanion.feature.product.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Product Repository
 * 
 * Single source of truth for product data.
 * Abstracts data sources from ViewModels/UseCases.
 * 
 * Responsibilities:
 * - Fetch products from database
 * - Convert entities to domain models
 * - Handle errors and wrap in Result
 * - Provide reactive data streams (Flow)
 * 
 * Why Repository Pattern?
 * - Separation of concerns
 * - Easy to add remote data source later
 * - ViewModels don't know about database
 * - Easy to mock for testing
 * 
 * Data Flow:
 * Database (ProductEntity) → Repository → Domain Model (Product)
 * 
 * @param productDao Injected by Hilt
 */
@Singleton
class ProductRepository @Inject constructor(
    private val productDao: ProductDao
) {
    
    /**
     * Get all products as Flow<Result<List<Product>>>
     * 
     * Automatically wraps in Result (Loading, Success, Error)
     * Converts ProductEntity to Product domain model
     */
    fun getAllProducts(): Flow<Result<List<Product>>> {
        return productDao.getAllProducts()
            .map { entities -> entities.map { it.toDomainModel() } }
            .asResult()
    }
    
    /**
     * Get single product by ID
     */
    fun getProductById(productId: Int): Flow<Result<Product?>> {
        return productDao.getProductById(productId)
            .map { it?.toDomainModel() }
            .asResult()
    }
    
    /**
     * Search products by name
     */
    fun searchProducts(query: String): Flow<Result<List<Product>>> {
        return productDao.searchProducts(query)
            .map { entities -> entities.map { it.toDomainModel() } }
            .asResult()
    }
    
    /**
     * Get products by category
     */
    fun getProductsByCategory(category: String): Flow<Result<List<Product>>> {
        return productDao.getProductsByCategory(category)
            .map { entities -> entities.map { it.toDomainModel() } }
            .asResult()
    }
    
    /**
     * Get low stock products
     */
    fun getLowStockProducts(threshold: Int = 10): Flow<Result<List<Product>>> {
        return productDao.getLowStockProducts(threshold)
            .map { entities -> entities.map { it.toDomainModel() } }
            .asResult()
    }
    
    /**
     * Get out of stock products
     */
    fun getOutOfStockProducts(): Flow<Result<List<Product>>> {
        return productDao.getOutOfStockProducts()
            .map { entities -> entities.map { it.toDomainModel() } }
            .asResult()
    }
    
    /**
     * Get all categories
     */
    fun getAllCategories(): Flow<Result<List<String>>> {
        return productDao.getAllCategories()
            .asResult()
    }
    
    /**
     * Update product stock quantity
     * 
     * @param productId Product ID
     * @param newQuantity New stock quantity
     * @return Result of the operation
     */
    suspend fun updateStock(productId: Int, newQuantity: Int): Result<Unit> {
        return try {
            productDao.updateStock(productId, newQuantity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to update stock: ${e.message}", e)
        }
    }
    
    /**
     * Get product count
     */
    fun getProductCount(): Flow<Result<Int>> {
        return productDao.getProductCount()
            .asResult()
    }
}

/**
 * Extension function to convert ProductEntity to Product domain model
 * 
 * Why separate models?
 * - Database entities should not be exposed to UI layer
 * - Domain models can have additional business logic
 * - Easier to change database without affecting UI
 */
private fun ProductEntity.toDomainModel(): Product {
    return Product(
        id = id,
        sku = sku,
        name = name,
        category = category,
        price = price,
        stockQty = stockQty,
        description = description,
        imageUri = imageUri,
        lastModified = lastModified
    )
}
