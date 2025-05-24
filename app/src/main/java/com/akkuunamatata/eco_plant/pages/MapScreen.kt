package com.akkuunamatata.eco_plant.pages

import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.akkuunamatata.eco_plant.database.plants.ParcelleData
import com.akkuunamatata.eco_plant.database.plants.PlantSpecies
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.views.overlay.Marker

@Composable
fun MapScreen() {


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


    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

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
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        ) { map ->
            // Position de départ par défaut (Montpellier)
            val startPoint = GeoPoint(43.764014, 3.869409)

            // Centrer initialement la carte sur la position par défaut
            map.controller.setCenter(startPoint)

            // Ajouter un overlay de localisation
            val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), map)
            locationOverlay.enableMyLocation()
            map.overlays.add(locationOverlay)

            // Utiliser le Handler du thread principal pour l'animation
            locationOverlay.runOnFirstFix {
                Handler(Looper.getMainLooper()).post {
                    map.controller.animateTo(locationOverlay.myLocation)
                }
            }
            //for each ParcelleData, add a marker to the map
            exampleData.forEach { parcelle ->
                val marker = Marker(map)
                marker.position = GeoPoint(parcelle.lat, parcelle.long)
                marker.title = "Parcelle de ${parcelle.idAuthor}"
                marker.snippet = parcelle.plants.joinToString("\n") { it.name }
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                map.overlays.add(marker)
            }

        }
    }
}