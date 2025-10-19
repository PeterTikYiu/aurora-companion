package com.auroracompanion.feature.task.ui.screen

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
import com.auroracompanion.feature.task.ui.components.*
import com.auroracompanion.feature.task.ui.viewmodel.TaskViewModel
import com.auroracompanion.feature.task.ui.viewmodel.TaskUiState
import com.auroracompanion.feature.task.ui.viewmodel.TaskStatus

/**
 * Task List Screen
 * 
 * Main screen for displaying and managing tasks.
 * 
 * Features:
 * - Tab navigation (All/Active/Completed)
 * - Search tasks
 * - Priority filters
 * - Task completion toggle
 * - FAB for creating new tasks
 * - Swipe to complete/delete
 * 
 * @param onTaskClick Callback when task is clicked
 * @param onCreateTask Callback to create new task
 * @param viewModel Task ViewModel (Hilt injected)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onTaskClick: (Int) -> Unit,
    onCreateTask: () -> Unit,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()
    val selectedPriority by viewModel.selectedPriority.collectAsState()
    
    Scaffold(
        topBar = {
            TaskListTopBar(
                searchQuery = searchQuery,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onClearSearch = viewModel::clearSearch
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateTask,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create task"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Status Tabs
            TaskStatusTabs(
                selectedStatus = selectedStatus,
                onStatusSelected = viewModel::onStatusSelected
            )
            
            // Priority Filter Chips
            PriorityFilterRow(
                selectedPriority = selectedPriority,
                onPrioritySelected = viewModel::onPrioritySelected
            )
            
            // Task List Content
            TaskListContent(
                uiState = uiState,
                onTaskClick = onTaskClick,
                onCompletionToggle = { taskId, isCompleted ->
                    viewModel.toggleTaskCompletion(taskId, isCompleted)
                },
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
private fun TaskListTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit
) {
    TopAppBar(
        title = {
            if (searchQuery.isEmpty()) {
                Text("Tasks")
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
                        contentDescription = "Search tasks"
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
        placeholder = { Text("Search tasks...") },
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
 * Status Tabs (All/Active/Completed)
 */
@Composable
private fun TaskStatusTabs(
    selectedStatus: TaskStatus,
    onStatusSelected: (TaskStatus) -> Unit
) {
    TabRow(
        selectedTabIndex = when (selectedStatus) {
            TaskStatus.ALL -> 0
            TaskStatus.ACTIVE -> 1
            TaskStatus.COMPLETED -> 2
        },
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        Tab(
            selected = selectedStatus == TaskStatus.ALL,
            onClick = { onStatusSelected(TaskStatus.ALL) },
            text = { Text("All") }
        )
        Tab(
            selected = selectedStatus == TaskStatus.ACTIVE,
            onClick = { onStatusSelected(TaskStatus.ACTIVE) },
            text = { Text("Active") }
        )
        Tab(
            selected = selectedStatus == TaskStatus.COMPLETED,
            onClick = { onStatusSelected(TaskStatus.COMPLETED) },
            text = { Text("Completed") }
        )
    }
}

/**
 * Priority Filter Row
 */
@Composable
private fun PriorityFilterRow(
    selectedPriority: String?,
    onPrioritySelected: (String?) -> Unit
) {
    val priorities = listOf(
        "All" to null,
        "🔴 High" to "HIGH",
        "🟡 Medium" to "MEDIUM",
        "🟢 Low" to "LOW"
    )
    
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(priorities) { (label, priority) ->
            FilterChip(
                selected = selectedPriority == priority,
                onClick = { onPrioritySelected(priority) },
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
 * Task List Content
 * 
 * Handles different UI states (Loading, Success, Error, Empty)
 */
@Composable
private fun TaskListContent(
    uiState: TaskUiState,
    onTaskClick: (Int) -> Unit,
    onCompletionToggle: (Int, Boolean) -> Unit,
    onRetry: () -> Unit
) {
    when (uiState) {
        is TaskUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        
        is TaskUiState.Success -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = uiState.tasks,
                    key = { it.id }
                ) { task ->
                    TaskCard(
                        task = task,
                        onTaskClick = { onTaskClick(task.id) },
                        onCompletionToggle = { isCompleted ->
                            onCompletionToggle(task.id, isCompleted)
                        }
                    )
                }
            }
        }
        
        is TaskUiState.Empty -> {
            TasksEmptyState(
                message = uiState.message
            )
        }
        
        is TaskUiState.Error -> {
            TasksErrorState(
                message = uiState.message,
                onRetry = onRetry
            )
        }
    }
}
