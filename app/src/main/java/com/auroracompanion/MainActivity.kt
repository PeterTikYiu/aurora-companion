package com.auroracompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.auroracompanion.core.data.preferences.UserPreferencesRepository
import com.auroracompanion.core.ui.theme.AuroraCompanionTheme
import com.auroracompanion.navigation.AuroraNavGraph
import com.auroracompanion.navigation.MainScreen
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
    
    @Inject
    lateinit var databaseSeeder: com.auroracompanion.core.data.local.DatabaseSeeder
    
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
                    MainContent(userPreferencesRepository, databaseSeeder)
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
private fun MainContent(
    userPreferencesRepository: UserPreferencesRepository,
    databaseSeeder: com.auroracompanion.core.data.local.DatabaseSeeder
) {
    var startDestination by remember { mutableStateOf<String?>(null) }
    var isSeeding by remember { mutableStateOf(false) }
    var showWelcome by remember { mutableStateOf<Boolean?>(null) }
    
    // Observe first launch status
    val isFirstLaunch by userPreferencesRepository.isFirstLaunch.collectAsState(initial = true)
    
    // Check first launch on composition
    LaunchedEffect(Unit) {
        try {
            // Seed database if needed (in background)
            isSeeding = true
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    databaseSeeder.seedDatabase()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            isSeeding = false
            
            startDestination = Screen.ProductList.route
            showWelcome = isFirstLaunch
        } catch (e: Exception) {
            // If error reading preferences, default to welcome screen
            e.printStackTrace()
            startDestination = Screen.ProductList.route
            showWelcome = true
            isSeeding = false
        }
    }
    
    // Update showWelcome when isFirstLaunch changes
    LaunchedEffect(isFirstLaunch) {
        if (startDestination != null) {
            showWelcome = isFirstLaunch
        }
    }
    
    // Show loading while checking first launch or seeding database
    if (startDestination == null || isSeeding || showWelcome == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                if (isSeeding) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading sample data...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    } else if (showWelcome == true) {
        // Welcome screen without bottom nav
        val navController = rememberNavController()
        AuroraNavGraph(
            navController = navController,
            startDestination = Screen.Welcome.route
        )
    } else {
        // Main screens with bottom nav
        MainScreen(startDestination = startDestination!!)
    }
}
