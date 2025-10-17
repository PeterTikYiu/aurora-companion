package com.auroracompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.auroracompanion.core.ui.theme.AuroraCompanionTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity - The single activity for the entire app
 * 
 * This app follows the Single-Activity Architecture pattern recommended
 * by Google for modern Android apps. All screens are Composable functions,
 * and navigation is handled by Jetpack Navigation Compose.
 * 
 * @AndroidEntryPoint annotation enables Hilt dependency injection in this activity.
 * This allows us to inject ViewModels and other dependencies throughout the app.
 * 
 * Architecture Flow:
 * MainActivity → Navigation Graph → Feature Screens → ViewModels → Repositories → Data Sources
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display (content draws behind system bars)
        enableEdgeToEdge()
        
        setContent {
            AuroraCompanionTheme {
                // Surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // TODO: Add Navigation Graph here
                    // For now, we'll add a placeholder composable
                    PlaceholderScreen()
                }
            }
        }
    }
}
