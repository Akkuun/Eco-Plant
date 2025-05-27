package com.akkuunamatata.eco_plant.pages.plotScreens

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
    var sortCriteria by remember { mutableStateOf(SortCriteria.NITROGEN_FIXATION) }

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

    val sortedPlants = remember(sortCriteria, allPlants) {
        when (sortCriteria) {
            SortCriteria.NITROGEN_FIXATION -> allPlants.sortedByDescending { it.services[0] }
            SortCriteria.SOIL_STRUCTURE -> allPlants.sortedByDescending { it.services[1] }
            SortCriteria.WATER_RETENTION -> allPlants.sortedByDescending { it.services[2] }
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
            // Options de tri
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = sortCriteria == SortCriteria.NITROGEN_FIXATION,
                    onClick = { sortCriteria = SortCriteria.NITROGEN_FIXATION },
                    label = { Text(stringResource(id = R.string.nitrogen_fixation)) }
                )

                FilterChip(
                    selected = sortCriteria == SortCriteria.SOIL_STRUCTURE,
                    onClick = { sortCriteria = SortCriteria.SOIL_STRUCTURE },
                    label = { Text(stringResource(id = R.string.soil_structure)) }
                )

                FilterChip(
                    selected = sortCriteria == SortCriteria.WATER_RETENTION,
                    onClick = { sortCriteria = SortCriteria.WATER_RETENTION },
                    label = { Text(stringResource(id = R.string.water_retention)) }
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
                                PlantAdviceCard(plant = plant)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlantAdviceCard(plant: PlantSpecies) {
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

            // La section des conditions culturelles a été supprimée
        }
    }
}