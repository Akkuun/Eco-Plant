package com.akkuunamatata.eco_plant.pages.mapsScreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.akkuunamatata.eco_plant.R
import com.akkuunamatata.eco_plant.components.ScoreBarDetailed
import com.akkuunamatata.eco_plant.database.plants.ParcelleData
import com.akkuunamatata.eco_plant.database.plants.PlantSpecies

@Composable
fun MapFullPreviewCard(parcelle: ParcelleData) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
            .padding(16.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = "${stringResource(R.string.plot_of)} ${parcelle.idAuthor}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.coordinates),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "${stringResource(R.string.latitude)}: ${parcelle.lat}",
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "${stringResource(R.string.longitude)}: ${parcelle.long}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.plants),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        parcelle.plants.forEach { plant ->
            Spacer(modifier = Modifier.height(8.dp))
            PlantCard(plant)
        }
    }
}

@Composable
private fun PlantCard(plant: PlantSpecies) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = plant.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.culture_condition),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            plant.culturalConditions.forEach { condition ->
                if (condition.isNotBlank()) {
                    Text(
                        text = "• $condition",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.serviceValues),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            // Affichage des services avec les barres de progression détaillées
            Spacer(modifier = Modifier.height(8.dp))

            // Service 1: Fixation d'azote
            ScoreBarDetailed(
                label = stringResource(R.string.nitrogen_fixation),
                value = plant.services[0],
                reliability = plant.reliabilities[0],
                condition = getConditionText(plant.culturalConditions, 0)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Service 2: Structure du sol
            ScoreBarDetailed(
                label = stringResource(R.string.soil_structure),
                value = plant.services[1],
                reliability = plant.reliabilities[1],
                condition = getConditionText(plant.culturalConditions, 1)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Service 3: Rétention d'eau
            ScoreBarDetailed(
                label = stringResource(R.string.water_retention),
                value = plant.services[2],
                reliability = plant.reliabilities[2],
                condition = getConditionText(plant.culturalConditions, 2)
            )
        }
    }
}

/**
 * Récupère le texte de condition à la position donnée, avec une valeur par défaut
 */
private fun getConditionText(conditions: Array<String>, index: Int): String {
    return if (index < conditions.size && conditions[index].isNotBlank()) {
        conditions[index]
    } else {
        "unknown"
    }
}