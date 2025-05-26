package com.akkuunamatata.eco_plant.pages.plotScreens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.akkuunamatata.eco_plant.navigation.Routes
import com.akkuunamatata.eco_plant.pages.plantIdentificationScreens.Plot
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import com.akkuunamatata.eco_plant.R
import com.akkuunamatata.eco_plant.components.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlotList(navController: NavHostController) {
    var searchText by remember { mutableStateOf("") }
    var plots by remember { mutableStateOf<List<Plot>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Firebase
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    // Formatage de date
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // String resources
    val loginRequiredMessage = stringResource(id = R.string.login_required)
    val errorLoadingMessage = stringResource(id = R.string.error_loading)


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
                        lastEdited = (doc.getTimestamp("lastEdited")?.toDate() ?: Date()),
                        location = doc.getString("location") ?: "Emplacement inconnu"
                    )
                }
                isLoading = false
                Log.d("HistoryScreen", "Parcelles chargées: ${plots.size}")
            } catch (e: Exception) {
                errorMessage = "$errorLoadingMessage ${e.message ?: "inconnue"}"
                isLoading = false
                Log.e("HistoryScreen", "Erreur lors du chargement des parcelles", e)
            }
        } else {
            errorMessage = loginRequiredMessage
            isLoading = false
        }
    }

    // Filtrer les parcelles par texte de recherche
    val filteredPlots = remember(searchText, plots) {
        if (searchText.isEmpty()) {
            plots
        } else {
            plots.filter { it.name.contains(searchText, ignoreCase = true) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Titre
        Text(
            text = stringResource(id = R.string.your_plots),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Barre de recherche
        SearchBar(
            value = searchText,
            onValueChange = { searchText = it },
            labelText = stringResource(id = R.string.search_plot),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Liste des parcelles
        Box(modifier = Modifier.weight(1f)) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center)
                )
            } else if (plots.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.no_plots_found),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(filteredPlots) { plot ->
                        PlotListItem(
                            plot = plot,
                            dateFormat = dateFormat,
                            onClick = {
                                navController.navigate("${Routes.PLOT_DETAIL}/${plot.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlotListItem(
    plot: Plot,
    dateFormat: SimpleDateFormat,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_plant_filled),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = plot.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Affichage de la localisation
                Text(
                    text = plot.location,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(
                        id = R.string.last_edited,
                        dateFormat.format(plot.lastEdited)
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}