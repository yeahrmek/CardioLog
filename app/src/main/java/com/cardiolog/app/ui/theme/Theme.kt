package com.cardiolog.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF006A65),
    secondary = Color(0xFF4A6360),
    tertiary = Color(0xFF456179),
    background = Color(0xFFF6FBF9),
    surface = Color(0xFFF6FBF9),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF80D5CE),
    secondary = Color(0xFFB0CCC7),
    tertiary = Color(0xFFADCBE6),
    background = Color(0xFF0E1514),
    surface = Color(0xFF0E1514),
)

@Composable
fun CardioLogTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content,
    )
}
