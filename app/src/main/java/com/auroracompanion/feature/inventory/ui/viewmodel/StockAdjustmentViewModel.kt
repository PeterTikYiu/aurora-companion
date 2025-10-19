package com.auroracompanion.feature.inventory.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auroracompanion.core.data.model.MovementType
import com.auroracompanion.core.data.model.Product
import com.auroracompanion.core.data.repository.InventoryRepository
import com.auroracompanion.core.data.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Stock Adjustment ViewModel
 * 
 * Manages state for stock adjustment screen.
 * Handles validation, submission, and error handling for stock changes.
 * 
 * Features:
 * - Add/remove stock with validation
 * - Movement type selection
 * - Reason input with suggestions
 * - Real-time new stock level calculation
 * - Staff member tracking
 * - Error handling and user feedback
 * 
 * Validation Rules:
 * - Quantity must be positive
 * - Removal cannot exceed current stock
 * - Movement type required
 * - Staff member optional but recommended
 * 
 * @param inventoryRepository Repository for inventory operations
 */
@HiltViewModel
class StockAdjustmentViewModel @Inject constructor(
    private val inventoryRepository: InventoryRepository
) : ViewModel() {
    
    // Adjustment UI state
    private val _adjustmentState = MutableStateFlow<AdjustmentState>(AdjustmentState.Idle)
    val adjustmentState: StateFlow<AdjustmentState> = _adjustmentState.asStateFlow()
    
    /**
     * Submit stock adjustment
     * 
     * @param product Product to adjust
     * @param quantity Amount to adjust (always positive)
     * @param isAddition True for add, false for remove
     * @param movementType Type of movement
     * @param reason Optional reason
     * @param staffMember Staff member name
     */
    fun submitAdjustment(
        product: Product,
        quantity: Int,
        isAddition: Boolean,
        movementType: MovementType,
        reason: String?,
        staffMember: String?
    ) {
        viewModelScope.launch {
            // Validation
            if (quantity <= 0) {
                _adjustmentState.value = AdjustmentState.Error("Quantity must be positive")
                return@launch
            }
            
            if (!isAddition && quantity > product.stockQty) {
                _adjustmentState.value = AdjustmentState.Error(
                    "Cannot remove $quantity units. Only ${product.stockQty} available."
                )
                return@launch
            }
            
            _adjustmentState.value = AdjustmentState.Loading
            
            // Calculate quantity change (negative for removals)
            val quantityChange = if (isAddition) quantity else -quantity
            
            // Submit adjustment
            val result = inventoryRepository.adjustStock(
                productId = product.id,
                quantityChange = quantityChange,
                movementType = movementType,
                reason = reason,
                staffMember = staffMember
            )
            
            _adjustmentState.value = when (result) {
                is Result.Success -> AdjustmentState.Success(result.data)
                is Result.Error -> AdjustmentState.Error(
                    result.exception?.message ?: "Failed to adjust stock"
                )
                is Result.Loading -> AdjustmentState.Loading
            }
        }
    }
    
    /**
     * Calculate new stock level preview
     * 
     * @param currentStock Current stock quantity
     * @param quantity Adjustment quantity
     * @param isAddition True for add, false for remove
     * @return New stock level, or null if invalid
     */
    fun calculateNewStockLevel(
        currentStock: Int,
        quantity: Int,
        isAddition: Boolean
    ): Int? {
        if (quantity <= 0) return null
        
        val newLevel = if (isAddition) {
            currentStock + quantity
        } else {
            currentStock - quantity
        }
        
        return if (newLevel >= 0) newLevel else null
    }
    
    /**
     * Validate adjustment input
     * 
     * @param quantity Adjustment quantity
     * @param currentStock Current stock level
     * @param isAddition True for add, false for remove
     * @return Validation message, or null if valid
     */
    fun validateAdjustment(
        quantity: Int,
        currentStock: Int,
        isAddition: Boolean
    ): String? {
        return when {
            quantity <= 0 -> "Quantity must be greater than 0"
            !isAddition && quantity > currentStock -> "Cannot remove $quantity units (only $currentStock available)"
            else -> null
        }
    }
    
    /**
     * Get suggested reasons for movement type
     * 
     * @param movementType Type of movement
     * @return List of suggested reasons
     */
    fun getSuggestedReasons(movementType: MovementType): List<String> {
        return when (movementType) {
            MovementType.RECEIVED -> listOf(
                "Shipment from supplier",
                "Restock delivery",
                "Transfer from warehouse"
            )
            MovementType.SOLD -> listOf(
                "Customer purchase",
                "Online order fulfillment",
                "Bulk sale"
            )
            MovementType.DAMAGED -> listOf(
                "Package damaged in transit",
                "Product expired",
                "Display damage",
                "Customer return - damaged"
            )
            MovementType.EXPIRED -> listOf(
                "Past expiry date",
                "Quality issue",
                "Recalled product"
            )
            MovementType.CORRECTION -> listOf(
                "Inventory count correction",
                "System error fix",
                "Stock audit adjustment"
            )
            MovementType.RETURNED -> listOf(
                "Customer return - unused",
                "Store credit return",
                "Exchange return"
            )
            MovementType.TRANSFER_OUT -> listOf(
                "Transfer to other store",
                "Warehouse return",
                "Regional redistribution"
            )
            MovementType.TRANSFER_IN -> listOf(
                "Transfer from other store",
                "Warehouse allocation",
                "Regional rebalance"
            )
        }
    }
    
    /**
     * Reset adjustment state
     */
    fun resetState() {
        _adjustmentState.value = AdjustmentState.Idle
    }
}

/**
 * Stock Adjustment State
 */
sealed interface AdjustmentState {
    data object Idle : AdjustmentState
    data object Loading : AdjustmentState
    data class Success(val newStockLevel: Int) : AdjustmentState
    data class Error(val message: String) : AdjustmentState
}
