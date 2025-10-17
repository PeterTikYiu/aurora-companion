package com.auroracompanion

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Aurora Companion Application Class
 * 
 * This is the entry point for the entire app. The @HiltAndroidApp annotation
 * triggers Hilt's code generation, including a base class for the application
 * that serves as the dependency container.
 * 
 * Key Responsibilities:
 * - Initialize Hilt dependency injection
 * - Set up global configurations
 * - Handle app-wide lifecycle events
 */
@HiltAndroidApp
class AuroraApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // TODO: Add any app-level initialization here
        // Examples:
        // - Timber for logging (if added)
        // - WorkManager for background tasks
        // - Crash reporting tools
    }
}
