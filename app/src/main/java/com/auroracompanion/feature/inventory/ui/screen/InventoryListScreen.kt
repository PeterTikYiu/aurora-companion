package com.auroracompanion.feature.inventory.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.auroracompanion.feature.inventory.ui.components.*
import com.auroracompanion.feature.inventory.ui.viewmodel.InventoryViewModel
import com.auroracompanion.feature.inventory.ui.viewmodel.InventoryUiState
import com.auroracompanion.feature.inventory.ui.viewmodel.StockStatus

/**
 * Inventory List Screen
 * 
 * Main screen for inventory management.
 * 
 * Features:
 * - Search products by name/SKU
 * - Filter by stock status (All/Low Stock/Out of Stock)
 * - Filter by category
 * - View stock levels with visual indicators
 * - Navigate to stock adjustment
 * - Low stock alert banner
 * 
 * @param onProductClick Callback when product is clicked
 * @param onAdjustStock Callback to adjust stock for a product
 * @param viewModel Inventory ViewModel (Hilt injected)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryListScreen(
    onProductClick: (Int) -> Unit,
    onAdjustStock: (Int) -> Unit,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedStockStatus by viewModel.selectedStockStatus.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val lowStockCount by viewModel.lowStockCount.collectAsState()
    
    Scaffold(
        topBar = {
            InventoryTopBar(
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
            // Low stock alert banner
            if (lowStockCount > 0 && selectedStockStatus == StockStatus.ALL) {
                LowStockAlertBanner(
                    lowStockCount = lowStockCount,
                    onViewClick = { viewModel.onStockStatusSelected(StockStatus.LOW_STOCK) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            // Stock status filter tabs
            StockStatusTabs(
                selectedStatus = selectedStockStatus,
                onStatusSelected = viewModel::onStockStatusSelected,
                lowStockCount = lowStockCount
            )
            
            // Category filter chips
            CategoryFilterRow(
                selectedCategory = selectedCategory,
                onCategorySelected = viewModel::onCategorySelected
            )
            
            // Inventory list content
            InventoryListContent(
                uiState = uiState,
                onProductClick = onProductClick,
                onAdjustStock = onAdjustStock,
                viewModel = viewModel,
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
private fun InventoryTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit
) {
    TopAppBar(
        title = {
            if (searchQuery.isEmpty()) {
                Text("Inventory")
            } else {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    onClearSearch = onClearSearch
                )
            }
        },
        actions = {
            if (searchQuery.isEmpty()) {
                IconButton(onClick = { onSearchQueryChange(" ") }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search inventory"
                    )
                }
            }
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
        value = query.trim(),
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
            IconButton(onClick = onClearSearch) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear search"
                )
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
 * Stock Status Tabs (All/Low Stock/Out of Stock)
 */
@Composable
private fun StockStatusTabs(
    selectedStatus: StockStatus,
    onStatusSelected: (StockStatus) -> Unit,
    lowStockCount: Int
) {
    TabRow(
        selectedTabIndex = when (selectedStatus) {
            StockStatus.ALL -> 0
            StockStatus.LOW_STOCK -> 1
            StockStatus.OUT_OF_STOCK -> 2
        },
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        Tab(
            selected = selectedStatus == StockStatus.ALL,
            onClick = { onStatusSelected(StockStatus.ALL) },
            text = { Text("All") }
        )
        Tab(
            selected = selectedStatus == StockStatus.LOW_STOCK,
            onClick = { onStatusSelected(StockStatus.LOW_STOCK) },
            text = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Low Stock")
                    if (lowStockCount > 0) {
                        Badge {
                            Text(lowStockCount.toString())
                        }
                    }
                }
            }
        )
        Tab(
            selected = selectedStatus == StockStatus.OUT_OF_STOCK,
            onClick = { onStatusSelected(StockStatus.OUT_OF_STOCK) },
            text = { Text("Out of Stock") }
        )
    }
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
        "🐕 Dogs" to "Dogs",
        "🐈 Cats" to "Cats",
        "🐠 Fish" to "Fish",
        "🐦 Birds" to "Birds",
        "🐹 Small Pets" to "Small Pets",
        "🦎 Reptiles" to "Reptiles"
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
 * Inventory List Content
 * 
 * Handles different UI states (Loading, Success, Error, Empty)
 */
@Composable
private fun InventoryListContent(
    uiState: InventoryUiState,
    onProductClick: (Int) -> Unit,
    onAdjustStock: (Int) -> Unit,
    viewModel: InventoryViewModel,
    onRetry: () -> Unit
) {
    when (uiState) {
        is InventoryUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        
        is InventoryUiState.Success -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = uiState.products,
                    key = { it.id }
                ) { product ->
                    val status = viewModel.getStockStatus(product)
                    StockLevelCard(
                        product = product,
                        status = status,
                        onClick = { onProductClick(product.id) }
                    )
                }
            }
        }
        
        is InventoryUiState.Empty -> {
            InventoryEmptyState(
                message = uiState.message
            )
        }
        
        is InventoryUiState.Error -> {
            InventoryErrorState(
                message = uiState.message,
                onRetry = onRetry
            )
        }
    }
}
