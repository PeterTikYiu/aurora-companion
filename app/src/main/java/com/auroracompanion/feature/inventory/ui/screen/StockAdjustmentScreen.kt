package com.auroracompanion.feature.inventory.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.auroracompanion.core.data.model.Product
import com.auroracompanion.core.data.model.MovementType
import com.auroracompanion.feature.inventory.ui.viewmodel.StockAdjustmentViewModel
import com.auroracompanion.feature.inventory.ui.viewmodel.AdjustmentState

/**
 * Stock Adjustment Screen
 * 
 * Screen for adjusting product stock levels.
 * 
 * Features:
 * - Add or remove stock
 * - Select movement type
 * - Add reason with suggestions
 * - Real-time validation
 * - Preview new stock level
 * - Confirmation before saving
 * 
 * @param product Product to adjust
 * @param onNavigateBack Callback to navigate back
 * @param viewModel Stock Adjustment ViewModel (Hilt injected)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockAdjustmentScreen(
    product: Product,
    onNavigateBack: () -> Unit,
    viewModel: StockAdjustmentViewModel = hiltViewModel()
) {
    var isAddition by remember { mutableStateOf(true) }
    var quantity by remember { mutableStateOf("") }
    var movementType by remember { mutableStateOf(MovementType.RECEIVED) }
    var reason by remember { mutableStateOf("") }
    var staffMember by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }
    
    val adjustmentState by viewModel.adjustmentState.collectAsState()
    
    // Handle successful adjustment
    LaunchedEffect(adjustmentState) {
        if (adjustmentState is AdjustmentState.Success) {
            onNavigateBack()
            viewModel.resetState()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adjust Stock") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Product info card
            ProductInfoCard(product = product)
            
            // Add/Remove selector
            Text(
                text = "Adjustment Type *",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = isAddition,
                    onClick = { 
                        isAddition = true
                        movementType = MovementType.RECEIVED
                    },
                    label = { Text("Add Stock") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = !isAddition,
                    onClick = { 
                        isAddition = false
                        movementType = MovementType.SOLD
                    },
                    label = { Text("Remove Stock") },
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Quantity input
            OutlinedTextField(
                value = quantity,
                onValueChange = { 
                    if (it.all { char -> char.isDigit() }) {
                        quantity = it
                    }
                },
                label = { Text("Quantity *") },
                modifier = Modifier.fillMaxWidth(),
                isError = quantity.isNotEmpty() && viewModel.validateAdjustment(
                    quantity.toIntOrNull() ?: 0,
                    product.stockQty,
                    isAddition
                ) != null,
                supportingText = {
                    val error = if (quantity.isNotEmpty()) {
                        viewModel.validateAdjustment(
                            quantity.toIntOrNull() ?: 0,
                            product.stockQty,
                            isAddition
                        )
                    } else null
                    
                    if (error != null) {
                        Text(error, color = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true
            )
            
            // Movement type selector
            MovementTypeSelector(
                selectedType = movementType,
                isAddition = isAddition,
                onTypeSelected = { movementType = it }
            )
            
            // Reason input with suggestions
            ReasonInput(
                reason = reason,
                onReasonChange = { reason = it },
                suggestions = viewModel.getSuggestedReasons(movementType),
                onSuggestionClick = { reason = it }
            )
            
            // Staff member input
            OutlinedTextField(
                value = staffMember,
                onValueChange = { staffMember = it },
                label = { Text("Staff Member") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Preview new stock level
            val newStockLevel = quantity.toIntOrNull()?.let {
                viewModel.calculateNewStockLevel(product.stockQty, it, isAddition)
            }
            
            if (newStockLevel != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "New Stock Level:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "$newStockLevel units",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            // Error message
            if (adjustmentState is AdjustmentState.Error) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = (adjustmentState as AdjustmentState.Error).message,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f),
                    enabled = adjustmentState !is AdjustmentState.Loading
                ) {
                    Text("Cancel")
                }
                
                Button(
                    onClick = { showConfirmDialog = true },
                    modifier = Modifier.weight(1f),
                    enabled = quantity.isNotEmpty() && 
                             viewModel.validateAdjustment(
                                 quantity.toIntOrNull() ?: 0,
                                 product.stockQty,
                                 isAddition
                             ) == null &&
                             adjustmentState !is AdjustmentState.Loading
                ) {
                    if (adjustmentState is AdjustmentState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Save Changes")
                    }
                }
            }
        }
    }
    
    // Confirmation dialog
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm Stock Adjustment") },
            text = {
                Text(
                    "${if (isAddition) "Add" else "Remove"} ${quantity} units " +
                    "${if (isAddition) "to" else "from"} ${product.name}?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        viewModel.submitAdjustment(
                            product = product,
                            quantity = quantity.toInt(),
                            isAddition = isAddition,
                            movementType = movementType,
                            reason = reason.ifBlank { null },
                            staffMember = staffMember.ifBlank { null }
                        )
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ProductInfoCard(product: Product) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "SKU: ${product.sku}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Current Stock:",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "${product.stockQty} units",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun MovementTypeSelector(
    selectedType: MovementType,
    isAddition: Boolean,
    onTypeSelected: (MovementType) -> Unit
) {
    val types = if (isAddition) {
        listOf(MovementType.RECEIVED, MovementType.RETURNED, MovementType.TRANSFER_IN, MovementType.CORRECTION)
    } else {
        listOf(MovementType.SOLD, MovementType.DAMAGED, MovementType.EXPIRED, MovementType.TRANSFER_OUT, MovementType.CORRECTION)
    }
    
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Reason *",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )
        
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            types.forEach { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { onTypeSelected(type) },
                    label = { Text(type.displayName()) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ReasonInput(
    reason: String,
    onReasonChange: (String) -> Unit,
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = reason,
            onValueChange = onReasonChange,
            label = { Text("Notes (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4
        )
        
        if (suggestions.isNotEmpty() && reason.isBlank()) {
            Text(
                text = "Suggestions:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            suggestions.forEach { suggestion ->
                SuggestionChip(
                    onClick = { onSuggestionClick(suggestion) },
                    label = { Text(suggestion, style = MaterialTheme.typography.bodySmall) }
                )
            }
        }
    }
}
