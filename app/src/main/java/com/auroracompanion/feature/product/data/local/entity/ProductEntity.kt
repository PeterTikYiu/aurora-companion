package com.auroracompanion.feature.product.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

/**
 * Room Entity for Product
 * 
 * This represents a product in the local SQLite database.
 * 
 * Key Design Decisions:
 * - `id`: Auto-generated primary key for Room
 * - `sku`: Unique product identifier (indexed for fast lookups)
 * - `category`: Indexed for filtering by pet type
 * - `lastModified`: Timestamp for future sync functionality
 * 
 * Indexes:
 * - SKU index: Fast product lookups by barcode/SKU
 * - Category index: Fast filtering by pet category
 * - Stock index: Quick queries for low stock alerts
 * 
 * @property id Auto-generated unique ID
 * @property sku Stock Keeping Unit - unique product code
 * @property name Product display name
 * @property category Pet category (Dog, Cat, Fish, etc.)
 * @property price Product price in GBP
 * @property stockQty Current stock quantity
 * @property description Optional product description
 * @property imageUri Optional local image path
 * @property lastModified Timestamp of last update (for sync)
 */
@Entity(
    tableName = "products",
    indices = [
        Index(value = ["sku"], unique = true),
        Index(value = ["category"]),
        Index(value = ["stockQty"])
    ]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    val sku: String,
    val name: String,
    val category: String,
    val price: Double,
    val stockQty: Int,
    val description: String? = null,
    val imageUri: String? = null,
    val lastModified: Long = System.currentTimeMillis()
)
