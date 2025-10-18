package com.auroracompanion.feature.product.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.auroracompanion.feature.product.ui.components.*
import com.auroracompanion.feature.product.ui.viewmodel.ProductViewModel
import com.auroracompanion.feature.product.ui.viewmodel.ProductUiState

/**
 * Product List Screen
 * 
 * Main screen for displaying products with search and filtering.
 * 
 * Features:
 * - Search products by name/SKU
 * - Filter by category
 * - Display stock status
 * - Navigate to product details
 * 
 * @param onProductClick Callback when product is clicked
 * @param viewModel Product ViewModel (Hilt injected)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    onProductClick: (String) -> Unit,
    viewModel: ProductViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    
    Scaffold(
        topBar = {
            ProductListTopBar(
                searchQuery = searchQuery,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onClearSearch = viewModel::clearSearch
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Category Filter Chips
            CategoryFilterRow(
                selectedCategory = selectedCategory,
                onCategorySelected = viewModel::onCategorySelected
            )
            
            // Product List Content
            ProductListContent(
                uiState = uiState,
                onProductClick = onProductClick,
                onRetry = viewModel::refresh
            )
        }
    }
}

/**
 * Top App Bar with Search
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductListTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit
) {
    TopAppBar(
        title = {
            SearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                onClearSearch = onClearSearch
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

/**
 * Search Bar Component
 */
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Search products...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClearSearch) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear search"
                    )
                }
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        singleLine = true
    )
}

/**
 * Category Filter Row
 */
@Composable
private fun CategoryFilterRow(
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    val categories = listOf(
        "All" to null,
        "Dog" to "Dog",
        "Cat" to "Cat",
        "Fish" to "Fish",
        "Bird" to "Bird",
        "Reptile" to "Reptile",
        "Small Pets" to "Small Pets"
    )
    
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { (label, category) ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}

/**
 * Product List Content
 * 
 * Handles different UI states (Loading, Success, Error, Empty)
 */
@Composable
private fun ProductListContent(
    uiState: ProductUiState,
    onProductClick: (String) -> Unit,
    onRetry: () -> Unit
) {
    when (uiState) {
        is ProductUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        
        is ProductUiState.Success -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = uiState.products,
                    key = { it.id }
                ) { product ->
                    ProductCard(
                        product = product,
                        onClick = { onProductClick(product.id) }
                    )
                }
            }
        }
        
        is ProductUiState.Empty -> {
            ProductsEmptyState(
                message = uiState.message
            )
        }
        
        is ProductUiState.Error -> {
            ProductsErrorState(
                message = uiState.message,
                onRetry = onRetry
            )
        }
    }
}
