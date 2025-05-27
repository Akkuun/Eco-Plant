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
import com.akkuunamatata.eco_plant.components.ScoreBar
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
                Text(
                    text = "• $condition",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.serviceValues),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            // Affichage des services avec les barres de progression
            Spacer(modifier = Modifier.height(4.dp))

            ScoreBar(
                label = stringResource(R.string.nitrogen_fixation),
                value = plant.services[0]
            )

            Spacer(modifier = Modifier.height(8.dp))

            ScoreBar(
                label = stringResource(R.string.soil_structure),
                value = plant.services[1]
            )

            Spacer(modifier = Modifier.height(8.dp))

            ScoreBar(
                label = stringResource(R.string.water_retention),
                value = plant.services[2]
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.reliability),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            // Affichage des valeurs de fiabilité avec les barres de progression
            Spacer(modifier = Modifier.height(4.dp))

            ScoreBar(
                label = stringResource(R.string.reliability),
                value = plant.reliabilities[0]
            )
            Spacer(modifier = Modifier.height(8.dp))
            ScoreBar(
                label = stringResource(R.string.reliability),
                value = plant.reliabilities[1]
            )
            Spacer(modifier = Modifier.height(8.dp))
            ScoreBar(
                label = stringResource(R.string.reliability),
                value = plant.reliabilities[2]
            )

        }
    }
}