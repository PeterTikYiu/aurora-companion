package com.auroracompanion.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.auroracompanion.core.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module for DataStore Dependencies
 * 
 * Provides DataStore for storing user preferences.
 * 
 * DataStore vs SharedPreferences:
 * - Type-safe with coroutines
 * - Atomic read/write operations
 * - No UI thread blocking
 * - Built-in Flow support
 * 
 * Stored Preferences:
 * - Store name
 * - Staff name
 * - Theme preference
 * - First launch flag
 * 
 * Extension Property Pattern:
 * Uses Kotlin extension property for DataStore creation
 * Ensures single instance per context
 */

// Extension property for DataStore
// Creates DataStore lazily when first accessed
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = Constants.DATASTORE_NAME
)

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    
    /**
     * Provides DataStore instance
     * 
     * @param context Application context
     * @return Singleton DataStore instance
     */
    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return context.dataStore
    }
}
