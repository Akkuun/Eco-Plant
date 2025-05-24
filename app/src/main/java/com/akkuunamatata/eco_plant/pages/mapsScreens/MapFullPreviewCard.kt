package com.akkuunamatata.eco_plant.pages.mapsScreens



import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.akkuunamatata.eco_plant.database.plants.ParcelleData
import com.akkuunamatata.eco_plant.database.plants.PlantSpecies

@Composable
fun MapFullPreviewCard(parcelle: ParcelleData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = "Parcelle de ${parcelle.idAuthor}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Coordonnées",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "Latitude: ${parcelle.lat}",
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "Longitude: ${parcelle.long}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Plantes",
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
            containerColor = MaterialTheme.colorScheme.surfaceVariant
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
                text = "Conditions de culture:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            plant.culturalConditions.forEach { condition ->
                Text(
                    text = "• $condition",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}