package com.akkuunamatata.eco_plant.pages.plantIdentificationScreens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.akkuunamatata.eco_plant.R
import com.akkuunamatata.eco_plant.navigation.Routes
import com.akkuunamatata.eco_plant.ui.theme.HighlightColors
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.akkuunamatata.eco_plant.ui.theme.Support
import com.akkuunamatata.eco_plant.components.SearchBar
import com.google.firebase.firestore.Query
import com.akkuunamatata.eco_plant.database.plants.PlantDatabaseHelper
import com.akkuunamatata.eco_plant.database.plants.PlantSpecies

data class Plot(
    val id: String,
    val name: String,
    val lastEdited: Date,
    val location: String = "Emplacement inconnu"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdentifiedPlant(
    navController: androidx.navigation.NavHostController,
    imageUri: Uri,
    plantName: String,
    scientificName: String
) {
    var selectedPlot by remember { mutableStateOf<Plot?>(null) }
    var searchText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isAddingPlant by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var plots by remember { mutableStateOf<List<Plot>>(emptyList()) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    // Ajout d'un état pour stocker les données de l'espèce de plante
    var plantSpecies by remember { mutableStateOf<PlantSpecies?>(null) }

    // Contexte pour instancier le PlantDatabaseHelper
    val context = LocalContext.current

    // Firebase
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    // Chargement des données de plante depuis la base de données
    LaunchedEffect(scientificName) {
        try {
            val databaseHelper = PlantDatabaseHelper.getInstance(context)
            plantSpecies = databaseHelper.getPlantSpeciesByScientificName(scientificName)

            // Ajout de logs pour vérifier les valeurs chargées
            if (plantSpecies != null) {
                Log.d("IdentifiedPlant", "Données de la plante chargées: ${plantSpecies?.name}")
                Log.d("IdentifiedPlant", "Services: ${plantSpecies?.services?.joinToString()}")
                Log.d("IdentifiedPlant", "Fiabilités: ${plantSpecies?.reliabilities?.joinToString()}")
            } else {
                Log.d("IdentifiedPlant", "Données non trouvées: $scientificName")
            }
        } catch (e: Exception) {
            errorMessage = "Erreur de recherche: ${e.message}"
            print("Erreur lors du chargement des données de la plante: ${e.message}")
        }
    }

    // Récupération des parcelles depuis Firestore
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            try {
                val snapshot = db.collection("users")
                    .document(currentUser.uid)
                    .collection("plots")
                    .orderBy("lastEdited", Query.Direction.DESCENDING)
                    .get()
                    .await()

                plots = snapshot.documents.map { doc ->
                    Plot(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        lastEdited = (doc.getTimestamp("lastEdited")?.toDate() ?: Date())
                    )
                }
                isLoading = false
            } catch (e: Exception) {
                errorMessage = "Erreur: ${e.message}"
                isLoading = false
            }
        } else {
            errorMessage = "Vous devez être connecté pour voir vos parcelles"
            isLoading = false
        }
    }

    // Fonction d'ajout de plante
    fun addPlantToPlot() {
        if (currentUser == null || selectedPlot == null) {
            errorMessage = "Veuillez sélectionner une parcelle"
            return
        }

        isAddingPlant = true

        try {
            // Récupérer les valeurs de service, fiabilité et conditions culturales
            val serviceValues = plantSpecies?.services?.toList() ?: listOf(-1f, -1f, -1f)
            val reliabilityValues = plantSpecies?.reliabilities?.toList() ?: listOf(-1f, -1f, -1f)
            val culturalConditions = plantSpecies?.culturalConditions?.toList() ?: listOf("", "", "")

            val plantMap = hashMapOf(
                "commonName" to plantName,
                "scientificName" to scientificName,
                "imageUrl" to "",
                "serviceValues" to serviceValues,
                "reliabilityValues" to reliabilityValues,
                "culturalConditions" to culturalConditions,
                "addedAt" to com.google.firebase.Timestamp.now()
            )

            // Ajout à Firestore
            db.collection("users")
                .document(currentUser.uid)
                .collection("plots")
                .document(selectedPlot!!.id)
                .collection("plants")
                .add(plantMap)
                .addOnSuccessListener {
                    db.collection("users")
                        .document(currentUser.uid)
                        .collection("plots")
                        .document(selectedPlot!!.id)
                        .update("lastEdited", com.google.firebase.Timestamp.now())
                        .addOnSuccessListener {
                            successMessage = "Plante ajoutée à ${selectedPlot!!.name}"
                            isAddingPlant = false
                        }
                        .addOnFailureListener { e ->
                            errorMessage = "Erreur lors de la mise à jour: ${e.message}"
                            isAddingPlant = false
                        }
                }
                .addOnFailureListener { e ->
                    errorMessage = "Erreur lors de l'ajout: ${e.message}"
                    isAddingPlant = false
                }
        } catch (e: Exception) {
            errorMessage = "Erreur: ${e.message}"
            isAddingPlant = false
        }
    }

    // Filtrer les parcelles en fonction du texte de recherche
    val filteredPlots = remember(searchText, plots) {
        if (searchText.isEmpty()) {
            plots
        } else {
            plots.filter { it.name.contains(searchText, ignoreCase = true) }
        }
    }

    // Formateur de date
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Image de la plante
        Image(
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = plantName,
            modifier = Modifier
                .height(180.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Nom de la plante
        Text(
            text = plantName,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        // Nom scientifique
        Text(
            text = scientificName,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Light,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            ),
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Champ de recherche
        SearchBar(
            value = searchText,
            onValueChange = { searchText = it },
            labelText = stringResource(R.string.search_plot),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Liste des parcelles
        Box(modifier = Modifier.weight(1f)) {
            if (isLoading) {
                // Indicateur de chargement
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    // Bouton Nouvelle parcelle
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { navController.navigate(Routes.NEW_PLOT) },
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = HighlightColors.Medium
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = stringResource(R.string.new_plot),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }

                    // Liste des parcelles existantes
                    items(filteredPlots) { plot ->
                        PlotItem(
                            plot = plot,
                            isSelected = selectedPlot?.id == plot.id,
                            dateFormat = dateFormat,
                            onSelect = { selectedPlot = plot }
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 0.dp)
                .padding(vertical = 8.dp)
        ) {
            // Message d'erreur
            errorMessage?.let {
                Snackbar(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = Support.Error.ErrorMediumLow,
                    contentColor = Support.Error.ErrorHigh,
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = it,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        TextButton(
                            onClick = { errorMessage = null },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Support.Error.ErrorHigh
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Text("OK", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }

            // Message de succès
            successMessage?.let {
                Snackbar(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = it,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        TextButton(
                            onClick = { successMessage = null },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.onTertiary
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Text("OK", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }

        // Boutons d'action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { addPlantToPlot() },
                enabled = selectedPlot != null && !isAddingPlant,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp)
            ) {
                if (isAddingPlant) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(stringResource(R.string.add_to_plot))
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { navController.navigate("scan") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(stringResource(R.string.take_another_picture))
            }
        }
    }
}

@Composable
fun PlotItem(
    plot: Plot,
    isSelected: Boolean,
    dateFormat: SimpleDateFormat,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onSelect() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.tertiary
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = { onSelect() }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = plot.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = stringResource(
                        R.string.last_edited,
                        dateFormat.format(plot.lastEdited)
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}