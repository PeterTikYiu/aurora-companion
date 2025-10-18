package com.auroracompanion.feature.product.domain.model

import com.auroracompanion.core.util.toCurrency

/**
 * Product Domain Model
 * 
 * Represents a product in the business logic layer.
 * Clean, independent of database implementation.
 * 
 * Why domain models?
 * - UI doesn't depend on database structure
 * - Can add business logic methods
 * - Easier to test
 * - Follows clean architecture
 * 
 * @property id Unique product identifier
 * @property sku Stock Keeping Unit
 * @property name Product name
 * @property category Pet category
 * @property price Price in GBP
 * @property stockQty Current stock quantity
 * @property description Optional description
 * @property imageUri Optional image path
 * @property lastModified Last update timestamp
 */
data class Product(
    val id: Int,
    val sku: String,
    val name: String,
    val category: String,
    val price: Double,
    val stockQty: Int,
    val description: String?,
    val imageUri: String?,
    val lastModified: Long
) {
    /**
     * Get formatted price string
     * Example: "£19.99"
     */
    val formattedPrice: String
        get() = price.toCurrency()
    
    /**
     * Check if product is in stock
     */
    val isInStock: Boolean
        get() = stockQty > 0
    
    /**
     * Check if product is low stock
     */
    val isLowStock: Boolean
        get() = stockQty in 1..10
    
    /**
     * Check if product is out of stock
     */
    val isOutOfStock: Boolean
        get() = stockQty == 0
    
    /**
     * Get stock status text
     */
    val stockStatus: StockStatus
        get() = when {
            isOutOfStock -> StockStatus.OUT_OF_STOCK
            isLowStock -> StockStatus.LOW_STOCK
            else -> StockStatus.IN_STOCK
        }
}

/**
 * Stock Status Enum
 * 
 * Represents different stock levels for UI display
 */
enum class StockStatus {
    IN_STOCK,
    LOW_STOCK,
    OUT_OF_STOCK
}
