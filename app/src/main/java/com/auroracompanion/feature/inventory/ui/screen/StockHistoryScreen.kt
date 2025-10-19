package com.auroracompanion.feature.inventory.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.auroracompanion.core.data.Result
import com.auroracompanion.core.data.model.Product
import com.auroracompanion.core.data.model.StockMovement
import com.auroracompanion.core.data.repository.InventoryRepository
import com.auroracompanion.feature.inventory.ui.components.StockMovementItem
import com.auroracompanion.feature.inventory.ui.components.InventoryEmptyState
import com.auroracompanion.feature.inventory.ui.components.InventoryErrorState
import kotlinx.coroutines.flow.Flow

/**
 * Stock History Screen
 * 
 * Displays timeline of all stock movements for a product.
 * 
 * Features:
 * - Chronological list of stock adjustments
 * - Movement type and quantity display
 * - Staff member and timestamp
 * - Reason/notes for each movement
 * 
 * @param product Product to show history for
 * @param stockHistoryFlow Flow of stock movements
 * @param onNavigateBack Callback to navigate back
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockHistoryScreen(
    product: Product,
    stockHistoryFlow: Flow<Result<List<StockMovement>>>,
    onNavigateBack: () -> Unit
) {
    val historyState by stockHistoryFlow.collectAsState(initial = Result.Loading)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Stock History")
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = historyState) {
            is Result.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            is Result.Success -> {
                if (state.data.isEmpty()) {
                    InventoryEmptyState(
                        message = "No stock history available for this product",
                        modifier = Modifier.padding(paddingValues)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Summary card
                        item {
                            StockSummaryCard(
                                currentStock = product.stockQty,
                                totalMovements = state.data.size
                            )
                        }
                        
                        // Movement timeline
                        items(
                            items = state.data,
                            key = { it.id }
                        ) { movement ->
                            StockMovementItem(
                                movement = movement,
                                productName = null
                            )
                        }
                    }
                }
            }
            
            is Result.Error -> {
                InventoryErrorState(
                    message = state.exception?.message ?: "Failed to load stock history",
                    onRetry = { /* Flow auto-retries */ },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun StockSummaryCard(
    currentStock: Int,
    totalMovements: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Current Stock",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "$currentStock units",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Total Movements",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = totalMovements.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
