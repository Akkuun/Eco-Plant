package com.akkuunamatata.eco_plant.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.akkuunamatata.eco_plant.R

@SuppressLint("DefaultLocale")
@Composable
fun ScoreBar(
    label: String,
    value: Float,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Si la valeur est -1, afficher un message "pas de valeur"
        if (value == -1f) {
            Text(
                text = stringResource(id = R.string.no_value),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            // Pour la barre avec valeur superposée
            val displayValue = value.coerceIn(0f, 1f)
            val formattedValue = String.format("%.0f%%", displayValue * 100)

            // Box pour permettre le positionnement superposé
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp) // Plus haut pour accommoder le texte
                    .padding(vertical = 2.dp)
            ) {
                // Barre de progression en arrière-plan
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .align(androidx.compose.ui.Alignment.Center)
                        .background(
                            color = MaterialTheme.colorScheme.tertiary.copy(red = MaterialTheme.colorScheme.tertiary.red * 0.94f,
                                green = MaterialTheme.colorScheme.tertiary.green * 0.93f,
                                blue = MaterialTheme.colorScheme.tertiary.blue * 0.925f),
                            shape = RoundedCornerShape(15.dp)
                        )
                ) {
                    // Partie remplie
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(displayValue)
                            .fillMaxHeight()
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(15.dp)
                            )
                    )
                }

                TextBorder(
                    text = formattedValue,
                    style = MaterialTheme.typography.bodySmall,
                    textColor = MaterialTheme.colorScheme.onPrimary,
                    borderColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(androidx.compose.ui.Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}