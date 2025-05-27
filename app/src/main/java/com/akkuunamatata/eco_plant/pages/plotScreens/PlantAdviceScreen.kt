package com.akkuunamatata.eco_plant.pages.plotScreens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.akkuunamatata.eco_plant.R
import com.akkuunamatata.eco_plant.components.ScoreBar
import com.akkuunamatata.eco_plant.database.plants.PlantDatabaseHelper
import com.akkuunamatata.eco_plant.database.plants.PlantSpecies

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.core.net.toUri

enum class SortCriteria {
    NITROGEN_FIXATION,
    SOIL_STRUCTURE,
    WATER_RETENTION
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantAdviceScreen(
    plotId: String,
    navController: NavHostController
) {
    val context = LocalContext.current
    var allPlants by remember { mutableStateOf<List<PlantSpecies>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var selectedCriteria by remember {
        mutableStateOf(setOf(SortCriteria.NITROGEN_FIXATION))
    }

    LaunchedEffect(Unit) {
        try {
            val databaseHelper = PlantDatabaseHelper.getInstance(context)
            allPlants = databaseHelper.getAllPlants().filter { plant ->
                plant.services.any { it > 0 }
            }
            isLoading = false
        } catch (e: Exception) {
            errorMessage = e.message
            isLoading = false
        }
    }

    val sortedPlants = remember(selectedCriteria, allPlants) {
        allPlants.sortedByDescending { plant ->
            // Calculer un score combiné basé sur les critères sélectionnés
            var combinedScore = 0f
            if (selectedCriteria.contains(SortCriteria.NITROGEN_FIXATION)) {
                combinedScore += plant.services[0]
            }
            if (selectedCriteria.contains(SortCriteria.SOIL_STRUCTURE)) {
                combinedScore += plant.services[1]
            }
            if (selectedCriteria.contains(SortCriteria.WATER_RETENTION)) {
                combinedScore += plant.services[2]
            }
            combinedScore
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.advice_to_improve_scores)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Filtres
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                val filterChipColors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    labelColor = MaterialTheme.colorScheme.onTertiary
                )

                FilterChip(
                    selected = selectedCriteria.contains(SortCriteria.NITROGEN_FIXATION),
                    onClick = {
                        selectedCriteria = updateCriteria(selectedCriteria, SortCriteria.NITROGEN_FIXATION)
                    },
                    label = { Text(stringResource(id = R.string.nitrogen_fixation)) },
                    colors = filterChipColors
                )

                FilterChip(
                    selected = selectedCriteria.contains(SortCriteria.SOIL_STRUCTURE),
                    onClick = {
                        selectedCriteria = updateCriteria(selectedCriteria, SortCriteria.SOIL_STRUCTURE)
                    },
                    label = { Text(stringResource(id = R.string.soil_structure)) },
                    colors = filterChipColors
                )

                FilterChip(
                    selected = selectedCriteria.contains(SortCriteria.WATER_RETENTION),
                    onClick = {
                        selectedCriteria = updateCriteria(selectedCriteria, SortCriteria.WATER_RETENTION)
                    },
                    label = { Text(stringResource(id = R.string.water_retention)) },
                    colors = filterChipColors
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    errorMessage != null -> {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    sortedPlants.isEmpty() -> {
                        Text(
                            text = stringResource(id = R.string.no_plants_found),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        LazyColumn {
                            items(sortedPlants) { plant ->
                                PlantAdviceCard(
                                    plant = plant,
                                    onMoreInfo = {
                                        val url = "https://www.tela-botanica.org/?s=${plant.name.replace(" ", "+")}"
                                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                        context.startActivity(intent)
                                    })
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun updateCriteria(currentCriteria: Set<SortCriteria>, criteria: SortCriteria): Set<SortCriteria> {
    val newCriteria = currentCriteria.toMutableSet()

    // Si le critère est déjà sélectionné, le retirer (sauf si c'est le seul)
    if (currentCriteria.contains(criteria)) {
        // Éviter d'avoir un ensemble vide
        if (currentCriteria.size > 1) {
            newCriteria.remove(criteria)
        }
    } else {
        // Sinon, ajouter ce critère
        newCriteria.add(criteria)
    }

    return newCriteria
}

@Composable
fun PlantAdviceCard(plant: PlantSpecies, onMoreInfo: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = plant.name,
                style = MaterialTheme.typography.titleMedium
            )

            ScoreBar(
                label = stringResource(id = R.string.nitrogen_fixation),
                value = plant.services[0],
                modifier = Modifier.padding(vertical = 4.dp)
            )

            ScoreBar(
                label = stringResource(id = R.string.soil_structure),
                value = plant.services[1],
                modifier = Modifier.padding(vertical = 4.dp)
            )

            ScoreBar(
                label = stringResource(id = R.string.water_retention),
                value = plant.services[2],
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onMoreInfo,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                ),
            ) {
                Text(
                    text = stringResource(id = R.string.more_info),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}