package com.akkuunamatata.eco_plant.pages

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import androidx.preference.PreferenceManager
import com.akkuunamatata.eco_plant.R
import com.akkuunamatata.eco_plant.database.plants.ParcelleData
import com.akkuunamatata.eco_plant.database.plants.PlantSpecies
import com.akkuunamatata.eco_plant.pages.mapsScreens.MapFullPreviewCard
import com.akkuunamatata.eco_plant.pages.mapsScreens.MapPreviewCard
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavHostController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    // Exemple data
    val exampleData = listOf(
        ParcelleData(
            lat = 43.764014,
            long = 3.869409,
            idAuthor = "user123",
            plants = listOf(
                PlantSpecies(
                    name = "Lavande",
                    services = floatArrayOf(0.9f, 0.8f, 0.7f),
                    reliabilities = floatArrayOf(0.95f, 0.9f, 0.85f),
                    culturalConditions = arrayOf(
                        "Soleil",
                        "Sol bien drainé",
                        "Résistant à la sécheresse"
                    )
                ),
            )
        ),
        ParcelleData(
            lat = 43.763200,
            long = 3.871500,
            idAuthor = "user456",
            plants = listOf(
                PlantSpecies(
                    name = "Romarin",
                    services = floatArrayOf(0.8f, 0.7f, 0.6f),
                    reliabilities = floatArrayOf(0.9f, 0.85f, 0.8f),
                    culturalConditions = arrayOf(
                        "Soleil",
                        "Sol bien drainé",
                        "Résistant à la sécheresse"
                    )
                )
            )
        )
    )

    // Selected marker state
    var selectedParcelleData by remember { mutableStateOf<ParcelleData?>(null) }

    // Bottom sheet state
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    // Search query state
    var searchQuery by remember { mutableStateOf("") }

    // Configurer osmdroid
    DisposableEffect(Unit) {
        Configuration.getInstance()
            .load(context, PreferenceManager.getDefaultSharedPreferences(context))
        onDispose { }
    }

    // Créer et configurer la carte
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(15.0)
        }
    }

    // Gérer le cycle de vie
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Map view
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        ) { map ->
            // Position de départ par défaut (Montpellier)
            val startPoint = GeoPoint(43.764014, 3.869409)

            // Centrer initialement la carte sur la position par défaut
            map.controller.setCenter(startPoint)

            // Ajouter un overlay de localisation sans recentrage automatique
            val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), map).apply {
                enableMyLocation()
                // Removed automatic recentering on first fix
            }

            // Clear existing overlays (to prevent duplicates on recomposition)
            map.overlays.clear()

            // Add location overlay
            map.overlays.add(locationOverlay)

            // Pour chaque ParcelleData, ajouter un marqueur à la carte
            exampleData.forEach { parcelle ->
                val marker = Marker(map)
                marker.position = GeoPoint(parcelle.lat, parcelle.long)
                marker.title = "Parcelle de ${parcelle.idAuthor}"
                marker.snippet = parcelle.plants.joinToString(", ") { it.name }
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                // Custom marker icon
                ContextCompat.getDrawable(context, R.drawable.ic_map_filled)?.let {
                    marker.icon = it
                }

                // Handle marker click
                marker.setOnMarkerClickListener { clickedMarker, _ ->
                    selectedParcelleData = parcelle
                    true
                }

                map.overlays.add(marker)
            }
        }

        // Search bar at top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface),
                placeholder = { Text("Rechercher une plante ou un lieu") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Transparent
                ),
                singleLine = true,
                shape = RoundedCornerShape(24.dp)
            )
        }

        // FABs at bottom-right
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Add new location FAB
            SmallFloatingActionButton(
                onClick = { navController.navigate("scan") },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Ajouter une parcelle"
                )
            }

            // My location FAB - Manual centering on user's location
            FloatingActionButton(
                onClick = {
                    val locationOverlay = mapView.overlays
                        .filterIsInstance<MyLocationNewOverlay>()
                        .firstOrNull()

                    locationOverlay?.myLocation?.let { location ->
                        mapView.controller.animateTo(location)
                    }
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Ma position"
                )
            }
        }

        // Preview card for selected marker
        AnimatedVisibility(
            visible = selectedParcelleData != null && !showBottomSheet,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            selectedParcelleData?.let { parcelle ->
                MapPreviewCard(
                    parcelle = parcelle,
                    onClose = { selectedParcelleData = null },
                    onExpandClick = { showBottomSheet = true }
                )
            }
        }

        // Full modal bottom sheet
        if (showBottomSheet && selectedParcelleData != null) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                selectedParcelleData?.let { parcelle ->
                    MapFullPreviewCard(parcelle = parcelle)
                }
            }
        }
    }
}