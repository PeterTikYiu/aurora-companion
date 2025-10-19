package com.auroracompanion.feature.inventory.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.auroracompanion.feature.product.data.local.entity.ProductEntity

/**
 * Room Entity for Stock Movement
 * 
 * Represents a single stock adjustment event in the database.
 * Maintains an immutable audit trail of all inventory changes.
 * 
 * Design Notes:
 * - Append-only table (no updates/deletes) to preserve audit integrity
 * - Foreign key ensures referential integrity with products
 * - Indexed for efficient querying by product, timestamp, and movement type
 * - quantityChange is positive for additions, negative for removals
 * 
 * @property id Auto-generated unique ID
 * @property productId Foreign key to ProductEntity
 * @property quantityChange Amount of stock added (+) or removed (-)
 * @property movementType Type of stock movement (RECEIVED, SOLD, DAMAGED, etc.)
 * @property reason Optional detailed explanation
 * @property staffMember Name of staff who performed adjustment
 * @property timestamp Unix timestamp (milliseconds) when movement occurred
 */
@Entity(
    tableName = "stock_movements",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["productId"]),
        Index(value = ["timestamp"]),
        Index(value = ["movementType"])
    ]
)
data class StockMovementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    val productId: Int,
    val quantityChange: Int,
    val movementType: String, // Stored as String (enum name)
    val reason: String?,
    val staffMember: String?,
    val timestamp: Long = System.currentTimeMillis()
)
