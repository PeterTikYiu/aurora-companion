package com.auroracompanion.feature.inventory.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.auroracompanion.core.data.model.Product
import com.auroracompanion.core.data.model.StockMovement
import com.auroracompanion.core.data.model.MovementType
import com.auroracompanion.feature.inventory.ui.viewmodel.ProductStockStatus
import java.text.SimpleDateFormat
import java.util.*

/**
 * Inventory UI Components
 * 
 * Reusable components for inventory feature screens.
 * 
 * Components:
 * - StockStatusBadge: Visual indicator for stock status
 * - StockLevelCard: Display stock quantity with visual bar
 * - StockMovementItem: Timeline item for stock history
 * - InventoryMetricsCard: Summary statistics card
 * - LowStockAlertBanner: Alert banner for low stock items
 * - InventoryEmptyState: Empty state messaging
 * - InventoryErrorState: Error state with retry
 */

/**
 * Stock Status Badge
 * 
 * Visual indicator showing stock availability status
 * 
 * @param status Stock status (In Stock, Low Stock, Out of Stock)
 * @param isCompact Compact mode for smaller badge
 */
@Composable
fun StockStatusBadge(
    status: ProductStockStatus,
    isCompact: Boolean = false,
    modifier: Modifier = Modifier
) {
    data class StatusStyle(
        val backgroundColor: Color,
        val textColor: Color,
        val icon: String,
        val displayName: String
    )
    
    val style = when (status) {
        ProductStockStatus.IN_STOCK -> StatusStyle(
            backgroundColor = Color(0xFF66BB6A).copy(alpha = 0.15f),
            textColor = Color(0xFF66BB6A),
            icon = "✓",
            displayName = "In Stock"
        )
        ProductStockStatus.LOW_STOCK -> StatusStyle(
            backgroundColor = Color(0xFFFFA726).copy(alpha = 0.15f),
            textColor = Color(0xFFFFA726),
            icon = "⚠",
            displayName = "Low Stock"
        )
        ProductStockStatus.OUT_OF_STOCK -> StatusStyle(
            backgroundColor = Color(0xFFEF5350).copy(alpha = 0.15f),
            textColor = Color(0xFFEF5350),
            icon = "✗",
            displayName = "Out of Stock"
        )
    }
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(style.backgroundColor)
            .padding(
                horizontal = if (isCompact) 8.dp else 12.dp,
                vertical = if (isCompact) 4.dp else 6.dp
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isCompact) {
                Text(
                    text = style.icon,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Text(
                text = style.displayName,
                style = if (isCompact) {
                    MaterialTheme.typography.labelSmall
                } else {
                    MaterialTheme.typography.labelMedium
                },
                fontWeight = FontWeight.Bold,
                color = style.textColor
            )
        }
    }
}

/**
 * Stock Level Card
 * 
 * Displays current stock quantity with visual progress bar
 * 
 * @param product Product to display
 * @param onClick Callback when card is clicked
 */
@Composable
fun StockLevelCard(
    product: Product,
    status: ProductStockStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "SKU: ${product.sku}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                StockStatusBadge(status = status)
            }
            
            // Stock quantity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = "Stock",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${product.stockQty} units",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Text(
                    text = "£${String.format("%.2f", product.price)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // Stock level indicator
            val stockPercentage = when {
                product.stockQty == 0 -> 0f
                product.stockQty <= 10 -> (product.stockQty / 10f).coerceIn(0f, 1f)
                else -> 1f
            }
            
            LinearProgressIndicator(
                progress = { stockPercentage },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = when (status) {
                    ProductStockStatus.IN_STOCK -> Color(0xFF66BB6A)
                    ProductStockStatus.LOW_STOCK -> Color(0xFFFFA726)
                    ProductStockStatus.OUT_OF_STOCK -> Color(0xFFEF5350)
                }
            )
        }
    }
}

/**
 * Stock Movement Item
 * 
 * Timeline item showing a single stock movement
 * 
 * @param movement Stock movement to display
 * @param productName Optional product name
 */
@Composable
fun StockMovementItem(
    movement: StockMovement,
    productName: String? = null,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    val isPositive = movement.quantityChange > 0
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Movement indicator
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isPositive) {
                            Color(0xFF66BB6A).copy(alpha = 0.15f)
                        } else {
                            Color(0xFFEF5350).copy(alpha = 0.15f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPositive) Icons.Default.Add else Icons.Default.Remove,
                    contentDescription = if (isPositive) "Added" else "Removed",
                    tint = if (isPositive) Color(0xFF66BB6A) else Color(0xFFEF5350)
                )
            }
            
            // Movement details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${if (isPositive) "+" else ""}${movement.quantityChange} units",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isPositive) Color(0xFF66BB6A) else Color(0xFFEF5350)
                    )
                }
                
                Text(
                    text = movement.movementType.displayName(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                if (productName != null) {
                    Text(
                        text = productName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (movement.reason != null) {
                    Text(
                        text = movement.reason,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = dateFormat.format(Date(movement.timestamp)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (movement.staffMember != null) {
                        Text(
                            text = "• ${movement.staffMember}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Inventory Metrics Card
 * 
 * Summary statistics for inventory overview
 * 
 * @param totalProducts Total number of products
 * @param lowStockCount Number of low stock items
 * @param outOfStockCount Number of out of stock items
 * @param totalValue Total inventory value
 */
@Composable
fun InventoryMetricsCard(
    totalProducts: Int,
    lowStockCount: Int,
    outOfStockCount: Int,
    totalValue: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Inventory Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricItem(
                    label = "Total Products",
                    value = totalProducts.toString(),
                    icon = Icons.Default.Inventory2
                )
                
                MetricItem(
                    label = "Low Stock",
                    value = lowStockCount.toString(),
                    icon = Icons.Default.Warning,
                    valueColor = Color(0xFFFFA726)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricItem(
                    label = "Out of Stock",
                    value = outOfStockCount.toString(),
                    icon = Icons.Default.Error,
                    valueColor = Color(0xFFEF5350)
                )
                
                MetricItem(
                    label = "Total Value",
                    value = "£${String.format("%.2f", totalValue)}",
                    icon = Icons.Default.AttachMoney
                )
            }
        }
    }
}

@Composable
private fun MetricItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    valueColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

/**
 * Low Stock Alert Banner
 * 
 * Alert banner shown when there are low stock items
 * 
 * @param lowStockCount Number of low stock items
 * @param onViewClick Callback to view low stock items
 */
@Composable
fun LowStockAlertBanner(
    lowStockCount: Int,
    onViewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (lowStockCount == 0) return
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFA726).copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = Color(0xFFFFA726),
                    modifier = Modifier.size(32.dp)
                )
                
                Column {
                    Text(
                        text = "$lowStockCount Low Stock Items",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Items need restocking",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            TextButton(onClick = onViewClick) {
                Text("View")
            }
        }
    }
}

/**
 * Empty State for Inventory
 * 
 * @param message Empty state message
 */
@Composable
fun InventoryEmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Inventory2,
                contentDescription = "Empty",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

/**
 * Error State for Inventory
 * 
 * @param message Error message
 * @param onRetry Retry callback
 */
@Composable
fun InventoryErrorState(
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Error",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}
