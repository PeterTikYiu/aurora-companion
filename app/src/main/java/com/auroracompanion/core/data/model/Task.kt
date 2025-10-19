package com.auroracompanion.core.data.model

/**
 * Task Domain Model
 * 
 * Represents a task in the UI layer.
 * This is the model used throughout the app's presentation layer.
 * 
 * Key difference from TaskEntity:
 * - Entity is for database (Room)
 * - Task is for UI/business logic
 * - Repository converts between them
 * 
 * @property id Unique task identifier
 * @property title Task title
 * @property description Detailed description
 * @property priority Priority level (High/Medium/Low)
 * @property assignedTo Staff member assigned
 * @property dueDate Due date timestamp (nullable)
 * @property isCompleted Completion status
 * @property createdAt Creation timestamp
 * @property completedAt Completion timestamp (nullable)
 */
data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val priority: String,
    val assignedTo: String?,
    val dueDate: Long?,
    val isCompleted: Boolean,
    val createdAt: Long,
    val completedAt: Long?
)
