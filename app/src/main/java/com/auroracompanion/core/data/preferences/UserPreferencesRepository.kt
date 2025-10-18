package com.auroracompanion.core.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.auroracompanion.core.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * User Preferences Repository
 * 
 * Manages user preferences using DataStore.
 * Provides type-safe access to stored preferences.
 * 
 * Stored Data:
 * - Store name and staff name (for login)
 * - First launch flag (to show welcome screen)
 * - Theme preference (light/dark/system)
 * 
 * Usage:
 * ```kotlin
 * // Read preferences (as Flow)
 * val storeName: Flow<String> = prefsRepository.storeName
 * 
 * // Write preferences (suspend function)
 * prefsRepository.saveStoreName("Pets at Home - London")
 * ```
 * 
 * Why Flow?
 * - Reactive: UI updates automatically when preferences change
 * - No polling: Efficient observation of changes
 * - Lifecycle-aware: Works well with Compose
 * 
 * @param dataStore DataStore instance injected by Hilt
 */
@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    
    // Preference keys (private to prevent external access)
    private object PreferenceKeys {
        val STORE_NAME = stringPreferencesKey(Constants.PREF_STORE_NAME)
        val STAFF_NAME = stringPreferencesKey(Constants.PREF_STAFF_NAME)
        val IS_FIRST_LAUNCH = booleanPreferencesKey(Constants.PREF_IS_FIRST_LAUNCH)
        val THEME_MODE = stringPreferencesKey(Constants.PREF_THEME_MODE)
    }
    
    // ============ Read Operations (Flows) ============
    
    /**
     * Store name Flow
     * Emits current store name or empty string
     */
    val storeName: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferenceKeys.STORE_NAME] ?: ""
    }
    
    /**
     * Staff name Flow
     * Emits current staff name or empty string
     */
    val staffName: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferenceKeys.STAFF_NAME] ?: ""
    }
    
    /**
     * First launch flag Flow
     * true = first launch (show welcome screen)
     * false = not first launch
     */
    val isFirstLaunch: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferenceKeys.IS_FIRST_LAUNCH] ?: true
    }
    
    /**
     * Theme mode Flow
     * Values: "light", "dark", "system"
     */
    val themeMode: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferenceKeys.THEME_MODE] ?: "system"
    }
    
    /**
     * Combined user info Flow
     * Emits UserInfo data class with all user data
     */
    val userInfo: Flow<UserInfo> = dataStore.data.map { preferences ->
        UserInfo(
            storeName = preferences[PreferenceKeys.STORE_NAME] ?: "",
            staffName = preferences[PreferenceKeys.STAFF_NAME] ?: "",
            isFirstLaunch = preferences[PreferenceKeys.IS_FIRST_LAUNCH] ?: true,
            themeMode = preferences[PreferenceKeys.THEME_MODE] ?: "system"
        )
    }
    
    // ============ Write Operations (Suspend Functions) ============
    
    /**
     * Save store name
     * @param name Store name to save
     */
    suspend fun saveStoreName(name: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.STORE_NAME] = name
        }
    }
    
    /**
     * Save staff name
     * @param name Staff member name
     */
    suspend fun saveStaffName(name: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.STAFF_NAME] = name
        }
    }
    
    /**
     * Save both store and staff name (for login)
     * @param storeName Store name
     * @param staffName Staff name
     */
    suspend fun saveUserInfo(storeName: String, staffName: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.STORE_NAME] = storeName
            preferences[PreferenceKeys.STAFF_NAME] = staffName
            preferences[PreferenceKeys.IS_FIRST_LAUNCH] = false
        }
    }
    
    /**
     * Mark first launch as complete
     */
    suspend fun setFirstLaunchComplete() {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.IS_FIRST_LAUNCH] = false
        }
    }
    
    /**
     * Save theme mode
     * @param mode Theme mode: "light", "dark", or "system"
     */
    suspend fun saveThemeMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.THEME_MODE] = mode
        }
    }
    
    /**
     * Clear all preferences (for logout/reset)
     */
    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

/**
 * Data class for user information
 * Combines all user-related preferences
 */
data class UserInfo(
    val storeName: String,
    val staffName: String,
    val isFirstLaunch: Boolean,
    val themeMode: String
)
