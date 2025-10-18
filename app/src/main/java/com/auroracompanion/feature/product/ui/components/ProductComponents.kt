package com.auroracompanion.feature.product.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.auroracompanion.core.ui.theme.*
import com.auroracompanion.feature.product.domain.model.Product
import com.auroracompanion.feature.product.domain.model.StockStatus

/**
 * Product Card Component
 * 
 * Displays product information in a card format.
 * Used in product list screens.
 * 
 * @param product Product to display
 * @param onClick Click handler for card
 * @param modifier Optional modifier
 */
@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Product Name
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // SKU
                Text(
                    text = "SKU: ${product.sku}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Category
                Text(
                    text = product.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Price and Stock
            Column(
                horizontalAlignment = Alignment.End
            ) {
                // Price
                Text(
                    text = product.formattedPrice,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Stock Badge
                StockBadge(
                    stockStatus = product.stockStatus,
                    stockQty = product.stockQty
                )
            }
        }
    }
}

/**
 * Stock Badge Component
 * 
 * Shows stock status with color-coded badge.
 * 
 * @param stockStatus Stock status enum
 * @param stockQty Quantity in stock
 */
@Composable
fun StockBadge(
    stockStatus: StockStatus,
    stockQty: Int,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, text) = when (stockStatus) {
        StockStatus.IN_STOCK -> Triple(
            InStockColor.copy(alpha = 0.15f),
            InStockColor,
            "Stock: $stockQty"
        )
        StockStatus.LOW_STOCK -> Triple(
            LowStockColor.copy(alpha = 0.15f),
            LowStockColor,
            "Low: $stockQty"
        )
        StockStatus.OUT_OF_STOCK -> Triple(
            OutOfStockColor.copy(alpha = 0.15f),
            OutOfStockColor,
            "Out of Stock"
        )
    }
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

/**
 * Product List Item Component
 * 
 * Simpler list item for compact views.
 * 
 * @param product Product to display
 * @param onClick Click handler
 */
@Composable
fun ProductListItem(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${product.category} • ${product.formattedPrice}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            StockBadge(
                stockStatus = product.stockStatus,
                stockQty = product.stockQty
            )
        }
    }
}

/**
 * Empty State Component
 * 
 * Shows when no products are found.
 */
@Composable
fun ProductsEmptyState(
    message: String = "No products found",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "📦",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Error State Component
 * 
 * Shows when error occurs.
 */
@Composable
fun ProductsErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "⚠️",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}
