package com.auroracompanion.feature.welcome.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.auroracompanion.feature.welcome.ui.viewmodel.WelcomeViewModel
import kotlinx.coroutines.delay

/**
 * Welcome Screen
 * 
 * First-time user experience for setting up store and staff information.
 * 
 * Features:
 * - Animated entrance
 * - Store name input with validation
 * - Staff name input with validation
 * - Smooth transitions
 * - Progress indicator during save
 * - Professional, welcoming design
 * 
 * UX Highlights:
 * - Material You design language
 * - Smooth fade-in animations
 * - Input validation feedback
 * - Auto-focus flow
 * - Keyboard actions (Next/Done)
 * - Accessibility support
 * 
 * @param onSetupComplete Callback when setup is finished
 * @param viewModel Welcome ViewModel (Hilt injected)
 */
@Composable
fun WelcomeScreen(
    onSetupComplete: () -> Unit,
    viewModel: WelcomeViewModel = hiltViewModel()
) {
    val storeName by viewModel.storeName.collectAsState()
    val staffName by viewModel.staffName.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSetupComplete by viewModel.isSetupComplete.collectAsState()
    
    // Navigate when setup is complete
    LaunchedEffect(isSetupComplete) {
        if (isSetupComplete) {
            delay(300) // Small delay for smooth transition
            onSetupComplete()
        }
    }
    
    // Entrance animation state
    var startAnimation by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        startAnimation = true
    }
    
    WelcomeContent(
        storeName = storeName,
        staffName = staffName,
        onStoreNameChange = viewModel::onStoreNameChange,
        onStaffNameChange = viewModel::onStaffNameChange,
        onCompleteSetup = viewModel::saveSetup,
        isLoading = isLoading,
        isAnimated = startAnimation
    )
}

/**
 * Welcome Content
 * 
 * Main content layout with animations
 */
@Composable
private fun WelcomeContent(
    storeName: String,
    staffName: String,
    onStoreNameChange: (String) -> Unit,
    onStaffNameChange: (String) -> Unit,
    onCompleteSetup: () -> Unit,
    isLoading: Boolean,
    isAnimated: Boolean
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    
    // Animation values
    val alphaAnimation by animateFloatAsState(
        targetValue = if (isAnimated) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "alpha"
    )
    
    val slideAnimation by animateIntAsState(
        targetValue = if (isAnimated) 0 else 50,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "slide"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(alphaAnimation)
                .offset(y = slideAnimation.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Welcome Header with Icon
            WelcomeHeader()
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Input Fields Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Text(
                        text = "Let's get you set up",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    // Store Name Input
                    StyledTextField(
                        value = storeName,
                        onValueChange = onStoreNameChange,
                        label = "Store Name",
                        placeholder = "e.g., Pets at Home - Manchester",
                        icon = Icons.Default.Store,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        enabled = !isLoading
                    )
                    
                    // Staff Name Input
                    StyledTextField(
                        value = staffName,
                        onValueChange = onStaffNameChange,
                        label = "Your Name",
                        placeholder = "e.g., John Smith",
                        icon = Icons.Default.Person,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                if (storeName.isNotBlank() && staffName.isNotBlank()) {
                                    onCompleteSetup()
                                }
                            }
                        ),
                        enabled = !isLoading
                    )
                }
            }
            
            // Continue Button
            AnimatedContinueButton(
                enabled = storeName.isNotBlank() && staffName.isNotBlank() && !isLoading,
                isLoading = isLoading,
                onClick = onCompleteSetup
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * Welcome Header
 * 
 * App branding with animated emoji and welcome text
 */
@Composable
private fun WelcomeHeader() {
    // Emoji scale animation
    val infiniteTransition = rememberInfiniteTransition(label = "emoji")
    val emojiScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "emoji_scale"
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Icon/Emoji
        Text(
            text = "🐾",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier
                .size(120.dp)
                .wrapContentSize()
                .graphicsLayer(
                    scaleX = emojiScale,
                    scaleY = emojiScale
                )
        )
        
        // Welcome Title
        Text(
            text = "Welcome to",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "Aurora Companion",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "Your intelligent store assistant",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Styled TextField Component
 * 
 * Consistent input field with icon and Material 3 styling
 */
@Composable
private fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        shape = MaterialTheme.shapes.large,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    )
}

/**
 * Animated Continue Button
 * 
 * Primary action button with loading state
 */
@Composable
private fun AnimatedContinueButton(
    enabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Button scale animation when enabled
    val scale by animateFloatAsState(
        targetValue = if (enabled && !isLoading) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "button_scale"
    )
    
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            ),
        shape = MaterialTheme.shapes.large,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp,
            disabledElevation = 0.dp
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Get Started",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "→",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}
