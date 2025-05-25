package com.akkuunamatata.eco_plant.pages.plantIdentificationScreens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

data class Plot(
    val id: String,
    val name: String,
    val lastEdited: Date
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdentifiedPlant(
    navController: androidx.navigation.NavHostController,
    imageUri: Uri,
    plantName: String,
    scientificName: String
) {
    // État pour stocker la parcelle sélectionnée
    var selectedPlot by remember { mutableStateOf<Plot?>(null) }

    // État pour stocker le texte de recherche
    var searchText by remember { mutableStateOf("") }

    // Liste de parcelles fictive (à remplacer par des données réelles)
    val mockPlots = remember {
        listOf(
            Plot("1", "Jardin potager", Date()),
            Plot("2", "Terrasse", Date(System.currentTimeMillis() - 86400000)),
            Plot("3", "Balcon", Date(System.currentTimeMillis() - 172800000)),
            Plot("4", "Jardin d'hiver", Date(System.currentTimeMillis() - 259200000))
        )
    }

    // Filtrer les parcelles en fonction du texte de recherche
    val filteredPlots = remember(searchText, mockPlots) {
        if (searchText.isEmpty()) {
            mockPlots
        } else {
            mockPlots.filter { it.name.contains(searchText, ignoreCase = true) }
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
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.search_plot)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Liste des parcelles
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Bouton Nouvelle parcelle
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { navController.navigate(Routes.NEW_PLOT) },
                    shape = RoundedCornerShape(8.dp),
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

        Spacer(modifier = Modifier.height(16.dp))

        // Boutons d'action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { /* Ajouter à la parcelle sélectionnée */ },
                enabled = selectedPlot != null,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(stringResource(R.string.add_to_plot))
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
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.scrim
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