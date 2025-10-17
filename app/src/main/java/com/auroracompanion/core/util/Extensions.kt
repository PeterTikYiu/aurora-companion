package com.auroracompanion.core.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import com.auroracompanion.core.data.Result
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Currency
import java.util.Date
import java.util.Locale

/**
 * Extension Functions for common operations
 * 
 * These utility functions make code more concise and readable.
 */

// ============ Flow Extensions ============

/**
 * Wraps a Flow<T> in a Result wrapper, handling loading and error states
 * 
 * Example:
 * ```
 * productDao.getAllProducts()
 *     .asResult()
 *     .collect { result ->
 *         when (result) {
 *             is Result.Success -> // handle success
 *             is Result.Error -> // handle error
 *             is Result.Loading -> // show loading
 *         }
 *     }
 * ```
 */
fun <T> Flow<T>.asResult(): Flow<Result<T>> {
    return this
        .map<T, Result<T>> { Result.Success(it) }
        .onStart { emit(Result.Loading) }
        .catch { emit(Result.Error(it.message ?: "Unknown error occurred", it)) }
}

// ============ String Extensions ============

/**
 * Capitalize first letter of each word
 */
fun String.toTitleCase(): String {
    return split(" ").joinToString(" ") { word ->
        word.lowercase().replaceFirstChar { it.uppercase() }
    }
}

/**
 * Validate if string is a valid SKU format
 * SKU format: Letters and numbers, 6-12 characters
 */
fun String.isValidSku(): Boolean {
    return matches(Regex("^[A-Z0-9]{6,12}$"))
}

// ============ Number Extensions ============

/**
 * Format Double as currency (£)
 * Example: 19.99.toCurrency() -> "£19.99"
 */
fun Double.toCurrency(): String {
    val format = NumberFormat.getCurrencyInstance(Locale.UK)
    format.currency = Currency.getInstance("GBP")
    return format.format(this)
}

/**
 * Format Int as currency
 */
fun Int.toCurrency(): String {
    return this.toDouble().toCurrency()
}

// ============ Date Extensions ============

/**
 * Format Date to readable string
 * Example: "Jan 15, 2025"
 */
fun Date.toFormattedString(pattern: String = "MMM dd, yyyy"): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(this)
}

/**
 * Format Long timestamp to readable string
 */
fun Long.toFormattedDate(pattern: String = "MMM dd, yyyy"): String {
    return Date(this).toFormattedString(pattern)
}

/**
 * Check if date is today
 */
fun Date.isToday(): Boolean {
    val today = Date()
    val formatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    return formatter.format(this) == formatter.format(today)
}

// ============ List Extensions ============

/**
 * Safely get element at index or return null
 */
fun <T> List<T>.getOrNull(index: Int): T? {
    return if (index in indices) this[index] else null
}
