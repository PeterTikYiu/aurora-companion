package com.auroracompanion.feature.inventory.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auroracompanion.core.data.model.Product
import com.auroracompanion.core.data.repository.InventoryRepository
import com.auroracompanion.core.data.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Inventory ViewModel
 * 
 * Manages state for inventory list screen with comprehensive filtering and search.
 * 
 * Features:
 * - Search products by name/SKU
 * - Filter by stock status (All/Low Stock/Out of Stock)
 * - Filter by category
 * - Display stock level indicators
 * - Calculate low stock alert count
 * - Sort inventory
 * 
 * Architecture:
 * - StateFlow for reactive UI updates
 * - Flow operators for data transformation
 * - Repository pattern for data access
 * - Result wrapper for error handling
 * 
 * @param inventoryRepository Repository for inventory operations
 */
@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val inventoryRepository: InventoryRepository
) : ViewModel() {
    
    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // Stock status filter
    private val _selectedStockStatus = MutableStateFlow(StockStatus.ALL)
    val selectedStockStatus: StateFlow<StockStatus> = _selectedStockStatus.asStateFlow()
    
    // Category filter (null = all categories)
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()
    
    // Sort option
    private val _sortOption = MutableStateFlow(SortOption.NAME)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()
    
    // Low stock alert count
    val lowStockCount: StateFlow<Int> = inventoryRepository.getLowStockProducts()
        .map { result ->
            when (result) {
                is Result.Success -> result.data.size
                else -> 0
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
    
    // UI State
    val uiState: StateFlow<InventoryUiState> = combine(
        searchQuery,
        selectedStockStatus,
        selectedCategory,
        sortOption
    ) { query, stockStatus, category, sort ->
        // Get base flow based on filters
        val baseFlow = when {
            // Search takes precedence
            query.isNotEmpty() -> inventoryRepository.searchInventory(query)
            
            // Category filter
            category != null -> inventoryRepository.getProductsByCategory(category)
            
            // Stock status filter
            stockStatus == StockStatus.LOW_STOCK -> inventoryRepository.getLowStockProducts()
            stockStatus == StockStatus.OUT_OF_STOCK -> inventoryRepository.getOutOfStockProducts()
            
            // All products
            else -> inventoryRepository.getAllProducts()
        }
        
        // Apply additional stock status filter if search or category is active
        baseFlow.map { result ->
            when (result) {
                is Result.Loading -> result
                is Result.Success -> {
                    var filtered = result.data
                    
                    // Apply stock status filter if not already filtered by it
                    if (query.isNotEmpty() || category != null) {
                        filtered = when (stockStatus) {
                            StockStatus.LOW_STOCK -> filtered.filter { it.stockQty <= 10 } // TODO: Use product.minStockLevel
                            StockStatus.OUT_OF_STOCK -> filtered.filter { it.stockQty == 0 }
                            StockStatus.ALL -> filtered
                        }
                    }
                    
                    Result.Success(filtered)
                }
                is Result.Error -> result
            }
        }
    }.flatMapLatest { flow ->
        flow.map { result ->
            when (result) {
                is Result.Loading -> InventoryUiState.Loading
                is Result.Success -> {
                    val products = result.data
                    
                    // Apply sorting
                    val sortedProducts = when (_sortOption.value) {
                        SortOption.NAME -> products.sortedBy { it.name }
                        SortOption.STOCK_ASC -> products.sortedBy { it.stockQty }
                        SortOption.STOCK_DESC -> products.sortedByDescending { it.stockQty }
                        SortOption.CATEGORY -> products.sortedBy { it.category }
                        SortOption.PRICE_ASC -> products.sortedBy { it.price }
                        SortOption.PRICE_DESC -> products.sortedByDescending { it.price }
                    }
                    
                    if (sortedProducts.isEmpty()) {
                        InventoryUiState.Empty(getEmptyMessage())
                    } else {
                        InventoryUiState.Success(sortedProducts)
                    }
                }
                is Result.Error -> InventoryUiState.Error(
                    result.exception?.message ?: "Unknown error occurred"
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InventoryUiState.Loading
    )
    
    /**
     * Update search query
     */
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
    
    /**
     * Clear search
     */
    fun clearSearch() {
        _searchQuery.value = ""
    }
    
    /**
     * Select stock status filter
     */
    fun onStockStatusSelected(status: StockStatus) {
        _selectedStockStatus.value = status
    }
    
    /**
     * Select category filter
     */
    fun onCategorySelected(category: String?) {
        _selectedCategory.value = category
    }
    
    /**
     * Select sort option
     */
    fun onSortSelected(sort: SortOption) {
        _sortOption.value = sort
    }
    
    /**
     * Refresh inventory
     */
    fun refresh() {
        // Flow automatically refreshes
    }
    
    /**
     * Get stock status for a product
     */
    fun getStockStatus(product: Product): ProductStockStatus {
        return when {
            product.stockQty == 0 -> ProductStockStatus.OUT_OF_STOCK
            product.stockQty <= 10 -> ProductStockStatus.LOW_STOCK // TODO: Use product.minStockLevel
            else -> ProductStockStatus.IN_STOCK
        }
    }
    
    /**
     * Get empty state message based on current filters
     */
    private fun getEmptyMessage(): String {
        return when {
            _searchQuery.value.isNotEmpty() -> "No products found matching \"${_searchQuery.value}\""
            _selectedCategory.value != null -> "No ${_selectedCategory.value} products found"
            _selectedStockStatus.value == StockStatus.LOW_STOCK -> "No low stock products! 🎉"
            _selectedStockStatus.value == StockStatus.OUT_OF_STOCK -> "No out of stock products! ✨"
            else -> "No products in inventory"
        }
    }
}

/**
 * Inventory UI State
 */
sealed interface InventoryUiState {
    data object Loading : InventoryUiState
    data class Success(val products: List<Product>) : InventoryUiState
    data class Error(val message: String) : InventoryUiState
    data class Empty(val message: String) : InventoryUiState
}

/**
 * Stock Status Filter
 */
enum class StockStatus {
    ALL,
    LOW_STOCK,
    OUT_OF_STOCK
}

/**
 * Product Stock Status (for UI indicators)
 */
enum class ProductStockStatus {
    IN_STOCK,
    LOW_STOCK,
    OUT_OF_STOCK
}

/**
 * Sort Options
 */
enum class SortOption {
    NAME,
    STOCK_ASC,
    STOCK_DESC,
    CATEGORY,
    PRICE_ASC,
    PRICE_DESC
}
