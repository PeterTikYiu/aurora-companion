package com.auroracompanion.feature.product.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.auroracompanion.feature.product.domain.model.Product
import com.auroracompanion.feature.product.domain.model.StockStatus
import com.auroracompanion.feature.product.ui.components.ProductsEmptyState
import com.auroracompanion.feature.product.ui.components.ProductsErrorState
import com.auroracompanion.feature.product.ui.components.StockBadge
import com.auroracompanion.feature.product.ui.viewmodel.ProductViewModel
import com.auroracompanion.feature.product.ui.viewmodel.ProductUiState

/**
 * Product Detail Screen
 * 
 * Displays detailed product information with stock management.
 * 
 * Features:
 * - Full product details
 * - Stock level display
 * - Stock adjustment controls (+/-)
 * - Image placeholder
 * 
 * @param productId Product ID to display
 * @param onNavigateBack Callback for back navigation
 * @param viewModel Product ViewModel (Hilt injected)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    onNavigateBack: () -> Unit,
    viewModel: ProductViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when (uiState) {
            is ProductUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            is ProductUiState.Success -> {
                val product = (uiState as ProductUiState.Success).products
                    .find { it.id == productId }
                
                if (product != null) {
                    ProductDetailContent(
                        product = product,
                        onStockUpdate = { newQty ->
                            viewModel.updateStock(productId, newQty)
                        },
                        modifier = Modifier.padding(paddingValues)
                    )
                } else {
                    ProductsEmptyState(
                        message = "Product not found",
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
            
            is ProductUiState.Empty -> {
                ProductsEmptyState(
                    message = (uiState as ProductUiState.Empty).message,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            
            is ProductUiState.Error -> {
                ProductsErrorState(
                    message = (uiState as ProductUiState.Error).message,
                    onRetry = viewModel::refresh,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

/**
 * Product Detail Content
 */
@Composable
private fun ProductDetailContent(
    product: Product,
    onStockUpdate: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Product Image Placeholder
        ProductImagePlaceholder()
        
        // Product Info Card
        ProductInfoCard(product = product)
        
        // Stock Management Card
        StockManagementCard(
            product = product,
            onStockUpdate = onStockUpdate
        )
        
        // Additional Details Card
        ProductDetailsCard(product = product)
    }
}

/**
 * Product Image Placeholder
 */
@Composable
private fun ProductImagePlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "📷",
            style = MaterialTheme.typography.displayLarge
        )
    }
}

/**
 * Product Info Card
 */
@Composable
private fun ProductInfoCard(product: Product) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = product.formattedPrice,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            DetailRow(label = "SKU", value = product.sku)
            DetailRow(label = "Category", value = product.category)
            DetailRow(label = "Barcode", value = product.barcode)
        }
    }
}

/**
 * Stock Management Card
 */
@Composable
private fun StockManagementCard(
    product: Product,
    onStockUpdate: (Int) -> Unit
) {
    var stockQty by remember(product.stockQty) { mutableIntStateOf(product.stockQty) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Stock Management",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            // Current Stock Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Current Stock:",
                    style = MaterialTheme.typography.bodyLarge
                )
                StockBadge(
                    stockStatus = product.stockStatus,
                    stockQty = product.stockQty
                )
            }
            
            Divider()
            
            // Stock Adjustment Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Decrease Button
                IconButton(
                    onClick = {
                        if (stockQty > 0) {
                            stockQty--
                        }
                    },
                    enabled = stockQty > 0
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease stock"
                    )
                }
                
                // Stock Quantity Display
                Text(
                    text = stockQty.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                // Increase Button
                IconButton(
                    onClick = { stockQty++ }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase stock"
                    )
                }
            }
            
            // Update Stock Button
            Button(
                onClick = { onStockUpdate(stockQty) },
                modifier = Modifier.fillMaxWidth(),
                enabled = stockQty != product.stockQty
            ) {
                Text("Update Stock")
            }
        }
    }
}

/**
 * Product Details Card
 */
@Composable
private fun ProductDetailsCard(product: Product) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Additional Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            product.description?.let {
                DetailRow(label = "Description", value = it)
            }
            
            product.supplier?.let {
                DetailRow(label = "Supplier", value = it)
            }
            
            DetailRow(
                label = "Min Stock Level",
                value = product.minStockLevel.toString()
            )
            
            DetailRow(
                label = "Last Updated",
                value = product.formattedLastUpdated
            )
        }
    }
}

/**
 * Detail Row Helper
 */
@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
