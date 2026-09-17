package com.victor.ulim.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = Blue80,
    onPrimary = Blue10,
    primaryContainer = Blue30,
    onPrimaryContainer = Blue90,
    secondary = Teal80,
    onSecondary = Teal30,
    secondaryContainer = Teal30,
    onSecondaryContainer = Teal90,
    tertiary = Amber80,
    onTertiary = Amber10,
    tertiaryContainer = Amber30,
    onTertiaryContainer = Amber90,
    error = ErrorRed80,
    onError = ErrorRed10,
    errorContainer = ErrorRed40,
    onErrorContainer = ErrorRed90,
    background = InkDark,
    surface = InkDark
)

private val LightColorScheme = lightColorScheme(
    primary = Blue40,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = Blue90,
    onPrimaryContainer = Blue20,
    secondary = Teal40,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = Teal90,
    onSecondaryContainer = Teal30,
    tertiary = Amber40,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    tertiaryContainer = Amber90,
    onTertiaryContainer = Amber10,
    error = ErrorRed40,
    onError = androidx.compose.ui.graphics.Color.White,
    errorContainer = ErrorRed90,
    onErrorContainer = ErrorRed10,
    background = InkLight,
    surface = InkLight
)

private val UlimShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun ULIMTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is disabled so the app keeps its branded look on every device
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = UlimShapes,
        content = content
    )
}
