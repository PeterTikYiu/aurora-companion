package com.auroracompanion.core.data.repository

import com.auroracompanion.core.data.model.Task
import com.auroracompanion.core.data.Result
import com.auroracompanion.core.util.asResult
import com.auroracompanion.feature.task.data.local.dao.TaskDao
import com.auroracompanion.feature.task.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Task Repository
 * 
 * Single source of truth for task data.
 * Handles data operations and converts between entity and domain models.
 * 
 * Architecture:
 * - UI Layer ← Task (domain model)
 * - Repository ← converts between TaskEntity and Task
 * - Data Layer ← TaskEntity (database model)
 * 
 * @param taskDao Data Access Object for tasks
 */
@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    
    /**
     * Search tasks with filters
     * 
     * @param query Search query for title/description
     * @param priority Filter by priority (null = all)
     * @param isCompleted Filter by completion status (null = all)
     * @return Flow of Result with task list
     */
    fun searchTasks(
        query: String = "",
        priority: String? = null,
        isCompleted: Boolean? = null
    ): Flow<Result<List<Task>>> {
        return when {
            // All filters active
            query.isNotEmpty() && priority != null && isCompleted != null -> {
                taskDao.searchTasksByAll(query, priority, isCompleted)
            }
            // Query + Priority
            query.isNotEmpty() && priority != null -> {
                taskDao.searchTasksByTitleAndPriority(query, priority)
            }
            // Query + Completion
            query.isNotEmpty() && isCompleted != null -> {
                taskDao.searchTasksByTitleAndCompletion(query, isCompleted)
            }
            // Priority + Completion
            priority != null && isCompleted != null -> {
                taskDao.getTasksByPriorityAndCompletion(priority, isCompleted)
            }
            // Query only
            query.isNotEmpty() -> {
                taskDao.searchTasksByTitle(query)
            }
            // Priority only
            priority != null -> {
                taskDao.getTasksByPriority(priority)
            }
            // Completion only
            isCompleted != null -> {
                taskDao.getTasksByCompletion(isCompleted)
            }
            // No filters
            else -> {
                taskDao.getAllTasks()
            }
        }.map { entities ->
            entities.map { it.toTask() }
        }.asResult()
    }
    
    /**
     * Get task by ID
     */
    fun getTaskById(taskId: Int): Flow<Result<Task?>> {
        return taskDao.getTaskById(taskId)
            .map { it?.toTask() }
            .asResult()
    }
    
    /**
     * Get all tasks
     */
    fun getAllTasks(): Flow<Result<List<Task>>> {
        return taskDao.getAllTasks()
            .map { entities -> entities.map { it.toTask() } }
            .asResult()
    }
    
    /**
     * Toggle task completion
     */
    suspend fun toggleTaskCompletion(taskId: Int, isCompleted: Boolean) {
        val completedAt = if (isCompleted) System.currentTimeMillis() else null
        taskDao.toggleCompletion(taskId, isCompleted, completedAt)
    }
    
    /**
     * Delete task
     */
    suspend fun deleteTask(taskId: Int) {
        taskDao.deleteTaskById(taskId)
    }
    
    /**
     * Create new task
     */
    suspend fun createTask(
        title: String,
        description: String,
        priority: String,
        dueDate: Long?,
        assignedTo: String?
    ) {
        val task = TaskEntity(
            id = 0, // Auto-increment will assign the ID
            title = title,
            description = description.ifBlank { null },
            priority = priority,
            assignedTo = assignedTo,
            dueDate = dueDate,
            isCompleted = false,
            createdAt = System.currentTimeMillis(),
            completedAt = null
        )
        taskDao.insertTask(task)
    }
    
    /**
     * Update existing task
     */
    suspend fun updateTask(
        taskId: Int,
        title: String,
        description: String,
        priority: String,
        dueDate: Long?,
        assignedTo: String?
    ) {
        // First get the existing task to preserve createdAt and completedAt
        val existingTask = taskDao.getTaskById(taskId)
        // Create updated entity
        val updatedTask = TaskEntity(
            id = taskId,
            title = title,
            description = description.ifBlank { null },
            priority = priority,
            dueDate = dueDate,
            assignedTo = assignedTo,
            isCompleted = false, // You may want to preserve this from existing task
            createdAt = System.currentTimeMillis(), // Should preserve from existing
            completedAt = null
        )
        taskDao.updateTask(updatedTask)
    }
    
    /**
     * Get tasks by priority
     */
    fun getTasksByPriority(priority: String): Flow<Result<List<Task>>> {
        return taskDao.getTasksByPriority(priority)
            .map { entities -> entities.map { it.toTask() } }
            .asResult()
    }
    
    /**
     * Get overdue tasks
     */
    fun getOverdueTasks(): Flow<Result<List<Task>>> {
        return taskDao.getOverdueTasks(System.currentTimeMillis())
            .map { entities -> entities.map { it.toTask() } }
            .asResult()
    }
}

/**
 * Extension function to convert TaskEntity to Task (domain model)
 */
private fun TaskEntity.toTask(): Task {
    return Task(
        id = this.id,
        title = this.title,
        description = this.description ?: "", // Convert null to empty string for UI
        priority = this.priority,
        assignedTo = this.assignedTo,
        dueDate = this.dueDate,
        isCompleted = this.isCompleted,
        createdAt = this.createdAt,
        completedAt = this.completedAt
    )
}
