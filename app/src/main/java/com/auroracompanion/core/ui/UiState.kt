package com.auroracompanion.core.ui

/**
 * Base UI State for screens
 * 
 * This sealed interface provides a common pattern for all screen states.
 * Each feature can extend this with feature-specific data.
 * 
 * Example Usage in a ViewModel:
 * ```
 * sealed interface ProductUiState : UiState {
 *     data object Loading : ProductUiState
 *     data class Success(val products: List<Product>) : ProductUiState
 *     data class Error(override val message: String) : ProductUiState
 *     data object Empty : ProductUiState
 * }
 * ```
 * 
 * Benefits:
 * - Consistent state handling across all screens
 * - Forces explicit empty state handling (better UX)
 * - Easy to test ViewModels
 */
sealed interface UiState {
    /**
     * Initial state when screen loads
     */
    data object Loading : UiState
    
    /**
     * Error state with message
     */
    data class Error(val message: String) : UiState
    
    /**
     * Empty state (no data available)
     */
    data object Empty : UiState
}

/**
 * UI Event for one-time actions
 * 
 * Use this for events that should only happen once, like:
 * - Showing a Snackbar
 * - Navigating to another screen
 * - Showing a dialog
 * 
 * Example:
 * ```
 * sealed interface ProductUiEvent : UiEvent {
 *     data class ShowSnackbar(override val message: String) : ProductUiEvent
 *     data class NavigateToDetail(val productId: Int) : ProductUiEvent
 * }
 * ```
 */
sealed interface UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent
    data class ShowError(val message: String) : UiEvent
    data class Navigate(val route: String) : UiEvent
}
