package com.focusflow.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = SageGreen,
    onPrimary = Color.White,
    primaryContainer = SageGreenLight,
    onPrimaryContainer = DeepForest,
    secondary = SoftBlue,
    onSecondary = Color.White,
    secondaryContainer = WarmBeige,
    onSecondaryContainer = DarkBrown,
    tertiary = AccentCoral,
    background = LightCream,
    onBackground = DarkBrown,
    surface = Color.White,
    onSurface = DarkBrown,
    surfaceVariant = WarmBeige,
    onSurfaceVariant = DeepForest
)

private val DarkColorScheme = darkColorScheme(
    primary = SageGreen,
    onPrimary = DeepForest,
    primaryContainer = DeepForest,
    onPrimaryContainer = SageGreenLight,
    secondary = SoftBlue,
    onSecondary = DarkBrown,
    secondaryContainer = DarkBrown,
    onSecondaryContainer = WarmBeige,
    tertiary = AccentCoral,
    background = Color(0xFF1C1C1E),
    onBackground = LightCream,
    surface = Color(0xFF2C2C2E),
    onSurface = LightCream
)

@Composable
fun FocusFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}