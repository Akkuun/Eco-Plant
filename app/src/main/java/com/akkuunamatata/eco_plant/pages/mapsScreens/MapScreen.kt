package com.akkuunamatata.eco_plant.pages.mapsScreens

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import androidx.preference.PreferenceManager
import com.akkuunamatata.eco_plant.R
import com.akkuunamatata.eco_plant.database.plants.ParcelleData
import com.akkuunamatata.eco_plant.database.plants.PlantSpecies
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.res.stringResource
import com.google.firebase.auth.FirebaseAuth


@Composable
fun MapScreen(
    navController: NavHostController,
    // get if the user is logged in
    isUserLoggedIn: Boolean = FirebaseAuth.getInstance().currentUser != null
) {
    // if not login, show the NotLoggedInScreen
    if (!isUserLoggedIn) {
        NotLoggedInScreen(navController)
        return
    }
    //else show the Map
    Map(navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Map(navController: NavHostController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

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
                    isSearchError =
                        true // Mettre l'état d'erreur à true au lieu d'afficher un message
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
            modifier = Modifier.fillMaxSize()
        ) { map ->
            // Default starting position -> actual user location
            val actualLocation = getActualLocation(context);
            // Use actual location if available, otherwise default to Paris coordinates
            val startPoint = actualLocation ?: GeoPoint(48.8566, 2.3522) // Paris coordinates

            // Center initially on default position
            map.controller.setCenter(startPoint)

            // Add location overlay without automatic recentering
            val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), map).apply {
                enableMyLocation()
            }

            // Clear existing overlays to prevent duplicates
            map.overlays.clear()

            // Add location overlay
            map.overlays.add(locationOverlay)

            // Add markers for each ParcelleData
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
                marker.setOnMarkerClickListener { _, _ ->
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
                                isSearchError =
                                    false // Réinitialiser l'erreur en effaçant la recherche
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
// Function to get the actual location of the user in geoPoint format
fun getActualLocation(context: Context): GeoPoint? {
    val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
    return try {
        // Get the last known location
        val location =
            locationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER)
                ?: return null // Return null if no location is found

        // Convert to GeoPoint
        GeoPoint(location.latitude, location.longitude)
    } catch (e: SecurityException) {
        e.printStackTrace()
        null // Return null if there is a security exception (e.g., permissions not granted)
    }

}


/**
 * Écran affiché lorsque l'utilisateur n'est pas connecté
 * Style inspiré de Google
 */
@Composable
fun NotLoggedInScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo ou icône
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location Icon",
                modifier = Modifier
                    .size(64.dp)
                    .padding(bottom = 16.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            // Titre de style Google
            Text(
                text = "EcoPlant Maps",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Message explicatif
            Text(
                text = "Cette fonctionnalité n'est disponible que pour les utilisateurs connectés.",
                fontSize = 16.sp,
                color = Color(0xFF5F6368),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Bouton de connexion style Google
            Button(
                onClick = { navController.navigate("settings") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(stringResource(R.string.Go_to_login), fontSize = 16.sp)
            }

            // Bouton secondaire pour s'inscrire
            OutlinedButton(
                onClick = { navController.navigate("sign_in") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(top = 12.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(stringResource(R.string.create_an_account), fontSize = 16.sp)
            }
        }
    }
}

/**
 * Search for a location using the Nominatim API directly
 * @return Pair<Double, Double> containing latitude and longitude if found, null otherwise
 */
@SuppressLint("SetJavaScriptEnabled")
suspend fun searchLocationWithNominatim(query: String): Pair<Double, Double>? {
    return withContext(Dispatchers.IO) {
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val urlString =
                "https://nominatim.openstreetmap.org/search?q=$encodedQuery&format=json&limit=1"

            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "EcoPlant/1.0 (Android)")
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.connect()

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = connection.inputStream.bufferedReader()
                val response = reader.readText()
                reader.close()

                val jsonArray = JSONArray(response)
                if (jsonArray.length() > 0) {
                    val result = jsonArray.getJSONObject(0)
                    val lat = result.getDouble("lat")
                    val lon = result.getDouble("lon")
                    return@withContext Pair(lat, lon)
                }
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}