package com.example.eco_plant.ui.theme

import android.app.Activity
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
    primary = HighlightColors.HighlightsHigh,
    secondary = Support.Sucess.SuccessMedium,
    tertiary = Support.Warning.WarningMedium
)

private val LightColorScheme = lightColorScheme(
    primary = HighlightColors.HighlightsLow,
    secondary = Support.Sucess.SuccessMediumLow,
    tertiary = Support.Warning.WarningMediumLow
)

@Composable
fun EcoPlantTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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