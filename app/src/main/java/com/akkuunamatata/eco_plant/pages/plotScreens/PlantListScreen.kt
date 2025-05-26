package com.akkuunamatata.eco_plant.pages.plotScreens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.akkuunamatata.eco_plant.R
import com.akkuunamatata.eco_plant.components.ScoreBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.content.Intent
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.akkuunamatata.eco_plant.components.SearchBar

// Modèle de données pour une plante dans une parcelle
data class PlotPlant(
    val id: String,
    val name: String,
    val scientificName: String,
    val serviceValues: List<Float>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantListScreen(
    plotId: String,
    navController: NavHostController
) {
    var plotName by remember { mutableStateOf("") }
    var searchText by remember { mutableStateOf("") }
    var plants by remember { mutableStateOf<List<PlotPlant>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    val loginRequiredMessage = stringResource(id = R.string.login_required)
    val errorLoadingMessage = stringResource(id = R.string.error_loading)
    val context = LocalContext.current

    // Récupération des informations de la parcelle et des plantes
    LaunchedEffect(plotId) {
        if (currentUser != null) {
            try {
                // Récupération du nom de la parcelle
                val plotDoc = db.collection("users")
                    .document(currentUser.uid)
                    .collection("plots")
                    .document(plotId)
                    .get()
                    .await()

                if (plotDoc.exists()) {
                    plotName = plotDoc.getString("name") ?: ""
                }

                // Récupération des plantes
                val plantsSnapshot = db.collection("users")
                    .document(currentUser.uid)
                    .collection("plots")
                    .document(plotId)
                    .collection("plants")
                    .get()
                    .await()

                plants = plantsSnapshot.documents.map { doc -> // Utilisation de map au lieu de mapNotNull
                    val name = doc.getString("name") ?: "Plante sans nom" // Valeur par défaut
                    val scientificName = doc.getString("scientificName") ?: ""

                    // Récupération des scores plus permissive
                    var serviceValues = listOf(0f, 0f, 0f)

                    try {
                        val rawServiceValues = doc.get("serviceValues")
                        if (rawServiceValues is List<*>) {
                            // Conversion des valeurs avec tolérance aux erreurs
                            val tempValues = mutableListOf<Float>()
                            for (i in 0 until (rawServiceValues.size.coerceAtMost(3))) {
                                val value = rawServiceValues[i]
                                val floatValue = when(value) {
                                    is Number -> value.toFloat()
                                    is String -> value.toFloatOrNull() ?: 0f
                                    else -> 0f
                                }
                                tempValues.add(floatValue)
                            }

                            while (tempValues.size < 3) {
                                tempValues.add(0f)
                            }

                            serviceValues = tempValues
                        }
                    } catch (e: Exception) {
                        Log.d("PlantListScreen", "Error parsing serviceValues for plant ${doc.id}: ${e.message}")
                    }

                    PlotPlant(
                        id = doc.id,
                        name = name,
                        scientificName = scientificName,
                        serviceValues = serviceValues
                    )
                }

                isLoading = false
            } catch (e: Exception) {
                errorMessage = "$errorLoadingMessage: ${e.message ?: ""}"
                isLoading = false
            }
        } else {
            errorMessage = loginRequiredMessage
            isLoading = false
        }
    }

    // Filtrer les plantes par texte de recherche
    val filteredPlants = remember(searchText, plants) {
        if (searchText.isEmpty()) plants
        else plants.filter {
            it.name.contains(searchText, ignoreCase = true) ||
                    it.scientificName.contains(searchText, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (plotName.isEmpty())
                            stringResource(id = R.string.plants_in_plot_generic)
                        else
                            stringResource(id = R.string.plants_in_plot, plotName)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back))
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
            // Barre de recherche
            SearchBar(
                value = searchText,
                onValueChange = { searchText = it },
                labelText = stringResource(id = R.string.search_plants),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Contenu principal
            Box(modifier = Modifier.weight(1f)) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp).align(Alignment.Center)
                    )
                } else if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (plants.isEmpty()) {
                    Text(
                        text = stringResource(id = R.string.no_plants_found),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filteredPlants) { plant ->
                            PlantCard(
                                plant = plant,
                                onRemove = { /* À implémenter plus tard */ },
                                onMoreInfo = {
                                    val url = "https://www.tela-botanica.org/?s=${plant.scientificName.replace(" ", "+")}"
                                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }
            }

            // Bouton retour à la parcelle
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(stringResource(id = R.string.back_to_plot))
            }
        }
    }
}

@Composable
fun PlantCard(
    plant: PlotPlant,
    onRemove: () -> Unit,
    onMoreInfo: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Titre de la plante
            Text(
                text = plant.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onTertiary
            )

            if (plant.scientificName.isNotEmpty()) {
                Text(
                    text = plant.scientificName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Barres de score
            ScoreBar(
                label = stringResource(id = R.string.nitrogen_fixation),
                value = plant.serviceValues.getOrElse(0) { 0f },
                modifier = Modifier.padding(vertical = 4.dp)
            )

            ScoreBar(
                label = stringResource(id = R.string.soil_structure),
                value = plant.serviceValues.getOrElse(1) { 0f },
                modifier = Modifier.padding(vertical = 4.dp)
            )

            ScoreBar(
                label = stringResource(id = R.string.water_retention),
                value = plant.serviceValues.getOrElse(2) { 0f },
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Boutons d'action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onRemove,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Text(stringResource(id = R.string.remove_plant))
                }

                Button(
                    onClick = onMoreInfo,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(id = R.string.more_info))
                }
            }
        }
    }
}