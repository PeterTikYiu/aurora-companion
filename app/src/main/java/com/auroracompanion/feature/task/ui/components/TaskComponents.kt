package com.auroracompanion.feature.task.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.auroracompanion.core.data.model.Task
import com.auroracompanion.core.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Task Card Component
 * 
 * Displays task information in a card format with completion toggle.
 * 
 * Features:
 * - Checkbox for completion
 * - Priority badge
 * - Due date with overdue warning
 * - Assigned staff member
 * - Strike-through when completed
 * 
 * @param task Task to display
 * @param onTaskClick Click handler for card
 * @param onCompletionToggle Handler for checkbox
 * @param modifier Optional modifier
 */
@Composable
fun TaskCard(
    task: Task,
    onTaskClick: () -> Unit,
    onCompletionToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onTaskClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Completion Checkbox
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = onCompletionToggle,
                modifier = Modifier.padding(end = 12.dp)
            )
            
            // Task Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Title
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (task.isCompleted) {
                        TextDecoration.LineThrough
                    } else {
                        TextDecoration.None
                    },
                    color = if (task.isCompleted) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                
                if (task.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Due Date and Assigned To
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Due Date
                    task.dueDate?.let { dueDate ->
                        DueDateChip(
                            dueDate = dueDate,
                            isCompleted = task.isCompleted
                        )
                    }
                    
                    // Assigned To
                    task.assignedTo?.let { assignedTo ->
                        Text(
                            text = "👤 $assignedTo",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Priority Badge
            PriorityBadge(
                priority = task.priority,
                isCompact = false
            )
        }
    }
}

/**
 * Priority Badge Component
 * 
 * Shows priority with color-coded badge.
 * 
 * @param priority Priority level (High/Medium/Low)
 * @param isCompact Use compact style
 */
@Composable
fun PriorityBadge(
    priority: String,
    isCompact: Boolean = false,
    modifier: Modifier = Modifier
) {
    data class PriorityStyle(
        val backgroundColor: Color,
        val textColor: Color,
        val icon: String,
        val displayName: String
    )
    
    val style = when (priority.uppercase()) {
        "HIGH" -> PriorityStyle(
            backgroundColor = Color(0xFFEF5350).copy(alpha = 0.15f),
            textColor = Color(0xFFEF5350),
            icon = "🔴",
            displayName = "High"
        )
        "MEDIUM" -> PriorityStyle(
            backgroundColor = Color(0xFFFFA726).copy(alpha = 0.15f),
            textColor = Color(0xFFFFA726),
            icon = "🟡",
            displayName = "Medium"
        )
        "LOW" -> PriorityStyle(
            backgroundColor = Color(0xFF66BB6A).copy(alpha = 0.15f),
            textColor = Color(0xFF66BB6A),
            icon = "🟢",
            displayName = "Low"
        )
        else -> PriorityStyle(
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            textColor = MaterialTheme.colorScheme.onSurfaceVariant,
            icon = "⚪",
            displayName = priority
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
 * Due Date Chip Component
 * 
 * Shows due date with overdue warning
 */
@Composable
fun DueDateChip(
    dueDate: Long,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    val now = System.currentTimeMillis()
    val isOverdue = !isCompleted && dueDate < now
    val isToday = isSameDay(dueDate, now)
    
    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    val dateText = when {
        isToday -> "Today"
        else -> dateFormat.format(Date(dueDate))
    }
    
    val (backgroundColor, textColor, icon) = when {
        isOverdue -> Triple(
            Color(0xFFEF5350).copy(alpha = 0.15f),
            Color(0xFFEF5350),
            "⚠️"
        )
        isToday -> Triple(
            Color(0xFFFFA726).copy(alpha = 0.15f),
            Color(0xFFFFA726),
            "📅"
        )
        else -> Triple(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            MaterialTheme.colorScheme.onPrimaryContainer,
            "📅"
        )
    }
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = dateText,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isOverdue) FontWeight.Bold else FontWeight.Medium,
                color = textColor
            )
        }
    }
}

/**
 * Task List Item Component
 * 
 * Compact list item for simple views
 */
@Composable
fun TaskListItem(
    task: Task,
    onTaskClick: () -> Unit,
    onCompletionToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onTaskClick,
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
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = onCompletionToggle,
                modifier = Modifier.padding(end = 12.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (task.isCompleted) {
                        TextDecoration.LineThrough
                    } else {
                        TextDecoration.None
                    }
                )
                
                task.dueDate?.let { dueDate ->
                    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
                    Text(
                        text = dateFormat.format(Date(dueDate)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            PriorityBadge(
                priority = task.priority,
                isCompact = true
            )
        }
    }
}

/**
 * Tasks Empty State Component
 */
@Composable
fun TasksEmptyState(
    message: String = "No tasks yet",
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
                text = "✓",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

/**
 * Tasks Error State Component
 */
@Composable
fun TasksErrorState(
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
                color = MaterialTheme.colorScheme.error,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

/**
 * Helper function to check if two timestamps are on the same day
 */
private fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = timestamp2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
