package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// -------------------------------------------------------------
// Cozy Companion - Modern Aesthetic Palette (Iris & Emerald Glow)
// -------------------------------------------------------------

// Light Theme Tokens
val LightPrimary = Color(0xFF6366F1)           // Vibrant Modern Indigo / Iris
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFEEF2FF)  // Soft soothing lavender-indigo tint
val LightOnPrimaryContainer = Color(0xFF3730A3)
val LightPrimaryFixed = Color(0xFFE0E7FF)
val LightPrimaryFixedDim = Color(0xFFC7D2FE)
val LightOnPrimaryFixed = Color(0xFF1E1B4B)
val LightOnPrimaryFixedVariant = Color(0xFF4338CA)

val LightSecondary = Color(0xFF10B981)         // Energizing Fresh Mint
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFD1FAE5)
val LightOnSecondaryContainer = Color(0xFF065F46)
val LightSecondaryFixed = Color(0xFFD1FAE5)
val LightSecondaryFixedDim = Color(0xFFA7F3D0)
val LightOnSecondaryFixed = Color(0xFF022C22)
val LightOnSecondaryFixedVariant = Color(0xFF047857)

val LightTertiary = Color(0xFFF97316)          // Warm Sunset Coral / Amber
val LightOnTertiary = Color(0xFFFFFFFF)
val LightTertiaryContainer = Color(0xFFFFEDD5)
val LightOnTertiaryContainer = Color(0xFF9A3412)
val LightTertiaryFixed = Color(0xFFFFEDD5)
val LightTertiaryFixedDim = Color(0xFFFED7AA)
val LightOnTertiaryFixed = Color(0xFF431407)
val LightOnTertiaryFixedVariant = Color(0xFFC2410C)

val LightBackground = Color(0xFFF6F8FD)        // Luminous airy cloud porcelain
val LightOnBackground = Color(0xFF0F172A)      // Deep slate for sharp legible contrast
val LightSurface = Color(0xFFF6F8FD)
val LightOnSurface = Color(0xFF0F172A)
val LightSurfaceVariant = Color(0xFFE2E8F0)
val LightOnSurfaceVariant = Color(0xFF64748B)

val LightSurfaceContainerLowest = Color(0xFFFFFFFF)
val LightSurfaceContainerLow = Color(0xFFF8FAFC)
val LightSurfaceContainer = Color(0xFFF1F5F9)
val LightSurfaceContainerHigh = Color(0xFFE2E8F0)
val LightSurfaceContainerHighest = Color(0xFFCBD5E1)

// Light Theme Card Specifics - Radiant Pure White on Soft Cloud
val LightCozyCardBackground = Color(0xFFFFFFFF)          // Crisp pure white cards that pop against porcelain
val LightCozyCardBackgroundElevated = Color(0xFFF1F5F9)  // Gentle soft pill layer for inner containers
val LightCozyCardBorder = Color(0xFFE2E8F4)              // Clean, delicate, modern slate border

// -------------------------------------------------------------
// Dark Theme Tokens (Deep Cosmic / Sleek Midnight)
// -------------------------------------------------------------
val DarkPrimary = Color(0xFF818CF8)            // Luminous pastel iris
val DarkOnPrimary = Color(0xFF1E1B4B)
val DarkPrimaryContainer = Color(0xFF3730A3)
val DarkOnPrimaryContainer = Color(0xFFEEF2FF)
val DarkPrimaryFixed = Color(0xFF312E81)
val DarkPrimaryFixedDim = Color(0xFF4338CA)
val DarkOnPrimaryFixed = Color(0xFFEEF2FF)
val DarkOnPrimaryFixedVariant = Color(0xFFC7D2FE)

val DarkSecondary = Color(0xFF34D399)          // Radiant emerald mint glow
val DarkOnSecondary = Color(0xFF064E3B)
val DarkSecondaryContainer = Color(0xFF065F46)
val DarkOnSecondaryContainer = Color(0xFFD1FAE5)
val DarkSecondaryFixed = Color(0xFF065F46)
val DarkSecondaryFixedDim = Color(0xFF047857)
val DarkOnSecondaryFixed = Color(0xFFECFDF5)
val DarkOnSecondaryFixedVariant = Color(0xFFA7F3D0)

val DarkTertiary = Color(0xFFFB923C)           // Warm amber peach glow
val DarkOnTertiary = Color(0xFF7C2D12)
val DarkTertiaryContainer = Color(0xFF9A3412)
val DarkOnTertiaryContainer = Color(0xFFFFEDD5)
val DarkTertiaryFixed = Color(0xFF9A3412)
val DarkTertiaryFixedDim = Color(0xFFC2410C)
val DarkOnTertiaryFixed = Color(0xFFFFF7ED)
val DarkOnTertiaryFixedVariant = Color(0xFFFED7AA)

val DarkBackground = Color(0xFF0F111A)         // Deep cosmic midnight
val DarkOnBackground = Color(0xFFF8FAFC)       // Crisp luminous white
val DarkSurface = Color(0xFF0F111A)
val DarkOnSurface = Color(0xFFF8FAFC)
val DarkSurfaceVariant = Color(0xFF1E2230)
val DarkOnSurfaceVariant = Color(0xFF94A3B8)

val DarkSurfaceContainerLowest = Color(0xFF0A0C13)
val DarkSurfaceContainerLow = Color(0xFF141724)
val DarkSurfaceContainer = Color(0xFF191D2C)
val DarkSurfaceContainerHigh = Color(0xFF22273B)
val DarkSurfaceContainerHighest = Color(0xFF2B324B)

// Dark Theme Card Specifics - Sleek Midnight Glass with delicate borders
val DarkCozyCardBackground = Color(0xFF171A27)           // Rich midnight card
val DarkCozyCardBackgroundElevated = Color(0xFF202537)   // Elevated sub-surface
val DarkCozyCardBorder = Color(0xFF2A3148)               // Subtle sleek border

// -------------------------------------------------------------
// Adaptive Dynamic References
// -------------------------------------------------------------
val Primary: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkPrimary else LightPrimary

val OnPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkOnPrimary else LightOnPrimary

val PrimaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkPrimaryContainer else LightPrimaryContainer

val OnPrimaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkOnPrimaryContainer else LightOnPrimaryContainer

val PrimaryFixed: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkPrimaryFixed else LightPrimaryFixed

val PrimaryFixedDim: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkPrimaryFixedDim else LightPrimaryFixedDim

val OnPrimaryFixed: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkOnPrimaryFixed else LightOnPrimaryFixed

val OnPrimaryFixedVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkOnPrimaryFixedVariant else LightOnPrimaryFixedVariant

val Secondary: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkSecondary else LightSecondary

val OnSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkOnSecondary else LightOnSecondary

val SecondaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkSecondaryContainer else LightSecondaryContainer

val OnSecondaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkOnSecondaryContainer else LightOnSecondaryContainer

val SecondaryFixed: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkSecondaryFixed else LightSecondaryFixed

val SecondaryFixedDim: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkSecondaryFixedDim else LightSecondaryFixedDim

val OnSecondaryFixed: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkOnSecondaryFixed else LightOnSecondaryFixed

val OnSecondaryFixedVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkOnSecondaryFixedVariant else LightOnSecondaryFixedVariant

val Tertiary: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkTertiary else LightTertiary

val OnTertiary: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkOnTertiary else LightOnTertiary

val TertiaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkTertiaryContainer else LightTertiaryContainer

val OnTertiaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkOnTertiaryContainer else LightOnTertiaryContainer

val TertiaryFixed: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkTertiaryFixed else LightTertiaryFixed

val TertiaryFixedDim: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkTertiaryFixedDim else LightTertiaryFixedDim

val OnTertiaryFixed: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkOnTertiaryFixed else LightOnTertiaryFixed

val OnTertiaryFixedVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkOnTertiaryFixedVariant else LightOnTertiaryFixedVariant

val Background: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkBackground else LightBackground

val OnBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkOnBackground else LightOnBackground

val Surface: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkSurface else LightSurface

val OnSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkOnSurface else LightOnSurface

val SurfaceVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkSurfaceVariant else LightSurfaceVariant

val OnSurfaceVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkOnSurfaceVariant else LightOnSurfaceVariant

val SurfaceContainerLowest: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkSurfaceContainerLowest else LightSurfaceContainerLowest

val SurfaceContainerLow: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkSurfaceContainerLow else LightSurfaceContainerLow

val SurfaceContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkSurfaceContainer else LightSurfaceContainer

val SurfaceContainerHigh: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkSurfaceContainerHigh else LightSurfaceContainerHigh

val SurfaceContainerHighest: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkSurfaceContainerHighest else LightSurfaceContainerHighest

// Distinct, non-blend card depth tones
val CozyCardBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkCozyCardBackground else LightCozyCardBackground

val CozyCardBackgroundElevated: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkCozyCardBackgroundElevated else LightCozyCardBackgroundElevated

val CozyCardBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkCozyCardBorder else LightCozyCardBorder

val Outline: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) Color(0xFF64748B) else Color(0xFF94A3B8)

val OutlineVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) Color(0xFF283046) else Color(0xFFE2E8F0)

val InverseSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) Color(0xFFF1F5F9) else Color(0xFF1E293B)

val InverseOnSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) Color(0xFF0F172A) else Color(0xFFF8FAFC)

val Error = Color(0xFFEF4444)
val OnError = Color(0xFFFFFFFF)
val ErrorContainer = Color(0xFFFEE2E2)
val OnErrorContainer = Color(0xFF991B1B)

// Accents
val SwatchLavender = Color(0xFFC7D2FE)
val SwatchPeach = Color(0xFFFED7AA)
val SwatchMint = Color(0xFFA7F3D0)
val SwatchButter = Color(0xFFFDE68A)
val GoldCoin = Color(0xFFF59E0B)
val StarGem = Color(0xFF8B5CF6)
