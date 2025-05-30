import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import androidx.preference.PreferenceManager
import com.akkuunamatata.eco_plant.R
import com.akkuunamatata.eco_plant.database.plants.ParcelleData
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.res.stringResource
import com.akkuunamatata.eco_plant.database.plants.maps.PlotRepository
import com.akkuunamatata.eco_plant.pages.mapsScreens.MapFullPreviewCard
import com.akkuunamatata.eco_plant.pages.mapsScreens.MapPreviewCard
import com.akkuunamatata.eco_plant.utils.getActualGeoPosition
import com.akkuunamatata.eco_plant.utils.searchLocationWithNominatim
import com.google.firebase.auth.FirebaseAuth


@Composable
fun MapScreen(
    navController: NavHostController,
    // get if the user is logged in
    isUserLoggedIn: Boolean = FirebaseAuth.getInstance().currentUser != null
) {
    // if not login, show the ScanNotLoggedInScreen.kt
    if (!isUserLoggedIn) {
        MapNotLoggedInScreen(navController)
        return
    }
    //else show the Map
    Map(navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Map(navController: NavHostController) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val repository = remember { PlotRepository.getInstance() }
    var parcelles by remember { mutableStateOf<List<ParcelleData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Stocker la position et le zoom actuels de la carte
    var currentMapPosition by remember { mutableStateOf<GeoPoint?>(null) }
    var currentMapZoom by remember { mutableStateOf(15.0) }

    // Variable pour suivre si la carte a déjà été centrée sur l'utilisateur
    var initialCenteringDone by remember { mutableStateOf(false) }

    // Variable pour verrouiller les changements de position
    var isRepositioningLocked by remember { mutableStateOf(false) }

    // Utiliser collectAsState pour observer le StateFlow
    val parcellesFlow = remember { repository.parcelles }.collectAsState()

    // Effet pour charger les données au démarrage
    LaunchedEffect(Unit) {
        isLoading = true
        repository.getAllParcelles()
        isLoading = false
    }

    // Mettre à jour parcelles quand parcellesFlow change
    LaunchedEffect(parcellesFlow.value) {
        parcelles = parcellesFlow.value
    }

    // Selected marker state
    var selectedParcelleData by remember { mutableStateOf<ParcelleData?>(null) }

    // Bottom sheet state
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    // Search query state
    var searchQuery by remember { mutableStateOf("") }

    // Search error state
    var isSearchError by remember { mutableStateOf(false) }

    // Loading state
    var isSearching by remember { mutableStateOf(false) }

    // Configure OSMDroid
    DisposableEffect(Unit) {
        Configuration.getInstance().apply {
            load(context, PreferenceManager.getDefaultSharedPreferences(context))
            userAgentValue = "EcoPlant/1.0 (Android)" // User agent for Nominatim
        }
        onDispose { }
    }

    // Create and configure the map
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            setBuiltInZoomControls(false) // Désactive les boutons +/-
            controller.setZoom(15.0)
        }
    }

    // Centrer la carte sur la position de l'utilisateur au démarrage uniquement
    LaunchedEffect(Unit) {
        // Attendre que la vue soit complètement initialisée
        delay(300)

        if (!initialCenteringDone) {
            try {
                // Obtenir la position actuelle de l'utilisateur
                val userLocation = getActualGeoPosition(context)

                if (userLocation != null) {
                    // Centrer la carte sur cette position
                    mapView.controller.setCenter(userLocation)

                    // Stocker cette position comme position initiale
                    currentMapPosition = userLocation

                    // Marquer que le centrage initial a été effectué
                    initialCenteringDone = true
                } else {
                    // Position par défaut (centre de la France) si la géolocalisation échoue
                    val defaultPosition = GeoPoint(46.603354, 1.888334)
                    mapView.controller.setCenter(defaultPosition)
                    currentMapPosition = defaultPosition
                    initialCenteringDone = true
                }
            } catch (e: Exception) {
                // En cas d'erreur, utiliser une position par défaut
                Log.e("MapScreen", "Erreur de géolocalisation: ${e.message}")
                val defaultPosition = GeoPoint(46.603354, 1.888334)
                mapView.controller.setCenter(defaultPosition)
                currentMapPosition = defaultPosition
                initialCenteringDone = true
            }
        }
    }

    // Fonction pour sauvegarder la position actuelle de la carte
    val saveCurrentMapState = {
        currentMapPosition = mapView.mapCenter as GeoPoint
        currentMapZoom = mapView.zoomLevelDouble
    }

    // Fonction pour restaurer la position sauvegardée
    val restoreMapState = {
        currentMapPosition?.let { position ->
            // Appliquer directement sans animation pour éviter le clignotement
            mapView.controller.setZoom(currentMapZoom)
            mapView.controller.setCenter(position)
        }
    }

    // Manage lifecycle
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

    // Geocoding function using Nominatim directly
    val geocodeLocation = { query: String ->
        coroutineScope.launch {
            isSearching = true
            isSearchError = false // Réinitialiser l'état d'erreur
            focusManager.clearFocus() // Ferme le clavier

            try {
                val result = withContext(Dispatchers.IO) {
                    searchLocationWithNominatim(query)
                }

                if (result != null) {
                    // Move map to found location
                    mapView.controller.animateTo(GeoPoint(result.first, result.second))
                    mapView.controller.setZoom(12.0) // Zoom level appropriate for cities
                } else {
                    isSearchError = true // Mettre l'état d'erreur à true au lieu d'afficher un message
                }
            } catch (e: Exception) {
                isSearchError = true // Mettre l'état d'erreur à true en cas d'exception
            } finally {
                isSearching = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Map view
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize(),
            update = { map ->
                // Mettre à jour la carte uniquement si le verrouillage n'est pas actif
                if (!isRepositioningLocked) {
                    // Add location overlay without automatic recentering
                    val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), map).apply {
                        enableMyLocation() // Activer la localisation mais PAS le suivi
                    }

                    // Clear existing overlays to prevent duplicates
                    map.overlays.clear()

                    // Add location overlay
                    map.overlays.add(locationOverlay)

                    parcelles.forEach { parcelle ->
                        // Create marker for each parcelle
                        val marker = Marker(map).apply {
                            position = GeoPoint(parcelle.lat, parcelle.long)
                            title = parcelle.idAuthor
                            icon = ContextCompat.getDrawable(context, R.drawable.ic_map_filled)

                            // Désactiver le centrage automatique par défaut
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            setPanToView(false)  // Empêcher le comportement par défaut

                            setOnMarkerClickListener { marker, _ ->
                                // Sauvegarder l'état de la carte avant de centrer sur le marqueur
                                saveCurrentMapState()

                                // Centrer manuellement la carte sur le marqueur
                                map.controller.animateTo(marker.position)

                                selectedParcelleData = parcelle // Set selected parcelle data
                                showBottomSheet = false // Close bottom sheet if open
                                true // Return true to indicate the click was handled
                            }
                        }
                        map.overlays.add(marker)
                    }

                    // Restaurer la position si elle existe et que ce n'est pas le centrage initial
                    if (currentMapPosition != null && initialCenteringDone) {
                        restoreMapState()
                    }
                }
            }
        )

        // Search bar at top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter)
        ) {
            // Barre de recherche avec bord rouge lorsqu'il y a une erreur
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp) // Hauteur fixe pour éviter les espaces
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = if (isSearchError) MaterialTheme.colorScheme.error else Color.Black.copy(
                            alpha = 0.2f
                        ),
                        spotColor = if (isSearchError) MaterialTheme.colorScheme.error else Color.Black.copy(
                            alpha = 0.2f
                        )
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .then(
                        if (isSearchError) {
                            Modifier.border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.error,
                                shape = RoundedCornerShape(24.dp)
                            )
                        } else {
                            Modifier
                        }
                    ),
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = if (isSearchError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    BasicTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            // Réinitialiser l'erreur si l'utilisateur modifie la requête
                            if (isSearchError) isSearchError = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            color = if (isSearchError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        ),
                        decorationBox = { innerTextField ->
                            Box(contentAlignment = Alignment.CenterStart) {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        stringResource(R.string.search_city),
                                        color = if (isSearchError)
                                            MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                                innerTextField()
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                if (searchQuery.isNotEmpty()) {
                                    geocodeLocation(searchQuery)
                                }
                                focusManager.clearFocus() // Ferme le clavier
                            }
                        )
                    )

                    if (isSearching) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else if (searchQuery.isNotEmpty()) {
                        Row {
                            // Search icon button
                            IconButton(onClick = {
                                if (searchQuery.isNotEmpty()) {
                                    geocodeLocation(searchQuery)
                                }
                                focusManager.clearFocus() // Ferme le clavier
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Rechercher",
                                    tint = if (isSearchError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                )
                            }

                            // Clear icon button
                            IconButton(onClick = {
                                searchQuery = ""
                                isSearchError = false // Réinitialiser l'erreur en effaçant la recherche
                                focusManager.clearFocus() // Ferme également le clavier lors de l'effacement
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Effacer",
                                    tint = if (isSearchError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
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
                    // Centrer manuellement sur la position actuelle de l'utilisateur
                    val userLocation = getActualGeoPosition(context)
                    mapView.controller.animateTo(userLocation)
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Ma position"
                )
            }
        }

        // Preview card for selected marker with animations améliorées
        AnimatedVisibility(
            visible = selectedParcelleData != null && !showBottomSheet,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(animationSpec = tween(150)),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(animationSpec = tween(100)),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            selectedParcelleData?.let { parcelle ->
                MapPreviewCard(
                    parcelle = parcelle,
                    onClose = {
                        // Verrouiller la position avant la fermeture
                        isRepositioningLocked = true

                        // Sauvegarde de l'état actuel
                        saveCurrentMapState()

                        // Fermer la carte d'information
                        selectedParcelleData = null

                        // Restaurer immédiatement la position
                        restoreMapState()

                        // Déverrouiller après une courte période
                        coroutineScope.launch {
                            withContext(Dispatchers.Main) {
                                isRepositioningLocked = false
                            }
                        }
                    },
                    onExpandClick = { showBottomSheet = true }
                )
            }
        }

        // Full modal bottom sheet
        if (showBottomSheet && selectedParcelleData != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    // Verrouiller la position avant la fermeture
                    isRepositioningLocked = true

                    // Sauvegarde de l'état actuel
                    saveCurrentMapState()

                    showBottomSheet = false

                    // Restaurer immédiatement la position
                    restoreMapState()

                    // Déverrouiller après une courte période
                    coroutineScope.launch {
                        withContext(Dispatchers.Main) {
                            isRepositioningLocked = false
                        }
                    }
                },
                sheetState = sheetState
            ) {
                selectedParcelleData?.let { parcelle ->
                    MapFullPreviewCard(parcelle = parcelle)
                }
            }
        }
    }
}