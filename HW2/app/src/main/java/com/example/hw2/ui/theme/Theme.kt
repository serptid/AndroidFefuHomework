package com.example.hw2.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = White,
    onPrimary = Black,

    secondary = Accent,
    onSecondary = Black,

    background = Black,
    onBackground = White,

    surface = DarkGray,
    onSurface = White,

    error = ErrorRed,
    onError = Color.White
)

@Composable
fun HW2Theme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
