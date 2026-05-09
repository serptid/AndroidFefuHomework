package com.example.hw3.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GamingDarkColorScheme = darkColorScheme(
    primary = GamingPrimary,
    primaryContainer = GamingPrimaryContainer,
    onPrimaryContainer = Color(0xFFF0DCFF),
    secondary = GamingSecondary,
    background = GamingDarkBackground,
    surface = GamingDarkSurface,
    surfaceVariant = GamingDarkSurfaceVariant,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed
)

private val AmoledColorScheme = darkColorScheme(
    primary = AmoledPrimary,
    primaryContainer = AmoledPrimaryContainer,
    onPrimaryContainer = Color(0xFFF3E5F5),
    secondary = AmoledSecondary,
    background = AmoledBackground,
    surface = AmoledSurface,
    surfaceVariant = AmoledSurfaceVariant,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed
)

private val NeonColorScheme = darkColorScheme(
    primary = NeonPrimary,
    primaryContainer = NeonPrimaryContainer,
    onPrimaryContainer = NeonPrimary,
    secondary = NeonSecondary,
    background = NeonBackground,
    surface = NeonSurface,
    surfaceVariant = NeonSurfaceVariant,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = Color(0xFF21005D),
    secondary = LightSecondary,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    onSurfaceVariant = Color(0xFF444466),
    error = ErrorRed
)

@Composable
fun Hw3Theme(
    themeMode: Int = 0,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        1 -> AmoledColorScheme
        2 -> NeonColorScheme
        3 -> LightColorScheme
        else -> GamingDarkColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
