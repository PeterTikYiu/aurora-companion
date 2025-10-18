package com.auroracompanion.core.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for Product JSON
 * 
 * Used for parsing products.json file.
 * Converts to ProductEntity for database storage.
 * 
 * @SerializedName maps JSON field names to Kotlin properties
 */
data class ProductDto(
    @SerializedName("sku")
    val sku: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("category")
    val category: String,
    
    @SerializedName("price")
    val price: Double,
    
    @SerializedName("stockQty")
    val stockQty: Int,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("imageUri")
    val imageUri: String? = null
)

/**
 * Data Transfer Object for Task JSON
 * 
 * Used for parsing tasks.json file.
 * Converts to TaskEntity for database storage.
 */
data class TaskDto(
    @SerializedName("title")
    val title: String,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("priority")
    val priority: String,
    
    @SerializedName("assignedTo")
    val assignedTo: String? = null,
    
    @SerializedName("dueDate")
    val dueDate: Long? = null
)
