package com.auroracompanion.feature.product.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auroracompanion.core.data.Result
import com.auroracompanion.core.ui.UiState
import com.auroracompanion.feature.product.data.repository.ProductRepository
import com.auroracompanion.feature.product.domain.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Product ViewModel
 * 
 * Manages UI state for product-related screens.
 * Handles user interactions and business logic.
 * 
 * Key Responsibilities:
 * - Load products from repository
 * - Handle search queries
 * - Filter by category
 * - Manage UI state (Loading, Success, Error, Empty)
 * - Update stock quantities
 * 
 * State Management:
 * - Uses StateFlow for UI state
 * - Reactive to repository changes
 * - Debounced search (300ms)
 * 
 * @param productRepository Injected by Hilt
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {
    
    // Search query state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // Selected category filter
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()
    
    // UI State for product list
    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()
    
    // Available categories
    val categories: StateFlow<List<String>> = productRepository.getAllCategories()
        .map { result ->
            when (result) {
                is Result.Success -> result.data
                else -> emptyList()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    init {
        // Combine search and category filter
        combine(
            _searchQuery.debounce(300), // Debounce search input
            _selectedCategory
        ) { query, category ->
            query to category
        }.flatMapLatest { (query, category) ->
            when {
                // Category filter active
                category != null -> productRepository.getProductsByCategory(category)
                // Search query active
                query.isNotBlank() -> productRepository.searchProducts(query)
                // Show all products
                else -> productRepository.getAllProducts()
            }
        }.onEach { result ->
            _uiState.value = when (result) {
                is Result.Loading -> ProductUiState.Loading
                is Result.Success -> {
                    if (result.data.isEmpty()) {
                        ProductUiState.Empty
                    } else {
                        ProductUiState.Success(result.data)
                    }
                }
                is Result.Error -> ProductUiState.Error(result.message)
            }
        }.launchIn(viewModelScope)
    }
    
    /**
     * Update search query
     * Automatically triggers new search with debounce
     */
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
    
    /**
     * Clear search query
     */
    fun clearSearch() {
        _searchQuery.value = ""
    }
    
    /**
     * Select category filter
     * @param category Category name or null to clear filter
     */
    fun onCategorySelected(category: String?) {
        _selectedCategory.value = category
    }
    
    /**
     * Update product stock quantity
     */
    fun updateStock(productId: Int, newQuantity: Int) {
        viewModelScope.launch {
            val result = productRepository.updateStock(productId, newQuantity)
            // Could emit event here for Snackbar feedback
        }
    }
    
    /**
     * Refresh products
     * Useful for pull-to-refresh
     */
    fun refresh() {
        // Current implementation auto-refreshes via Flow
        // Manual refresh would require repository method
    }
}

/**
 * UI State for Product screens
 * 
 * Sealed interface ensures all states are handled in UI
 */
sealed interface ProductUiState {
    /**
     * Loading state - show progress indicator
     */
    data object Loading : ProductUiState
    
    /**
     * Success state with products
     */
    data class Success(val products: List<Product>) : ProductUiState
    
    /**
     * Error state with message
     */
    data class Error(val message: String) : ProductUiState
    
    /**
     * Empty state - no products found
     */
    data object Empty : ProductUiState
}
