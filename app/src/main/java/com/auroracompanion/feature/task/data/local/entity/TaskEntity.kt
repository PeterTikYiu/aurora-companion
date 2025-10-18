package com.auroracompanion.feature.task.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

/**
 * Room Entity for Task
 * 
 * Represents a store task (e.g., feeding pets, restocking, cleaning).
 * 
 * Design Notes:
 * - Tasks can be created by staff or preloaded from JSON
 * - Priority is stored as String (LOW, MEDIUM, HIGH) for simplicity
 * - Completion tracking with timestamps
 * - Indexed by priority and dueDate for efficient sorting/filtering
 * 
 * @property id Auto-generated unique ID
 * @property title Task name/summary
 * @property description Detailed task description
 * @property priority Task priority (LOW, MEDIUM, HIGH)
 * @property assignedTo Staff member name (optional)
 * @property dueDate Due date timestamp (optional)
 * @property isCompleted Completion status
 * @property createdAt Task creation timestamp
 * @property completedAt Completion timestamp (null if not completed)
 */
@Entity(
    tableName = "tasks",
    indices = [
        Index(value = ["priority"]),
        Index(value = ["dueDate"]),
        Index(value = ["isCompleted"])
    ]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    val title: String,
    val description: String? = null,
    val priority: String, // LOW, MEDIUM, HIGH
    val assignedTo: String? = null,
    val dueDate: Long? = null,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
