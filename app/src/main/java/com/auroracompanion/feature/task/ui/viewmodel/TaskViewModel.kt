package com.auroracompanion.feature.task.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auroracompanion.core.data.model.Task
import com.auroracompanion.core.data.repository.TaskRepository
import com.auroracompanion.core.data.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Task ViewModel
 * 
 * Manages state for Task screens with comprehensive filtering and search.
 * 
 * Features:
 * - Search tasks by title/description
 * - Filter by priority (High/Medium/Low)
 * - Filter by status (All/Active/Completed)
 * - Sort by due date, priority, creation date
 * - Complete/uncomplete tasks
 * - Delete tasks
 * - Create/update tasks
 * 
 * Architecture:
 * - StateFlow for reactive UI updates
 * - Flow operators for data transformation
 * - Repository pattern for data access
 * - Result wrapper for error handling
 * 
 * @param taskRepository Repository for task data operations
 */
@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    
    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // Priority filter (null = all, "High"/"Medium"/"Low" = specific)
    private val _selectedPriority = MutableStateFlow<String?>(null)
    val selectedPriority: StateFlow<String?> = _selectedPriority.asStateFlow()
    
    // Status filter
    private val _selectedStatus = MutableStateFlow(TaskStatus.ALL)
    val selectedStatus: StateFlow<TaskStatus> = _selectedStatus.asStateFlow()
    
    // Sort option
    private val _sortOption = MutableStateFlow(SortOption.DUE_DATE)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()
    
    // UI State
    val uiState: StateFlow<TaskUiState> = combine(
        searchQuery,
        selectedPriority,
        selectedStatus,
        sortOption
    ) { query, priority, status, sort ->
        // Combine all filters and get tasks
        taskRepository.searchTasks(
            query = query,
            priority = priority,
            isCompleted = when (status) {
                TaskStatus.ACTIVE -> false
                TaskStatus.COMPLETED -> true
                TaskStatus.ALL -> null
            }
        )
    }.flatMapLatest { flow ->
        flow.map { result ->
            when (result) {
                is Result.Loading -> TaskUiState.Loading
                is Result.Success -> {
                    val tasks = result.data
                    
                    // Apply sorting
                    val sortedTasks = when (_sortOption.value) {
                        SortOption.DUE_DATE -> tasks.sortedBy { it.dueDate }
                        SortOption.PRIORITY -> tasks.sortedByDescending { 
                            when (it.priority) {
                                "High" -> 3
                                "Medium" -> 2
                                "Low" -> 1
                                else -> 0
                            }
                        }
                        SortOption.CREATED_DATE -> tasks.sortedByDescending { it.createdAt }
                    }
                    
                    if (sortedTasks.isEmpty()) {
                        TaskUiState.Empty(getEmptyMessage())
                    } else {
                        TaskUiState.Success(sortedTasks)
                    }
                }
                is Result.Error -> TaskUiState.Error(
                    result.exception?.message ?: "Unknown error occurred"
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskUiState.Loading
    )
    
    /**
     * Update search query
     */
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
    
    /**
     * Clear search
     */
    fun clearSearch() {
        _searchQuery.value = ""
    }
    
    /**
     * Select priority filter
     */
    fun onPrioritySelected(priority: String?) {
        _selectedPriority.value = priority
    }
    
    /**
     * Select status filter
     */
    fun onStatusSelected(status: TaskStatus) {
        _selectedStatus.value = status
    }
    
    /**
     * Select sort option
     */
    fun onSortSelected(sort: SortOption) {
        _sortOption.value = sort
    }
    
    /**
     * Toggle task completion
     */
    fun toggleTaskCompletion(taskId: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            taskRepository.toggleTaskCompletion(taskId, isCompleted)
        }
    }
    
    /**
     * Delete task
     */
    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            taskRepository.deleteTask(taskId)
        }
    }
    
    /**
     * Create new task
     */
    fun createTask(
        title: String,
        description: String,
        priority: String,
        dueDate: Long?,
        assignedTo: String?
    ) {
        viewModelScope.launch {
            taskRepository.createTask(
                title = title,
                description = description,
                priority = priority,
                dueDate = dueDate,
                assignedTo = assignedTo
            )
        }
    }
    
    /**
     * Update existing task
     */
    fun updateTask(
        taskId: Int,
        title: String,
        description: String,
        priority: String,
        dueDate: Long?,
        assignedTo: String?
    ) {
        viewModelScope.launch {
            taskRepository.updateTask(
                taskId = taskId,
                title = title,
                description = description,
                priority = priority,
                dueDate = dueDate,
                assignedTo = assignedTo
            )
        }
    }
    
    /**
     * Refresh tasks
     */
    fun refresh() {
        // State will automatically refresh due to Flow
    }
    
    /**
     * Get empty state message based on current filters
     */
    private fun getEmptyMessage(): String {
        return when {
            _searchQuery.value.isNotEmpty() -> "No tasks found matching \"${_searchQuery.value}\""
            _selectedPriority.value != null -> "No ${_selectedPriority.value?.lowercase()?.replaceFirstChar { it.uppercase() }} priority tasks"
            _selectedStatus.value == TaskStatus.ACTIVE -> "No active tasks! You're all caught up 🎉"
            _selectedStatus.value == TaskStatus.COMPLETED -> "No completed tasks yet"
            else -> "No tasks yet. Tap + to create one!"
        }
    }
}

/**
 * Task UI State
 */
sealed interface TaskUiState {
    data object Loading : TaskUiState
    data class Success(val tasks: List<Task>) : TaskUiState
    data class Error(val message: String) : TaskUiState
    data class Empty(val message: String) : TaskUiState
}

/**
 * Task Status Filter
 */
enum class TaskStatus {
    ALL,
    ACTIVE,
    COMPLETED
}

/**
 * Sort Options
 */
enum class SortOption {
    DUE_DATE,
    PRIORITY,
    CREATED_DATE
}
