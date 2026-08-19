package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = TukTukGreenPrimary,
    onPrimary = TukTukWhite,
    primaryContainer = TukTukGreenLight,
    onPrimaryContainer = TukTukGreenDark,
    secondary = TukTukGreenDark,
    onSecondary = TukTukWhite,
    secondaryContainer = TukTukGreenContainer,
    onSecondaryContainer = TukTukGreenDark,
    tertiary = TukTukAccentRed,
    onTertiary = TukTukWhite,
    tertiaryContainer = TukTukRedContainer,
    onTertiaryContainer = TukTukAccentRed,
    background = TukTukBackground,
    onBackground = TukTukTextPrimary,
    surface = TukTukSurface,
    onSurface = TukTukTextPrimary,
    surfaceVariant = TukTukSurfaceVariant,
    onSurfaceVariant = TukTukTextSecondary,
    outline = TukTukCardBorder,
    error = TukTukAccentRed,
    onError = TukTukWhite
)

private val DarkColorScheme = darkColorScheme(
    primary = TukTukGreenPrimary,
    onPrimary = TukTukWhite,
    primaryContainer = TukTukGreenDark,
    onPrimaryContainer = TukTukGreenLight,
    secondary = TukTukGreenLight,
    onSecondary = TukTukGreenDark,
    background = Color(0xFF0F1710),
    onBackground = Color(0xFFF1F5F1),
    surface = Color(0xFF162017),
    onSurface = Color(0xFFF1F5F1),
    surfaceVariant = Color(0xFF1E2B20),
    onSurfaceVariant = Color(0xFFA1ADA1),
    outline = Color(0xFF2C3E2F),
    error = TukTukAccentRed,
    onError = TukTukWhite
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

