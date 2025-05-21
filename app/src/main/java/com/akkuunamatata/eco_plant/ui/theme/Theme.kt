package com.akkuunamatata.eco_plant.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    background = Neutral.Dark.Darkest,
    primary = HighlightColors.Lightest,
    secondary = Support.Sucess.SuccessMedium,
    tertiary = Support.Warning.WarningMedium,
    error = Support.Error.ErrorHigh,
    surface = Neutral.Dark.Darkest,
    surfaceVariant = Neutral.Dark.Dark,
    inverseSurface = HighlightColors.Dark,
)
//Light Theme
private val LightColorScheme = lightColorScheme(
    background = Neutral.Light.Lightest,
    primary =  HighlightColors.Dark, //
    secondary = HighlightColors.Darkest, // Text des bouton
    tertiary = HighlightColors.Medium, // Text des bouton
    error = Support.Error.ErrorHigh,
    surface = Neutral.Light.Lightest,
    surfaceVariant = HighlightColors.Lightest,
    inverseSurface = HighlightColors.Light,
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