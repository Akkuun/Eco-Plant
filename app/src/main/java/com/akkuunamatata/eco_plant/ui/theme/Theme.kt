package com.akkuunamatata.eco_plant.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    background = Neutral.Dark.Darkest,
    primary = HighlightColors.Lightest, // Primary button (Default for Button !) TODO Fix
    onPrimary = HighlightColors.Dark, // Primary button text (Default for Button !) TODO Fix
    secondary = HighlightColors.Light, // Secondary button TODO Fix
    onSecondary = Neutral.Dark.Darkest, // Secondary button text TODO Fix
    tertiary = HighlightColors.Lightest, // Tertiary button TODO Fix
    onTertiary = HighlightColors.Darkest, // Tertiary button text TODO Fix
    scrim = Neutral.Dark.Darkest, // Gray inactive TODO Fix
    error = Support.Error.ErrorHigh,
    surface = Neutral.Dark.Darkest, // Surface color TODO Fix
    surfaceVariant = Neutral.Dark.Dark,
    inverseSurface = HighlightColors.Dark,
)

//Light Theme
private val LightColorScheme = lightColorScheme(
    background = Neutral.Light.Lightest,
    primary =  HighlightColors.Darkest, // Primary button (Default for Button !)
    onPrimary = Neutral.Light.Lightest, // Primary button text (Default for Button !)
    secondary = HighlightColors.Light, // Secondary button
    onSecondary = Neutral.Dark.Darkest, // Secondary button text
    tertiary = HighlightColors.Lightest, // Tertiary button
    onTertiary = HighlightColors.Darkest, // Tertiary button text
    scrim = Neutral.Dark.Lightest, // Gray inactive
//    error = Support.Error.ErrorHigh,
    surface = Neutral.Light.Lightest, // Surface color
//    surfaceVariant = HighlightColors.Lightest,
//    inverseSurface = HighlightColors.Light,

    // Set debugging colors (kept them as debug mode as long as they are unused)
    error = Color(0xFFFF0000), // Error color
    // surface = Color(0xFF00FF00), // Surface color
    surfaceVariant = Color(0xFF0000FF), // Surface variant color
    inverseSurface = Color(0xFFFFFF00), // Inverse surface color
)

@Composable
fun EcoPlantTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Now accepts user's theme preference
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = InterTypography, // Use the custom typography
        content = content
    )
}