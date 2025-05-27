package com.akkuunamatata.eco_plant.pages.plotScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.akkuunamatata.eco_plant.components.ScoreBar
import com.akkuunamatata.eco_plant.pages.plantIdentificationScreens.Plot
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*
import com.akkuunamatata.eco_plant.R
import com.akkuunamatata.eco_plant.navigation.Routes

@Composable
fun PlotDetailScreen(
    plotId: String,
    navController: NavHostController
) {
    var plot by remember { mutableStateOf<Plot?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var avgScores by remember { mutableStateOf(listOf(0f, 0f, 0f)) }
    var personalNotes by remember { mutableStateOf("") }

    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    val loginRequiredMessage = stringResource(id = R.string.login_required)
    val errorLoadingMessage = stringResource(id = R.string.error_loading)
    val plotNotFoundMessage = stringResource(id = R.string.plot_not_found)

    // Chargement des données de la parcelle
    LaunchedEffect(plotId) {
        if (currentUser != null) {
            try {
                // Récupération des détails de la parcelle
                val plotDoc = db.collection("users")
                    .document(currentUser.uid)
                    .collection("plots")
                    .document(plotId)
                    .get()
                    .await()

                if (plotDoc.exists()) {
                    val name = plotDoc.getString("name") ?: ""
                    val lastEdited = plotDoc.getTimestamp("lastEdited")?.toDate() ?: Date()
                    val notes = plotDoc.getString("notes") ?: ""

                    personalNotes = notes
                    plot = Plot(id = plotId, name = name, lastEdited = lastEdited)

                    // Récupération des plantes de la parcelle
                    val plantsSnapshot = db.collection("users")
                        .document(currentUser.uid)
                        .collection("plots")
                        .document(plotId)
                        .collection("plants")
                        .get()
                        .await()

                    if (!plantsSnapshot.isEmpty) {
                        // Calcul des scores moyens
                        var totalService0 = 0f
                        var totalService1 = 0f
                        var totalService2 = 0f
                        var count = 0

                        for (plantDoc in plantsSnapshot.documents) {
                            val serviceValues = plantDoc.get("serviceValues") as? List<*>
                            if (serviceValues != null && serviceValues.size >= 3) {
                                totalService0 += (serviceValues[0] as? Number)?.toFloat() ?: 0f
                                totalService1 += (serviceValues[1] as? Number)?.toFloat() ?: 0f
                                totalService2 += (serviceValues[2] as? Number)?.toFloat() ?: 0f
                                count++
                            }
                        }

                        if (count > 0) {
                            avgScores = listOf(
                                totalService0 / count,
                                totalService1 / count,
                                totalService2 / count
                            )
                        }
                    }
                } else {
                    errorMessage = plotNotFoundMessage
                }

                isLoading = false

            } catch (e: Exception) {
                errorMessage = "$errorLoadingMessage ${e.message ?: "inconnue"}"
                isLoading = false
            }
        } else {
            errorMessage = loginRequiredMessage
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else if (plot != null) {
            // En-tête avec nom et icône de paramètres
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Parcelle : ${plot!!.name}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.weight(1f))
                FilledIconButton(
                    onClick = { navController.navigate("${Routes.PLOT_SETTINGS}/${plotId}") },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(id = R.string.settings)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Barres de score
            ScoreBar(
                label = stringResource(id = R.string.nitrogen_fixation),
                value = avgScores[0],
                modifier = Modifier.padding(vertical = 8.dp)
            )

            ScoreBar(
                label = stringResource(id = R.string.soil_structure),
                value = avgScores[1],
                modifier = Modifier.padding(vertical = 8.dp)
            )

            ScoreBar(
                label = stringResource(id = R.string.water_retention),
                value = avgScores[2],
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bouton de conseils
            Button(
                onClick = { navController.navigate("plant_advice/${plotId}") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text(stringResource(id = R.string.advice_to_improve_scores))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notes personnelles
            Text(
                text = stringResource(id = R.string.personal_notes),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = personalNotes.ifEmpty { stringResource(id = R.string.no_notes) },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
                    .fillMaxWidth()

            )

            Spacer(modifier = Modifier.weight(1f))

            // Boutons d'action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { navController.navigate("${Routes.PLANTS_IN_PLOT}/${plotId}") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(stringResource(id = R.string.view_plants))
                }

                Button(
                    onClick = { /* Ajouter une plante */ },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    )
                ) {
                    Text(stringResource(id = R.string.add_plant))
                }
            }
        }
    }
}