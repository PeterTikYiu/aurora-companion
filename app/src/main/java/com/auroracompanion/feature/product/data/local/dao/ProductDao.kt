package com.auroracompanion.feature.product.data.local.dao

import androidx.room.*
import com.auroracompanion.feature.product.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Product operations
 * 
 * Provides database operations for products with reactive Flow support.
 * All queries return Flow for automatic UI updates when data changes.
 * 
 * Key Operations:
 * - getAllProducts: Returns all products as Flow (auto-updates UI)
 * - getProductById: Get single product by ID
 * - getProductBySku: Get product by SKU (for barcode scanning)
 * - searchProducts: Full-text search by name
 * - getProductsByCategory: Filter by pet category
 * - getLowStockProducts: Find products below threshold
 * - insertProduct: Add new product
 * - insertAll: Bulk insert (for seeding database)
 * - updateProduct: Update existing product
 * - updateStock: Quick stock quantity update
 * - deleteProduct: Remove product
 * 
 * Note: All suspend functions run on background thread automatically.
 * Flow queries emit updates when database changes.
 */
@Dao
interface ProductDao {
    
    /**
     * Get all products as Flow
     * Automatically updates UI when data changes
     */
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>
    
    /**
     * Get single product by ID
     */
    @Query("SELECT * FROM products WHERE id = :productId")
    fun getProductById(productId: Int): Flow<ProductEntity?>
    
    /**
     * Get product by SKU (for barcode scanning)
     */
    @Query("SELECT * FROM products WHERE sku = :sku LIMIT 1")
    suspend fun getProductBySku(sku: String): ProductEntity?
    
    /**
     * Search products by name (case-insensitive)
     * Uses LIKE for partial matching
     */
    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchProducts(query: String): Flow<List<ProductEntity>>
    
    /**
     * Get products by category
     */
    @Query("SELECT * FROM products WHERE category = :category ORDER BY name ASC")
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>>
    
    /**
     * Get low stock products (below threshold)
     * Useful for stock alerts
     */
    @Query("SELECT * FROM products WHERE stockQty <= :threshold ORDER BY stockQty ASC")
    fun getLowStockProducts(threshold: Int = 10): Flow<List<ProductEntity>>
    
    /**
     * Get out of stock products
     */
    @Query("SELECT * FROM products WHERE stockQty = 0 ORDER BY name ASC")
    fun getOutOfStockProducts(): Flow<List<ProductEntity>>
    
    /**
     * Get all categories (distinct)
     * Useful for filter dropdown
     */
    @Query("SELECT DISTINCT category FROM products ORDER BY category ASC")
    fun getAllCategories(): Flow<List<String>>
    
    /**
     * Insert single product
     * @return row ID of inserted product
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long
    
    /**
     * Insert multiple products (for database seeding)
     * @return list of row IDs
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>): List<Long>
    
    /**
     * Update existing product
     */
    @Update
    suspend fun updateProduct(product: ProductEntity)
    
    /**
     * Update stock quantity only (faster than full update)
     */
    @Query("UPDATE products SET stockQty = :newQuantity, lastModified = :timestamp WHERE id = :productId")
    suspend fun updateStock(productId: Int, newQuantity: Int, timestamp: Long = System.currentTimeMillis())
    
    /**
     * Delete product
     */
    @Delete
    suspend fun deleteProduct(product: ProductEntity)
    
    /**
     * Delete all products (for testing/reset)
     */
    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()
    
    /**
     * Get product count (for statistics)
     */
    @Query("SELECT COUNT(*) FROM products")
    fun getProductCount(): Flow<Int>
}
