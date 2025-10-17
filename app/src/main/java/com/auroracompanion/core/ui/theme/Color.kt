package com.auroracompanion.core.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Aurora Companion Color Palette
 * 
 * Based on Pets at Home brand colors with Material 3 tokens.
 * 
 * Light Theme:
 * - Primary: Teal (#00897B) - main brand color
 * - Secondary: Yellow (#FDD835) - accent color
 * 
 * Dark Theme:
 * - Adjusted for better contrast and accessibility
 */

// Light Theme Colors
val PrimaryLight = Color(0xFF00897B)          // Teal - main actions, app bar
val OnPrimaryLight = Color(0xFFFFFFFF)        // White text on primary
val PrimaryContainerLight = Color(0xFFB2DFDB) // Light teal - backgrounds
val OnPrimaryContainerLight = Color(0xFF004D40)

val SecondaryLight = Color(0xFFFDD835)        // Yellow - accents, highlights
val OnSecondaryLight = Color(0xFF000000)      // Black text on yellow
val SecondaryContainerLight = Color(0xFFFFF9C4)
val OnSecondaryContainerLight = Color(0xFF6D4C00)

val TertiaryLight = Color(0xFF6200EE)         // Purple - tertiary actions
val OnTertiaryLight = Color(0xFFFFFFFF)
val TertiaryContainerLight = Color(0xFFBB86FC)
val OnTertiaryContainerLight = Color(0xFF3700B3)

val BackgroundLight = Color(0xFFFAFAFA)       // Very light gray
val OnBackgroundLight = Color(0xFF1C1B1F)     // Almost black text

val SurfaceLight = Color(0xFFFFFFFF)          // White cards/surfaces
val OnSurfaceLight = Color(0xFF1C1B1F)        // Dark text on surfaces
val SurfaceVariantLight = Color(0xFFE0E0E0)   // Slightly gray surfaces
val OnSurfaceVariantLight = Color(0xFF49454F)

val ErrorLight = Color(0xFFB3261E)            // Red for errors
val OnErrorLight = Color(0xFFFFFFFF)
val ErrorContainerLight = Color(0xFFF9DEDC)
val OnErrorContainerLight = Color(0xFF410E0B)

val OutlineLight = Color(0xFF79747E)          // Borders, dividers
val OutlineVariantLight = Color(0xFFCAC4D0)

// Dark Theme Colors
val PrimaryDark = Color(0xFF4DB6AC)           // Lighter teal for dark mode
val OnPrimaryDark = Color(0xFF003D33)
val PrimaryContainerDark = Color(0xFF00695C)
val OnPrimaryContainerDark = Color(0xFFB2DFDB)

val SecondaryDark = Color(0xFFFDD835)         // Same yellow (good contrast)
val OnSecondaryDark = Color(0xFF000000)
val SecondaryContainerDark = Color(0xFFC6A700)
val OnSecondaryContainerDark = Color(0xFFFFF9C4)

val TertiaryDark = Color(0xFFBB86FC)
val OnTertiaryDark = Color(0xFF3700B3)
val TertiaryContainerDark = Color(0xFF6200EE)
val OnTertiaryContainerDark = Color(0xFFE1BEE7)

val BackgroundDark = Color(0xFF1C1B1F)        // Very dark gray
val OnBackgroundDark = Color(0xFFE6E1E5)      // Light gray text

val SurfaceDark = Color(0xFF1C1B1F)           // Dark surfaces
val OnSurfaceDark = Color(0xFFE6E1E5)
val SurfaceVariantDark = Color(0xFF49454F)
val OnSurfaceVariantDark = Color(0xFFCAC4D0)

val ErrorDark = Color(0xFFF2B8B5)
val OnErrorDark = Color(0xFF601410)
val ErrorContainerDark = Color(0xFF8C1D18)
val OnErrorContainerDark = Color(0xFFF9DEDC)

val OutlineDark = Color(0xFF938F99)
val OutlineVariantDark = Color(0xFF49454F)

// Custom Semantic Colors (used throughout app)
val SuccessColor = Color(0xFF4CAF50)          // Green - success states
val WarningColor = Color(0xFFFFA726)          // Orange - warnings
val InfoColor = Color(0xFF2196F3)             // Blue - informational

val LowStockColor = Color(0xFFFF9800)         // Orange - low stock warning
val OutOfStockColor = Color(0xFFF44336)       // Red - out of stock error
val InStockColor = Color(0xFF4CAF50)          // Green - in stock
