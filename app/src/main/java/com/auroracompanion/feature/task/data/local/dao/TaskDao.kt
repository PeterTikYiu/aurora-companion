package com.auroracompanion.feature.task.data.local.dao

import androidx.room.*
import com.auroracompanion.feature.task.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Task operations
 * 
 * Provides database operations for tasks with reactive Flow support.
 * 
 * Key Operations:
 * - getAllTasks: All tasks ordered by due date
 * - getTaskById: Single task details
 * - getTasksByPriority: Filter by priority level
 * - getIncompleteTasks: Active tasks only
 * - getCompletedTasks: Finished tasks
 * - getTasksDueToday: Tasks due today
 * - insertTask: Add new task
 * - updateTask: Update existing task
 * - completeTask: Mark task as complete
 * - deleteTask: Remove task
 */
@Dao
interface TaskDao {
    
    /**
     * Get all tasks ordered by due date (nulls last)
     */
    @Query("SELECT * FROM tasks ORDER BY CASE WHEN dueDate IS NULL THEN 1 ELSE 0 END, dueDate ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>
    
    /**
     * Get single task by ID
     */
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Int): Flow<TaskEntity?>
    
    /**
     * Get tasks by priority
     */
    @Query("SELECT * FROM tasks WHERE priority = :priority ORDER BY dueDate ASC")
    fun getTasksByPriority(priority: String): Flow<List<TaskEntity>>
    
    /**
     * Get incomplete tasks only
     */
    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY CASE WHEN dueDate IS NULL THEN 1 ELSE 0 END, dueDate ASC")
    fun getIncompleteTasks(): Flow<List<TaskEntity>>
    
    /**
     * Get completed tasks
     */
    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY completedAt DESC")
    fun getCompletedTasks(): Flow<List<TaskEntity>>
    
    /**
     * Get tasks by completion status
     */
    @Query("SELECT * FROM tasks WHERE isCompleted = :isCompleted ORDER BY CASE WHEN dueDate IS NULL THEN 1 ELSE 0 END, dueDate ASC")
    fun getTasksByCompletion(isCompleted: Boolean): Flow<List<TaskEntity>>
    
    /**
     * Get tasks due today
     * @param startOfDay Timestamp for start of day (00:00:00)
     * @param endOfDay Timestamp for end of day (23:59:59)
     */
    @Query("SELECT * FROM tasks WHERE dueDate >= :startOfDay AND dueDate <= :endOfDay ORDER BY dueDate ASC")
    fun getTasksDueToday(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>>
    
    /**
     * Get overdue tasks (past due date and not completed)
     */
    @Query("SELECT * FROM tasks WHERE dueDate < :currentTime AND isCompleted = 0 ORDER BY dueDate ASC")
    fun getOverdueTasks(currentTime: Long = System.currentTimeMillis()): Flow<List<TaskEntity>>
    
    /**
     * Get tasks assigned to specific staff member
     */
    @Query("SELECT * FROM tasks WHERE assignedTo = :staffName ORDER BY dueDate ASC")
    fun getTasksByAssignee(staffName: String): Flow<List<TaskEntity>>
    
    /**
     * Search tasks by title (case-insensitive)
     */
    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' ORDER BY dueDate ASC")
    fun searchTasksByTitle(query: String): Flow<List<TaskEntity>>
    
    /**
     * Search tasks by title and priority
     */
    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' AND priority = :priority ORDER BY dueDate ASC")
    fun searchTasksByTitleAndPriority(query: String, priority: String): Flow<List<TaskEntity>>
    
    /**
     * Search tasks by title and completion status
     */
    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' AND isCompleted = :isCompleted ORDER BY dueDate ASC")
    fun searchTasksByTitleAndCompletion(query: String, isCompleted: Boolean): Flow<List<TaskEntity>>
    
    /**
     * Get tasks by priority and completion status
     */
    @Query("SELECT * FROM tasks WHERE priority = :priority AND isCompleted = :isCompleted ORDER BY dueDate ASC")
    fun getTasksByPriorityAndCompletion(priority: String, isCompleted: Boolean): Flow<List<TaskEntity>>
    
    /**
     * Search tasks by all filters (title, priority, completion)
     */
    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' AND priority = :priority AND isCompleted = :isCompleted ORDER BY dueDate ASC")
    fun searchTasksByAll(query: String, priority: String, isCompleted: Boolean): Flow<List<TaskEntity>>
    
    /**
     * Insert new task
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long
    
    /**
     * Insert multiple tasks (for seeding)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskEntity>): List<Long>
    
    /**
     * Update task
     */
    @Update
    suspend fun updateTask(task: TaskEntity)
    
    /**
     * Mark task as complete
     */
    @Query("UPDATE tasks SET isCompleted = 1, completedAt = :completedAt WHERE id = :taskId")
    suspend fun completeTask(taskId: Int, completedAt: Long = System.currentTimeMillis())
    
    /**
     * Mark task as incomplete
     */
    @Query("UPDATE tasks SET isCompleted = 0, completedAt = NULL WHERE id = :taskId")
    suspend fun uncompleteTask(taskId: Int)
    
    /**
     * Toggle task completion
     */
    @Query("UPDATE tasks SET isCompleted = :isCompleted, completedAt = :completedAt WHERE id = :taskId")
    suspend fun toggleCompletion(taskId: Int, isCompleted: Boolean, completedAt: Long?)
    
    /**
     * Delete task by ID
     */
    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Int)
    
    /**
     * Delete task
     */
    @Delete
    suspend fun deleteTask(task: TaskEntity)
    
    /**
     * Delete completed tasks (cleanup)
     */
    @Query("DELETE FROM tasks WHERE isCompleted = 1")
    suspend fun deleteCompletedTasks()
    
    /**
     * Delete all tasks
     */
    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
    
    /**
     * Get task count by status
     */
    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = :isCompleted")
    fun getTaskCountByStatus(isCompleted: Boolean): Flow<Int>
}
