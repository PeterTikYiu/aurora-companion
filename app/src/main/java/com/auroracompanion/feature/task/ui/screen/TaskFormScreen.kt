package com.auroracompanion.feature.task.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.auroracompanion.feature.task.ui.viewmodel.TaskViewModel
import com.auroracompanion.feature.task.ui.viewmodel.TaskUiState
import java.text.SimpleDateFormat
import java.util.*

/**
 * Task Form Screen
 * 
 * Screen for creating or editing tasks.
 * 
 * Features:
 * - Title input with validation
 * - Description input
 * - Priority picker
 * - Due date picker
 * - Assigned staff input
 * - Save/Cancel actions
 * 
 * @param taskId Task ID to edit (null for new task)
 * @param onNavigateBack Callback for navigation
 * @param viewModel Task ViewModel (Hilt injected)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormScreen(
    taskId: Int?,
    onNavigateBack: () -> Unit,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Load existing task if editing
    val existingTask = remember(taskId, uiState) {
        if (taskId != null && uiState is TaskUiState.Success) {
            (uiState as TaskUiState.Success).tasks.find { it.id == taskId }
        } else {
            null
        }
    }
    
    // Form state
    var title by remember(existingTask) { mutableStateOf(existingTask?.title ?: "") }
    var description by remember(existingTask) { mutableStateOf(existingTask?.description ?: "") }
    var priority by remember(existingTask) { mutableStateOf(existingTask?.priority ?: "MEDIUM") }
    var dueDate by remember(existingTask) { mutableStateOf<Long?>(existingTask?.dueDate) }
    var assignedTo by remember(existingTask) { mutableStateOf(existingTask?.assignedTo ?: "") }
    var showDatePicker by remember { mutableStateOf(false) }
    
    val isFormValid = title.isNotBlank()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(if (taskId == null) "Create Task" else "Edit Task") 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (taskId == null) {
                                // Create new task
                                viewModel.createTask(
                                    title = title.trim(),
                                    description = description.trim(),
                                    priority = priority,
                                    dueDate = dueDate,
                                    assignedTo = assignedTo.ifBlank { null }
                                )
                            } else {
                                // Update existing task
                                viewModel.updateTask(
                                    taskId = taskId,
                                    title = title.trim(),
                                    description = description.trim(),
                                    priority = priority,
                                    dueDate = dueDate,
                                    assignedTo = assignedTo.ifBlank { null }
                                )
                            }
                            onNavigateBack()
                        },
                        enabled = isFormValid
                    ) {
                        Text(
                            text = "Save",
                            fontWeight = FontWeight.Bold
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
            // Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title *") },
                placeholder = { Text("e.g., Feed fish tanks") },
                modifier = Modifier.fillMaxWidth(),
                isError = title.isBlank(),
                supportingText = {
                    if (title.isBlank()) {
                        Text("Title is required")
                    }
                },
                singleLine = true
            )
            
            // Description Input
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                placeholder = { Text("Add more details...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )
            
            // Priority Picker
            PriorityPicker(
                selectedPriority = priority,
                onPrioritySelected = { priority = it }
            )
            
            // Due Date Picker
            DueDatePicker(
                dueDate = dueDate,
                onDateSelected = { dueDate = it },
                onShowPicker = { showDatePicker = true }
            )
            
            // Assigned To Input
            OutlinedTextField(
                value = assignedTo,
                onValueChange = { assignedTo = it },
                label = { Text("Assigned To") },
                placeholder = { Text("Staff member name") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Text("👤", style = MaterialTheme.typography.titleMedium)
                },
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Helper Text
            Text(
                text = "* Required field",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    
    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = dueDate ?: System.currentTimeMillis()
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        dueDate = datePickerState.selectedDateMillis
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

/**
 * Priority Picker Component
 */
@Composable
private fun PriorityPicker(
    selectedPriority: String,
    onPrioritySelected: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Priority *",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("HIGH", "MEDIUM", "LOW").forEach { priority ->
                val isSelected = selectedPriority == priority
                val (emoji, color, displayName) = when (priority) {
                    "HIGH" -> Triple("🔴", androidx.compose.ui.graphics.Color(0xFFEF5350), "High")
                    "MEDIUM" -> Triple("🟡", androidx.compose.ui.graphics.Color(0xFFFFA726), "Medium")
                    "LOW" -> Triple("🟢", androidx.compose.ui.graphics.Color(0xFF66BB6A), "Low")
                    else -> Triple("⚪", MaterialTheme.colorScheme.primary, priority)
                }
                
                FilterChip(
                    selected = isSelected,
                    onClick = { onPrioritySelected(priority) },
                    label = { 
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Text(emoji)
                            Text(displayName)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = color.copy(alpha = 0.2f),
                        selectedLabelColor = color
                    )
                )
            }
        }
    }
}

/**
 * Due Date Picker Component
 */
@Composable
private fun DueDatePicker(
    dueDate: Long?,
    onDateSelected: (Long?) -> Unit,
    onShowPicker: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Due Date",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onShowPicker,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (dueDate != null) {
                        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        "📅 ${dateFormat.format(Date(dueDate))}"
                    } else {
                        "📅 Select Date"
                    }
                )
            }
            
            if (dueDate != null) {
                OutlinedButton(
                    onClick = { onDateSelected(null) }
                ) {
                    Text("Clear")
                }
            }
        }
    }
}
