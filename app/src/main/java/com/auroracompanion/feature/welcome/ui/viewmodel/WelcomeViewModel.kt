package com.auroracompanion.feature.welcome.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auroracompanion.core.data.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Welcome ViewModel
 * 
 * Manages state for the Welcome screen where users set up
 * their store and staff information on first launch.
 * 
 * Responsibilities:
 * - Store name input validation
 * - Staff name input validation
 * - Save setup to DataStore
 * - Loading state management
 * - Setup completion tracking
 * 
 * @param userPreferencesRepository Repository for user preferences
 */
@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    
    // Store name input
    private val _storeName = MutableStateFlow("")
    val storeName: StateFlow<String> = _storeName.asStateFlow()
    
    // Staff name input
    private val _staffName = MutableStateFlow("")
    val staffName: StateFlow<String> = _staffName.asStateFlow()
    
    // Loading state during save
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Setup completion flag
    private val _isSetupComplete = MutableStateFlow(false)
    val isSetupComplete: StateFlow<Boolean> = _isSetupComplete.asStateFlow()
    
    /**
     * Update store name
     */
    fun onStoreNameChange(name: String) {
        _storeName.value = name
    }
    
    /**
     * Update staff name
     */
    fun onStaffNameChange(name: String) {
        _staffName.value = name
    }
    
    /**
     * Save setup to DataStore
     * 
     * Validates input and persists user preferences.
     * Shows loading state during save.
     */
    fun saveSetup() {
        // Validate inputs
        if (_storeName.value.isBlank() || _staffName.value.isBlank()) {
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                // Save to DataStore
                userPreferencesRepository.saveStoreName(_storeName.value.trim())
                userPreferencesRepository.saveStaffName(_staffName.value.trim())
                userPreferencesRepository.setFirstLaunchComplete()
                
                // Small delay for smooth UX (allows user to see the loading state)
                delay(500)
                
                // Mark setup as complete
                _isSetupComplete.value = true
            } catch (e: Exception) {
                // Handle error (could add error state here)
                println("Error saving setup: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
