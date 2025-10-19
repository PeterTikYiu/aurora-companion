package com.auroracompanion.feature.inventory.data.local.dao

import androidx.room.*
import com.auroracompanion.feature.inventory.data.local.entity.StockMovementEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Stock Movements
 * 
 * Provides query methods for inventory audit trail operations.
 * All methods return Flow for reactive updates to UI.
 * 
 * Query Strategy:
 * - Most queries ordered by timestamp DESC (newest first)
 * - Indexed fields used in WHERE clauses for performance
 * - No update/delete methods (append-only audit log)
 */
@Dao
interface StockMovementDao {
    
    /**
     * Insert a new stock movement
     * 
     * @param movement Stock movement to record
     * @return ID of inserted movement
     */
    @Insert
    suspend fun insertMovement(movement: StockMovementEntity): Long
    
    /**
     * Insert multiple stock movements (batch operation)
     * 
     * @param movements List of movements to record
     */
    @Insert
    suspend fun insertMovements(movements: List<StockMovementEntity>)
    
    /**
     * Get all stock movements for a specific product
     * 
     * @param productId Product ID to query
     * @return Flow of movements ordered by timestamp (newest first)
     */
    @Query("SELECT * FROM stock_movements WHERE productId = :productId ORDER BY timestamp DESC")
    fun getMovementsByProduct(productId: Int): Flow<List<StockMovementEntity>>
    
    /**
     * Get stock movements within a date range
     * 
     * @param startTimestamp Start of range (inclusive)
     * @param endTimestamp End of range (inclusive)
     * @return Flow of movements in range
     */
    @Query("SELECT * FROM stock_movements WHERE timestamp BETWEEN :startTimestamp AND :endTimestamp ORDER BY timestamp DESC")
    fun getMovementsByDateRange(startTimestamp: Long, endTimestamp: Long): Flow<List<StockMovementEntity>>
    
    /**
     * Get stock movements by type
     * 
     * @param movementType Type of movement (e.g., "RECEIVED", "SOLD")
     * @return Flow of movements of specified type
     */
    @Query("SELECT * FROM stock_movements WHERE movementType = :movementType ORDER BY timestamp DESC")
    fun getMovementsByType(movementType: String): Flow<List<StockMovementEntity>>
    
    /**
     * Get stock movements for a product within a date range
     * 
     * @param productId Product ID to query
     * @param startTimestamp Start of range
     * @param endTimestamp End of range
     * @return Flow of movements matching criteria
     */
    @Query("""
        SELECT * FROM stock_movements 
        WHERE productId = :productId 
        AND timestamp BETWEEN :startTimestamp AND :endTimestamp 
        ORDER BY timestamp DESC
    """)
    fun getProductMovementsByDateRange(
        productId: Int,
        startTimestamp: Long,
        endTimestamp: Long
    ): Flow<List<StockMovementEntity>>
    
    /**
     * Get recent stock movements across all products
     * 
     * @param limit Maximum number of movements to return
     * @return Flow of recent movements
     */
    @Query("SELECT * FROM stock_movements ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentMovements(limit: Int = 50): Flow<List<StockMovementEntity>>
    
    /**
     * Get total stock change for a product
     * 
     * @param productId Product ID to calculate
     * @return Sum of all quantity changes (can be used to verify current stock)
     */
    @Query("SELECT COALESCE(SUM(quantityChange), 0) FROM stock_movements WHERE productId = :productId")
    suspend fun getTotalStockChange(productId: Int): Int
    
    /**
     * Get count of movements for a product
     * 
     * @param productId Product ID to count
     * @return Number of stock movements recorded
     */
    @Query("SELECT COUNT(*) FROM stock_movements WHERE productId = :productId")
    suspend fun getMovementCount(productId: Int): Int
    
    /**
     * Delete all stock movements (for testing/reset only)
     * 
     * WARNING: This destroys audit trail. Use with caution.
     */
    @Query("DELETE FROM stock_movements")
    suspend fun deleteAllMovements()
    
    /**
     * Get movements by staff member
     * 
     * @param staffMember Name of staff member
     * @return Flow of movements performed by this staff member
     */
    @Query("SELECT * FROM stock_movements WHERE staffMember = :staffMember ORDER BY timestamp DESC")
    fun getMovementsByStaff(staffMember: String): Flow<List<StockMovementEntity>>
    
    /**
     * Get stock movements for a product by type
     * 
     * @param productId Product ID
     * @param movementType Type of movement
     * @return Flow of movements matching criteria
     */
    @Query("""
        SELECT * FROM stock_movements 
        WHERE productId = :productId 
        AND movementType = :movementType 
        ORDER BY timestamp DESC
    """)
    fun getProductMovementsByType(
        productId: Int,
        movementType: String
    ): Flow<List<StockMovementEntity>>
}
