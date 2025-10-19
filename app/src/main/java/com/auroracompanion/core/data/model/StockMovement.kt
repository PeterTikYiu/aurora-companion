package com.auroracompanion.core.data.model

/**
 * Stock Movement Domain Model
 * 
 * Represents a single stock adjustment event in the inventory system.
 * Used in the UI layer for displaying stock history and audit trails.
 * 
 * All stock changes (additions, removals, corrections) are tracked as movements
 * to maintain a complete audit trail for inventory management.
 * 
 * @property id Unique identifier for this movement
 * @property productId ID of the product this movement affects
 * @property quantityChange Amount of stock added (positive) or removed (negative)
 * @property movementType Category of stock movement (received, sold, damaged, etc.)
 * @property reason Optional detailed explanation for this movement
 * @property staffMember Name of staff member who performed this adjustment
 * @property timestamp Unix timestamp (milliseconds) when movement occurred
 */
data class StockMovement(
    val id: Int,
    val productId: Int,
    val quantityChange: Int,
    val movementType: MovementType,
    val reason: String?,
    val staffMember: String?,
    val timestamp: Long
)

/**
 * Stock Movement Types
 * 
 * Categorizes the reason for stock adjustments to enable
 * better reporting and audit trail analysis.
 */
enum class MovementType {
    /**
     * Stock received from supplier/shipment
     */
    RECEIVED,
    
    /**
     * Stock sold to customers
     */
    SOLD,
    
    /**
     * Stock damaged and removed from inventory
     */
    DAMAGED,
    
    /**
     * Stock expired and removed from inventory
     */
    EXPIRED,
    
    /**
     * Manual correction to fix inventory discrepancies
     */
    CORRECTION,
    
    /**
     * Stock returned by customer
     */
    RETURNED,
    
    /**
     * Stock transferred to another store
     */
    TRANSFER_OUT,
    
    /**
     * Stock received from another store
     */
    TRANSFER_IN;
    
    /**
     * Get display-friendly name for UI
     */
    fun displayName(): String = when (this) {
        RECEIVED -> "Received"
        SOLD -> "Sold"
        DAMAGED -> "Damaged"
        EXPIRED -> "Expired"
        CORRECTION -> "Correction"
        RETURNED -> "Returned"
        TRANSFER_OUT -> "Transfer Out"
        TRANSFER_IN -> "Transfer In"
    }
}
