package com.auroracompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.auroracompanion.core.data.repository.UserPreferencesRepository
import com.auroracompanion.core.ui.theme.AuroraCompanionTheme
import com.auroracompanion.navigation.AuroraNavGraph
import com.auroracompanion.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

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
 * 
 * First Launch Flow:
 * - Checks UserPreferences for firstLaunch flag
 * - If true: Shows Welcome screen for setup
 * - If false: Shows Product List directly
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository
    
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
                    // Determine start destination based on first launch
                    MainContent(userPreferencesRepository)
                }
            }
        }
    }
}

/**
 * Main Content
 * 
 * Checks first launch status and sets appropriate start destination
 */
@Composable
private fun MainContent(userPreferencesRepository: UserPreferencesRepository) {
    var startDestination by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    
    // Check first launch on composition
    LaunchedEffect(Unit) {
        scope.launch {
            val isFirstLaunch = userPreferencesRepository.isFirstLaunch.first()
            startDestination = if (isFirstLaunch) {
                Screen.Welcome.route
            } else {
                Screen.ProductList.route
            }
        }
    }
    
    // Show loading while checking first launch
    if (startDestination == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        val navController = rememberNavController()
        AuroraNavGraph(
            navController = navController,
            startDestination = startDestination!!
        )
    }
}
