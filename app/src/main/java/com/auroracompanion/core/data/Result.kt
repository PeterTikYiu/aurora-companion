package com.auroracompanion.core.data

/**
 * A generic wrapper for handling operation results.
 * 
 * This sealed interface represents the three possible states of any operation:
 * - Success: Operation completed successfully with data
 * - Error: Operation failed with an error message
 * - Loading: Operation is in progress
 * 
 * Usage Example:
 * ```
 * when (result) {
 *     is Result.Success -> showData(result.data)
 *     is Result.Error -> showError(result.message)
 *     is Result.Loading -> showLoadingSpinner()
 * }
 * ```
 * 
 * Benefits:
 * - Type-safe error handling
 * - Forces explicit handling of all states
 * - Works seamlessly with Kotlin's when expressions
 */
sealed interface Result<out T> {
    /**
     * Successful operation with data
     * @param data The result data
     */
    data class Success<T>(val data: T) : Result<T>
    
    /**
     * Failed operation with error details
     * @param message Human-readable error message
     * @param exception Optional exception for logging/debugging
     */
    data class Error(
        val message: String,
        val exception: Throwable? = null
    ) : Result<Nothing>
    
    /**
     * Operation in progress
     */
    data object Loading : Result<Nothing>
}

/**
 * Extension function to safely map Success results
 * 
 * Example:
 * ```
 * val productResult: Result<Product> = ...
 * val nameResult: Result<String> = productResult.map { it.name }
 * ```
 */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
    return when (this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Error -> this
        is Result.Loading -> this
    }
}

/**
 * Extension function to execute code only on Success
 */
inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) {
        action(data)
    }
    return this
}

/**
 * Extension function to execute code only on Error
 */
inline fun <T> Result<T>.onError(action: (String, Throwable?) -> Unit): Result<T> {
    if (this is Result.Error) {
        action(message, exception)
    }
    return this
}
